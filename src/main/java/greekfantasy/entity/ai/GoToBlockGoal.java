package greekfantasy.entity.ai;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class GoToBlockGoal extends Goal {

  protected final CreatureEntity creature;
  protected final int searchRadius;
  protected final double speed;
  protected final Predicate<BlockState> predicate;
  
  protected double targetX;
  protected double targetY;
  protected double targetZ;

  public GoToBlockGoal(final CreatureEntity entity, final int radius, final double speed, final Predicate<BlockState> blockPred) {
    this.creature = entity;
    this.searchRadius = radius;
    this.speed = speed;
    this.predicate = blockPred;
    setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean shouldExecute() {
    if (this.predicate.test(this.creature.getEntityWorld().getBlockState(this.creature.getPosition().down()))) {
      return false;
    }

    BlockPos target = findNearbyBlock();
    if (target == null) {
      return false;
    }

    this.targetX = target.getX() + 0.5F;
    this.targetY = target.getY();
    this.targetZ = target.getZ() + 0.5F;
    return true;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return !this.creature.getNavigator().noPath();
  }

  @Override
  public void startExecuting() {
    this.creature.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
  }
  
  private BlockPos findNearbyBlock() {
    Random rand = this.creature.getRNG();

    BlockPos pos1 = this.creature.getPosition().down();

    for (int i = 0; i < 20; i++) {
      BlockPos pos2 = pos1.add(rand.nextInt(searchRadius * 2) - searchRadius, rand.nextInt(searchRadius) - searchRadius / 2,
          rand.nextInt(searchRadius * 2) - searchRadius);

      if (!this.creature.getEntityWorld().getBlockState(pos2.up(1)).isSolid()
          && this.predicate.test(this.creature.getEntityWorld().getBlockState(pos2))) {
        return new BlockPos(pos2.getX(), pos2.getY(), pos2.getZ());
      }
    }
    return null;
  }

}