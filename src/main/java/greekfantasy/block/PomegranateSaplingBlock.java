package greekfantasy.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class PomegranateSaplingBlock extends SaplingBlock {

  public PomegranateSaplingBlock(Tree treeIn, Properties properties) {
    super(treeIn, properties);
  }

  @Override
  public void placeTree(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
    if (state.get(STAGE) == 0) {
      world.setBlockState(pos, state.cycleValue(STAGE), 4);
    } else {
      if(Dimension.THE_NETHER.equals(world.getDimensionKey())) {
        super.placeTree(world, pos, state, rand);
      } else {
        // explode when not in nether
        world.removeBlock(pos, false);
        world.createExplosion(null, null, null, pos.getX(), pos.getY(), pos.getZ(), 2.0F, true, Explosion.Mode.DESTROY);
      }
    }
  }
  
  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return state.matchesBlock(Blocks.SOUL_SAND) || state.matchesBlock(Blocks.CRIMSON_NYLIUM) || state.matchesBlock(Blocks.WARPED_NYLIUM) 
        || super.isValidGround(state, worldIn, pos);
  }
}
