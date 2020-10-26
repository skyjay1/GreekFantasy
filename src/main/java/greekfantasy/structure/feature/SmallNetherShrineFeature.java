package greekfantasy.structure.feature;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SmallNetherShrineFeature extends Feature<NoFeatureConfig> {
  
  private static final ResourceLocation STRUCTURE = new ResourceLocation(GreekFantasy.MODID, "small_nether_shrine");

  public SmallNetherShrineFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean func_241855_a(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // template loading
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    final Template template = manager.getTemplateDefaulted(STRUCTURE);
    
    // position for generation
    Optional<BlockPos> optionalPos = getRandomPositionInChunk(reader, blockPosIn, rand);
    
    // check for valid position
    if(!optionalPos.isPresent()) {
      return false;
    }
    final BlockPos pos = optionalPos.get();
    
    // rotation / mirror
    Mirror mirror = Mirror.NONE;
    Rotation rotation = Rotation.randomRotation(rand);
    
    // placement settings
    MutableBoundingBox mbb = new MutableBoundingBox(pos.getX() - 8, pos.getY() - 16, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 16, pos.getZ() + 8);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // DEBUG
    GreekFantasy.LOGGER.debug("Generating nether shrine near " + pos);
    // actually generate the structure
    return template.func_237146_a_(reader, pos, pos, placement, rand, 2);
  }
  
  // try to find a valid position in this chunk
  private Optional<BlockPos> getRandomPositionInChunk(final ISeedReader reader, final BlockPos blockPosIn, final Random rand) {
    for(int i = 0; i < 20; i++) {
      BlockPos pos = new BlockPos(
        blockPosIn.getX() + 4 + rand.nextInt(8), 
        32 + rand.nextInt(120 - 32), 
        blockPosIn.getZ() + 4 + rand.nextInt(8));
      final BlockState state = reader.getBlockState(pos);
      if(state.isSolid() && state.getMaterial() != Material.PLANTS && !reader.getBlockState(pos.up(1)).isSolid()) {
        return Optional.of(pos);
      }
    }
    return Optional.empty();
  }

}
