package greekfantasy.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class SwimUpGoal extends Goal {

    private final PathfinderMob mob;
    private final double speedModifier;
    private final int seaLevel;
    private final int deltaSeaLevel;
    private boolean stuck;

    /**
     * @param mob the mob
     * @param speedModifier the speed modifier
     * @param seaLevel the sea level
     * @param deltaSealevel the desired position below sea level
     */
    public SwimUpGoal(PathfinderMob mob, double speedModifier, int seaLevel, int deltaSealevel) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.seaLevel = seaLevel;
        this.deltaSeaLevel = deltaSealevel;
    }

    @Override
    public boolean canUse() {
        return this.mob.isInWater() && this.mob.getY() < (double) (this.seaLevel - deltaSeaLevel);
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() && !this.stuck;
    }

    @Override
    public void tick() {
        if (this.mob.getY() < (double) (this.seaLevel - deltaSeaLevel) && (this.mob.getNavigation().isDone() || closeToNextPos(this.mob))) {
            Vec3 vec3 = DefaultRandomPos.getPosTowards(this.mob, 4, 8, new Vec3(this.mob.getX(), (double) (this.seaLevel - deltaSeaLevel), this.mob.getZ()), (double) ((float) Math.PI / 2F));
            if (vec3 == null) {
                this.stuck = true;
                return;
            }

            this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
        }

    }

    @Override
    public void start() {
        this.stuck = false;
    }

    protected static boolean closeToNextPos(PathfinderMob mob) {
        Path path = mob.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            if (blockpos != null) {
                double d0 = mob.distanceToSqr(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                if (d0 < 4.0D) {
                    return true;
                }
            }
        }

        return false;
    }
}
