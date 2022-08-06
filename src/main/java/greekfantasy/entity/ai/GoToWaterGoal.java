package greekfantasy.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class GoToWaterGoal extends Goal {
    private final PathfinderMob mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final boolean nightOnly;
    private final Level level;

    public GoToWaterGoal(PathfinderMob mob, double speed, boolean nightOnly) {
        this.mob = mob;
        this.speedModifier = speed;
        this.nightOnly = nightOnly;
        this.level = mob.level;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (nightOnly && !this.level.isDay()) {
            return false;
        } else if (this.mob.isInWater()) {
            return false;
        } else {
            Vec3 vec3 = this.getWaterPos();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Nullable
    private Vec3 getWaterPos() {
        RandomSource random = this.mob.getRandom();
        BlockPos blockpos = this.mob.blockPosition();

        for(int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                return Vec3.atBottomCenterOf(blockpos1);
            }
        }

        return null;
    }
}
