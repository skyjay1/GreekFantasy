package greekfantasy.entity.ai;

import greekfantasy.entity.misc.ISwimmingMob;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class SwimUpGoal<T extends CreatureEntity & ISwimmingMob> extends Goal {
    protected final T entity;
    protected final double speed;
    protected final int targetY;
    protected boolean obstructed;

    public SwimUpGoal(T entityIn, double speedIn, int seaLevel) {
        this.entity = entityIn;
        this.speed = speedIn;
        this.targetY = seaLevel;
    }

    @Override
    public boolean canUse() {
        return (entity.isInWater() && entity.getY() < (this.targetY - 2.3D) &&
                (entity.getNavigation().isDone() || entity.getY() < entity.getNavigation().getPath().getEndNode().y));
    }

    @Override
    public boolean canContinueToUse() {
        return (canUse() && !this.obstructed);
    }

    @Override
    public void tick() {
        if (entity.getY() < (this.targetY - 1.0D) && (entity.getNavigation().isDone() || isCloseToPathTarget())) {

            Vector3d vec = RandomPositionGenerator.getPosTowards(entity, 4, 8,
                    new Vector3d(entity.getX(), this.targetY - 1.0D, entity.getZ()));

            if (vec == null) {
                this.obstructed = true;
                return;
            }
            entity.getNavigation().moveTo(vec.x, vec.y, vec.z, this.speed);
        }
    }

    @Override
    public void start() {
        entity.setSwimmingUp(true);
        this.obstructed = false;
    }

    @Override
    public void stop() {
        entity.setSwimmingUp(false);
    }

    protected boolean isCloseToPathTarget() {
        Path path = entity.getNavigation().getPath();
        if (path != null) {
            BlockPos pos = path.getTarget();
            if (pos != null) {
                double dis = entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
                return dis < 4.0D;
            }
        }
        return false;
    }
}
