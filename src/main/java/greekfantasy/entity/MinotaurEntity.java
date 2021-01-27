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
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(MinotaurEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "MinotaurState";
  //bytes to use in STATE
  private static final byte NONE = (byte) 0;
  private static final byte CHARGING = (byte) 1;
  private static final byte STUNNED = (byte) 2;
  
  private static final int STUN_DURATION = 80;
  
  private final AttributeModifier knockbackModifier = new AttributeModifier("Charge knockback bonus", 2.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
  private final AttributeModifier attackModifier = new AttributeModifier("Charge attack bonus", 2.5F, AttributeModifier.Operation.MULTIPLY_TOTAL);

  public MinotaurEntity(final EntityType<? extends MinotaurEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.24D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.5D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.25D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(NONE));
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
    if(GreekFantasy.CONFIG.MINOTAUR_ATTACK.get()) {
      this.goalSelector.addGoal(2, new ChargeAttackGoal(1.68D));
    }
  }
 
  @Override
  public void livingTick() {
    super.livingTick();
    
    // spawn particles
    if (world.isRemote() && this.isStunned()) {
      spawnStunnedParticles();
    }
  }

  // Sound methods
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_COW_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_COW_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_COW_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) {
    this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
  }
  
  @Override
  public boolean attackEntityFrom(final DamageSource source, final float amount) {
    if(this.isServerWorld()) {
      this.setCharging(false);
      this.setStunned(false);
    }
    return super.attackEntityFrom(source, amount);
  }
 
  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     compound.putByte(KEY_STATE, this.getMinotaurState());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.setMinotaurState(compound.getByte(KEY_STATE));
  }
  
  // States
  
  public byte getMinotaurState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setMinotaurState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
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
      world.addParticle(ParticleTypes.INSTANT_EFFECT, 
          this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
          this.getPosYEye() + (world.rand.nextDouble() - 0.5D) * radius * 0.75D, 
          this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
          (world.rand.nextDouble() - 0.5D) * motion);
    }
  }
  
  public void applyChargeAttack(final LivingEntity target) {
    // temporarily increase knockback attack
    this.getAttribute(Attributes.ATTACK_DAMAGE).applyNonPersistentModifier(attackModifier);
    this.getAttribute(Attributes.ATTACK_KNOCKBACK).applyNonPersistentModifier(this.knockbackModifier);
    this.attackEntityAsMob(target);
    this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(attackModifier);
    this.getAttribute(Attributes.ATTACK_KNOCKBACK).removeModifier(this.knockbackModifier);
    // apply stunned effect
    if(GreekFantasy.CONFIG.isStunningNerf()) {
      target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, STUN_DURATION, 0));
      target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, STUN_DURATION, 0));
    } else {
      target.addPotionEffect(new EffectInstance(GFRegistry.STUNNED_EFFECT, STUN_DURATION, 0));
    }
  }
  
  static class StunnedGoal extends Goal {
    
    private final MinotaurEntity entity;
    private final int MAX_STUN_TIME = 50;
    private int stunTime;
    
    protected StunnedGoal(final MinotaurEntity entityIn) {
      this.setMutexFlags(EnumSet.allOf(Goal.Flag.class));
      this.entity = entityIn;
    }

    @Override
    public boolean shouldExecute() {   
      return entity.isStunned();
    }
   
    @Override
    public void startExecuting() {
      this.stunTime = 1;
    }
    
    @Override
    public void tick() {
      if(stunTime > 0 && stunTime < MAX_STUN_TIME) {
        stunTime++;
        this.entity.getNavigator().clearPath();
        this.entity.getLookController().setLookPositionWithEntity(this.entity, 0, 0);
      } else {
        resetTask();
      } 
    }
    
    @Override
    public void resetTask() {
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
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      speed = speedIn;
      targetPos = null;
    }

    @Override
    public boolean shouldExecute() {
      // only execute if cooldown reaches zero and:
      // valid attack targetPos
      // move helper is not updating
      // passes random check
      // attack targetPos is within direct sight
      // attack targetPos is more than 3 blocks away
      if(this.cooldown > 0) {
        cooldown--;
      } else if (MinotaurEntity.this.isNoneState() && MinotaurEntity.this.getAttackTarget() != null 
          && !MinotaurEntity.this.getMoveHelper().isUpdating() && hasDirectPath(MinotaurEntity.this.getAttackTarget())
          && MinotaurEntity.this.getDistanceSq(MinotaurEntity.this.getAttackTarget()) > (minRange * minRange)) {
        return true;
      }
      return false;
    }
    
    @Override
    public boolean shouldContinueExecuting() { 
      return (MinotaurEntity.this.isCharging() && MinotaurEntity.this.getAttackTarget() != null 
              && MinotaurEntity.this.getAttackTarget().isAlive()) && hasDirectPath(MinotaurEntity.this.getAttackTarget()); 
    }
    
    @Override
    public void startExecuting() {
      MinotaurEntity.this.setCharging(true);
      this.chargingTimer = 1;
    }

    @Override
    public void tick() {
      LivingEntity target = MinotaurEntity.this.getAttackTarget();
      final double disSqToTargetEntity = MinotaurEntity.this.getDistanceSq(target);
      final boolean hitTarget = disSqToTargetEntity < 1.1D;
      final boolean hasTarget = targetPos != null;
      final boolean finished = hitTarget || (hasTarget && MinotaurEntity.this.getDistanceSq(targetPos) < 0.9D);
      final boolean isCharging = chargingTimer > 0 && chargingTimer++ < maxCharging;
      if(finished) {
        // if charge attack hit the player
        if(hitTarget) {
          MinotaurEntity.this.applyChargeAttack(target);
        } else {
          MinotaurEntity.this.setStunned(true);
        }
        // reset values
        resetTask();
      } else if(isCharging) {
        // prevent the entity from moving while preparing to charge attack
        MinotaurEntity.this.getNavigator().clearPath();
        MinotaurEntity.this.getLookController().setLookPosition(target.getEyePosition(1.0F));
      } else if(hasTarget) {
        // continue moving toward the target that was set earlier
        MinotaurEntity.this.getMoveHelper().setMoveTo(targetPos.x, targetPos.y, targetPos.z, speed);
        MinotaurEntity.this.getLookController().setLookPosition(targetPos.add(0, target.getEyeHeight(), 0));
      } else {
        // determine where the charge attack should target
        this.targetPos = getExtendedTarget(target, disSqToTargetEntity + 16.0D);
      }
    }
    
    @Override
    public void resetTask() { 
      if(MinotaurEntity.this.isCharging()) {
        MinotaurEntity.this.setCharging(false);
      }
      this.chargingTimer = 0;
      this.cooldown = maxCooldown;
      this.targetPos = null;
    }
    
    private Vector3d getExtendedTarget(final LivingEntity targetEntity, final double maxDistanceSq) {
      Vector3d start = MinotaurEntity.this.getPositionVec().add(0, 0.1D, 0);
      Vector3d target = targetEntity.getPositionVec().add(0, 0.1D, 0);
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
      return hasDirectPath(target.getPositionVec().add(0, 0.1D, 0));
    }
    
    /** @return whether there is an unobstructed straight path from the entity to the target position **/
    private boolean hasDirectPath(final Vector3d target) {
      Vector3d start = MinotaurEntity.this.getPositionVec().add(0, 0.1D, 0);
      return MinotaurEntity.this.world.rayTraceBlocks(new RayTraceContext(start, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, MinotaurEntity.this)).getType() == RayTraceResult.Type.MISS;
    }
  }  
}
