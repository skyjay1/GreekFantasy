package greekfantasy.feature;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public class PomegranateTreeFeature extends TreeFeature {
  
  public PomegranateTreeFeature(final Codec<BaseTreeFeatureConfig> codec) {
    super(codec);
  }
  
  @Override
  public boolean place(IWorldGenerationReader reader, Random rand, BlockPos blockPosIn, Set<BlockPos> set1, Set<BlockPos> set2, MutableBoundingBox mbb, BaseTreeFeatureConfig config) {
    // check dimension from config
    if(!SimpleTemplateFeature.isValidDimension((ISeedReader)reader)) {
      return false;
    }
    Optional<BlockPos> blockPos = getRandomPositionInChunk((ISeedReader)reader, blockPosIn, new BlockPos(3, 5, 3), 0, rand, Rotation.NONE);
    if(!blockPos.isPresent()) {
      return false;
    }
    return super.place(reader, rand, blockPos.get(), set1, set2, mbb, config);
  }

  //try to find a valid position in this chunk
  private Optional<BlockPos> getRandomPositionInChunk(final ISeedReader reader, final BlockPos blockPosIn, final BlockPos size,
      final int down, final Random rand, final Rotation r) {
    for (int i = 0; i < 8; i++) {
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
    return pos.getY() > 3 && reader.getBlockState(pos).isSolid() && isReplaceableAt(reader, pos.up(1));
  }
}
