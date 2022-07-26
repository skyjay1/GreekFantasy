package greekfantasy.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

/**
 * Causes the entity to float just below the surface of the water
 */
public class GFFloatGoal extends Goal {
    protected final Mob mob;

    public GFFloatGoal(final PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        BlockPos pos = mob.blockPosition().above((int) Math.ceil(mob.getBbHeight()));
        return this.mob.isInWater() && mob.level.getBlockState(pos).is(Blocks.WATER);
    }

    @Override
    public void tick() {
        mob.setDeltaMovement(mob.getDeltaMovement().add(0.0F, 0.006F, 0.0F));
    }
}
