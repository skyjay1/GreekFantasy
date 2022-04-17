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
    final BlockPos offset = new BlockPos(-(template.getSize().getX() / 2), -1, -(template.getSize().getZ() / 2));
    final BlockPos heightPos = getHeightPos(reader, blockPosIn.offset(offset.rotate(rotation)));
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(heightPos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getMinBlockX() - 16, heightPos.getY() - 16, chunkPos.getMinBlockZ() - 16, chunkPos.getMaxBlockX() + 16, heightPos.getY() + 16, chunkPos.getMaxBlockZ() + 16);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // check position
    if(!isValidPosition(reader, heightPos, template.getSize(), rotation)) {
      return false;
    }
    
    // actually generate the structure
    template.placeInWorld(reader, heightPos, heightPos, placement, rand, 2);
    fillBelow(reader, heightPos.below(), template.getSize(), rotation, new Block[] { Blocks.DIRT });
    
    // spawn a cyclopes
    final BlockPos entityPos = heightPos.subtract((offset.offset(2, 0, 2).rotate(rotation))).above();
    final CyclopesEntity entity = GFRegistry.CYCLOPES_ENTITY.create(reader.getLevel());
    entity.moveTo(entityPos.getX() + rand.nextDouble(), entityPos.getY() + 0.5D, entityPos.getZ() + rand.nextDouble(), 0, 0);
    entity.setPersistenceRequired();
    reader.addFreshEntity(entity);
    
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
    return STRUCTURE_CAVE;
  }

}
