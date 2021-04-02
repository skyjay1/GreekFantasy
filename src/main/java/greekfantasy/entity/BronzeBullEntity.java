package greekfantasy.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.ShootFireGoal;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BronzeBullEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(BronzeBullEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "BullState";
  private static final String KEY_SPAWN = "SpawnTime";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  private static final byte FIRING = (byte)2;
  private static final byte GORING = (byte)3;
  // bytes to use in World#setEntityState
  private static final byte SPAWN_CLIENT = 8;
  private static final byte FIRING_CLIENT = 9;
  private static final byte GORING_CLIENT = 10;
  
  private static final double FIRE_RANGE = 8.0D;
  private static final int MAX_SPAWN_TIME = 90;  
  private static final int MAX_FIRING_TIME = 89;
  private static final int MAX_GORING_TIME = 130;
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS));
  
  private int firingTime;
  private int spawnTime;
  private int goringTime;
  
  public BronzeBullEntity(final EntityType<? extends BronzeBullEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 150.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 24.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 9.0D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.75D)
        .createMutableAttribute(Attributes.ARMOR, 8.0D);
  }
  
  public static BronzeBullEntity spawnBronzeBull(final World world, final BlockPos pos, final float yaw) {
    BronzeBullEntity entity = GFRegistry.BRONZE_BULL_ENTITY.create(world);
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
    this.goalSelector.addGoal(0, new BronzeBullEntity.SpawningGoal());
    this.goalSelector.addGoal(1, new BronzeBullEntity.FireAttackGoal(MAX_FIRING_TIME, 120));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25D, false));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

    // update spawn time
    if(isSpawning() || spawnTime > 0) {
      // update timer
      if(--spawnTime <= 0) {
        setSpawning(false);
      }
    }
    
    // update goring attack
    if(isGoring() || goringTime > 0) {
      // update timer
      if(--goringTime <= 0) {
        setGoring(false);
      }
    }
    
    // update fire attack
    if(isFiring() || firingTime > 0) {
      // update timer
      if(--firingTime <= 0) {
        setFiring(false);
      }
    }    
    
    // update fire attack target
    if(this.isServerWorld() && (this.isFiring() || this.isGoring()) && this.getAttackTarget() == null) {
      this.setFiring(false);
      this.setGoring(false);
    }
  
    // spawn particles
    if (world.isRemote() && this.isFiring()) {
      spawnFireParticles();
    }
   
    // spawn particles
    if(this.world.isRemote()) {
      final double x = this.getPosX();
      final double y = this.getPosY() + 0.25D;
      final double z = this.getPosZ();
      final double motion = 0.06D;
      final double radius = this.getWidth() * 1.15D;
      world.addParticle(ParticleTypes.LAVA, 
          x + (world.rand.nextDouble() - 0.5D) * radius, 
          y + (world.rand.nextDouble() - 0.5D) * radius, 
          z + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          (world.rand.nextDouble() - 0.5D) * 0.07D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
 }

  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      // set goring
      setGoring(true);
      // break intersecting blocks
      destroyIntersectingBlocks(2.25F, 2.0D);
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
    return isSpawning() || source.isMagicDamage() || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.WITHER 
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
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.setState(compound.getByte(KEY_STATE));
     spawnTime = compound.getInt(KEY_SPAWN);
  }
  
  public void spawnFireParticles() {
    if(!world.isRemote()) {
      return;
    }
    Vector3d lookVec = this.getLookVec();
    Vector3d pos = this.getEyePosition(1.0F);
    final double motion = 0.06D;
    final double radius = 0.75D;
    
    for (int i = 0; i < 5; i++) {
      world.addParticle(ParticleTypes.FLAME, 
          pos.x + (world.rand.nextDouble() - 0.5D) * radius, 
          pos.y + (world.rand.nextDouble() - 0.5D) * radius, 
          pos.z + (world.rand.nextDouble() - 0.5D) * radius,
          lookVec.x * motion * FIRE_RANGE, 
          lookVec.y * motion * 0.5D,
          lookVec.z * motion * FIRE_RANGE);
    }
  }
  
  public byte getState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getState() == NONE; }
  
  public boolean isSpawning() { return spawnTime > 0 || getState() == SPAWNING; }

  public boolean isFiring() { return getState() == FIRING; }
  
  public boolean isGoring() { return getState() == GORING; }
  
  public void setFiring(final boolean firing) {
    firingTime = firing ? MAX_FIRING_TIME : 0;
    setState(firing ? FIRING : NONE);
    if(firing && !this.world.isRemote()) {
      this.world.setEntityState(this, FIRING_CLIENT);
    }
  }
  
  public void setGoring(final boolean goring) { 
    goringTime = goring ? MAX_GORING_TIME : 0;
    setState(goring ? GORING : NONE);
    if(goring && !this.world.isRemote()) {
      this.world.setEntityState(this, GORING_CLIENT);
    }
  }

  public void setSpawning(final boolean spawning) {
    spawnTime = spawning ? MAX_SPAWN_TIME : 0;
    setState(spawning ? SPAWNING : NONE);
    if(spawning && !this.world.isRemote()) {
      this.world.setEntityState(this, SPAWN_CLIENT);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case SPAWN_CLIENT:
      setSpawning(true);
      break;
    case FIRING_CLIENT:
      setFiring(true);
      break;
    case GORING_CLIENT:
      setGoring(true);
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
   
  public float getSpawnPercent(final float partialTick) { return getPercent(spawnTime, MAX_SPAWN_TIME, partialTick); }
  
  public float getFiringPercent(final float partialTick) { return getPercent(firingTime, MAX_FIRING_TIME, partialTick); }
  
  public float getGoringPercent(final float partialTick) { return getPercent(goringTime, MAX_GORING_TIME, partialTick); }
  
  private float getPercent(final int timer, final int maxValue, final float partialTick) {
    if(timer <= 0) {
      return 0.0F;
    }
    final float prevSpawnPercent = Math.max((float)timer - partialTick, 0.0F) / (float)maxValue;
    final float spawnPercent = (float)timer / (float)maxValue;
    return 1.0F - MathHelper.lerp(partialTick / 6, prevSpawnPercent, spawnPercent); 
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
  
  
  /**
   * Breaks blocks within this entity's bounding box
   * @param offset the forward distance to offset the bounding box
   **/
  private void destroyIntersectingBlocks(final float maxHardness, final double offset) {
    if(!world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
      return;
    }
    final Vector3d facing = Vector3d.fromPitchYaw(this.getPitchYaw());
    final AxisAlignedBB box = this.getBoundingBox().offset(facing.normalize().scale(offset));
    BlockPos p;
    BlockState s;
    for(double x = box.minX - 0.25D; x < box.maxX + 0.25D; x++) {
      for(double y = box.minY + 1.1D; y < box.maxY + 0.5D; y++) {
        for(double z = box.minZ - 0.25D; z < box.maxZ + 0.25D; z++) {
          p = new BlockPos(x, y, z);
          s = this.getEntityWorld().getBlockState(p);
          if((s.isSolid() || s.getMaterial().blocksMovement()) && s.getBlockHardness(world, p) < maxHardness && !s.isIn(BlockTags.WITHER_IMMUNE)) {
            this.getEntityWorld().destroyBlock(p, true);
          }
        }
      }
    }
  }
  
  // Custom goals
  
  class SpawningGoal extends Goal {

    public SpawningGoal() { setMutexFlags(EnumSet.allOf(Goal.Flag.class)); }

    @Override
    public boolean shouldExecute() { return BronzeBullEntity.this.isSpawning(); }

    @Override
    public void tick() { 
      BronzeBullEntity.this.getNavigator().clearPath(); 
      BronzeBullEntity.this.getLookController().setLookPosition(BronzeBullEntity.this.getPosX(), BronzeBullEntity.this.getPosY(), BronzeBullEntity.this.getPosZ());
      BronzeBullEntity.this.setRotation(0, 0);
    }
  }
  
  class FireAttackGoal extends ShootFireGoal {

    protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn) {
      super(BronzeBullEntity.this, fireTimeIn, maxCooldownIn, FIRE_RANGE);
    }

    @Override
    public boolean shouldExecute() {  
      return super.shouldExecute() && BronzeBullEntity.this.isNoneState();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting() && BronzeBullEntity.this.isFiring();
    }
   
    @Override
    public void startExecuting() {
      super.startExecuting();
      BronzeBullEntity.this.setFiring(true);
    }
   
    @Override
    public void resetTask() {
      super.resetTask();
      BronzeBullEntity.this.setFiring(false);
    }
  }
}
