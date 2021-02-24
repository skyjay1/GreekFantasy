package greekfantasy.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.CyclopesEntity;
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

public class CyclopesCaveFeature extends SimpleTemplateFeature {
  
  private static final ResourceLocation STRUCTURE_CAVE = new ResourceLocation(GreekFantasy.MODID, "cyclopes_cave");

  public CyclopesCaveFeature(final Codec<NoFeatureConfig> codec) {
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
    final BlockPos offset = new BlockPos(-(template.getSize().getX() / 2), -1, -(template.getSize().getZ() / 2));
    final BlockPos heightPos = getHeightPos(reader, blockPosIn.add(offset.rotate(rotation)));
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(heightPos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart() - 16, heightPos.getY() - 16, chunkPos.getZStart() - 16, chunkPos.getXEnd() + 16, heightPos.getY() + 16, chunkPos.getZEnd() + 16);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // check position
    if(!isValidPosition(reader, heightPos, template.getSize(), rotation)) {
      return false;
    }
    
    // actually generate the structure
    template.func_237146_a_(reader, heightPos, heightPos, placement, rand, 2);
    fillBelow(reader, heightPos.down(), template.getSize(), rotation, new Block[] { Blocks.DIRT });
    
    // spawn a cyclopes
    final BlockPos entityPos = heightPos.subtract((offset.add(2, 0, 2).rotate(rotation))).up();
    final CyclopesEntity entity = GFRegistry.CYCLOPES_ENTITY.create(reader.getWorld());
    entity.setLocationAndAngles(entityPos.getX() + rand.nextDouble(), entityPos.getY() + 0.5D, entityPos.getZ() + rand.nextDouble(), 0, 0);
    entity.enablePersistence();
    reader.addEntity(entity);
   
    // DEBUG
    GreekFantasy.LOGGER.debug("Generating cyclopes cave near " + heightPos);
    
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
    return STRUCTURE_CAVE;
  }

}
