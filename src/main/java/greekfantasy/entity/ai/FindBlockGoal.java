package greekfantasy.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

public abstract class FindBlockGoal extends Goal {

    protected final PathfinderMob creature;
    protected final int searchRadiusXZ;
    protected final int searchRadiusY;
    protected final int maxCooldown;

    protected int cooldown;
    protected BlockPos targetPos;

    public FindBlockGoal(final PathfinderMob entity, final int radius, final int cooldown) {
        this(entity, radius, Math.max(1, radius / 2), cooldown);
    }

    public FindBlockGoal(final PathfinderMob entity, final int radiusXZ, final int radiusY, final int cooldownIn) {
        setFlags(EnumSet.noneOf(Goal.Flag.class));
        this.creature = entity;
        this.searchRadiusXZ = radiusXZ;
        this.searchRadiusY = radiusY;
        this.maxCooldown = cooldownIn;
        this.cooldown = 10;
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
        } else {
            // if already near targetPos, do not execute
            if (isOnBlock() || isNearTarget(1.2D)) {
                return false;
            }

            this.targetPos = findNearbyBlock().orElse(null);
            return this.targetPos != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        this.cooldown = maxCooldown;
        onFoundBlock(this.creature.level, this.targetPos);
    }

    /**
     * Required to use this goal. Determines if the given block pos should be used.
     *
     * @param worldIn the world
     * @param pos     the BlockPos to check
     * @return whether the entity should move toward the given block
     **/
    public abstract boolean isTargetBlock(final LevelReader worldIn, final BlockPos pos);

    /**
     * Called when the block has been found and a target will be remembered.
     *
     * @param worldIn the world
     * @param target  the BlockPos that was found
     **/
    public abstract void onFoundBlock(final LevelReader worldIn, final BlockPos target);

    /**
     * @return whether the entity is already at the target position
     **/
    protected boolean isOnBlock() {
        return isTargetBlock(this.creature.level, this.creature.blockPosition());
    }

    /**
     * @param distance the maximum distance to consider "near" the target
     * @return whether the entity is within the given distance to the target
     **/
    protected boolean isNearTarget(final double distance) {
        return this.targetPos != null && Vec3.atBottomCenterOf(targetPos).closerThan(this.creature.position(), distance);
    }

    /**
     * Iterates through several randomly chosen blocks in a nearby radius
     * until one is found that can become the target.
     *
     * @return an Optional containing a block pos if one is found, otherwise empty
     **/
    protected Optional<BlockPos> findNearbyBlock() {
        Random rand = this.creature.getRandom();
        BlockPos pos1 = this.creature.blockPosition().below();
        // choose 20 random positions to check
        for (int i = 0; i < 20; i++) {
            BlockPos pos2 = pos1.offset(rand.nextInt(searchRadiusXZ * 2) - searchRadiusXZ, rand.nextInt(searchRadiusY * 2) - searchRadiusY,
                    rand.nextInt(searchRadiusXZ * 2) - searchRadiusXZ);
            // check the block to see if creature should move here
            if (this.isTargetBlock(this.creature.getCommandSenderWorld(), pos2)) {
                return Optional.of(new BlockPos(pos2.getX(), pos2.getY(), pos2.getZ()));
            }
        }
        return Optional.empty();
    }

}