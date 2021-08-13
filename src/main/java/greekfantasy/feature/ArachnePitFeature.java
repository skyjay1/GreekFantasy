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

public class ArachnePitFeature extends SimpleTemplateFeature {
  
  private static final ResourceLocation STRUCTURE_PIT = new ResourceLocation(GreekFantasy.MODID, "arachne_pit");

  public ArachnePitFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean generate(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // check dimension from config
    if(!SimpleTemplateFeature.isValidDimension(reader)) {
      return false;
    }
    // load templates
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    final Template template = manager.getTemplate(getStructure(rand));
    
    // rotation / mirror
    Rotation rotation = Rotation.randomRotation(rand);
    Mirror mirror = Mirror.NONE;

    // position for generation
    final BlockPos offset = new BlockPos(-9, 0, -9);
    final BlockPos pos = new BlockPos(blockPosIn.getX(), 8 + rand.nextInt(92), blockPosIn.getZ()).add(offset.rotate(rotation));
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart() - 10, pos.getY() - 16, chunkPos.getZStart() - 10, chunkPos.getXEnd() + 10, pos.getY() + 16, chunkPos.getZEnd() + 10);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb);
    
    // check position
    if(!isValidPosition(reader, pos, template.getSize(), placement.getRotation())) {
      return false;
    }
    
    // actually generate the structure
    template.func_237146_a_(reader, pos, pos, placement, rand, 2);
   
    // DEBUG
    // GreekFantasy.LOGGER.debug("Generating arachne pit near " + pos);
    
    return true;
  }
  
  protected static boolean canPlaceOnBlock(final ISeedReader world, final BlockPos pos) {
    return pos.getY() > 3 && world.getBlockState(pos).isSolid() && !world.getBlockState(pos.up(3)).isSolid();
  }
  
  @Override
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 7 && pos.getY() < 100 && reader.getBlockState(pos).isSolid();
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return STRUCTURE_PIT;
  }

}
