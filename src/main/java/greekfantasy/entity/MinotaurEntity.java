package greekfantasy.entity;

import java.util.EnumSet;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MinotaurEntity extends MonsterEntity {
  protected static final DataParameter<Byte> STATE = EntityDataManager.defineId(MinotaurEntity.class, DataSerializers.BYTE);
  protected static final String KEY_STATE = "MinotaurState";
  //bytes to use in STATE
  protected static final byte NONE = (byte) 0;
  protected static final byte CHARGING = (byte) 1;
  protected static final byte STUNNED = (byte) 2;
  
  protected static final int STUN_DURATION = 80;
  
  protected final AttributeModifier knockbackModifier = new AttributeModifier("Charge knockback bonus", 2.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
  protected final AttributeModifier attackModifier = new AttributeModifier("Charge attack bonus", 2.5F, AttributeModifier.Operation.MULTIPLY_TOTAL);

  public MinotaurEntity(final EntityType<? extends MinotaurEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 24.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.24D)
        .add(Attributes.ATTACK_DAMAGE, 3.5D)
        .add(Attributes.ATTACK_KNOCKBACK, 1.25D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.97D)
        .add(Attributes.ARMOR, 2.0D);
  }
  
  @Override
  public void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(STATE, Byte.valueOf(NONE));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new StunnedGoal(this));
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    registerChargeGoal();
  }
  
  protected void registerChargeGoal() {
    if(GreekFantasy.CONFIG.MINOTAUR_ATTACK.get()) {
      this.goalSelector.addGoal(2, new ChargeAttackGoal(1.68D));
    }
  }
 
  @Override
  public void aiStep() {
    super.aiStep();
    
    // spawn particles
    if (level.isClientSide() && this.isStunned()) {
      spawnStunnedParticles();
    }
  }

  // Sound methods
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.COW_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.COW_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.COW_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) {
    this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
  }
  
  @Override
  public boolean hurt(final DamageSource source, final float amount) {
    if(this.isEffectiveAi()) {
      this.setCharging(false);
      this.setStunned(false);
    }
    return super.hurt(source, amount);
  }
 
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
     super.addAdditionalSaveData(compound);
     compound.putByte(KEY_STATE, this.getMinotaurState());
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
     super.readAdditionalSaveData(compound);
     this.setMinotaurState(compound.getByte(KEY_STATE));
  }
  
  // States
  
  public byte getMinotaurState() { return this.getEntityData().get(STATE).byteValue(); }
  
  public void setMinotaurState(final byte state) { this.getEntityData().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getMinotaurState() == NONE; }
  
  public boolean isCharging() { return getMinotaurState() == CHARGING; }
  
  public boolean isStunned() { return getMinotaurState() == STUNNED; }
  
  public void setCharging(final boolean charging) { setMinotaurState(charging ? CHARGING : NONE); }
  
  public void setStunned(final boolean stunned) { setMinotaurState(stunned ? STUNNED : NONE); }

  public float getStompingSpeed() { return 0.58F; }
  
  public void spawnStunnedParticles() {
    final double motion = 0.09D;
    final double radius = 0.7D;
    for (int i = 0; i < 2; i++) {
      level.addParticle(ParticleTypes.INSTANT_EFFECT, 
          this.getX() + (level.random.nextDouble() - 0.5D) * radius, 
          this.getEyeY() + (level.random.nextDouble() - 0.5D) * radius * 0.75D, 
          this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
          (level.random.nextDouble() - 0.5D) * motion, 
          (level.random.nextDouble() - 0.5D) * motion * 0.5D,
          (level.random.nextDouble() - 0.5D) * motion);
    }
  }
  
  public void applyChargeAttack(final LivingEntity target) {
    // temporarily increase knockback attack
    this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(attackModifier);
    this.getAttribute(Attributes.ATTACK_KNOCKBACK).addTransientModifier(this.knockbackModifier);
    this.doHurtTarget(target);
    this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(attackModifier);
    this.getAttribute(Attributes.ATTACK_KNOCKBACK).removeModifier(this.knockbackModifier);
    // apply stunned effect
    if(GreekFantasy.CONFIG.isStunningNerf()) {
      target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, STUN_DURATION, 0));
      target.addEffect(new EffectInstance(Effects.WEAKNESS, STUN_DURATION, 0));
    } else {
      target.addEffect(new EffectInstance(GFRegistry.STUNNED_EFFECT, STUN_DURATION, 0));
    }
  }
  
  static class StunnedGoal extends Goal {
    
    private final MinotaurEntity entity;
    private final int MAX_STUN_TIME = 50;
    private int stunTime;
    
    protected StunnedGoal(final MinotaurEntity entityIn) {
      this.setFlags(EnumSet.allOf(Goal.Flag.class));
      this.entity = entityIn;
    }

    @Override
    public boolean canUse() {   
      return entity.isStunned();
    }
   
    @Override
    public void start() {
      this.stunTime = 1;
    }
    
    @Override
    public void tick() {
      if(stunTime > 0 && stunTime < MAX_STUN_TIME) {
        stunTime++;
        this.entity.getNavigation().stop();
        this.entity.getLookControl().setLookAt(this.entity, 0, 0);
      } else {
        stop();
      } 
    }
    
    @Override
    public void stop() {
      this.entity.setStunned(false);
      this.stunTime = 0;
    }
  }
  
  class ChargeAttackGoal extends Goal {
    
    private final int maxCooldown = 200;
    private final int maxCharging = 40;
    private final double minRange = 2.5D;
    private final double speed;

    private int chargingTimer;
    private int cooldown = maxCooldown;
    private Vector3d targetPos;
    
    protected ChargeAttackGoal(final double speedIn) {
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      speed = speedIn;
      targetPos = null;
    }

    @Override
    public boolean canUse() {
      // only execute if cooldown reaches zero and:
      // valid attack targetPos
      // move helper is not updating
      // passes random check
      // attack targetPos is within direct sight
      // attack targetPos is more than 3 blocks away
      if(this.cooldown > 0) {
        cooldown--;
      } else if (MinotaurEntity.this.isNoneState() && MinotaurEntity.this.getTarget() != null 
          && !MinotaurEntity.this.getMoveControl().hasWanted() && hasDirectPath(MinotaurEntity.this.getTarget())
          && MinotaurEntity.this.distanceToSqr(MinotaurEntity.this.getTarget()) > (minRange * minRange)) {
        return true;
      }
      return false;
    }
    
    @Override
    public boolean canContinueToUse() { 
      return (MinotaurEntity.this.isCharging() && MinotaurEntity.this.getTarget() != null 
              && MinotaurEntity.this.getTarget().isAlive()) && hasDirectPath(MinotaurEntity.this.getTarget()); 
    }
    
    @Override
    public void start() {
      MinotaurEntity.this.setCharging(true);
      this.chargingTimer = 1;
    }

    @Override
    public void tick() {
      LivingEntity target = MinotaurEntity.this.getTarget();
      final double disSqToTargetEntity = MinotaurEntity.this.distanceToSqr(target);
      final boolean hitTarget = disSqToTargetEntity < 1.1D;
      final boolean hasTarget = targetPos != null;
      final boolean finished = hitTarget || (hasTarget && MinotaurEntity.this.distanceToSqr(targetPos) < 0.9D);
      final boolean isCharging = chargingTimer > 0 && chargingTimer++ < maxCharging;
      if(finished) {
        // if charge attack hit the player
        if(hitTarget) {
          MinotaurEntity.this.applyChargeAttack(target);
        } else {
          MinotaurEntity.this.setStunned(true);
        }
        // reset values
        stop();
      } else if(isCharging) {
        // prevent the entity from moving while preparing to charge attack
        MinotaurEntity.this.getNavigation().stop();
        MinotaurEntity.this.getLookControl().setLookAt(target.getEyePosition(1.0F));
      } else if(hasTarget) {
        // continue moving toward the target that was set earlier
        MinotaurEntity.this.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, speed);
        MinotaurEntity.this.getLookControl().setLookAt(targetPos.add(0, target.getEyeHeight(), 0));
      } else {
        // determine where the charge attack should target
        this.targetPos = getExtendedTarget(target, disSqToTargetEntity + 16.0D);
      }
    }
    
    @Override
    public void stop() { 
      if(MinotaurEntity.this.isCharging()) {
        MinotaurEntity.this.setCharging(false);
      }
      this.chargingTimer = 0;
      this.cooldown = maxCooldown;
      this.targetPos = null;
    }
    
    private Vector3d getExtendedTarget(final LivingEntity targetEntity, final double maxDistanceSq) {
      Vector3d start = MinotaurEntity.this.position().add(0, 0.1D, 0);
      Vector3d target = targetEntity.position().add(0, 0.1D, 0);
      Vector3d end = target;
      Vector3d vecDiff = end.subtract(start);
      double length = vecDiff.length();
      vecDiff = vecDiff.normalize();
      // repeatedly scale the vector
      do {
        target = end;
        end = start.add(vecDiff.scale(++length));
      } while((length * length) < maxDistanceSq && hasDirectPath(end));
      // the vector has either reached max length, or encountered a block
      return target;
    }
    
    /** @return whether there is an unobstructed straight path from the entity to the target entity **/
    private boolean hasDirectPath(final LivingEntity target) {
      return hasDirectPath(target.position().add(0, 0.1D, 0));
    }
    
    /** @return whether there is an unobstructed straight path from the entity to the target position **/
    private boolean hasDirectPath(final Vector3d target) {
      Vector3d start = MinotaurEntity.this.position().add(0, 0.1D, 0);
      return MinotaurEntity.this.level.clip(new RayTraceContext(start, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, MinotaurEntity.this)).getType() == RayTraceResult.Type.MISS;
    }
  }  
}
