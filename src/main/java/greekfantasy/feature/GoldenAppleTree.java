package greekfantasy.feature;

import java.util.OptionalInt;
import java.util.Random;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldGen;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.foliageplacer.FancyFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.FancyTrunkPlacer;

public class GoldenAppleTree extends Tree {
  
  private static ConfiguredFeature<BaseTreeFeatureConfig, ?> GOLDEN_APPLE_TREE_CONFIGURATION;

  @Override
  protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(final Random rand, final boolean hasNearbyFlora) {
    if(GOLDEN_APPLE_TREE_CONFIGURATION == null) {
      GOLDEN_APPLE_TREE_CONFIGURATION = getConfiguredTree();
    }
    return GOLDEN_APPLE_TREE_CONFIGURATION;
  }
  
  public static ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredTree() {
    return GFWorldGen.GOLDEN_APPLE_TREE_FEATURE.withConfiguration(new BaseTreeFeatureConfig.Builder(
        new SimpleBlockStateProvider(Blocks.OAK_LOG.getDefaultState()), 
        new SimpleBlockStateProvider(GFRegistry.GOLDEN_APPLE_LEAVES.getDefaultState()), 
        new FancyFoliagePlacer(FeatureSpread.create(2), FeatureSpread.create(4), 4), 
        new FancyTrunkPlacer(4, 6, 0), new TwoLayerFeature(0, 0, 0, OptionalInt.of(4)))
        .setIgnoreVines().setHeightmap(Heightmap.Type.MOTION_BLOCKING).build());
  }

}
