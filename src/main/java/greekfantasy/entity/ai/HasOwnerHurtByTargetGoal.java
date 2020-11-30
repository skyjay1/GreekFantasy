package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.entity.misc.IHasOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

public class HasOwnerHurtByTargetGoal<T extends MobEntity & IHasOwner<T>> extends TargetGoal {
  private T entity;
  private LivingEntity attacker;
  private int timestamp;

  public HasOwnerHurtByTargetGoal(final T entityIn) {
    super(entityIn, false);
    setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    entity = entityIn;
  }

  @Override
  public boolean shouldExecute() {
    if (!entity.hasOwner()) {
      return false;
    }
    LivingEntity owner = entity.getOwner();
    if (owner == null) {
      return false;
    }
    this.attacker = owner.getRevengeTarget();
    int revengeTimer = owner.getRevengeTimer();
    return (revengeTimer != this.timestamp && isSuitableTarget(this.attacker, EntityPredicate.DEFAULT)
        && entity.shouldAttackEntity(this.attacker, owner));
  }

  @Override
  public void startExecuting() {
    this.goalOwner.setAttackTarget(this.attacker);
    LivingEntity owner = entity.getOwner();
    if (owner != null) {
      this.timestamp = owner.getRevengeTimer();
    }
    super.startExecuting();
  }
}
