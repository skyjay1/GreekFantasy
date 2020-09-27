package greekfantasy.entity.ai;

import java.util.EnumSet;
import java.util.Optional;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

/**
 * A version of FindBlockGoal with two differences:
 * First, it ignores the cooldown value.
 * Second, it affects the entity path finding to move
 * toward the block that is found.
 *
 **/
public abstract class GoToBlockGoal extends FindBlockGoal {

  protected final double speed;
  
  protected Optional<Vector3d> target = Optional.empty();
  
  public GoToBlockGoal(final CreatureEntity entity, final double speed, final int radius) {
    this(entity, speed, radius, Math.max(1, radius / 2));
  }

  public GoToBlockGoal(final CreatureEntity entity, final double speedIn, final int radiusXZ, final int radiusY) {
    super(entity, radiusXZ, radiusY, 0);
    setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    speed = speedIn;
  }

  @Override
  public boolean shouldExecute() {
    if (this.creature.getNavigator().hasPath()) {
      return false;
    }
    return super.shouldExecute();
  }

  @Override
  public boolean shouldContinueExecuting() {
    return this.creature.getNavigator().noPath() && shouldExecute();
  }

  @Override
  public void onFoundBlock(final IWorldReader worldIn, final BlockPos target) {
    this.target = Optional.of(getVecForBlockPos(target));
    moveToPos(this.target.get());
    this.target = Optional.empty();
  }

  protected void moveToPos(final Vector3d vec) {
    this.creature.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, this.speed);
  }
}