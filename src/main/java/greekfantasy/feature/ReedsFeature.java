package greekfantasy.feature;

import com.mojang.serialization.Codec;
import greekfantasy.GFRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class ReedsFeature extends Feature<BlockClusterFeatureConfig> {

    public ReedsFeature(Codec<BlockClusterFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
                         final BlockPos blockPosIn, final BlockClusterFeatureConfig config) {
        // check dimension from config
        if (!SimpleTemplateFeature.isValidDimension(reader)) {
            return false;
        }
        BlockPos pos;
        BlockState state = GFRegistry.REEDS.defaultBlockState();

        pos = reader.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, blockPosIn.offset(rand.nextInt(8) - 4, 0, rand.nextInt(8) - 4));

        int placed = 0;

        BlockPos.Mutable mutpos = new BlockPos.Mutable();
        for (int i = 0, l = config.tries; i < l; i++) {
            // get random position nearby
            mutpos.setWithOffset(pos, rand.nextInt(config.xspread + 1) - rand.nextInt(config.xspread + 1), rand.nextInt(config.yspread + 1) - rand.nextInt(config.yspread + 1), rand.nextInt(config.zspread + 1) - rand.nextInt(config.zspread + 1));
            BlockPos posDown = mutpos.below();
            BlockState stateDown = reader.getBlockState(posDown);
            FluidState fluidState = reader.getFluidState(mutpos);
            // check if the block can be placed here
            if ((reader.isEmptyBlock(mutpos) || fluidState.is(FluidTags.WATER) || (config.canReplace && reader.getBlockState(mutpos).getMaterial().isReplaceable()))
                    && state.canSurvive(reader, mutpos) && (config.whitelist.isEmpty() || config.whitelist.contains(stateDown.getBlock()))
                    && !config.blacklist.contains(stateDown)) {
                // actually place the block
                config.blockPlacer.place(reader, mutpos, state, rand);
                placed++;
            }
        }
        return (placed > 0);
    }

}
