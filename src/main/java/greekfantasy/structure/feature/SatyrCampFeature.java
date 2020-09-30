package greekfantasy.structure.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.SatyrEntity;
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

public class SatyrCampFeature extends Feature<NoFeatureConfig> {
  
  private static final ResourceLocation STRUCTURE_CAMPFIRE = new ResourceLocation(GreekFantasy.MODID, "satyr_camp/satyr_campfire");
  private static final ResourceLocation STRUCTURE_TENT_CHEST = new ResourceLocation(GreekFantasy.MODID, "satyr_camp/satyr_tent_with_chest");
  private static final ResourceLocation STRUCTURE_TENT_NOCHEST = new ResourceLocation(GreekFantasy.MODID, "satyr_camp/satyr_tent");
  
  public SatyrCampFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean func_241855_a(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // DEBUG
    GreekFantasy.LOGGER.debug("Generating satyr tents near " + blockPosIn);
    // load templates
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    final Template templateFire = manager.getTemplateDefaulted(STRUCTURE_CAMPFIRE);
    final Template templateChest = manager.getTemplateDefaulted(STRUCTURE_TENT_CHEST);
    final Template templateNoChest = manager.getTemplateDefaulted(STRUCTURE_TENT_NOCHEST);
  
    // position for generation
    final BlockPos tent1Offset = new BlockPos(8 + rand.nextInt(4), 0, rand.nextInt(4) - 2);
    final BlockPos tent2Offset = new BlockPos(2 + rand.nextInt(4), 0, 7 + rand.nextInt(3));
    final BlockPos tent3Offset = new BlockPos(2 + rand.nextInt(4), 0, 7 + rand.nextInt(3));
    final BlockPos tent4Offset = new BlockPos(2 + rand.nextInt(4), 0, 7 + rand.nextInt(3));
    
    // placement settings
    Rotation rotation = Rotation.randomRotation(rand);
    Mirror mirror = Mirror.NONE;
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // generate the campfire
    BlockPos campfirePos = getHeightPos(reader, blockPosIn.add(4 + rand.nextInt(8), 0, 4 + rand.nextInt(8)));
    if(generateTemplate(reader, templateFire, placement, campfirePos, rand, false, 0)) {
      // generate the first tent
      int tentsGenerated = 0;
      BlockPos tentPos = getHeightPos(reader, campfirePos.add(tent1Offset.rotate(rotation)));
      //rotation = rotation.add(Rotation.CLOCKWISE_90);
      if(generateTemplate(reader, templateChest, placement, tentPos, rand, true, tentsGenerated)) {
        tentsGenerated++;
      }
      
      // generate the second tent
      tentPos = getHeightPos(reader, campfirePos.add(tent2Offset.rotate(rotation)));
      rotation = rotation.add(Rotation.CLOCKWISE_90);
      if(generateTemplate(reader, templateNoChest, placement.setRotation(rotation), tentPos, rand, true, tentsGenerated)) {
        tentsGenerated++;
      }
      
      // generate the third tent
      tentPos = getHeightPos(reader, campfirePos.add(tent3Offset.rotate(rotation)));
      rotation = rotation.add(Rotation.CLOCKWISE_90);
      if(generateTemplate(reader, templateChest, placement.setRotation(rotation), tentPos, rand, true, tentsGenerated)) {
        tentsGenerated++;
      }
      
      // generate the fourth tent
      tentPos = getHeightPos(reader, campfirePos.add(tent4Offset.rotate(rotation)));
      rotation = rotation.add(Rotation.CLOCKWISE_90);
      if(generateTemplate(reader, templateNoChest, placement.setRotation(rotation), tentPos, rand, true, tentsGenerated)) {
        tentsGenerated++;
      } 
    }
    
    return true;
  }
 
  protected boolean generateTemplate(final ISeedReader reader, final Template template, final PlacementSettings placement,
      final BlockPos pos, final Random rand, final boolean satyrs, final int tentsGenerated) {
    if(tentsGenerated > 2 || pos.getY() < 3 || !reader.getBlockState(pos).isSolid() || reader.getBlockState(pos.up(3)).isSolid()) {
      return false;
    }
    
    // placement settings
    ChunkPos chunkPos = new ChunkPos(pos);
    MutableBoundingBox mbb = new MutableBoundingBox(chunkPos.getXStart() - 8, pos.getY() - 8, chunkPos.getZStart() - 8, chunkPos.getXEnd() + 8, pos.getY() + 16, chunkPos.getZEnd() + 8);
    
    // actually generate the structure
    template.func_237146_a_(reader, pos, pos, placement.setBoundingBox(mbb), rand, 0);
    
    // add entities
    if(satyrs) {
      addSatyr(reader, rand, pos.add(new BlockPos(3, 2, 2).rotate(placement.getRotation())), 1 + rand.nextInt(2));
    }
    return true;
  }

  protected static BlockPos getHeightPos(final ISeedReader world, final BlockPos original) {
    int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, original).getY();
    final BlockPos pos = new BlockPos(original.getX(), y, original.getZ());
    return world.getBlockState(pos).isIn(Blocks.SNOW) ? pos.down(2) : pos.down(1);
  }

  protected static void addSatyr(final ISeedReader world, final Random rand, final BlockPos pos, final int count) {
    for(int i = 0; i < count; i++) {
      // spawn a satyr
      final SatyrEntity entity = GFRegistry.SATYR_ENTITY.create(world.getWorld());
      entity.setLocationAndAngles(pos.getX() + rand.nextDouble(), pos.getY() + 0.5D, pos.getZ() + rand.nextDouble(), 0, 0);
      entity.enablePersistence();
      // random shaman chance
      if(rand.nextInt(100) < GreekFantasy.CONFIG.getSatyrShamanChance()) {
        entity.setShaman(true);
      }
      world.addEntity(entity);
    }
  }

}
