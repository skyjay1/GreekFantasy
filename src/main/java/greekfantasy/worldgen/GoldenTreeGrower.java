package greekfantasy.worldgen;

import greekfantasy.GreekFantasy;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
public class GoldenTreeGrower extends AbstractTreeGrower {

    private static final ResourceKey<ConfiguredFeature<?, ?>> KEY = ResourceKey.create(BuiltinRegistries.CONFIGURED_FEATURE.key(), new ResourceLocation(GreekFantasy.MODID, "golden_tree"));

    @Override
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return new Holder.Direct<>(BuiltinRegistries.CONFIGURED_FEATURE.get(KEY));
    }
}