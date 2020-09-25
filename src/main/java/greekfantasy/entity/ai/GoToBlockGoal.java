package greekfantasy.entity.ai;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

public abstract class GoToBlockGoal extends Goal {

  protected final CreatureEntity creature;
  protected final int searchRadiusXZ;
  protected final int searchRadiusY;
  protected final double speed;
  
  protected Optional<Vector3d> target = Optional.empty();
  
  public GoToBlockGoal(final CreatureEntity entity, final int radius, final double speed) {
    this(entity, radius, Math.max(1, radius / 2), speed);
  }

  public GoToBlockGoal(final CreatureEntity entity, final int radiusXZ, final int radiusY, final double speed) {
    this.creature = entity;
    this.searchRadiusXZ = radiusXZ;
    this.searchRadiusY = radiusY;
    this.speed = speed;
    setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean shouldExecute() {
    // if already near target, do not execute
    if (isOnBlock() || isNearTarget(1.2D) || this.creature.getNavigator().hasPath()) {
      return false;
    }

    final Optional<BlockPos> blockPos = findNearbyBlock();
    if (blockPos.isPresent()) {
      this.target = Optional.of(getVecForBlockPos(blockPos.get()));
      return true;
    }
   
    return false;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }

  @Override
  public void startExecuting() {
    if(this.target.isPresent()) {
      this.creature.getNavigator().tryMoveToXYZ(this.target.get().x, this.target.get().y, this.target.get().z, this.speed);
      this.target = Optional.empty();
    }
  }
  
  /**
   * Required to use this goal.
   * @param worldIn the world
   * @param pos the BlockPos to check
   * @return whether the entity should move toward the given block
   **/
  public abstract boolean shouldMoveTo(final IWorldReader worldIn, final BlockPos pos);
  
  protected boolean isOnBlock() {
    return shouldMoveTo(this.creature.getEntityWorld(), this.creature.getPosition());
  }
  
  protected Vector3d getVecForBlockPos(final BlockPos pos) {
    return new Vector3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
  }
  
  public boolean isNearTarget(final double distance) {
    return this.target.isPresent() && this.target.get().isWithinDistanceOf(this.creature.getPositionVec(), distance);
  }
  
  private Optional<BlockPos> findNearbyBlock() {
    Random rand = this.creature.getRNG();
    BlockPos pos1 = this.creature.getPosition().down();
    // choose 20 random positions to check
    for (int i = 0; i < 20; i++) {
      BlockPos pos2 = pos1.add(rand.nextInt(searchRadiusXZ * 2) - searchRadiusXZ, rand.nextInt(searchRadiusY * 2) - searchRadiusY,
          rand.nextInt(searchRadiusXZ * 2) - searchRadiusXZ);
      // check the block to see if creature should move here
      if (this.shouldMoveTo(this.creature.getEntityWorld(), pos2)) {
        return Optional.of(new BlockPos(pos2.getX(), pos2.getY(), pos2.getZ()));
      }
    }
    return Optional.empty();
  }

}