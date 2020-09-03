package greekfantasy.entity;

import java.util.EnumSet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MinotaurEntity extends MonsterEntity implements IHoofedEntity {
  
  private static final DataParameter<Boolean> DATA_STOMPING = EntityDataManager.createKey(MinotaurEntity.class, DataSerializers.BOOLEAN);
  private static final DataParameter<Boolean> DATA_STUNNED = EntityDataManager.createKey(MinotaurEntity.class, DataSerializers.BOOLEAN);
  
  public MinotaurEntity(final EntityType<? extends MinotaurEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.27D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
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
  public void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_STOMPING, Boolean.valueOf(false));
    this.getDataManager().register(DATA_STUNNED, Boolean.valueOf(false));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // spawn particles
    if (world.isRemote() && this.isStunned()) {
      final double motion = 0.09D;
      final double radius = 0.6D;
      for (int i = 0; i < 2; i++) {
        world.addParticle(ParticleTypes.ENCHANTED_HIT, 
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
  public void setStomping(final boolean stomping) {
    this.getDataManager().set(DATA_STOMPING, Boolean.valueOf(stomping));
  }

  @Override
  public boolean isStomping() {
    return this.getDataManager().get(DATA_STOMPING);
  }

  @Override
  public float getStompingSpeed() {
    return 0.74F;
  }
  
  public void setStunned(final boolean stunned) {
    this.getDataManager().set(DATA_STUNNED, Boolean.valueOf(stunned));
  }

  public boolean isStunned() {
    return this.getDataManager().get(DATA_STUNNED);
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
      if(this.cooldown > 0) {
        cooldown--;
      } else if (this.entity.getAttackTarget() != null && !this.entity.getMoveHelper().isUpdating() && entity.getRNG().nextInt(7) == 0) {
        // if the target is more than 3 blocks away
        return (this.entity.getDistanceSq(this.entity.getAttackTarget()) > 9.0D);
      }
      return false;
    }
    
    @Override
    public boolean shouldContinueExecuting() { 
      return (this.entity.isStomping() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isAlive()); 
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
      final boolean isStomping = stompingTimer > 0 && stompingTimer++ < 50;
      if(finished) {
        // reset values
        this.entity.setStomping(false);
        this.stompingTimer = 0;
        this.targetPos = null;
        this.cooldown = 200;
        // if charge attack hit the player
        if(hitTarget) {
          this.entity.attackEntityAsMob(target);
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
  }
  
  
  
}
