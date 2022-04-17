package greekfantasy.feature;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GreekFantasy;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SmallNetherShrineFeature extends SimpleTemplateFeature {
  
  private static final ResourceLocation STRUCTURE = new ResourceLocation(GreekFantasy.MODID, "small_nether_shrine");

  public SmallNetherShrineFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean place(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // check dimension from config
    if(!SimpleTemplateFeature.isValidDimension(reader)) {
      return false;
    }
    // template loading
    final TemplateManager manager = reader.getLevel().getStructureManager();
    final Template template = manager.getOrCreate(STRUCTURE);
    
    // rotation / mirror
    Mirror mirror = Mirror.NONE;
    Rotation rotation = Rotation.getRandom(rand);
    
    // position for generation
    Optional<BlockPos> optionalPos = getRandomPositionInChunk(reader, blockPosIn, template.getSize(), 2, rand, rotation);
    
    // check for valid position
    if(!optionalPos.isPresent()) {
      return false;
    }
    final BlockPos pos = optionalPos.get();
    
    // placement settings
    MutableBoundingBox mbb = new MutableBoundingBox(pos.getX() - 8, pos.getY() - 16, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 16, pos.getZ() + 8);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);

    // actually generate the structure
    if(template.placeInWorld(reader, pos, pos, placement, rand, 2)) {
      return true;
    }
    return false;
  }
  
  // try to find a valid position in this chunk
  private Optional<BlockPos> getRandomPositionInChunk(final ISeedReader reader, final BlockPos blockPosIn, 
      final BlockPos size, final int down, final Random rand, final Rotation r) {
    for(int i = 0; i < 8; i++) {
      BlockPos pos = new BlockPos(
        blockPosIn.getX() + 4 + rand.nextInt(8), 
        32 + rand.nextInt(120 - 32), 
        blockPosIn.getZ() + 4 + rand.nextInt(8));
      if(isValidPosition(reader, pos, size, r)) {
        return Optional.of(pos);
      }
    }
    return Optional.empty();
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return STRUCTURE;
  }

}
