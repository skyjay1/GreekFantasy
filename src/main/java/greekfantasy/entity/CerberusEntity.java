package greekfantasy.entity;

import java.util.EnumSet;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.ShootFireGoal;
import greekfantasy.entity.ai.SummonMobGoal;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CerberusEntity extends CreatureEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(CerberusEntity.class, DataSerializers.BYTE);
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
  private static final byte SUMMON_CLIENT = 10;

  private int spawnTime;
  private int summonTime;
  
  private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

  public CerberusEntity(final EntityType<? extends CerberusEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 190.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.26D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 8.0D);
  }
  
  public static CerberusEntity spawnCerberus(final World world, final Vector3d pos) {
    CerberusEntity entity = GFRegistry.CERBERUS_ENTITY.create(world);
    entity.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
    entity.renderYawOffset = 0.0F;
    entity.setSpawning(true);
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
  public void livingTick() {
    super.livingTick();
    
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
    if(this.isServerWorld() && this.isFiring() && this.getAttackTarget() == null) {
      this.setFiring(false);
    }
  
    // spawn particles
    if (world.isRemote() && this.isFiring()) {
      spawnFireParticles();
    }
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
  }
  
  @Override
  public void applyEntityCollision(Entity entityIn) { 
    if(this.canBePushed() && !this.isSpawning()) {
      super.applyEntityCollision(entityIn);
    }
  }
  
  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_WOLF_GROWL;
  }
  
  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_WOLF_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_WOLF_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.2F; }
  
  @Override
  protected float getSoundPitch() { return 0.4F + rand.nextFloat() * 0.2F; }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) { this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 0.6F); }
 
  // Boss logic

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
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     compound.putByte(KEY_STATE, this.getCerberusState());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.setCerberusState(compound.getByte(KEY_STATE));
  }
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if(key == STATE) {
      this.spawnTime = isSpawning() ? MAX_SPAWN_TIME : 0;
      this.summonTime = isSummoning() ? 1 : 0;
    }
  }
  
  // State logic
  
  public byte getCerberusState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setCerberusState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getCerberusState() == NONE; }
  
  public boolean isFiring() { return getCerberusState() == FIRING; }
  
  public void setFiring(final boolean firing) { setCerberusState(firing ? FIRING : NONE); }
 
  public boolean isSummoning() { return getCerberusState() == SUMMONING; }
  
  public void setSummoning(final boolean summoning) { setCerberusState(summoning ? SUMMONING : NONE); }
  
  public boolean isSpawning() { return spawnTime > 0 || getCerberusState() == SPAWNING; }
  
  public void setSpawning(final boolean spawning) {
    spawnTime = spawning ? MAX_SPAWN_TIME : 0;
    setCerberusState(spawning ? SPAWNING : NONE); 
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
  
  private void addSpawningParticles(final IParticleData particle, final int count) {
    if(!this.world.isRemote()) {
      return;
    }
    final double x = this.getPosX();
    final double y = this.getPosY() + 0.1D;
    final double z = this.getPosZ();
    final double motion = 0.08D;
    final double radius = this.getWidth();
    for (int i = 0; i < count; i++) {
      world.addParticle(particle, 
          x + (world.rand.nextDouble() - 0.5D) * radius, 
          y + (world.rand.nextDouble() - 0.5D) * radius, 
          z + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          0.15D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    if(id == SUMMON_CLIENT) {
      this.getEntityWorld().playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WOLF_HOWL, this.getSoundCategory(), 1.1F, 0.9F + this.getRNG().nextFloat() * 0.2F, false);
    } else {
      super.handleStatusUpdate(id);
    }
  }
  
  // Custom goals
  
  class SpawningGoal extends Goal {

    public SpawningGoal() { setMutexFlags(EnumSet.allOf(Goal.Flag.class)); }

    @Override
    public boolean shouldExecute() { return CerberusEntity.this.isSpawning(); }

    @Override
    public void tick() { CerberusEntity.this.getNavigator().clearPath(); }
  }
  
  class FireAttackGoal extends ShootFireGoal {

    protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn) {
      super(CerberusEntity.this, fireTimeIn, maxCooldownIn, FIRE_RANGE);
    }

    @Override
    public boolean shouldExecute() {  
      return super.shouldExecute() && CerberusEntity.this.isNoneState();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting() && CerberusEntity.this.isFiring();
    }
   
    @Override
    public void startExecuting() {
      super.startExecuting();
      CerberusEntity.this.setFiring(true);
    }
   
    @Override
    public void resetTask() {
      super.resetTask();
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
    public boolean shouldExecute() { return super.shouldExecute() && CerberusEntity.this.isNoneState(); }
    
    @Override
    public void startExecuting() { 
      super.startExecuting();
      CerberusEntity.this.setSummoning(true);
    }
    
    @Override
    public void resetTask() {
      super.resetTask();
      CerberusEntity.this.setSummoning(false);
    }
    
    @Override
    public void tick() {
      super.tick();
      if(this.progress == 8) {
        CerberusEntity.this.getEntityWorld().setEntityState(CerberusEntity.this, SUMMON_CLIENT);
      }
    }
    
    @Override
    protected void summonMob(final OrthusEntity mobEntity) {
      mobEntity.setLimitedLife(lifespan);
      super.summonMob(mobEntity);
    }
  }
}
