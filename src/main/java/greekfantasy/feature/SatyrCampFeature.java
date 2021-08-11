package greekfantasy.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.SatyrEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SatyrCampFeature extends SimpleTemplateFeature {
  
  private static final ResourceLocation STRUCTURE_CAMPFIRE = new ResourceLocation(GreekFantasy.MODID, "satyr_camp/satyr_campfire");
  private static final ResourceLocation STRUCTURE_TENT_CHEST = new ResourceLocation(GreekFantasy.MODID, "satyr_camp/satyr_tent_with_chest");
  private static final ResourceLocation STRUCTURE_TENT_NOCHEST = new ResourceLocation(GreekFantasy.MODID, "satyr_camp/satyr_tent");
  
  public SatyrCampFeature(final Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean generate(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final NoFeatureConfig config) {
    // load templates
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
  
    // position for generation
    final BlockPos tent1Offset = new BlockPos(4 + rand.nextInt(4), 0, rand.nextInt(4) - 2);
    final BlockPos tent2Offset = new BlockPos(2 + rand.nextInt(4), 0, 5 + rand.nextInt(3));
    final BlockPos tent3Offset = new BlockPos(2 + rand.nextInt(4), 0, 5 + rand.nextInt(3));
    final BlockPos tent4Offset = new BlockPos(2 + rand.nextInt(4), 0, 5 + rand.nextInt(3));
    
    // placement settings
    Rotation rotation = Rotation.randomRotation(rand);
    Mirror mirror = Mirror.NONE;
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
    
    // determine satyr colors
    SatyrEntity.GroupData data = new SatyrEntity.GroupData(Util.getRandomObject(CoatColors.values(), rand));
    
    // generate the campfire
    BlockPos campfirePos = getHeightPos(reader, blockPosIn.add(4 + rand.nextInt(8), 0, 4 + rand.nextInt(8)));
    if(generateTemplate(reader, manager.getTemplateDefaulted(STRUCTURE_CAMPFIRE), placement, campfirePos, rand, false, data, 0)) {
      // add 1-2 satyrs
      addSatyr(reader, rand, campfirePos.up(), data, 1 + rand.nextInt(2));
      // generate the first tent
      int tentsGenerated = 0;
      BlockPos tentPos = getHeightPos(reader, campfirePos.add(tent1Offset.rotate(rotation)));
      if(generateTemplate(reader, manager.getTemplateDefaulted(getStructure(rand)), placement, tentPos, rand, true, data, tentsGenerated)) {
        tentsGenerated++;
      }
      
      // generate the second tent
      tentPos = getHeightPos(reader, campfirePos.add(tent2Offset.rotate(rotation)));
      rotation = rotation.add(Rotation.CLOCKWISE_90);
      if(generateTemplate(reader, manager.getTemplateDefaulted(getStructure(rand)), placement.setRotation(rotation), tentPos, rand, true, data, tentsGenerated)) {
        tentsGenerated++;
      }
      
      // generate the third tent
      tentPos = getHeightPos(reader, campfirePos.add(tent3Offset.rotate(rotation)));
      rotation = rotation.add(Rotation.CLOCKWISE_90);
      if(generateTemplate(reader, manager.getTemplateDefaulted(getStructure(rand)), placement.setRotation(rotation), tentPos, rand, true, data, tentsGenerated)) {
        tentsGenerated++;
      }
      
      // generate the fourth tent
      tentPos = getHeightPos(reader, campfirePos.add(tent4Offset.rotate(rotation)));
      rotation = rotation.add(Rotation.CLOCKWISE_90);
      if(generateTemplate(reader, manager.getTemplateDefaulted(getStructure(rand)), placement.setRotation(rotation), tentPos, rand, true, data, tentsGenerated)) {
        tentsGenerated++;
      }
      
      // finished
      return true;
    }
    return false;
  }
 
  protected boolean generateTemplate(final ISeedReader reader, final Template template, final PlacementSettings placement,
      final BlockPos pos, final Random rand, final boolean satyrs, final ILivingEntityData data, final int tentsGenerated) {
    if((tentsGenerated > 2 || !isValidPosition(reader, pos, template.getSize(), placement.getRotation()))) {
      return false;
    }
    // placement settings
    MutableBoundingBox mbb = new MutableBoundingBox(pos.getX() - 12, pos.getY() - 16, pos.getZ() - 12, pos.getX() + 12, pos.getY() + 16, pos.getZ() + 12);
    
    // actually generate the structure
    template.func_237146_a_(reader, pos, pos, placement.setBoundingBox(mbb), rand, 2);
    fillBelow(reader, pos.down(), template.getSize(), placement.getRotation(), new Block[] { Blocks.DIRT });
    
    // add entities
    if(satyrs) {
      addSatyr(reader, rand, pos.add(new BlockPos(3, 1, 2).rotate(placement.getRotation())), data, 1 + rand.nextInt(2));
    }
    return true;
  }

  protected static void addSatyr(final ISeedReader world, final Random rand, final BlockPos pos, final ILivingEntityData data, final int count) {
    for(int i = 0; i < count; i++) {
      // spawn a satyr
      final SatyrEntity entity = GFRegistry.SATYR_ENTITY.create(world.getWorld());
      entity.setLocationAndAngles(pos.getX() + rand.nextDouble(), pos.getY() + 0.5D, pos.getZ() + rand.nextDouble(), 0, 0);
      entity.enablePersistence();
      // random shaman chance
      if(rand.nextInt(100) < GreekFantasy.CONFIG.getSatyrShamanChance()) {
        entity.setShaman(true);
      }
      // coat colors
      entity.setCoatColor(((SatyrEntity.GroupData)data).variant);
      world.addEntity(entity);
    }
  }
  
  @Override
  protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
    return pos.getY() > 3 && reader.getBlockState(pos).isSolid() && isReplaceableAt(reader, pos.up(3));
  }

  @Override
  protected ResourceLocation getStructure(Random rand) {
    return rand.nextBoolean() ? STRUCTURE_TENT_CHEST : STRUCTURE_TENT_NOCHEST;
  }
}
