package greekfantasy.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.AraEntity;
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

public class AraCampFeature extends SimpleTemplateFeature {
  
  private static final ResourceLocation STRUCTURE_TENT_CHEST = new ResourceLocation(GreekFantasy.MODID, "ara_camp/ara_tent_with_chest");
  private static final ResourceLocation STRUCTURE_TENT_NOCHEST = new ResourceLocation(GreekFantasy.MODID, "ara_camp/ara_tent");

  public AraCampFeature(final Codec<NoFeatureConfig> codec) {
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
    int tentsGenerated = 0;
    
    // rotation / mirror
    Rotation rotation = Rotation.getRandom(rand);
    Mirror mirror = Mirror.NONE;

    // position for generation
    BlockPos tentPos = getHeightPos(reader, blockPosIn.offset(4 + rand.nextInt(8), 0, 4 + rand.nextInt(8)));
    
    // placement settings
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // generate the first tent
    if(generateTent(reader, manager.getOrCreate(getStructure(rand)), placement, tentPos, rand)) {
      tentsGenerated++;
    }
    
    // position settings for second tent
    final BlockPos tent2Offset = new BlockPos(-1 - rand.nextInt(4), 0, 7 + rand.nextInt(3)).rotate(rotation);
    tentPos = getHeightPos(reader, tentPos.offset(tent2Offset));
    rotation = rotation.getRotated(Rotation.CLOCKWISE_90);
    
    // generate the second tent
    if(generateTent(reader, manager.getOrCreate(getStructure(rand)), placement.setRotation(rotation), tentPos, rand)) {
      tentsGenerated++;
    }
    
    // DEBUG
//    if(tentsGenerated > 0) GreekFantasy.LOGGER.debug("Generating " + tentsGenerated + " ara tents near " + tentPos);
    
    return tentsGenerated > 0;
  }
  
  protected boolean generateTent(final ISeedReader reader, final Template template, final PlacementSettings placement,
      final BlockPos pos, final Random rand) {
    if(!isValidPosition(reader, pos, template.getSize().below(3), placement.getRotation())) {
      return false;
    }
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getMinBlockX() - 8, pos.getY() - 8, chunkPos.getMinBlockZ() - 8, chunkPos.getMaxBlockX() + 8, pos.getY() + 16, chunkPos.getMaxBlockZ() + 8);
    
    // actually generate the structure
    template.placeInWorld(reader, pos.below(), pos.below(), placement.setBoundingBox(mbb), rand, 2);
    fillBelow(reader, pos.below(2), template.getSize(), placement.getRotation(), new Block[] { Blocks.STONE });
    
    // add entities
    addAra(reader, rand, pos.offset(new BlockPos(3, 1, 2).rotate(placement.getRotation())), 1 + rand.nextInt(3));
    return true;
  }
    
  protected static void addAra(final ISeedReader world, final Random rand, final BlockPos pos, final int count) {
    for(int i = 0; i < count; i++) {
      // spawn an ara
      final AraEntity entity = GFRegistry.ARA_ENTITY.create(world.getLevel());
      entity.moveTo(pos.getX() + rand.nextDouble(), pos.getY() + 0.5D, pos.getZ() + rand.nextDouble(), 0, 0);
      entity.setPersistenceRequired();
      world.addFreshEntity(entity);
    }
  }
  
  protected static boolean canPlaceOnBlock(final ISeedReader world, final BlockPos pos) {
    return pos.getY() > 3 && world.getBlockState(pos).canOcclude() && !world.getBlockState(pos.above(3)).canOcclude();
  }
  
  @Override
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 3 && reader.getBlockState(pos).canOcclude() && isReplaceableAt(reader, pos.above(3));
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return rand.nextInt(100) < 38 ? STRUCTURE_TENT_CHEST : STRUCTURE_TENT_NOCHEST;
  }

}
