package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.entity.misc.IFavorable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class FavorableResetTargetGoal<T extends MobEntity & IFavorable> extends Goal {
  
  protected T entity;
  protected int interval;
  
  public FavorableResetTargetGoal(final T entityIn) { this(entityIn, 10); }
  
  public FavorableResetTargetGoal(final T entityIn, int intervalIn) {
    this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    entity = entityIn;
    interval = Math.max(1, intervalIn);
  }

  @Override
  public boolean shouldExecute() {
    return entity.ticksExisted % interval == 0 && entity.isAlive() && entity.getAttackTarget() instanceof PlayerEntity && !entity.targetFavorAttackable(entity.getAttackTarget());
  }
  
  @Override
  public boolean shouldContinueExecuting() { return false; }
  
  @Override
  public void startExecuting() {
    entity.setAttackTarget(null);
  }

}
