package greekfantasy.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FollowWaterMobGoal extends Goal {
    private final Mob mob;
    private final Predicate<Mob> followPredicate;
    @Nullable
    private Mob followingMob;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float areaSize;

    public FollowWaterMobGoal(Mob mob, Class<? extends Mob> clazz, double speedMultiplier, float stopDistance, float areaSize) {
        this.mob = mob;
        this.followPredicate = e -> e != null && e.getClass() == clazz && !e.isInvisible();
        this.speedModifier = speedMultiplier;
        this.navigation = mob.getNavigation();
        this.stopDistance = stopDistance;
        this.areaSize = areaSize;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof WaterBoundPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowWaterMobGoal");
        }
    }

    @Override
    public boolean canUse() {
        List<Mob> list = this.mob.level.getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate((double) this.areaSize), this.followPredicate);
        if (!list.isEmpty()) {
            this.followingMob = list.get(0);
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > (double) (this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.followingMob = null;
        this.navigation.stop();
    }

    @Override
    public void tick() {
        if (this.followingMob != null && !this.mob.isLeashed()) {
            this.mob.getLookControl().setLookAt(this.followingMob, 10.0F, (float) this.mob.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                double d0 = this.mob.getX() - this.followingMob.getX();
                double d1 = this.mob.getY() - this.followingMob.getY();
                double d2 = this.mob.getZ() - this.followingMob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (!(d3 <= (double) (this.stopDistance * this.stopDistance))) {
                    this.navigation.moveTo(this.followingMob, this.speedModifier);
                } else {
                    this.navigation.stop();
                    LookControl lookcontrol = this.followingMob.getLookControl();
                    if (d3 <= (double) this.stopDistance || lookcontrol.getWantedX() == this.mob.getX() && lookcontrol.getWantedY() == this.mob.getY() && lookcontrol.getWantedZ() == this.mob.getZ()) {
                        double d4 = this.followingMob.getX() - this.mob.getX();
                        double d5 = this.followingMob.getZ() - this.mob.getZ();
                        this.navigation.moveTo(this.mob.getX() - d4, this.mob.getY(), this.mob.getZ() - d5, this.speedModifier);
                    }

                }
            }
        }
    }
}
