package greekfantasy.entity.ai;

import greekfantasy.entity.ISwimmingMob;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class SwimUpGoal<T extends CreatureEntity & ISwimmingMob> extends Goal {
  protected final T entity;
  private final double speed;
  private final int targetY;
  private boolean obstructed;

  public SwimUpGoal(T entityIn, double speedIn, int seaLevel) {
    this.entity = entityIn;
    this.speed = speedIn;
    this.targetY = seaLevel;
  }

  @Override
  public boolean shouldExecute() {
    return (entity.isInWater() && entity.getPosY() < (this.targetY - 2.3D) && 
        (entity.getNavigator().noPath() || entity.getPosY() < entity.getNavigator().getPath().getFinalPathPoint().y));
  }

  @Override
  public boolean shouldContinueExecuting() {
    return (shouldExecute() && !this.obstructed);
  }

  @Override
  public void tick() {
    if (entity.getPosY() < (this.targetY - 1.0D) && (entity.getNavigator().noPath() || isCloseToPathTarget())) {

      Vector3d vec = RandomPositionGenerator.findRandomTargetBlockTowards(entity, 4, 8,
          new Vector3d(entity.getPosX(), this.targetY - 1.0D, entity.getPosZ()));

      if (vec == null) {
        this.obstructed = true;
        return;
      }
      entity.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, this.speed);
    }
  }

  @Override
  public void startExecuting() {
    entity.setSwimmingUp(true);
    this.obstructed = false;
  }

  @Override
  public void resetTask() {
    entity.setSwimmingUp(false);
  }

  private boolean isCloseToPathTarget() {
    Path path = entity.getNavigator().getPath();
    if (path != null) {
      BlockPos pos = path.getTarget();
      if (pos != null) {
        double dis = entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
        if (dis < 4.0D) {
          return true;
        }
      }
    }
    return false;
  }
}
