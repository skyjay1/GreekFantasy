package greekfantasy.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.ShootFireGoal;
import greekfantasy.entity.ai.SummonMobGoal;
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
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CerberusEntity extends CreatureEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.defineId(CerberusEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "CerberusState";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  private static final byte FIRING = (byte)2;
  private static final byte SUMMONING = (byte)4;
  
  private static final double FIRE_RANGE = 6.0D;
  private static final int MAX_SPAWN_TIME = 90;  
  private static final int MAX_FIRING_TIME = 66;
  private static final int MAX_SUMMON_TIME = 35;

  // bytes to use in World#setEntityState
  private static final byte SPAWN_CLIENT = 9;
  private static final byte SUMMON_CLIENT = 10;

  private int spawnTime;
  private int summonTime;
  
  private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

  public CerberusEntity(final EntityType<? extends CerberusEntity> type, final World worldIn) {
    super(type, worldIn);
    this.maxUpStep = 1.0F;
    this.xpReward = 50;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 190.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.26D)
        .add(Attributes.FOLLOW_RANGE, 48.0D)
        .add(Attributes.ATTACK_DAMAGE, 8.0D);
  }
  
  public static CerberusEntity spawnCerberus(final World world, final Vector3d pos) {
    CerberusEntity entity = GFRegistry.CERBERUS_ENTITY.create(world);
    entity.moveTo(pos.x(), pos.y(), pos.z(), 0.0F, 0.0F);
    entity.yBodyRot = 0.0F;
    world.addFreshEntity(entity);
    entity.setSpawning(true);
    // trigger spawn for nearby players
    for (ServerPlayerEntity player : world.getEntitiesOfClass(ServerPlayerEntity.class, entity.getBoundingBox().inflate(25.0D))) {
      CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
    }
    // play sound
    world.playLocalSound(pos.x(), pos.y(), pos.z(), SoundEvents.WITHER_SPAWN, entity.getSoundSource(), 1.2F, 1.0F, false);
    return entity;
  }
  
  @Override
  public void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(STATE, Byte.valueOf(NONE));
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new CerberusEntity.SpawningGoal());
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new CerberusEntity.FireAttackGoal(MAX_FIRING_TIME, 120));
    this.goalSelector.addGoal(2, new CerberusEntity.SummonOrthusGoal(MAX_SUMMON_TIME, 310, 300));
    this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.6F));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // update spawning
    if(isSpawning()) {
      // update timer
      if(--spawnTime <= 0) {
        setSpawning(false);
      }
      // spawn particles
      addSpawningParticles(ParticleTypes.FLAME, 4);
      addSpawningParticles(ParticleTypes.SMOKE, 1);
      addSpawningParticles(ParticleTypes.LARGE_SMOKE, 1);
    }
    
    // update summoning
    if(this.isSummoning()) {
      summonTime++;
    } else if(summonTime > 0) {
      summonTime = 0;
    }
    
    // update firing
    if(this.isEffectiveAi() && this.isFiring() && this.getTarget() == null) {
      this.setFiring(false);
    }
  
    // spawn particles
    if (level.isClientSide() && this.isFiring()) {
      spawnFireParticles();
    }
  }
  
  @Override
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    setSpawning(true);
    return data;
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
  }
  
  @Override
  public void push(Entity entityIn) { 
    if(this.isPushable() && !this.isSpawning()) {
      super.push(entityIn);
    }
  }
  
  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.WOLF_GROWL;
  }
  
  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.WOLF_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.WOLF_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.2F; }
  
  @Override
  protected float getVoicePitch() { return 0.4F + random.nextFloat() * 0.2F; }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) { this.playSound(SoundEvents.WOLF_STEP, 0.15F, 0.6F); }
 
  // Boss logic

  @Override
  public void startSeenByPlayer(ServerPlayerEntity player) {
    super.startSeenByPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void stopSeenByPlayer(ServerPlayerEntity player) {
    super.stopSeenByPlayer(player);
    this.bossInfo.removePlayer(player);
  }
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
     super.addAdditionalSaveData(compound);
     compound.putByte(KEY_STATE, this.getCerberusState());
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
     super.readAdditionalSaveData(compound);
     this.setCerberusState(compound.getByte(KEY_STATE));
  }
  
  // State logic
  
  public byte getCerberusState() { return this.getEntityData().get(STATE).byteValue(); }
  
  public void setCerberusState(final byte state) { this.getEntityData().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getCerberusState() == NONE; }
  
  public boolean isFiring() { return getCerberusState() == FIRING; }
  
  public void setFiring(final boolean firing) { setCerberusState(firing ? FIRING : NONE); }
 
  public boolean isSummoning() { return getCerberusState() == SUMMONING; }
  
  public void setSummoning(final boolean summoning) { 
    setCerberusState(summoning ? SUMMONING : NONE);
    this.summonTime = summoning ? 1 : 0;
    if(summoning && !level.isClientSide()) {
      level.broadcastEntityEvent(this, SUMMON_CLIENT);
    }
  }
  
  public boolean isSpawning() { return spawnTime > 0 || getCerberusState() == SPAWNING; }
  
  public void setSpawning(final boolean spawning) {
    spawnTime = spawning ? MAX_SPAWN_TIME : 0;
    setCerberusState(spawning ? SPAWNING : NONE);
    if(spawning && !level.isClientSide()) {
      level.broadcastEntityEvent(this, SPAWN_CLIENT);
    }
  }
  
  // Animation methods
  
  @OnlyIn(Dist.CLIENT)
  public float getSpawnTime(final float ageInTicks) { return (float) (spawnTime + (ageInTicks < 1.0F ? ageInTicks : 0)); }
  
  @OnlyIn(Dist.CLIENT)
  public float getSpawnPercent(final float ageInTicks) { return 1.0F - ((float)spawnTime / (float)MAX_SPAWN_TIME); }
  
  @OnlyIn(Dist.CLIENT)
  public float getSummonTime(final float partialTick) { return summonTime + (partialTick < 1.0F ? partialTick : 0); }
  
  @OnlyIn(Dist.CLIENT)
  public float getSummonPercent(final float partialTick) { return summonTime > 0 ? getSummonTime(partialTick) / (float)MAX_SUMMON_TIME : 0; }
  
  // Fire-breathing particles
  
  public void spawnFireParticles() {
    if(!level.isClientSide()) {
      return;
    }
    Vector3d lookVec = this.getLookAngle();
    Vector3d pos = this.getEyePosition(1.0F);
    final double motion = 0.06D;
    final double radius = 0.75D;
    
    for (int i = 0; i < 5; i++) {
      level.addParticle(ParticleTypes.FLAME, 
          pos.x + (level.random.nextDouble() - 0.5D) * radius, 
          pos.y + (level.random.nextDouble() - 0.5D) * radius, 
          pos.z + (level.random.nextDouble() - 0.5D) * radius,
          lookVec.x * motion * FIRE_RANGE, 
          lookVec.y * motion * 0.5D,
          lookVec.z * motion * FIRE_RANGE);
    }
  }
  
  private void addSpawningParticles(final IParticleData particle, final int count) {
    if(!this.level.isClientSide()) {
      return;
    }
    final double x = this.getX();
    final double y = this.getY() + 0.1D;
    final double z = this.getZ();
    final double motion = 0.08D;
    final double radius = this.getBbWidth();
    for (int i = 0; i < count; i++) {
      level.addParticle(particle, 
          x + (level.random.nextDouble() - 0.5D) * radius, 
          y + (level.random.nextDouble() - 0.5D) * radius, 
          z + (level.random.nextDouble() - 0.5D) * radius,
          (level.random.nextDouble() - 0.5D) * motion, 
          0.15D,
          (level.random.nextDouble() - 0.5D) * motion);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleEntityEvent(byte id) {
    if(id == SPAWN_CLIENT) {
      setSpawning(true);
    } else if(id == SUMMON_CLIENT) {
      this.getCommandSenderWorld().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WOLF_HOWL, this.getSoundSource(), 1.1F, 0.9F + this.getRandom().nextFloat() * 0.2F, false);
    } else {
      super.handleEntityEvent(id);
    }
  }
  
  // Custom goals
  
  class SpawningGoal extends Goal {

    public SpawningGoal() { setFlags(EnumSet.allOf(Goal.Flag.class)); }

    @Override
    public boolean canUse() { return CerberusEntity.this.isSpawning(); }

    @Override
    public void tick() { CerberusEntity.this.getNavigation().stop(); }
  }
  
  class FireAttackGoal extends ShootFireGoal {

    protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn) {
      super(CerberusEntity.this, fireTimeIn, maxCooldownIn, FIRE_RANGE);
    }

    @Override
    public boolean canUse() {  
      return super.canUse() && CerberusEntity.this.isNoneState();
    }
    
    @Override
    public boolean canContinueToUse() {
      return super.canContinueToUse() && CerberusEntity.this.isFiring();
    }
   
    @Override
    public void start() {
      super.start();
      CerberusEntity.this.setFiring(true);
    }
   
    @Override
    public void stop() {
      super.stop();
      CerberusEntity.this.setFiring(false);
    }
  }
  
  class SummonOrthusGoal extends SummonMobGoal<OrthusEntity> {
    
    private final int lifespan;

    public SummonOrthusGoal(int summonProgressIn, int summonCooldownIn, int lifespanIn) {
      super(CerberusEntity.this, summonProgressIn, summonCooldownIn, GFRegistry.ORTHUS_ENTITY);
      lifespan = lifespanIn;
    }
    
    @Override
    public boolean canUse() { return super.canUse() && CerberusEntity.this.isNoneState(); }
    
    @Override
    public void start() { 
      super.start();
      CerberusEntity.this.setSummoning(true);
    }
    
    @Override
    public void stop() {
      super.stop();
      CerberusEntity.this.setSummoning(false);
    }
    
    @Override
    public void tick() {
      super.tick();
      if(this.progress == 8) {
        CerberusEntity.this.getCommandSenderWorld().broadcastEntityEvent(CerberusEntity.this, SUMMON_CLIENT);
      }
    }
    
    @Override
    protected void summonMob(final OrthusEntity mobEntity) {
      mobEntity.setLimitedLife(lifespan);
      super.summonMob(mobEntity);
    }
  }
}
