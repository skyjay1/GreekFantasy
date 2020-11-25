package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.entity.IHasOwner;
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
    this.playerPredicate = (new EntityPredicate()).setDistance((double) minDistance).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks();
    this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean shouldExecute() {
    this.player = entity.world.getClosestPlayer(this.playerPredicate, entity);
    return this.player == null ? false : entity.hasTamingItemInHand(this.player);
  }

  @Override
  public boolean shouldContinueExecuting() {
    if (!this.player.isAlive()) {
      return false;
    } else if (entity.getDistanceSq(this.player) > (double) (this.minPlayerDistance * this.minPlayerDistance)) {
      return false;
    } else {
      return this.timeoutCounter > 0 && entity.hasTamingItemInHand(this.player);
    }
  }

  @Override
  public void startExecuting() {
    this.timeoutCounter = 40 + entity.getRNG().nextInt(40);
  }

  @Override
  public void resetTask() {
    this.player = null;
  }

  @Override
  public void tick() {
    entity.getLookController().setLookPosition(this.player.getPosX(), this.player.getPosYEye(),
        this.player.getPosZ(), 10.0F, (float) entity.getVerticalFaceSpeed());
    entity.getNavigator().clearPath();
    --this.timeoutCounter;
    if(entity.getAttackTarget() == this.player) {
      entity.setAttackTarget(null);
    }
  }
}
