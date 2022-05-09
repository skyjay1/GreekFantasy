package greekfantasy.feature;

import com.mojang.serialization.Codec;
import greekfantasy.GFRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;

public class PomegranateTreeFeature extends TreeFeature {

    public PomegranateTreeFeature(final Codec<BaseTreeFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean doPlace(IWorldGenerationReader reader, Random rand, BlockPos blockPosIn, Set<BlockPos> logPositions,
                           Set<BlockPos> foliagePositions, MutableBoundingBox boundingBoxIn, BaseTreeFeatureConfig configIn) {
        // check dimension from config
        if (!SimpleTemplateFeature.isValidDimension((ISeedReader) reader)) {
            return false;
        }
        Optional<BlockPos> p;
        if(configIn.fromSapling) {
            p = Optional.of(blockPosIn);
        } else {
            p = getRandomPositionInChunk((ISeedReader) reader, blockPosIn, new BlockPos(3, 5, 3), 0, rand, Rotation.NONE);
        }
        if (!p.isPresent()) {
            return false;
        }
        int i = configIn.trunkPlacer.getTreeHeight(rand);
        int j = configIn.foliagePlacer.foliageHeight(rand, i, configIn);
        int k = i - j;
        int l = configIn.foliagePlacer.foliageRadius(rand, k);
        BlockPos blockpos = p.get();

        if (blockpos.getY() >= 1 && blockpos.getY() + i + 1 <= 120) {
            OptionalInt optionalint = configIn.minimumSize.minClippedHeight();
            int l1 = this.getMaxFreeTreeHeightAt(reader, i, blockpos, configIn);
            if (l1 >= i || optionalint.isPresent() && l1 >= optionalint.getAsInt()) {
                List<FoliagePlacer.Foliage> list = configIn.trunkPlacer.placeTrunk(reader, rand, l1, blockpos, logPositions,
                        boundingBoxIn, configIn);
                list.forEach((foliage) -> {
                    configIn.foliagePlacer.createFoliage(reader, rand, configIn, l1, foliage, j, l, foliagePositions, boundingBoxIn);
                });
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // copied from TreeFeature
    private int getMaxFreeTreeHeightAt(IWorldGenerationBaseReader reader, int trunkHeight, BlockPos topPosition, BaseTreeFeatureConfig config) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i <= trunkHeight + 1; ++i) {
            int j = config.minimumSize.getSizeAtHeight(trunkHeight, i);

            for (int k = -j; k <= j; ++k) {
                for (int l = -j; l <= j; ++l) {
                    blockpos$mutable.setWithOffset(topPosition, k, i, l);
                    if (!isFree(reader, blockpos$mutable) || !config.ignoreVines && reader.isStateAtPosition(blockpos$mutable, (state) -> state.is(Blocks.VINE))) {
                        return i - 2;
                    }
                }
            }
        }

        return trunkHeight;
    }

    //try to find a valid position in this chunk
    private Optional<BlockPos> getRandomPositionInChunk(final ISeedReader reader, final BlockPos blockPosIn, final BlockPos size,
                                                        final int down, final Random rand, final Rotation r) {
        for (int i = 0; i < 18; i++) {
            BlockPos pos = new BlockPos(
                    blockPosIn.getX() + 4 + rand.nextInt(8),
                    32 + rand.nextInt(120 - 32),
                    blockPosIn.getZ() + 4 + rand.nextInt(8));
            if (isValidPosition(reader, pos)) {
                return Optional.of(pos);
            }
        }
        return Optional.empty();
    }

    protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
        return pos.getY() > 32 && GFRegistry.BlockReg.POMEGRANATE_SAPLING.defaultBlockState().canSurvive(reader, pos);
    }
}
