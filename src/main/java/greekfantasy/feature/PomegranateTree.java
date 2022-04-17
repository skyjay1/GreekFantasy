package greekfantasy.feature;

import java.util.OptionalInt;
import java.util.Random;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldGen;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.foliageplacer.FancyFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.FancyTrunkPlacer;

public class PomegranateTree extends Tree {
  
  private static ConfiguredFeature<BaseTreeFeatureConfig, ?> POMEGRANATE_TREE_CONFIGURATION;

  @Override
  protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(final Random rand, final boolean hasNearbyFlora) {
    if(POMEGRANATE_TREE_CONFIGURATION == null) {
      POMEGRANATE_TREE_CONFIGURATION = getConfiguredTree();
    }
    return POMEGRANATE_TREE_CONFIGURATION;
  }
  
  public static ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredTree() {
    return GFWorldGen.POMEGRANATE_TREE_FEATURE.configured(new BaseTreeFeatureConfig.Builder(
        new SimpleBlockStateProvider(GFRegistry.POMEGRANATE_LOG.defaultBlockState()), 
        new SimpleBlockStateProvider(GFRegistry.POMEGRANATE_LEAVES.defaultBlockState()), 
        new FancyFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(4), 4), 
        new FancyTrunkPlacer(3, 11, 0), new TwoLayerFeature(0, 0, 0, OptionalInt.of(4)))
        .ignoreVines().heightmap(Heightmap.Type.MOTION_BLOCKING).build());
  }

}
