package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.GreekFantasy;
import greekfantasy.favor.FavorRange;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class NearestAttackableFavorablePlayerResetGoal extends Goal {

  protected MobEntity entity;
  protected int interval;
  
 public NearestAttackableFavorablePlayerResetGoal(final MobEntity entityIn) { this(entityIn, 10); }
  
  public NearestAttackableFavorablePlayerResetGoal(final MobEntity entityIn, int intervalIn) {
    this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    entity = entityIn;
    interval = intervalIn;
  }

  @Override
  public boolean shouldExecute() {
    if(entity.ticksExisted % interval == 0 && entity.isAlive() && entity.getAttackTarget() instanceof PlayerEntity
        && entity.getAttackTarget() != entity.getRevengeTarget()) {
      FavorRange range = GreekFantasy.PROXY.getFavorRangeTarget().get(entity.getType());
      return range != FavorRange.EMPTY && !range.isInFavorRange((PlayerEntity)entity.getAttackTarget());
    }
    return false;
  }
  
  @Override
  public boolean shouldContinueExecuting() { return false; }
  
  @Override
  public void startExecuting() {
    entity.setAttackTarget(null);
  }
}

