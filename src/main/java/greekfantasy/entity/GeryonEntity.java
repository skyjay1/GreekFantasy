package greekfantasy.entity;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.SummonMobGoal;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GeryonEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(GeryonEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "GeryonState";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  private static final byte SMASH = (byte)2;
  private static final byte SUMMON_COW = (byte)4;
  // bytes to use in World#setEntityState
  private static final byte SMASH_CLIENT = 9;
  private static final byte SUMMON_COW_CLIENT = 10;
  
  private static final int MAX_SPAWN_TIME = 110;
  private static final int MAX_SMASH_TIME = 42;
  private static final int MAX_SUMMON_TIME = 35;
  private static final double SMASH_RANGE = 12.0D;
  private static final int ATTACK_COOLDOWN = 38;
  private static final int STUN_DURATION = 35;
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);
  
  private int spawnTime;
  private int smashTime;
  private int summonTime;
  
  private int attackCooldown;
  
  public GeryonEntity(final EntityType<? extends GeryonEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 160.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.21D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.98D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.5D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT);
  }
  
  public static boolean canGeryonSpawnOn(final IWorld world, final BlockPos pos) {    
    final AxisAlignedBB box = new AxisAlignedBB(pos).expand(-1, 0, -1).expand(1, 5, 1);
    BlockPos p;
    BlockState s;
    // check surrounding area (if it's big enough)
    for(double x = box.minX; x < box.maxX; x++) {
      for(double y = box.minY; y < box.maxY; y++) {
        for(double z = box.minZ; z < box.maxZ; z++) {
          p = new BlockPos(x, y, z);
          s = world.getBlockState(p);
          if(s.isIn(BlockTags.WITHER_IMMUNE)) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  public static GeryonEntity spawnGeryon(final World world, final BlockPos pos, final float yaw) {
    GeryonEntity entity = GFRegistry.GERYON_ENTITY.create(world);
    entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
    entity.renderYawOffset = yaw;
    entity.setSpawning(true);
    entity.setHeldItem(Hand.MAIN_HAND, new ItemStack(GFRegistry.IRON_CLUB));
    world.addEntity(entity);
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
    this.goalSelector.addGoal(0, new GeryonEntity.SpawningGoal());
    this.goalSelector.addGoal(1, new GeryonEntity.SummonCowGoal(MAX_SUMMON_TIME, 440));
    this.goalSelector.addGoal(2, new GeryonEntity.SmashAttackGoal(SMASH_RANGE, 210));
    this.goalSelector.addGoal(3, new GeryonEntity.MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // attack cooldown
    attackCooldown = Math.max(attackCooldown - 1,  0);
    
    // update spawn time
    if(isSpawning()) {
      // update timer
      if(--spawnTime <= 0) {
        setSpawning(false);
        if(!world.isRemote()) {
          destroyIntersectingBlocks(0);
        }
      }
      // spawn particles
      addBlockParticles(10);
    }
    
    // update smash attack
    if(this.isSmashAttack()) {
      smashTime++;
    } else if(smashTime > 0) {
      smashTime = 0;
    }
        
    // update summoning
    if(this.isSummoning()) {
      summonTime++;
      if(this.getHeldItem(Hand.OFF_HAND).isEmpty()) {
        this.setHeldItem(Hand.OFF_HAND, new ItemStack(GFRegistry.HORN));
      }
    } else if(summonTime > 0) {
      summonTime = 0;
      if(this.getHeldItem(Hand.OFF_HAND).getItem() == GFRegistry.HORN) {
        this.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
      }
    }

    // spawn particles
    if (horizontalMag(this.getMotion()) > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
      addBlockParticles(2);
    }
 }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (!source.isDamageAbsolute() && GreekFantasy.CONFIG.GERYON_RESISTANCE.get()) {
      amount *= 0.4F;
    }
    super.damageEntity(source, amount);
  }
  
  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      // apply extra upward velocity when attacking
      entityIn.setMotion(entityIn.getMotion().add(0.0D, (double)0.25F, 0.0D));
      return true;
    }
    return false;
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.82F * this.getJumpFactor();
  }

  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  public void applyEntityCollision(Entity entityIn) { 
    if(this.canBePushed()) {
      super.applyEntityCollision(entityIn);
    }
  }
  
  @Override
  public boolean isNonBoss() { return false; }
  
  @Override
  public boolean canDespawn(final double disToPlayer) { return false; }
  
  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    this.setHeldItem(Hand.MAIN_HAND, new ItemStack(GFRegistry.IRON_CLUB));
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_VILLAGER_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_VILLAGER_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_VILLAGER_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.5F; }
  
  @Override
  protected float getSoundPitch() { return 0.089F; }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     compound.putByte(KEY_STATE, this.getGeryonState());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.setGeryonState(compound.getByte(KEY_STATE));
  }
  
  public byte getGeryonState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setGeryonState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getGeryonState() == NONE; }
  
  public boolean isSmashAttack() { return getGeryonState() == SMASH; }
  
  public void setSmashAttack(final boolean smash) { setGeryonState(smash ? SMASH : NONE); }
 
  public boolean isSummoning() { return getGeryonState() == SUMMON_COW; }
  
  public void setSummoning(final boolean smash) { setGeryonState(smash ? SUMMON_COW : NONE); }
  
  public boolean isSpawning() { return spawnTime > 0 || getGeryonState() == SPAWNING; }
  
  public void setSpawning(final boolean spawning) {
    spawnTime = spawning ? MAX_SPAWN_TIME : 0;
    setGeryonState(spawning ? SPAWNING : NONE); 
  }
  
  public void setAttackCooldown() { attackCooldown = ATTACK_COOLDOWN; }
  
  public boolean hasNoCooldown() { return attackCooldown <= 0; }
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if(key == STATE) {
      this.spawnTime = isSpawning() ? MAX_SPAWN_TIME : 0;
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case SMASH_CLIENT:
      // spawn particles for all nearby entities
      final List<Entity> targets = this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(GeryonEntity.this, GeryonEntity.this.getBoundingBox().grow(SMASH_RANGE, SMASH_RANGE / 2, SMASH_RANGE));
      for(final Entity e : targets) {
        addSmashParticlesAt(e);
      }
      // add sound and block particles here
      world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_IRON_GOLEM_ATTACK, this.getSoundCategory(), 2.0F, 0.4F, false);
      addBlockParticles(45);
      break;
    case SUMMON_COW_CLIENT:
      for(int i = 0; i < 4; i++) {
        this.getEntityWorld().playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, this.getSoundCategory(), 2.0F, 0.2F, false);
      }
      for(int i = 0; i < 2; i++) {
        this.getEntityWorld().playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, this.getSoundCategory(), 1.8F, 0.4F, false);
      }
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  /**
   * Adds particles using the data of the block below this entity
   * @param count the number of particles to add
   **/
  private void addBlockParticles(final int count) {
    int i = MathHelper.floor(this.getPosX());
    int j = MathHelper.floor(this.getPosY() - (double)0.2F);
    int k = MathHelper.floor(this.getPosZ());
    BlockPos pos = new BlockPos(i, j, k);
    BlockState blockstate = this.world.getBlockState(pos);
    if (!blockstate.isAir(this.world, pos)) {
      final BlockParticleData data = new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos);
      final double radius = this.getWidth();
      final double motion = 4.0D;
      for(int c = 0; c < count; c++) {
        this.world.addParticle(data, 
           this.getPosX() + (this.rand.nextDouble() - 0.5D) * radius * 2, 
           this.getPosY() + 0.1D, 
           this.getPosZ() + (this.rand.nextDouble() - 0.5D) * radius * 2, 
           motion * (this.rand.nextDouble() - 0.5D), 0.5D, (this.rand.nextDouble() - 0.5D) * motion);
      }
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  private void addSmashParticlesAt(final Entity e) {
    final double x = e.getPosX() + 0.5D;
    final double y = e.getPosY() + 0.1D;
    final double z = e.getPosZ() + 0.5D;
    final double motion = 0.08D;
    final double radius = e.getWidth();
    for (int i = 0; i < 25; i++) {
      world.addParticle(ParticleTypes.CRIT, 
          x + (world.rand.nextDouble() - 0.5D) * radius, 
          y, 
          z + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          0.5D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public float getSmashTime(final float partialTick) { return smashTime + (partialTick < 1.0F ? partialTick : 0); }
  
  @OnlyIn(Dist.CLIENT)
  public float getSmashPercent(final float partialTick) { return smashTime > 0 ? getSmashTime(partialTick) / (float)MAX_SMASH_TIME : 0; }
  
  @OnlyIn(Dist.CLIENT)
  public float getSpawnTime(final float ageInTicks) { return (float) (spawnTime + (ageInTicks < 1.0F ? ageInTicks : 0)); }
  
  @OnlyIn(Dist.CLIENT)
  public float getSpawnPercent(final float ageInTicks) { return spawnTime > 0 ? 1.0F - (getSpawnTime(ageInTicks) / (float)MAX_SPAWN_TIME) : 1.0F; }
  
  @OnlyIn(Dist.CLIENT)
  public float getSummonTime(final float partialTick) { return summonTime + (partialTick < 1.0F ? partialTick : 0); }
  
  @OnlyIn(Dist.CLIENT)
  public float getSummonPercent(final float partialTick) { return summonTime > 0 ? getSummonTime(partialTick) / (float)MAX_SUMMON_TIME : 0; }
  
  /**
   * @param entity the entity to check
   * @return whether the given entity should not be affected by smash attack
   **/
  private boolean isExemptFromSmashAttack(final Entity entity) {
    return !entity.isNonBoss() || entity.hasNoGravity() || entity.getType() == GFRegistry.GIGANTE_ENTITY 
        || entity.getType() == GFRegistry.MAD_COW_ENTITY || entity.isSpectator()
        || (entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative());
  }
  
  /**
   * Applies a smash attack to the given entity
   * @param entity the target entity
   **/
  private void useSmashAttack(final Entity entity) {
    // if entitiy is touching the ground, knock it into the air and apply stun
    if(entity.isOnGround() && !isExemptFromSmashAttack(entity)) {
      entity.addVelocity(0.0D, 0.65D, 0.0D);
      entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
      // stun effect (for living entities)
      if(entity instanceof LivingEntity) {
        final LivingEntity target = (LivingEntity)entity;
        if(GreekFantasy.CONFIG.isStunningNerf()) {
          target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, STUN_DURATION, 0));
          target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, STUN_DURATION, 0));
        } else {
          target.addPotionEffect(new EffectInstance(GFRegistry.STUNNED_EFFECT, STUN_DURATION, 0));
        }
      }
    }
  }
  
  /**
   * Breaks blocks within this entity's bounding box
   * @param offset the forward distance to offset the bounding box
   **/
  private void destroyIntersectingBlocks(final double offset) {
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
          if((s.isSolid() || s.getMaterial().blocksMovement()) && !s.isIn(BlockTags.WITHER_IMMUNE)) {
            this.getEntityWorld().destroyBlock(p, true);
          }
        }
      }
    }
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
    public boolean shouldExecute() { return GeryonEntity.this.isSpawning(); }

    @Override
    public void tick() { GeryonEntity.this.getNavigator().clearPath(); }
  }
  
  class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

    public MeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
      super(creature, speedIn, useLongMemory);
    }
    
    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      if(GeryonEntity.this.hasNoCooldown()) {
        super.checkAndPerformAttack(enemy, distToEnemySqr);
      }
    }
    
    @Override
    protected void func_234039_g_() {
      super.func_234039_g_();
      GeryonEntity.this.setAttackCooldown();
    }
  }
  
  class SmashAttackGoal extends Goal {
    
    private final double range;
    private final int maxCooldown;
    private int cooldown = 90;
    private boolean isBlockSmash;
    
    public SmashAttackGoal(final double rangeIn, final int maxCooldownIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      range = rangeIn;
      maxCooldown = maxCooldownIn;
    }
    
    @Override
    public void startExecuting() {
      GeryonEntity.this.setSmashAttack(true);
      GeryonEntity.this.getNavigator().getPathToEntity(GeryonEntity.this.getAttackTarget(), 0);
      isBlockSmash = GeryonEntity.this.getNavigator().noPath();
          //.getPathToEntity(GeryonEntity.this.getAttackTarget(), 0) == null;
    }
    
    @Override
    public boolean shouldExecute() {
      if(this.cooldown > 0) {
        cooldown--;
        return false;
      }
      return GeryonEntity.this.getAttackTarget() != null && GeryonEntity.this.isNoneState()
          && GeryonEntity.this.hasNoCooldown()
          && GeryonEntity.this.getDistanceSq(GeryonEntity.this.getAttackTarget()) < (range * range);      
    }
    
    @Override
    public void tick() {
      GeryonEntity.this.getNavigator().clearPath();
      GeryonEntity.this.getLookController().setLookPositionWithEntity(GeryonEntity.this.getAttackTarget(), GeryonEntity.this.getHorizontalFaceSpeed(), GeryonEntity.this.getVerticalFaceSpeed());
      if(GeryonEntity.this.smashTime >= GeryonEntity.MAX_SMASH_TIME) {
        // notify client (spawns particles around entities)
        GeryonEntity.this.getEntityWorld().setEntityState(GeryonEntity.this, GeryonEntity.SMASH_CLIENT);
        // get a list of nearby entities and use smash attack on each one
        GeryonEntity.this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(GeryonEntity.this, GeryonEntity.this.getBoundingBox().grow(range, range / 2, range))
          .forEach(e -> GeryonEntity.this.useSmashAttack(e));
        GeryonEntity.this.setAttackCooldown();
        // destroy nearby blocks
        if(isBlockSmash) {
          GeryonEntity.this.destroyIntersectingBlocks(2.5D);
        }
        // finish task
        this.resetTask();
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() { 
      return GeryonEntity.this.isSmashAttack() && GeryonEntity.this.getAttackTarget() != null 
              && GeryonEntity.this.getDistanceSq(GeryonEntity.this.getAttackTarget()) < (range * range); 
    }
    
    @Override
    public void resetTask() {
      GeryonEntity.this.setSmashAttack(false);
      GeryonEntity.this.smashTime = 0;
      isBlockSmash = false;
      cooldown = maxCooldown;
    }
  }
  
  class SummonCowGoal extends SummonMobGoal<MadCowEntity> {
   
    public SummonCowGoal(final int summonProgressIn, final int summonCooldownIn) {
      super(GeryonEntity.this, summonProgressIn, summonCooldownIn, GFRegistry.MAD_COW_ENTITY);
    }
    
    @Override
    public boolean shouldExecute() {
      return super.shouldExecute() && GeryonEntity.this.hasNoCooldown() && GeryonEntity.this.isNoneState();
    }
    
    @Override
    public void startExecuting() {
      super.startExecuting();
      GeryonEntity.this.setSummoning(true);
    }
    
    @Override
    protected void summonMob(final MadCowEntity mobEntity) {
      super.summonMob(mobEntity);
      GeryonEntity.this.getEntityWorld().setEntityState(GeryonEntity.this, SUMMON_COW_CLIENT);
    }
    
    @Override
    public void resetTask() {
      super.resetTask();
      GeryonEntity.this.setAttackCooldown();
      GeryonEntity.this.setSummoning(false);
    }
  }
  
}
