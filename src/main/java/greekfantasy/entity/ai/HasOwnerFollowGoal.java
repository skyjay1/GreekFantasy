package greekfantasy.entity.ai;

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

import java.util.EnumSet;

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
        this.world = entityIn.level;
        this.followSpeed = followSpeedIn;
        this.navigator = entityIn.getNavigation();
        this.farDist = farDistance;
        this.closeDist = closeDistance;
        this.teleportToLeaves = teleportToLeavesIn;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = this.entity.getOwner();
        if (owner == null) {
            return false;
        }
        if (owner.isSpectator() || this.entity.distanceToSqr(owner) < (this.farDist * this.farDist)) {
            return false;
        }
        this.owner = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigator.isDone()) {
            return false;
        }
        return !(this.entity.distanceToSqr(this.owner) <= (this.closeDist * this.closeDist));
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.entity.getPathfindingMalus(PathNodeType.WATER);
        this.entity.setPathfindingMalus(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigator.stop();
        this.entity.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.entity.getLookControl().setLookAt(this.owner, 10.0F, this.entity.getMaxHeadXRot());

        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;

        if (this.entity.isLeashed() || this.entity.isPassenger()) {
            return;
        }

        if (this.entity.distanceToSqr(this.owner) >= 4 * (this.farDist * this.farDist)) {
            tryToTeleportNearEntity();
        } else {
            this.navigator.moveTo(this.owner, this.followSpeed);
        }
    }

    private void tryToTeleportNearEntity() {
        BlockPos ownerPos = this.owner.blockPosition();

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
        if (Math.abs(x - this.owner.getX()) < 2.0D && Math.abs(z - this.owner.getZ()) < 2.0D) {
            return false;
        }
        if (!isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        }
        this.entity.moveTo(x + 0.5D, y, z + 0.5D, this.entity.yRot,
                this.entity.xRot);
        this.navigator.stop();
        return true;
    }

    private boolean isTeleportFriendlyBlock(BlockPos pos) {
        PathNodeType pathType = WalkNodeProcessor.getBlockPathTypeStatic(this.world, pos.mutable());

        if (pathType != PathNodeType.WALKABLE) {
            return false;
        }

        BlockState posDown = this.world.getBlockState(pos.below());
        if (!this.teleportToLeaves && posDown.getBlock() instanceof net.minecraft.block.LeavesBlock) {
            return false;
        }

        BlockPos distance = pos.subtract(this.entity.blockPosition());
        return this.world.noCollision(this.entity, this.entity.getBoundingBox().move(distance));
    }

    private int getRandomNumber(int min, int max) {
        return this.entity.getRandom().nextInt(max - min + 1) + min;
    }
}
