package greekfantasy.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TalosEntity extends MonsterEntity implements IRangedAttackMob {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(TalosEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "TalosState";
  private static final String KEY_SPAWN = "SpawnTime";
  private static final String KEY_SHOOT = "ShootTime";
  private static final String KEY_COOLDOWN = "AttackCooldown";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  private static final byte SHOOT = (byte)2;
  // bytes to use in World#setEntityState
  private static final byte SPAWN_CLIENT = 8;
  private static final byte SHOOT_CLIENT = 9;
  
  private static final int MAX_SPAWN_TIME = 94;
  private static final int MAX_SHOOT_TIME = 80;
  private static final int SHOOT_COOLDOWN = 188;
  private static final int MELEE_COOLDOWN = 40;
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);
  
  private int spawnTime;
  private int shootTime;
  private int attackCooldown;
  
  public TalosEntity(final EntityType<? extends TalosEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 150.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.21D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 9.0D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.75D)
        .createMutableAttribute(Attributes.ARMOR, 8.0D);
  }
  
  public static TalosEntity spawnTalos(final World world, final BlockPos pos, final float yaw) {
    TalosEntity entity = GFRegistry.TALOS_ENTITY.create(world);
    entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
    entity.renderYawOffset = yaw;
    world.addEntity(entity);
    entity.setSpawning(true);
    // trigger spawn for nearby players
    for (ServerPlayerEntity player : world.getEntitiesWithinAABB(ServerPlayerEntity.class, entity.getBoundingBox().grow(25.0D))) {
      CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
    }
    // play sound
    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_WITHER_SPAWN, entity.getSoundCategory(), 1.2F, 1.0F, false);
    return entity;
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(NONE));
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new TalosEntity.SpawningGoal());
    this.goalSelector.addGoal(1, new TalosEntity.RangedAttackGoal(4, 25.0F));
    this.goalSelector.addGoal(3, new TalosEntity.MeleeAttackGoal(1.0D, false));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // attack cooldown
    attackCooldown = Math.max(attackCooldown - 1,  0);
    
    // update spawn time
    if(isSpawning() || spawnTime > 0) {
      // update timer
      if(--spawnTime <= 0) {
        setSpawning(false);
      }
    }
    
    // update shoot time
    if(isShooting() || shootTime > 0) {
      // update timer
      if(shootTime++ >= MAX_SHOOT_TIME) {
        setShooting(false);
        shootTime = 0;
      }
    }
   
    // spawn particles
    if (horizontalMag(this.getMotion()) > (double)2.5000003E-7F && this.rand.nextInt(3) == 0) {
      int i = MathHelper.floor(this.getPosX());
      int j = MathHelper.floor(this.getPosY() - (double)0.2F);
      int k = MathHelper.floor(this.getPosZ());
      BlockPos pos = new BlockPos(i, j, k);
      BlockState blockstate = this.world.getBlockState(pos);
      if (!this.world.isAirBlock(pos)) {
        final BlockParticleData data = new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos);
        final double radius = this.getWidth();
        final double motion = 4.0D;
        this.world.addParticle(data, 
           this.getPosX() + (this.rand.nextDouble() - 0.5D) * radius * 2, 
           this.getPosY() + 0.1D, 
           this.getPosZ() + (this.rand.nextDouble() - 0.5D) * radius * 2, 
           motion * (this.rand.nextDouble() - 0.5D), 0.45D, (this.rand.nextDouble() - 0.5D) * motion);
      }
    }
 }

  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      // apply extra knockback velocity when attacking (ignores knockback resistance)
      final double knockbackFactor = 0.92D;
      final Vector3d myPos = this.getPositionVec();
      final Vector3d ePos = entityIn.getPositionVec();
      final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
      final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
      entityIn.addVelocity(dX, knockbackFactor / 2.0D, dZ);
      entityIn.velocityChanged = true;
      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 0.6F + rand.nextFloat() * 0.2F);
      return true;
    }
    return false;
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
     ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
     this.setSpawning(true);
     return data;
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.42F * this.getJumpFactor();
  }

  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  public void collideWithNearbyEntities() { }
  
  @Override
  public boolean isNonBoss() { return false; }
  
  @Override
  public boolean canDespawn(final double disToPlayer) { return false; }
  
  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return isSpawning() || source.isMagicDamage() || source == DamageSource.IN_WALL || source == DamageSource.WITHER 
        || source.getImmediateSource() instanceof AbstractArrowEntity || super.isInvulnerableTo(source);
  }
  
  @Override
  public int getTalkInterval() { return 280; }

  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_BLAZE_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_IRON_GOLEM_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_IRON_GOLEM_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.8F; }
  
  @Override
  protected float getSoundPitch() { return 0.6F + rand.nextFloat() * 0.25F; }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     compound.putByte(KEY_STATE, this.getState());
     compound.putInt(KEY_SPAWN, spawnTime);
     compound.putInt(KEY_SHOOT, shootTime);
     compound.putInt(KEY_COOLDOWN, attackCooldown);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.setState(compound.getByte(KEY_STATE));
     spawnTime = compound.getInt(KEY_SPAWN);
     shootTime = compound.getInt(KEY_SHOOT);
     attackCooldown = compound.getInt(KEY_COOLDOWN);
  }
  
  public byte getState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getState() == NONE; }
  
  public boolean isSpawning() { return spawnTime > 0 || getState() == SPAWNING; }
  
  public void setShooting(final boolean shoot) { setState(shoot ? SHOOT : NONE); }
  
  public boolean isShooting() { return getState() == SHOOT; }

  public void setSpawning(final boolean spawning) {
    spawnTime = spawning ? MAX_SPAWN_TIME : 0;
    setState(spawning ? SPAWNING : NONE);
    if(spawning && !this.world.isRemote()) {
      this.world.setEntityState(this, SPAWN_CLIENT);
    }
  }
  
  public void setAttackCooldown(final int cooldown) { attackCooldown = cooldown; }
  
  public boolean hasNoCooldown() { return attackCooldown <= 0; }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case SPAWN_CLIENT:
      setSpawning(true);
      break;
    case SHOOT_CLIENT:
      shootTime = 1;
      this.playSound(SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 1.1F, 1.0F);
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
   
  public float getSpawnPercent(final float partialTick) { 
    if(spawnTime <= 0) {
      return 1.0F;
    }
    final float prevSpawnPercent = Math.max((float)spawnTime - partialTick, 0.0F) / (float)MAX_SPAWN_TIME;
    final float spawnPercent = (float)spawnTime / (float)MAX_SPAWN_TIME;
    return 1.0F - MathHelper.lerp(partialTick / 6, prevSpawnPercent, spawnPercent); 
  }
 
  @Override
  public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
    if(this.world.isRemote() || !this.isShooting() || shootTime < (MAX_SHOOT_TIME / 4)) {
      return;
    }
    ItemStack itemstack = new ItemStack(Items.ARROW);
    AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, itemstack, distanceFactor);
    // this is adapted from LlamaSpit code, it moves the arrow nearer to right-front of the body
    // arrow.setPosition(this.getPosX() - (this.getWidth() + 1.0F) * 0.5D * MathHelper.sin(this.renderYawOffset * 0.017453292F), this.getPosYEye() - 0.10000000149011612D, this.getPosZ() + (this.getWidth() + 1.0F) * 0.5D * MathHelper.cos(this.renderYawOffset * 0.017453292F));
    arrow.setPosition(this.getPosX() - (this.getWidth()) * 0.85D * MathHelper.sin(this.renderYawOffset * 0.017453292F + 1.0F), this.getPosYEye() - 0.74D, this.getPosZ() + (this.getWidth()) * 0.85D * MathHelper.cos(this.renderYawOffset * 0.017453292F + 1.0F));
    double dx = target.getPosX() - arrow.getPosX();
    double dy = target.getPosYHeight(0.67D) - arrow.getPosY();
    double dz = target.getPosZ() - arrow.getPosZ();
    double dis = (double)MathHelper.sqrt(dx * dx + dz * dz);
    arrow.shoot(dx, dy + dis * (double)0.2F, dz, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
    arrow.setDamage(1.0D + this.world.getDifficulty().getId() * 0.25D);
    arrow.setShooter(this);
    this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    this.world.addEntity(arrow);
  }
  
  // Boss Logic

  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }
  
  // Custom goals
  
  class SpawningGoal extends Goal {

    public SpawningGoal() { setMutexFlags(EnumSet.allOf(Goal.Flag.class)); }

    @Override
    public boolean shouldExecute() { return TalosEntity.this.isSpawning(); }

    @Override
    public void tick() { 
      TalosEntity.this.getNavigator().clearPath(); 
      TalosEntity.this.getLookController().setLookPosition(TalosEntity.this.getPosX(), TalosEntity.this.getPosY(), TalosEntity.this.getPosZ());
      TalosEntity.this.setRotation(0, 0);
    }
  }
  
  class RangedAttackGoal extends net.minecraft.entity.ai.goal.RangedAttackGoal {
    
    public RangedAttackGoal(int interval, float attackDistance) {
      super(TalosEntity.this, 1.0F, interval, attackDistance);
    }
    
    @Override
    public boolean shouldExecute() {
      final LivingEntity target = TalosEntity.this.getAttackTarget();
      double disSq = (target != null) ? TalosEntity.this.getDistanceSq(target) : 0.0D;
      return TalosEntity.this.isNoneState() && TalosEntity.this.hasNoCooldown() && disSq > 9.0D && super.shouldExecute();
    }
   
    @Override
    public void startExecuting() {
      super.startExecuting();
      TalosEntity.this.setAttackCooldown(SHOOT_COOLDOWN);
      TalosEntity.this.setShooting(true);
      TalosEntity.this.world.setEntityState(TalosEntity.this, SHOOT_CLIENT);
      TalosEntity.this.shootTime = 1;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return TalosEntity.this.shootTime > 0 && TalosEntity.this.isShooting();
    }
    
    @Override
    public void tick() {
      super.tick();
    }
    
    @Override
    public void resetTask() {
      TalosEntity.this.setState(NONE);
      TalosEntity.this.shootTime = 0;
      super.resetTask();
    }
  }
  
  class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

    public MeleeAttackGoal(double speedIn, boolean useLongMemory) {
      super(TalosEntity.this, speedIn, useLongMemory);
    }
    
    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      if(TalosEntity.this.hasNoCooldown()) {
        super.checkAndPerformAttack(enemy, distToEnemySqr);
      }
    }
    
    @Override
    protected void func_234039_g_() {
      super.func_234039_g_();
      TalosEntity.this.setAttackCooldown(MELEE_COOLDOWN);
    }
  }
}
