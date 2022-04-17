package greekfantasy.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GreekFantasy;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class PythonPitFeature extends SimpleTemplateFeature {
  
  private static final ResourceLocation STRUCTURE_PIT = new ResourceLocation(GreekFantasy.MODID, "python_pit");

  public PythonPitFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean place(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // check dimension from config
    if(!SimpleTemplateFeature.isValidDimension(reader)) {
      return false;
    }
    // load templates
    final TemplateManager manager = reader.getLevel().getStructureManager();
    final Template template = manager.get(getStructure(rand));
    
    // rotation / mirror
    Rotation rotation = Rotation.getRandom(rand);
    Mirror mirror = Mirror.NONE;

    // position for generation
    final BlockPos offset = new BlockPos(-7, -4 - rand.nextInt(3), -7);
    final BlockPos pos = getHeightPos(reader, blockPosIn).offset(offset.rotate(rotation));
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getMinBlockX() - 8, pos.getY() - 16, chunkPos.getMinBlockZ() - 8, chunkPos.getMaxBlockX() + 8, pos.getY() + 16, chunkPos.getMaxBlockZ() + 8);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // check position
    if(!isValidPosition(reader, pos, template.getSize().below(3), placement.getRotation())) {
      return false;
    }
    
    // actually generate the structure
    template.placeInWorld(reader, pos, pos, placement, rand, 2);
   
    // DEBUG
//    GreekFantasy.LOGGER.debug("Generating pyton pit near " + pos);
    
    return true;
  }
  
  protected static boolean canPlaceOnBlock(final ISeedReader world, final BlockPos pos) {
    return pos.getY() > 3 && world.getBlockState(pos).canOcclude() && !world.getBlockState(pos.above(3)).canOcclude();
  }
  
  @Override
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 11 && pos.getY() < 200 && reader.getBlockState(pos).canOcclude();
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return STRUCTURE_PIT;
  }

}
