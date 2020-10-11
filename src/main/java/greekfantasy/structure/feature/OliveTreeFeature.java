package greekfantasy.structure.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.IPlantable;

public class OliveTreeFeature extends Feature<BaseTreeFeatureConfig> {
  
  private static final ResourceLocation OLIVE_TREE_0 = new ResourceLocation(GreekFantasy.MODID, "olive_tree/olive_tree_0");
  private static final ResourceLocation OLIVE_TREE_1 = new ResourceLocation(GreekFantasy.MODID, "olive_tree/olive_tree_1");
  private static final ResourceLocation OLIVE_TREE_2 = new ResourceLocation(GreekFantasy.MODID, "olive_tree/olive_tree_2");

  public OliveTreeFeature(final Codec<BaseTreeFeatureConfig> codec) {
    super(codec);
  }

  @Override
  public boolean func_241855_a(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
      final BlockPos blockPosIn, final BaseTreeFeatureConfig config) {    
    // rotation / mirror
    Mirror mirror = Mirror.NONE;
    Rotation rotation = Rotation.randomRotation(rand);
    
    // template for tree
    final TemplateManager manager = reader.getWorld().getStructureTemplateManager();
    final Template template = manager.getTemplateDefaulted(getRandomTree(rand));
    
    // position for tree
    final BlockPos offset = new BlockPos(-Math.ceil((template.getSize().getX() / 2.0F) + 0.5F), 0, -Math.floor(template.getSize().getZ() / 2.0F));
    final BlockPos pos = blockPosIn.add(offset.rotate(rotation));
    
    // conditions for generation
    if (!config.forcePlacement && !reader.getBlockState(pos).canSustainPlant(reader, pos, Direction.UP, (IPlantable)GFRegistry.OLIVE_SAPLING)) {
      return false;
    }   
    
    // placement settings
    MutableBoundingBox mbb = new MutableBoundingBox(pos.getX() - 8, pos.getY() - 16, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 16, pos.getZ() + 8);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);

    return template.func_237146_a_(reader, pos, pos, placement, rand, 2);
  }
  
  private static ResourceLocation getRandomTree(final Random rand) {
    final int r = rand.nextInt(3);
    switch(r) {
    case 0: return OLIVE_TREE_0;
    case 1: return OLIVE_TREE_1;
    case 2: default: return OLIVE_TREE_2;
    }
  }

}
