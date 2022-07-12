package greekfantasy.worldgen;

import com.mojang.serialization.Codec;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;

public class DimensionFilter extends PlacementFilter {
    private static final DimensionFilter INSTANCE = new DimensionFilter();
    public static Codec<DimensionFilter> CODEC = Codec.unit(() -> {
        return INSTANCE;
    });

    private DimensionFilter() {
    }

    public static DimensionFilter dimension() {
        return INSTANCE;
    }

    protected boolean shouldPlace(PlacementContext context, Random rand, BlockPos pos) {
        PlacedFeature placedfeature = context.topFeature().orElseThrow(() -> {
            return new IllegalStateException("Tried to dimension check an unregistered feature");
        });
        return GreekFantasy.CONFIG.featureMatchesDimension(context.getLevel().getLevel());
    }

    public PlacementModifierType<?> type() {
        return GFRegistry.PlacementTypeReg.DIMENSION_FILTER.get();
    }
}