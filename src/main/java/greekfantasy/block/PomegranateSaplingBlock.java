package greekfantasy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;

public class PomegranateSaplingBlock extends SaplingBlock {

    public PomegranateSaplingBlock(AbstractTreeGrower treeIn, Properties properties) {
        super(treeIn, properties);
    }

    @Override
    public void advanceTree(ServerLevel world, BlockPos pos, BlockState state, Random rand) {
        if (state.getValue(STAGE) == 0) {
            world.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            if (Level.NETHER.equals(world.dimension())) {
                super.advanceTree(world, pos, state, rand);
            } else {
                // explode when not in nether
                world.removeBlock(pos, false);
                world.explode(null, null, null, pos.getX(), pos.getY(), pos.getZ(), 2.0F, true, Explosion.BlockInteraction.DESTROY);
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return state.is(BlockTags.NYLIUM) || state.is(Blocks.SOUL_SAND) || super.mayPlaceOn(state, worldIn, pos);
    }
}
