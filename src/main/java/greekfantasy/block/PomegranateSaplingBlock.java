package greekfantasy.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class PomegranateSaplingBlock extends SaplingBlock {

    public PomegranateSaplingBlock(Tree treeIn, Properties properties) {
        super(treeIn, properties);
    }

    @Override
    public void advanceTree(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
        if (state.getValue(STAGE) == 0) {
            world.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            if (Dimension.NETHER.equals(world.dimension())) {
                super.advanceTree(world, pos, state, rand);
            } else {
                // explode when not in nether
                world.removeBlock(pos, false);
                world.explode(null, null, null, pos.getX(), pos.getY(), pos.getZ(), 2.0F, true, Explosion.Mode.DESTROY);
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.is(Blocks.SOUL_SAND) || state.is(Blocks.CRIMSON_NYLIUM) || state.is(Blocks.WARPED_NYLIUM)
                || super.mayPlaceOn(state, worldIn, pos);
    }
}
