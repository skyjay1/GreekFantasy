package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.ShootFireGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class OrthusEntity extends MonsterEntity {
  
  protected static final DataParameter<Boolean> FIRE = EntityDataManager.createKey(OrthusEntity.class, DataSerializers.BOOLEAN);
  protected static final String KEY_FIRE = "Firing";
  protected static final String KEY_LIFE_TICKS = "LifeTicks";
  
  protected static final double FIRE_RANGE = 4.5D;
  protected static final int MAX_FIRE_TIME = 52;
  protected static final int FIRE_COOLDOWN = 165;

  /** The number of ticks until the entity starts taking damage **/
  protected boolean limitedLifespan;
  protected int limitedLifeTicks;
  
  public OrthusEntity(final EntityType<? extends OrthusEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.29D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(FIRE, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp());
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    if(GreekFantasy.CONFIG.ORTHUS_ATTACK.get()) {
      this.goalSelector.addGoal(2, new OrthusEntity.FireAttackGoal(MAX_FIRE_TIME, FIRE_COOLDOWN, FIRE_RANGE));
    }
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 40;
      attackEntityFrom(DamageSource.STARVE, 1.0F);
    }
    
    // update fire attack
    if(this.isServerWorld() && this.isFireAttack() && this.getAttackTarget() == null) {
      this.setFireAttack(false);
    }
  
    // spawn particles
    if (world.isRemote() && this.isFireAttack()) {
      spawnFireParticles();
    }
  }
  
  public void setLimitedLife(int life) {
    this.limitedLifespan = true;
    this.limitedLifeTicks = life;
  }

  @Override
  protected SoundEvent getAmbientSound() {
    if (this.rand.nextInt(3) == 0) {
      return SoundEvents.ENTITY_WOLF_AMBIENT;
    } else if (this.rand.nextInt(3) == 0) {
      return SoundEvents.ENTITY_WOLF_PANT;
    } else {
      return SoundEvents.ENTITY_WOLF_GROWL;
    }
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_WOLF_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_WOLF_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) { this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F); }
  
  @Override
  public ResourceLocation getLootTable() {
    return limitedLifespan ? LootTables.EMPTY : super.getLootTable();
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putBoolean(KEY_FIRE, isFireAttack());
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setFireAttack(compound.getBoolean(KEY_FIRE));
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
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
  
  public void setFireAttack(final boolean shooting) { this.dataManager.set(FIRE, shooting); }
  
  public boolean isFireAttack() { return this.getDataManager().get(FIRE).booleanValue(); }
  
  class FireAttackGoal extends ShootFireGoal {

    protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn, final double fireRange) {
      super(OrthusEntity.this, fireTimeIn, maxCooldownIn, fireRange);
    }

    @Override
    public boolean shouldExecute() {  
      return super.shouldExecute() && !OrthusEntity.this.isFireAttack();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting() && OrthusEntity.this.isFireAttack();
    }
   
    @Override
    public void startExecuting() {
      super.startExecuting();
      OrthusEntity.this.setFireAttack(true);
    }
    
    @Override
    public void tick() {
      super.tick();
      OrthusEntity.this.setJumping(false);
    }
   
    @Override
    public void resetTask() {
      super.resetTask();
      OrthusEntity.this.setFireAttack(false);
    }
  }
}
