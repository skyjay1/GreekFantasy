package greekfantasy.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;

/**
 * Causes the entity to float just below the surface of the water
 */
public class GFFloatGoal extends Goal {
    protected final Mob mob;
    protected final Function<Mob, Integer> heightFunction;

    /**
     * @param mob the mob
     * @param heightFunction a function for the number of blocks below the surface to float
     */
    public GFFloatGoal(final PathfinderMob mob, Function<Mob, Integer> heightFunction) {
        this.mob = mob;
        this.heightFunction = heightFunction;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        BlockPos pos = mob.blockPosition().above(heightFunction.apply(mob));
        return this.mob.isInWater() && mob.level.getBlockState(pos).is(Blocks.WATER);
    }

    @Override
    public void tick() {
        mob.setDeltaMovement(mob.getDeltaMovement().add(0.0F, 0.012F, 0.0F));
    }
}
