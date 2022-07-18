package greekfantasy.worldgen;

import greekfantasy.GFRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Random;

public class GoldenTreeGrower extends AbstractTreeGrower {
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random random, boolean hasFlowers) {
        return GFRegistry.FeatureReg.GOLDEN_TREE;
    }
}