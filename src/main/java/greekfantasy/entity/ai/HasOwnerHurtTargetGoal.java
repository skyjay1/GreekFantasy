package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.entity.misc.IHasOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

public class HasOwnerHurtTargetGoal<T extends MobEntity & IHasOwner<T>> extends TargetGoal {
  private T entity;
  private LivingEntity attacker;
  private int timestamp;

  public HasOwnerHurtTargetGoal(final T entityIn) {
    super(entityIn, false);
    setFlags(EnumSet.of(Goal.Flag.TARGET));
    entity = entityIn;
  }

  public boolean canUse() {
    if (!entity.hasOwner()) {
      return false;
    }
    LivingEntity owner = entity.getOwner();
    if (owner == null) {
      return false;
    }
    this.attacker = owner.getLastHurtMob();
    int lastAttackedTime = owner.getLastHurtMobTimestamp();
    return (lastAttackedTime != this.timestamp && canAttack(this.attacker, EntityPredicate.DEFAULT)
        && entity.shouldAttackEntity(this.attacker, owner));
  }

  @Override
  public void start() {
    this.mob.setTarget(this.attacker);
    LivingEntity owner = entity.getOwner();
    if (owner != null) {
      this.timestamp = owner.getLastHurtMobTimestamp();
    }
    super.start();
  }
}
