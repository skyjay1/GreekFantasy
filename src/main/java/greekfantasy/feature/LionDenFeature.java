package greekfantasy.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GreekFantasy;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

public class LionDenFeature extends SimpleTemplateFeature {
  
  private static final ResourceLocation STRUCTURE_LION_DEN = new ResourceLocation(GreekFantasy.MODID, "lion_den");

  public LionDenFeature(final Codec<NoFeatureConfig> codec) {
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
    
    // rotation / mirror
    Rotation rotation = Rotation.randomRotation(rand);
    Mirror mirror = Mirror.NONE;

    // position for generation
    BlockPos genPos = getHeightPos(reader, blockPosIn.add(4 + rand.nextInt(8), 0, 4 + rand.nextInt(8)));
    
    // placement settings
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
  
    return generateDen(reader, manager.getTemplateDefaulted(getStructure(rand)), placement, genPos, rand);
  }
  
  protected boolean generateDen(final ISeedReader reader, final Template template, final PlacementSettings placement,
      final BlockPos pos, final Random rand) {
    if(!isValidPosition(reader, pos, template.getSize().down(4), placement.getRotation())) {
      return false;
    }
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart() - 16, pos.getY() - 8, chunkPos.getZStart() - 16, chunkPos.getXEnd() + 16, pos.getY() + 16, chunkPos.getZEnd() + 16);
    
    // actually generate the structure
    template.func_237146_a_(reader, pos.down(), pos.down(), placement.setBoundingBox(mbb), rand, 2);
    fillBelow(reader, pos.down(3), template.getSize(), placement.getRotation(), new Block[] { Blocks.SANDSTONE });
    return true;
  }
  
  protected static boolean canPlaceOnBlock(final ISeedReader world, final BlockPos pos) {
    return pos.getY() > 4 && world.getBlockState(pos).isSolid() && !world.getBlockState(pos.up(4)).isSolid();
  }
  
  @Override
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 4 && reader.getBlockState(pos).isSolid() && isReplaceableAt(reader, pos.up(4));
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return STRUCTURE_LION_DEN;
  }

}
