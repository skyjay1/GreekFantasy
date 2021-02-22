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
  public boolean generate(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // load templates
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    final Template template = manager.getTemplate(getStructure(rand));
    
    // rotation / mirror
    Rotation rotation = Rotation.randomRotation(rand);
    Mirror mirror = Mirror.NONE;

    // position for generation
    final BlockPos offset = new BlockPos(-7, -4 - rand.nextInt(3), -7);
    final BlockPos pos = getHeightPos(reader, blockPosIn).add(offset.rotate(rotation));
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart() - 8, pos.getY() - 16, chunkPos.getZStart() - 8, chunkPos.getXEnd() + 8, pos.getY() + 16, chunkPos.getZEnd() + 8);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // check position
    if(!isValidPosition(reader, pos, template.getSize().down(3), placement.getRotation())) {
      return false;
    }
    
    // actually generate the structure
    template.func_237146_a_(reader, pos, pos, placement, rand, 2);
   
    // DEBUG
//    GreekFantasy.LOGGER.debug("Generating pyton pit near " + pos);
    
    return true;
  }
  
  protected static boolean canPlaceOnBlock(final ISeedReader world, final BlockPos pos) {
    return pos.getY() > 3 && world.getBlockState(pos).isSolid() && !world.getBlockState(pos.up(3)).isSolid();
  }
  
  @Override
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 11 && pos.getY() < 200 && reader.getBlockState(pos).isSolid();
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return STRUCTURE_PIT;
  }

}
