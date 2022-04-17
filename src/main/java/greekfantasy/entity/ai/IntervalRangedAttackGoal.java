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
    this.setFlags(EnumSet.allOf(Goal.Flag.class));
    rangedEntity = entityIn;
    entity = (MobEntity)entityIn;
    maxTime = duration;
    maxCooldown = maxCooldownIn;
    cooldown = 30;
    interval = Math.floorDiv(Math.max(count, duration - 20), count);
  }

  @Override
  public boolean canUse() {  
    if(this.cooldown > 0) {
      cooldown--;
    } else if (entity.getTarget() != null
        && entity.canSee(entity.getTarget())
        && isWithinRange(entity.getTarget())) {
      return true;
    }
    return false;
  }
  
  @Override
  public boolean canContinueToUse() {
    return goalTime > 0 && entity.getTarget() != null
        && entity.canSee(entity.getTarget())
        && isWithinRange(entity.getTarget());
  }
 
  @Override
  public void start() {
    goalTime = 1;
    entity.setAggressive(true);
  }
  
  @Override
  public void tick() {
    // stop the entity from moving, and adjust look vecs
    final LivingEntity target = entity.getTarget();
    entity.getNavigation().stop();
    entity.lookAt(target, 100.0F, 100.0F);
    entity.getLookControl().setLookAt(target, 100.0F, 100.0F);
    // spit attack on interval
    if(goalTime % interval == 0) {
      rangedEntity.performRangedAttack(target, 0.1F);
    }
    // finish the spit attack
    if(goalTime++ >= maxTime) {
      stop();
    }
  }
  
  @Override
  public void stop() {
    entity.setAggressive(false);
    this.goalTime = 0;
    this.cooldown = maxCooldown;
  }
  
  protected boolean isWithinRange(final LivingEntity target) {
    if(target != null) {
      final double disSq = entity.distanceToSqr(target);
      final double maxRange = entity.getAttributeValue(Attributes.FOLLOW_RANGE) * 0.75D;
      return disSq > 9.0D && disSq < (maxRange * maxRange);
    }
    return false;
  }
}
