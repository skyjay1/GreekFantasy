package greekfantasy.entity.ai;

import java.util.EnumSet;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;

public class IntervalRangedAttackGoal extends Goal {
  protected IRangedAttackMob rangedEntity;
  protected MobEntity entity;
  protected int goalTime;
  protected int maxTime;
  protected int maxCooldown;
  protected int interval;
  protected int cooldown;
  
  public IntervalRangedAttackGoal(final IRangedAttackMob entityIn, final int duration, final int count, final int maxCooldownIn) {
    if(!(entityIn instanceof MobEntity)) {
      throw new IllegalArgumentException("IntervalRangedAttackGoal requires MobEntity that implements IRangedAttackMob");
    }
    this.setMutexFlags(EnumSet.allOf(Goal.Flag.class));
    rangedEntity = entityIn;
    entity = (MobEntity)entityIn;
    maxTime = duration;
    maxCooldown = maxCooldownIn;
    cooldown = 30;
    interval = Math.floorDiv(Math.max(count, duration - 20), count);
  }

  @Override
  public boolean shouldExecute() {  
    if(this.cooldown > 0) {
      cooldown--;
    } else if (entity.getAttackTarget() != null
        && entity.canEntityBeSeen(entity.getAttackTarget())
        && isWithinRange(entity.getAttackTarget())) {
      return true;
    }
    return false;
  }
  
  @Override
  public boolean shouldContinueExecuting() {
    return goalTime > 0 && entity.getAttackTarget() != null
        && entity.canEntityBeSeen(entity.getAttackTarget())
        && isWithinRange(entity.getAttackTarget());
  }
 
  @Override
  public void startExecuting() {
    goalTime = 1;
    entity.setAggroed(true);
  }
  
  @Override
  public void tick() {
    // stop the entity from moving, and adjust look vecs
    final LivingEntity target = entity.getAttackTarget();
    entity.getNavigator().clearPath();
    entity.faceEntity(target, 100.0F, 100.0F);
    entity.getLookController().setLookPositionWithEntity(target, 100.0F, 100.0F);
    // spit attack on interval
    if(goalTime % interval == 0) {
      rangedEntity.attackEntityWithRangedAttack(target, 0.1F);
    }
    // finish the spit attack
    if(goalTime++ >= maxTime) {
      resetTask();
    }
  }
  
  @Override
  public void resetTask() {
    entity.setAggroed(false);
    this.goalTime = 0;
    this.cooldown = maxCooldown;
  }
  
  protected boolean isWithinRange(final LivingEntity target) {
    if(target != null) {
      final double disSq = entity.getDistanceSq(target);
      final double maxRange = entity.getAttributeValue(Attributes.FOLLOW_RANGE) * 0.75D;
      return disSq > 9.0D && disSq < (maxRange * maxRange);
    }
    return false;
  }
}
