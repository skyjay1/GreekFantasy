package greekfantasy.entity;

import java.util.EnumSet;

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
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MinotaurEntity extends MonsterEntity implements IHoofedEntity {
  
  private static final byte STOMPING_START = 4;
  private static final byte STOMPING_END = 5;
  private static final byte STUNNED_START = 6;
  private static final byte STUNNED_END = 7;
  
  private boolean isStomping;
  private boolean isStunned;
  
  private final AttributeModifier knockbackModifier = new AttributeModifier("Charge knockback bonus", 2.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
  private final AttributeModifier attackModifier = new AttributeModifier("Charge attack bonus", 1.75F, AttributeModifier.Operation.MULTIPLY_TOTAL);

  public MinotaurEntity(final EntityType<? extends MinotaurEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.27D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.25D);
 }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new StunnedGoal(this));
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new ChargeAttackGoal(this, 1.68D));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
    //this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
 
  @Override
  public void livingTick() {
    super.livingTick();
    
    if(this.isServerWorld() && this.isStomping() && this.getAttackTarget() == null) {
      this.setStomping(false);
    }
    
    // spawn particles
    if (world.isRemote() && this.isStunned()) {
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
  }
  
  @Override
  public boolean attackEntityFrom(final DamageSource source, final float amount) {
    if(this.isServerWorld()) {
      this.setStomping(false);
      this.setStunned(false);
    }
    return super.attackEntityFrom(source, amount);
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case STOMPING_START:
      this.isStomping = true;
      break;
    case STOMPING_END:
      this.isStomping = false;
      break;
    case STUNNED_START:
      this.isStunned = true;
      this.isStomping = false;
      break;
    case STUNNED_END:
      this.isStunned = false;
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  @Override
  public void setStomping(final boolean stomping) {
    this.isStomping = stomping;
    this.world.setEntityState(this, stomping ? STOMPING_START : STOMPING_END);    
  }

  @Override
  public boolean isStomping() {
    return this.isStomping;
  }

  @Override
  public float getStompingSpeed() {
    return 0.74F;
  }
  
  public void setStunned(final boolean stunned) {
    this.isStunned = stunned;
    this.world.setEntityState(this, stunned ? STUNNED_START : STUNNED_END);    
  }

  public boolean isStunned() {
    return this.isStunned;
  }
  
  public void applyChargeAttack(final LivingEntity target) {
    // temporarily increase knockback attack
    this.getAttribute(Attributes.ATTACK_DAMAGE).applyNonPersistentModifier(attackModifier);
    this.getAttribute(Attributes.ATTACK_KNOCKBACK).applyNonPersistentModifier(this.knockbackModifier);
    this.attackEntityAsMob(target);
    this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(attackModifier);
    this.getAttribute(Attributes.ATTACK_KNOCKBACK).removeModifier(this.knockbackModifier);
    // apply potion effects (if we make a "Stunned" effect later, use that instead)
    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 2 * 20, 1, false, false, true));
    target.addPotionEffect(new EffectInstance(Effects.NAUSEA, 3 * 20, 0, false, false, true));
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
  
  static class ChargeAttackGoal extends Goal {
    
    private final MinotaurEntity entity;
    private final double speed;
    private final int MAX_COOLDOWN = 500;
    private final int MAX_STOMPING = 40;
    private int stompingTimer;
    private int cooldown;
    private Vector3d targetPos;
    
    protected ChargeAttackGoal(final MinotaurEntity entityIn, final double speedIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      entity = entityIn;
      speed = speedIn;
      targetPos = null;
    }

    @Override
    public boolean shouldExecute() {
      // only execute if cooldown reaches zero and:
      // valid attack target
      // move helper is not updating
      // passes random check
      // attack target is within direct sight
      // attack target is more than 3 blocks away
      if(this.cooldown > 0) {
        cooldown--;
      } else if (this.entity.getAttackTarget() != null && !this.entity.getMoveHelper().isUpdating() 
          && entity.getRNG().nextInt(7) == 0 && hasDirectPath(this.entity.getAttackTarget())
          && entity.getDistanceSq(this.entity.getAttackTarget()) > 9.0D) {
        return true;
      }
      return false;
    }
    
    @Override
    public boolean shouldContinueExecuting() { 
      return (this.entity.isStomping() && this.entity.getAttackTarget() != null 
              && this.entity.getAttackTarget().isAlive()) && hasDirectPath(this.entity.getAttackTarget()); 
    }
    
    @Override
    public void startExecuting() {
      this.entity.setStomping(true);
      this.stompingTimer = 1;
    }

    @Override
    public void tick() {
      LivingEntity target = this.entity.getAttackTarget();
      final boolean hitTarget = this.entity.getDistanceSq(target) < 1.1D;
      final boolean hasTarget = targetPos != null;
      final boolean finished = hitTarget || (hasTarget && this.entity.getDistanceSq(targetPos) < 0.9D);
      final boolean isStomping = stompingTimer > 0 && stompingTimer++ < MAX_STOMPING;
      if(finished) {
        // reset values
        this.entity.setStomping(false);
        this.stompingTimer = 0;
        this.targetPos = null;
        this.cooldown = MAX_COOLDOWN;
        // if charge attack hit the player
        if(hitTarget) {
          this.entity.applyChargeAttack(target);
        } else {
          this.entity.setStunned(true);
        }
      } else if(isStomping) {
        // prevent the entity from moving
        this.entity.getNavigator().clearPath();
        this.entity.getLookController().setLookPosition(target.getEyePosition(1.0F));
      } else if(hasTarget) {
        this.entity.getMoveHelper().setMoveTo(targetPos.x, targetPos.y, targetPos.z, speed);
        this.entity.getLookController().setLookPosition(targetPos.add(0, target.getEyeHeight(), 0));
      } else {
        // launch the charge attack
        // TODO get a vector from entity to target and extend it
        // to allow minotaur to run past the player position
        this.targetPos = target.getPositionVec();
      }
    }
    
    @Override
    public void resetTask() { 
      this.entity.setStomping(false);
      this.stompingTimer = 0;
      this.cooldown = 0;
      this.targetPos = null;
    }
    
    private boolean hasDirectPath(final LivingEntity target) {
      Vector3d vector3d = new Vector3d(this.entity.getPosX(), this.entity.getPosY() + 0.1D, this.entity.getPosZ());
      Vector3d vector3d1 = new Vector3d(target.getPosX(), target.getPosY() + 0.1D, target.getPosZ());
      return this.entity.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.entity)).getType() == RayTraceResult.Type.MISS;
    }
  }  
}
