package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.entity.misc.IHasOwner;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class HasOwnerBegGoal<T extends MobEntity & IHasOwner<T>> extends Goal {
  protected final T entity;
  private final float minPlayerDistance;
  private final EntityPredicate playerPredicate;

  private PlayerEntity player;
  private int timeoutCounter;

  public HasOwnerBegGoal(final T entityHasOwner, final float minDistance) {
    this.entity = entityHasOwner;
    this.minPlayerDistance = minDistance;
    this.playerPredicate = (new EntityPredicate()).range((double) minDistance).allowInvulnerable().allowSameTeam().allowNonAttackable();
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    this.player = entity.level.getNearestPlayer(this.playerPredicate, entity);
    return this.player == null ? false : entity.hasTamingItemInHand(this.player);
  }

  @Override
  public boolean canContinueToUse() {
    if (!this.player.isAlive()) {
      return false;
    } else if (entity.distanceToSqr(this.player) > (double) (this.minPlayerDistance * this.minPlayerDistance)) {
      return false;
    } else {
      return this.timeoutCounter > 0 && entity.hasTamingItemInHand(this.player);
    }
  }

  @Override
  public void start() {
    this.timeoutCounter = 40 + entity.getRandom().nextInt(40);
  }

  @Override
  public void stop() {
    this.player = null;
  }

  @Override
  public void tick() {
    entity.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(),
        this.player.getZ(), 10.0F, (float) entity.getMaxHeadXRot());
    entity.getNavigation().stop();
    --this.timeoutCounter;
    if(entity.getTarget() == this.player) {
      entity.setTarget(null);
    }
  }
}
