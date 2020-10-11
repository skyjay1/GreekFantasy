package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.entity.IHasOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

public class HasOwnerHurtTargetGoal<T extends MobEntity & IHasOwner> extends TargetGoal {
  private T entity;
  private LivingEntity attacker;
  private int timestamp;

  public HasOwnerHurtTargetGoal(final T entityIn) {
    super(entityIn, false);
    setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    entity = entityIn;
  }

  public boolean shouldExecute() {
    if (!entity.hasOwner()) {
      return false;
    }
    LivingEntity owner = entity.getOwner();
    if (owner == null) {
      return false;
    }
    this.attacker = owner.getLastAttackedEntity();
    int lastAttackedTime = owner.getLastAttackedEntityTime();
    return (lastAttackedTime != this.timestamp && isSuitableTarget(this.attacker, EntityPredicate.DEFAULT)
        && entity.shouldAttackEntity(this.attacker, owner));
  }

  @Override
  public void startExecuting() {
    this.goalOwner.setAttackTarget(this.attacker);
    LivingEntity owner = entity.getOwner();
    if (owner != null) {
      this.timestamp = owner.getLastAttackedEntityTime();
    }
    super.startExecuting();
  }
}
