package greekfantasy.structure.feature;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.DryadEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

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
    BlockPos placementPos = blockPosIn;
    if(!config.forcePlacement) {
      // placementPos = placementPos.add(rand.nextInt(12) + 2, 0, rand.nextInt(12) + 2)
      placementPos = getHeightPos(reader, placementPos);
      if(!isDirtOrGrassAt(reader, placementPos.down()) || !isAllReplaceable(reader, placementPos.add(2, 1, 2), 3, 3)) {
        return false;
      }
    }
    
    final BlockPos offset = new BlockPos(-3, 0, -3);
    BlockPos pos = placementPos.add(offset.rotate(rotation));

    // placement settings
    MutableBoundingBox mbb = new MutableBoundingBox(pos.getX() - 8, pos.getY() - 16, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 16, pos.getZ() + 8);
    PlacementSettings placement = new PlacementSettings()
        .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
        .addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
    // actually build using the template
    template.func_237146_a_(reader, pos, pos, placement, rand, 2);
    // percent chance to spawn a dryad
    if(config.forcePlacement && rand.nextInt(100) < GreekFantasy.CONFIG.DRYAD_SPAWN_WEIGHT.get()) {
      addDryad(reader, rand, placementPos.down());
    }
    return true;
  }
  
  protected static void addDryad(final ISeedReader world, final Random rand, final BlockPos pos) {
    // spawn an olive dryad
    final DryadEntity entity = GFRegistry.DRYAD_ENTITY.create(world.getWorld());
    entity.setLocationAndAngles(pos.getX() + rand.nextDouble(), pos.getY() + 0.5D, pos.getZ() + rand.nextDouble(), 0, 0);
    entity.setVariant(DryadEntity.Variant.OLIVE);
    entity.setTreePos(Optional.of(pos));
    entity.setHiding(true);
    world.addEntity(entity);
  }
  
  private static ResourceLocation getRandomTree(final Random rand) {
    final int r = rand.nextInt(3);
    switch(r) {
    case 0: return OLIVE_TREE_0;
    case 1: return OLIVE_TREE_1;
    case 2: default: return OLIVE_TREE_2;
    }
  }
  
  protected static BlockPos getHeightPos(final ISeedReader world, final BlockPos original) {
    int y = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, original).getY();
    return new BlockPos(original.getX(), y, original.getZ());
  }
  
  protected static boolean isAllReplaceable(ISeedReader world, final BlockPos corner, final int width, final int height) {
    for(int i = 0; i < width; i++) {
      for(int j = 0; j < height; j++) {
        for(int k = 0; k < width; k++) {
          if(!isReplaceableAt(world, corner.add(i, j, k))) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  protected static boolean isDirtOrGrassAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.hasBlockState(pos, state -> {
      Block block = state.getBlock();
      return (isDirt(block) || block == Blocks.GRASS_BLOCK);
    });
  }

  protected static boolean isPlantAt(IWorldGenerationReader reader, BlockPos pos) {
    return reader.hasBlockState(pos, state -> {
      Material m = state.getMaterial();
      return (m == Material.TALL_PLANTS || m == Material.PLANTS);
    });
  }
  
  protected static boolean isReplaceableAt(IWorldGenerationReader reader, BlockPos pos) {
    return (isAirAt(reader, pos) || isPlantAt(reader, pos));
  }
  

}
