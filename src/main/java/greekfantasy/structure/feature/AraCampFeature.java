package greekfantasy.structure.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.AraEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class AraCampFeature extends Feature<NoFeatureConfig> {
  
  private static final ResourceLocation STRUCTURE_TENT_CHEST = new ResourceLocation(GreekFantasy.MODID, "ara_camp/ara_tent_with_chest");
  private static final ResourceLocation STRUCTURE_TENT_NOCHEST = new ResourceLocation(GreekFantasy.MODID, "ara_camp/ara_tent");

  public AraCampFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean generate(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // DEBUG
    GreekFantasy.LOGGER.debug("Generating ara tents near " + blockPosIn);
    // load templates
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    final Template templateChest = manager.getTemplateDefaulted(STRUCTURE_TENT_CHEST);
    final Template templateNoChest = manager.getTemplateDefaulted(STRUCTURE_TENT_NOCHEST);
    
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
    generateTent(reader, templateChest, placement, tentPos, rand);
    
    // position settings for second tent
    final BlockPos tent2Offset = new BlockPos(-1 - rand.nextInt(4), 0, 7 + rand.nextInt(3)).rotate(rotation);
    tentPos = getHeightPos(reader, tentPos.add(tent2Offset));
    rotation = rotation.add(Rotation.CLOCKWISE_90);
    
    // generate the second tent
    generateTent(reader, templateNoChest, placement.setRotation(rotation), tentPos, rand);
    
    return true;
  }
  
  protected static BlockPos getHeightPos(final ISeedReader world, final BlockPos original) {
    int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, original).getY();
    final BlockPos pos = new BlockPos(original.getX(), y, original.getZ());
    return world.getBlockState(pos).isIn(Blocks.SNOW) ? pos.down(3) : pos.down(2);
  }
  
  protected static boolean generateTent(final ISeedReader reader, final Template template, final PlacementSettings placement,
      final BlockPos pos, final Random rand) {
    final BlockPos offset = new BlockPos(-template.getSize().getX(), 0, -template.getSize().getZ());
    if(!canPlaceOnBlock(reader, pos) || !canPlaceOnBlock(reader, pos.add(offset.rotate(placement.getRotation())))) {
      return false;
    }
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart() - 8, pos.getY() - 8, chunkPos.getZStart() - 8, chunkPos.getXEnd() + 8, pos.getY() + 16, chunkPos.getZEnd() + 8);
    
    // actually generate the structure
    template.func_237146_a_(reader, pos, pos, placement.setBoundingBox(mbb), rand, 2);
    
    // add entities
    addAra(reader, rand, pos.add(new BlockPos(3, 2, 2).rotate(placement.getRotation())), 1 + rand.nextInt(3));
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

}
