package greekfantasy.entity;

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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OrthusEntity extends MonsterEntity {
  
//  private static final byte FIRE_START = 6;
//  private static final byte FIRE_END = 7;
//  
//  private static final double FIRE_RANGE = 5.0D;
//  private boolean isFireBreathing;
  
  protected static final String KEY_LIFE_TICKS = "LifeTicks";

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
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
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
//    if(GreekFantasy.CONFIG.ORTHUS_ATTACK.get()) {
//      this.goalSelector.addGoal(2, new FireAttackGoal(this));
//    }
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 40;
      attackEntityFrom(DamageSource.STARVE, 1.0F);
    }
    
//    if(this.isServerWorld() && this.isFireBreathing() && this.getAttackTarget() == null) {
//      this.setFireBreathing(false);
//    }
//  
//    // spawn particles
//    if (world.isRemote() && this.isFireBreathing()) {
//      spawnFireParticles();
//    }
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
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    return SoundEvents.ENTITY_WOLF_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_WOLF_DEATH;
  }

  @Override
  protected float getSoundVolume() {
    return 0.4F;
  }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) {
    this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
  }
  
//  @OnlyIn(Dist.CLIENT)
//  public void handleStatusUpdate(byte id) {
//    switch(id) {
//    case FIRE_START:
//      this.isFireBreathing = true;
//      break;
//    case FIRE_END:
//      this.isFireBreathing = false;
//      break;
//    default:
//      super.handleStatusUpdate(id);
//      break;
//    }
//  }
//  
//  public void spawnFireParticles() {
//    Vector3d lookVec = this.getLookVec();
//    Vector3d pos = this.getEyePosition(1.0F);
//    final double motion = 0.06D;
//    final double radius = 0.5D;
//    
//    for (int i = 0; i < 5; i++) {
//      world.addParticle(ParticleTypes.FLAME, 
//          pos.x + (world.rand.nextDouble() - 0.5D) * radius, 
//          pos.y + (world.rand.nextDouble() - 0.5D) * radius, 
//          pos.z + (world.rand.nextDouble() - 0.5D) * radius,
//          lookVec.x * motion * FIRE_RANGE, 
//          lookVec.y * motion * 0.5D,
//          lookVec.z * motion * FIRE_RANGE);
//    }
//  }
//  
//  public void setFireBreathing(final boolean fireBreathing) {
//    this.isFireBreathing = fireBreathing;
//    if(this.isServerWorld()) {
//      this.world.setEntityState(this, fireBreathing ? FIRE_START : FIRE_END);    
//    }
//  }
//
//  public boolean isFireBreathing() {
//    return this.isFireBreathing;
//  }
//  
//  static class FireAttackGoal extends Goal {
//    private final OrthusEntity entity;
//    private final int MAX_FIRE_TIME = 90;
//    private final int MAX_COOLDOWN = 160;
//    private int fireBreathingTime;
//    private int cooldown;
//    
//    protected FireAttackGoal(final OrthusEntity entityIn) {
//      this.setMutexFlags(EnumSet.allOf(Goal.Flag.class));
//      this.entity = entityIn;
//    }
//
//    @Override
//    public boolean shouldExecute() {  
//      if(this.cooldown > 0) {
//        cooldown--;
//      } else if (this.entity.getAttackTarget() != null
//          && entity.getDistanceSq(this.entity.getAttackTarget()) < (FIRE_RANGE * FIRE_RANGE)
//          && this.entity.canEntityBeSeen(this.entity.getAttackTarget())) {
//        return true;
//      }
//      return false;
//    }
//    
//    @Override
//    public boolean shouldContinueExecuting() {
//      return this.entity.isFireBreathing() && this.entity.getAttackTarget() != null
//          && this.entity.canEntityBeSeen(this.entity.getAttackTarget())
//          && this.entity.getDistanceSq(this.entity.getAttackTarget()) < (FIRE_RANGE * FIRE_RANGE);
//    }
//   
//    @Override
//    public void startExecuting() {
//      this.fireBreathingTime = 1;
//      this.entity.setFireBreathing(true);
//    }
//    
//    @Override
//    public void tick() {
//      if(fireBreathingTime > 0 && fireBreathingTime < MAX_FIRE_TIME) {
//        fireBreathingTime++;
//        // stop the entity from moving, and adjust look vecs
//        this.entity.getNavigator().clearPath();
//        this.entity.faceEntity(this.entity.getAttackTarget(), 100.0F, 100.0F);
//        this.entity.getLookController().setLookPositionWithEntity(this.entity.getAttackTarget(), 100.0F, 100.0F);
//        // set fire to targetPos
//        if(fireBreathingTime > 10 && fireBreathingTime % 10 == 0) {
//          final Vector3d entityPos = new Vector3d(entity.getPosX(), entity.getPosYEye(), entity.getPosZ());
//          igniteInRange(entityPos, entity.getAttackTarget().getPositionVec(), 0.65D, 5);
//        }
//      } else {
//        resetTask();
//      } 
//    }
//    
//    @Override
//    public void resetTask() {
//      this.entity.setFireBreathing(false);
//      this.fireBreathingTime = 0;
//      this.cooldown = MAX_COOLDOWN;
//    }
//    
//    /**
//     * Ignites all entities along a raytrace given the start and end positions
//     * @param startPos the starting position
//     * @param endPos the ending position
//     * @param radius the radius around each point in the ray to check for entities
//     * @param fireTime the amount of time to set fire to the entity
//     **/
//    private void igniteInRange(final Vector3d startPos, final Vector3d endPos, final double radius, final int fireTime) {    
//      Vector3d vecDifference = endPos.subtract(startPos);
//      // step along the vector created by adding the start position and the difference vector
//      for(double i = 0.1, l = vecDifference.length(), stepSize = radius * 0.75D; i < l; i += stepSize) {
//        Vector3d scaled = startPos.add(vecDifference.scale(i));
//        // make a box at this position along the vector
//        final AxisAlignedBB aabb = new AxisAlignedBB(scaled.x - radius, scaled.y - radius, scaled.z - radius, scaled.x + radius, scaled.y + radius, scaled.z + radius);
//        for(final Entity e : this.entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this.entity, aabb)) {
//          // set fire to any entities inside the box
//          e.setFire(fireTime + this.entity.getRNG().nextInt(5) - 2);
//        }
//      }
//    }
//  }

}
