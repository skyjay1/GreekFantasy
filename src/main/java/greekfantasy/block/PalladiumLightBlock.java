package greekfantasy.block;

import greekfantasy.entity.Palladium;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

public class PalladiumLightBlock extends LightBlock {
    public PalladiumLightBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        state.setValue(WATERLOGGED, oldState.getFluidState().is(FluidTags.WATER));
        worldIn.scheduleTick(pos, this, 9);
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            worldIn.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
        super.tick(state, level, pos, rand);
        level.scheduleTick(pos, this, 9);
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        AABB aabb = new AABB(pos);
        List<Palladium> list = level.getEntitiesOfClass(Palladium.class, aabb);

        if (list.isEmpty()) {
            // remove block
            BlockState replaceWith = state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.defaultFluidState().createLegacyBlock() : Blocks.AIR.defaultBlockState();
            level.setBlock(pos, replaceWith, Block.UPDATE_ALL);
        }
    }
}
