package greekfantasy.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class GoToWaterGoal extends Goal {
  protected final CreatureEntity entity;
  private final int detectWaterRadius;
  private double targetX;
  private double targetY;
  private double targetZ;
  private final double speed;

  public GoToWaterGoal(final CreatureEntity entityIn, final double speed, final int radius) {
    this.entity = entityIn;
    this.detectWaterRadius = radius;
    this.speed = speed;
    setFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean canUse() {
    if (this.entity.isInWater()) {
      return false;
    }

    Vector3d target = getNearbyWater();
    if (target == null) {
      return false;
    }

    this.targetX = target.x;
    this.targetY = target.y;
    this.targetZ = target.z;
    return true;
  }

  @Override
  public boolean canContinueToUse() {
    return !this.entity.getNavigation().isDone();
  }

  @Override
  public void start() {
    this.entity.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
  }

  private Vector3d getNearbyWater() {
    Random rand = this.entity.getRandom();
    BlockPos pos1 = this.entity.blockPosition().below();

    for (int i = 0; i < 10; i++) {
      BlockPos pos2 = pos1.offset(rand.nextInt(detectWaterRadius * 2) - detectWaterRadius, 2 - rand.nextInt(8),
          rand.nextInt(detectWaterRadius * 2) - detectWaterRadius);

      if (this.entity.getCommandSenderWorld().getBlockState(pos2).getFluidState().getType().is(FluidTags.WATER)) {
        return new Vector3d(pos2.getX(), pos2.getY(), pos2.getZ());
      }
    }
    return null;
  }
}
