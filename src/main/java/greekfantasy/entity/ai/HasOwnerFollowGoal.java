package greekfantasy.entity.ai;

import java.util.EnumSet;

import greekfantasy.entity.misc.IHasOwner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class HasOwnerFollowGoal<T extends MobEntity & IHasOwner<T>> extends Goal {
  private final T entity;
  private LivingEntity owner;
  private final IWorldReader world;
  private final double followSpeed;
  private final PathNavigator navigator;
  private int timeToRecalcPath;
  private final float closeDist;
  private final float farDist;
  private float oldWaterCost;
  private final boolean teleportToLeaves;

  public HasOwnerFollowGoal(T entityIn, double followSpeedIn, float farDistance, float closeDistance, boolean teleportToLeavesIn) {
    this.entity = entityIn;
    this.world = entityIn.world;
    this.followSpeed = followSpeedIn;
    this.navigator = entityIn.getNavigator();
    this.farDist = farDistance;
    this.closeDist = closeDistance;
    this.teleportToLeaves = teleportToLeavesIn;
    setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean shouldExecute() {
    LivingEntity owner = this.entity.getOwner();
    if (owner == null) {
      return false;
    }
    if (owner.isSpectator() || this.entity.getDistanceSq(owner) < (this.farDist * this.farDist)) {
      return false;
    }
    this.owner = owner;
    return true;
  }

  @Override
  public boolean shouldContinueExecuting() {
    if (this.navigator.noPath()) {
      return false;
    }
    if (this.entity.getDistanceSq(this.owner) <= (this.closeDist * this.closeDist)) {
      return false;
    }
    return true;
  }

  @Override
  public void startExecuting() {
    this.timeToRecalcPath = 0;
    this.oldWaterCost = this.entity.getPathPriority(PathNodeType.WATER);
    this.entity.setPathPriority(PathNodeType.WATER, 0.0F);
  }

  @Override
  public void resetTask() {
    this.owner = null;
    this.navigator.clearPath();
    this.entity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
  }

  @Override
  public void tick() {
    this.entity.getLookController().setLookPositionWithEntity(this.owner, 10.0F, this.entity.getVerticalFaceSpeed());

    if (--this.timeToRecalcPath > 0) {
      return;
    }
    this.timeToRecalcPath = 10;

    if (this.entity.getLeashed() || this.entity.isPassenger()) {
      return;
    }

    if (this.entity.getDistanceSq(this.owner) >= 4 * (this.farDist * this.farDist)) {
      tryToTeleportNearEntity();
    } else {
      this.navigator.tryMoveToEntityLiving(this.owner, this.followSpeed);
    }
  }

  private void tryToTeleportNearEntity() {
    BlockPos ownerPos = this.owner.getPosition();

    for (int attempts = 0; attempts < 10; attempts++) {
      int x = getRandomNumber(-3, 3);
      int y = getRandomNumber(-1, 1);
      int z = getRandomNumber(-3, 3);
      boolean teleportSuccess = tryToTeleportToLocation(ownerPos.getX() + x, ownerPos.getY() + y,
          ownerPos.getZ() + z);
      if (teleportSuccess) {
        return;
      }
    }
  }

  private boolean tryToTeleportToLocation(int x, int y, int z) {
    if (Math.abs(x - this.owner.getPosX()) < 2.0D && Math.abs(z - this.owner.getPosZ()) < 2.0D) {
      return false;
    }
    if (!isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
      return false;
    }
    this.entity.setLocationAndAngles(x + 0.5D, y, z + 0.5D, this.entity.rotationYaw,
        this.entity.rotationPitch);
    this.navigator.clearPath();
    return true;
  }

  private boolean isTeleportFriendlyBlock(BlockPos pos) {
    PathNodeType pathType = WalkNodeProcessor.getFloorNodeType(this.world, pos.toMutable());

    if (pathType != PathNodeType.WALKABLE) {
      return false;
    }

    BlockState posDown = this.world.getBlockState(pos.down());
    if (!this.teleportToLeaves && posDown.getBlock() instanceof net.minecraft.block.LeavesBlock) {
      return false;
    }

    BlockPos distance = pos.subtract(this.entity.getPosition());
    if (!this.world.hasNoCollisions(this.entity, this.entity.getBoundingBox().offset(distance))) {
      return false;
    }

    return true;
  }

  private int getRandomNumber(int min, int max) {
    return this.entity.getRNG().nextInt(max - min + 1) + min;
  }
}
