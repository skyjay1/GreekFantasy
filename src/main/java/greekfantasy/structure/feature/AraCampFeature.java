package greekfantasy.structure.feature;

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
  public boolean generate(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // load templates
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    int tentsGenerated = 0;
    
    // rotation / mirror
    Rotation rotation = Rotation.randomRotation(rand);
    Mirror mirror = Mirror.NONE;

    // position for generation
    BlockPos tentPos = getHeightPos(reader, blockPosIn.add(4 + rand.nextInt(8), 0, 4 + rand.nextInt(8)));
    
    // placement settings
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // generate the first tent
    if(generateTent(reader, manager.getTemplateDefaulted(getStructure(rand)), placement, tentPos, rand)) {
      tentsGenerated++;
    }
    
    // position settings for second tent
    final BlockPos tent2Offset = new BlockPos(-1 - rand.nextInt(4), 0, 7 + rand.nextInt(3)).rotate(rotation);
    tentPos = getHeightPos(reader, tentPos.add(tent2Offset));
    rotation = rotation.add(Rotation.CLOCKWISE_90);
    
    // generate the second tent
    if(generateTent(reader, manager.getTemplateDefaulted(getStructure(rand)), placement.setRotation(rotation), tentPos, rand)) {
      tentsGenerated++;
    }
    
    // DEBUG
//    if(tentsGenerated > 0) GreekFantasy.LOGGER.debug("Generating " + tentsGenerated + " ara tents near " + tentPos);
    
    return tentsGenerated > 0;
  }
  
  protected boolean generateTent(final ISeedReader reader, final Template template, final PlacementSettings placement,
      final BlockPos pos, final Random rand) {
    if(!isValidPosition(reader, pos, template.getSize().down(3), placement.getRotation())) {
      return false;
    }
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart() - 8, pos.getY() - 8, chunkPos.getZStart() - 8, chunkPos.getXEnd() + 8, pos.getY() + 16, chunkPos.getZEnd() + 8);
    
    // actually generate the structure
    template.func_237146_a_(reader, pos.down(), pos.down(), placement.setBoundingBox(mbb), rand, 2);
    fillBelow(reader, pos.down(2), template.getSize(), placement.getRotation(), new Block[] { Blocks.STONE });
    
    // add entities
    addAra(reader, rand, pos.add(new BlockPos(3, 1, 2).rotate(placement.getRotation())), 1 + rand.nextInt(3));
    return true;
  }
    
  protected static void addAra(final ISeedReader world, final Random rand, final BlockPos pos, final int count) {
    for(int i = 0; i < count; i++) {
      // spawn an ara
      final AraEntity entity = GFRegistry.ARA_ENTITY.create(world.getWorld());
      entity.setLocationAndAngles(pos.getX() + rand.nextDouble(), pos.getY() + 0.5D, pos.getZ() + rand.nextDouble(), 0, 0);
      entity.enablePersistence();
      world.addEntity(entity);
    }
  }
  
  protected static boolean canPlaceOnBlock(final ISeedReader world, final BlockPos pos) {
    return pos.getY() > 3 && world.getBlockState(pos).isSolid() && !world.getBlockState(pos.up(3)).isSolid();
  }
  
  @Override
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 3 && reader.getBlockState(pos).isSolid() && isReplaceableAt(reader, pos.up(3));
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return rand.nextInt(100) < 38 ? STRUCTURE_TENT_CHEST : STRUCTURE_TENT_NOCHEST;
  }

}
