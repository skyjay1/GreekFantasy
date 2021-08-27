package greekfantasy.feature;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
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

public class PomegranateTreeFeature extends TreeFeature {
  
  public PomegranateTreeFeature(final Codec<BaseTreeFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean place(IWorldGenerationReader reader, Random rand, BlockPos blockPosIn, Set<BlockPos> logPositions,
      Set<BlockPos> foliagePositions, MutableBoundingBox boundingBoxIn, BaseTreeFeatureConfig configIn) {
    // check dimension from config
    if (!SimpleTemplateFeature.isValidDimension((ISeedReader) reader)) {
      return false;
    }
    Optional<BlockPos> p = getRandomPositionInChunk((ISeedReader) reader, blockPosIn, new BlockPos(3, 5, 3), 0, rand,
        Rotation.NONE);
    if (!p.isPresent()) {
      return false;
    }
    int i = configIn.trunkPlacer.getHeight(rand);
    int j = configIn.foliagePlacer.func_230374_a_(rand, i, configIn);
    int k = i - j;
    int l = configIn.foliagePlacer.func_230376_a_(rand, k);
    BlockPos blockpos = p.get();

    if (blockpos.getY() >= 1 && blockpos.getY() + i + 1 <= 120) {
      OptionalInt optionalint = configIn.minimumSize.func_236710_c_();
      int l1 = this.getMaxFreeTreeHeightAt(reader, i, blockpos, configIn);
      if (l1 >= i || optionalint.isPresent() && l1 >= optionalint.getAsInt()) {
        List<FoliagePlacer.Foliage> list = configIn.trunkPlacer.getFoliages(reader, rand, l1, blockpos, logPositions,
            boundingBoxIn, configIn);
        list.forEach((foliage) -> {
          configIn.foliagePlacer.func_236752_a_(reader, rand, configIn, l1, foliage, j, l, foliagePositions, boundingBoxIn);
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

    for(int i = 0; i <= trunkHeight + 1; ++i) {
       int j = config.minimumSize.func_230369_a_(trunkHeight, i);

       for(int k = -j; k <= j; ++k) {
          for(int l = -j; l <= j; ++l) {
             blockpos$mutable.setAndOffset(topPosition, k, i, l);
             if (!isLogsAt(reader, blockpos$mutable) || !config.ignoreVines && reader.hasBlockState(blockpos$mutable, (state) -> state.matchesBlock(Blocks.VINE))) {
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
    return pos.getY() > 32 && GFRegistry.POMEGRANATE_SAPLING.getDefaultState().isValidPosition(reader, pos);
  }
}
