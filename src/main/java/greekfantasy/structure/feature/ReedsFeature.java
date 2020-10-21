package greekfantasy.structure.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ReedsFeature extends Feature<BlockClusterFeatureConfig> {

  public ReedsFeature(Codec<BlockClusterFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean func_241855_a(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final BlockClusterFeatureConfig config) {
    BlockPos pos;
    BlockState state = GFRegistry.REEDS.getDefaultState();
    
    pos = reader.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockPosIn);
    
    int placed = 0;
    
    BlockPos.Mutable mutpos = new BlockPos.Mutable();
    for (int i = 0, l = config.tryCount; i < l; i++) {
      // get random position nearby
      mutpos.setAndOffset(pos, rand.nextInt(config.xSpread + 1) - rand.nextInt(config.xSpread + 1), rand.nextInt(config.ySpread + 1) - rand.nextInt(config.ySpread + 1), rand.nextInt(config.zSpread + 1) - rand.nextInt(config.zSpread + 1));
      BlockPos posDown = mutpos.down();
      BlockState stateDown = reader.getBlockState(posDown);
      FluidState fluidState = reader.getFluidState(mutpos);
      // check if the block can be placed here
      if ((reader.isAirBlock(mutpos) || fluidState.isTagged(FluidTags.WATER) || (config.isReplaceable && reader.getBlockState(mutpos).getMaterial().isReplaceable())) 
          && state.isValidPosition(reader, mutpos) && (config.whitelist.isEmpty() || config.whitelist.contains(stateDown.getBlock()))
          && !config.blacklist.contains(stateDown)) {
          // actually place the block
          config.blockPlacer.place(reader, mutpos, state, rand);
          placed++;
      } 
    } 
    // DEBUG
    if(placed > 0) GreekFantasy.LOGGER.debug("Placed " + placed + " reeds near " + mutpos);
    return (placed > 0);
  }

}
