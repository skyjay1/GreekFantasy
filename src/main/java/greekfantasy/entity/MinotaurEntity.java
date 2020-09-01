package greekfantasy.entity;

import java.util.EnumSet;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MinotaurEntity extends CreatureEntity implements IHoofedEntity {
  
  protected boolean isStomping;

  public MinotaurEntity(final EntityType<? extends MinotaurEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(2, new ChargeAttackGoal(this, 1.0D));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
 }

  @Override
  public boolean isStomping() {
    return isStomping;
  }

  @Override
  public float getStompingSpeed() {
    return 0.64F;
  }
  
  class ChargeAttackGoal extends Goal {
    
    private final MinotaurEntity entity;
    private final double speed;
    private int stompingTimer;
    private final int maxStompingTime = 80;
    
    protected ChargeAttackGoal(final MinotaurEntity entityIn, final double speedIn) {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      entity = entityIn;
      speed = speedIn;
    }

    @Override
    public boolean shouldExecute() {
      // if the target is more than 3 blocks away
      if (this.entity.getAttackTarget() != null && !this.entity.getMoveHelper().isUpdating() && entity.getRNG().nextInt(7) == 0) {
        return (this.entity.getDistanceSq(this.entity.getAttackTarget()) > 9.0D);
      }
      return false;
    }
    
    @Override
    public boolean shouldContinueExecuting() { 
      return (this.entity.isStomping && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isAlive()); 
    }
    
    public void startExecuting() {
      this.entity.isStomping = true;
      this.stompingTimer = 1;
    }


    @Override
    public void tick() {
      LivingEntity target = this.entity.getAttackTarget();
      if (this.entity.getBoundingBox().intersects(target.getBoundingBox())) {
        // charge attack was successful
        this.entity.attackEntityAsMob(target);
        this.entity.isStomping = false;
      } else if(stompingTimer > 0 && stompingTimer++ < maxStompingTime) {
        // launch the charge attack
        // TODO get a vector from entity to target and extend it
        // to allow minotaur to run past the player position
        Vector3d pos = target.getPositionVec();
        this.entity.getMoveHelper().setMoveTo(pos.x, pos.y, pos.z, speed);
      } else {
        // prevent the entity from moving
        this.entity.getMoveHelper().setMoveTo(entity.getPosX(), entity.getPosY(), entity.getPosZ(), 0.01D);
      }
    }
    
    @Override
    public void resetTask() { 
      this.entity.isStomping = false;
      this.stompingTimer = 0;
    }
  }
  
  
  
}
