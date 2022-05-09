package greekfantasy.util;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldGen;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.DryadEntity;
import greekfantasy.entity.LampadEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BiomeHelper {

    public static final ResourceLocation TAG_OLIVE_LOGS = new ResourceLocation(GreekFantasy.MODID, "olive_logs");

    private BiomeHelper() {
    }

    private static final Map<RegistryKey<Biome>, DryadEntity.Variant> DRYAD_MAP = new HashMap<>();
    private static final Map<RegistryKey<Biome>, LampadEntity.Variant> LAMPAD_MAP = new HashMap<>();
    private static final Map<RegistryKey<Biome>, Block> LOG_MAP = new HashMap<>();

    public static DryadEntity.Variant getDryadVariantForBiome(final Optional<RegistryKey<Biome>> biome) {
        if (DRYAD_MAP.isEmpty()) {
            initDryadMap();
        }
        return biome.flatMap(b -> Optional.ofNullable(DRYAD_MAP.get(b))).orElse(DryadEntity.Variant.OAK);
    }

    public static LampadEntity.Variant getLampadVariantForBiome(final Optional<RegistryKey<Biome>> biome) {
        if (LAMPAD_MAP.isEmpty()) {
            initLampadMap();
        }
        return biome.flatMap(b -> Optional.ofNullable(LAMPAD_MAP.get(b))).orElse(LampadEntity.Variant.CRIMSON);
    }

    public static BlockState getLogForBiome(final Optional<RegistryKey<Biome>> biome) {
        if (LOG_MAP.isEmpty()) {
            initLogMap();
        }
        return (biome.flatMap(b -> Optional.ofNullable(LOG_MAP.get(b))).orElse(Blocks.OAK_LOG)).defaultBlockState();
    }

    public static ITag<Block> getOliveLogs() {
        return BlockTags.getAllTags().getTag(TAG_OLIVE_LOGS);
    }

    private static void initDryadMap() {
        DRYAD_MAP.clear();
        // Acacia biomes
        DRYAD_MAP.put(Biomes.SAVANNA, DryadEntity.Variant.ACACIA);
        DRYAD_MAP.put(Biomes.SAVANNA_PLATEAU, DryadEntity.Variant.ACACIA);
        DRYAD_MAP.put(Biomes.SHATTERED_SAVANNA, DryadEntity.Variant.ACACIA);
        DRYAD_MAP.put(Biomes.SHATTERED_SAVANNA_PLATEAU, DryadEntity.Variant.ACACIA);
        // Birch biomes
        DRYAD_MAP.put(Biomes.BIRCH_FOREST, DryadEntity.Variant.BIRCH);
        DRYAD_MAP.put(Biomes.BIRCH_FOREST_HILLS, DryadEntity.Variant.BIRCH);
        DRYAD_MAP.put(Biomes.TALL_BIRCH_FOREST, DryadEntity.Variant.BIRCH);
        DRYAD_MAP.put(Biomes.TALL_BIRCH_HILLS, DryadEntity.Variant.BIRCH);
        // Dark Oak biomes
        DRYAD_MAP.put(Biomes.DARK_FOREST, DryadEntity.Variant.DARK_OAK);
        DRYAD_MAP.put(Biomes.DARK_FOREST_HILLS, DryadEntity.Variant.DARK_OAK);
        // Jungle biomes
        DRYAD_MAP.put(Biomes.JUNGLE, DryadEntity.Variant.JUNGLE);
        DRYAD_MAP.put(Biomes.JUNGLE_EDGE, DryadEntity.Variant.JUNGLE);
        DRYAD_MAP.put(Biomes.JUNGLE_HILLS, DryadEntity.Variant.JUNGLE);
        DRYAD_MAP.put(Biomes.BAMBOO_JUNGLE_HILLS, DryadEntity.Variant.JUNGLE);
        DRYAD_MAP.put(Biomes.BAMBOO_JUNGLE, DryadEntity.Variant.JUNGLE);
        DRYAD_MAP.put(Biomes.MODIFIED_JUNGLE, DryadEntity.Variant.JUNGLE);
        DRYAD_MAP.put(Biomes.MODIFIED_JUNGLE_EDGE, DryadEntity.Variant.JUNGLE);
        // Spruce biomes
        DRYAD_MAP.put(Biomes.TAIGA, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.TAIGA_HILLS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.TAIGA_MOUNTAINS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.GIANT_SPRUCE_TAIGA, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.GIANT_TREE_TAIGA, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.GIANT_TREE_TAIGA_HILLS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.MOUNTAINS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.WOODED_MOUNTAINS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.GRAVELLY_MOUNTAINS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.SNOWY_BEACH, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.SNOWY_MOUNTAINS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.SNOWY_TAIGA, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.SNOWY_TAIGA_HILLS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.SNOWY_TAIGA_MOUNTAINS, DryadEntity.Variant.SPRUCE);
        DRYAD_MAP.put(Biomes.SNOWY_TUNDRA, DryadEntity.Variant.SPRUCE);
        // Olive biomes
        DRYAD_MAP.put(GFWorldGen.OLIVE_FOREST, DryadEntity.Variant.OLIVE);
    }

    private static void initLampadMap() {
        LAMPAD_MAP.clear();
        // Nether biomes
        LAMPAD_MAP.put(Biomes.CRIMSON_FOREST, LampadEntity.Variant.CRIMSON);
        LAMPAD_MAP.put(Biomes.WARPED_FOREST, LampadEntity.Variant.WARPED);
        LAMPAD_MAP.put(Biomes.SOUL_SAND_VALLEY, LampadEntity.Variant.POMEGRANATE);
    }

    private static void initLogMap() {
        LOG_MAP.clear();
        // Acacia biomes
        LOG_MAP.put(Biomes.SAVANNA, Blocks.ACACIA_LOG);
        LOG_MAP.put(Biomes.SAVANNA_PLATEAU, Blocks.ACACIA_LOG);
        LOG_MAP.put(Biomes.SHATTERED_SAVANNA, Blocks.ACACIA_LOG);
        LOG_MAP.put(Biomes.SHATTERED_SAVANNA_PLATEAU, Blocks.ACACIA_LOG);
        // Birch biomes
        LOG_MAP.put(Biomes.BIRCH_FOREST, Blocks.BIRCH_LOG);
        LOG_MAP.put(Biomes.BIRCH_FOREST_HILLS, Blocks.BIRCH_LOG);
        LOG_MAP.put(Biomes.TALL_BIRCH_FOREST, Blocks.BIRCH_LOG);
        LOG_MAP.put(Biomes.TALL_BIRCH_HILLS, Blocks.BIRCH_LOG);
        // Dark Oak biomes
        LOG_MAP.put(Biomes.DARK_FOREST, Blocks.DARK_OAK_LOG);
        LOG_MAP.put(Biomes.DARK_FOREST_HILLS, Blocks.DARK_OAK_LOG);
        // Jungle biomes
        LOG_MAP.put(Biomes.JUNGLE, Blocks.JUNGLE_LOG);
        LOG_MAP.put(Biomes.JUNGLE_EDGE, Blocks.JUNGLE_LOG);
        LOG_MAP.put(Biomes.JUNGLE_HILLS, Blocks.JUNGLE_LOG);
        LOG_MAP.put(Biomes.BAMBOO_JUNGLE_HILLS, Blocks.JUNGLE_LOG);
        LOG_MAP.put(Biomes.BAMBOO_JUNGLE, Blocks.JUNGLE_LOG);
        LOG_MAP.put(Biomes.MODIFIED_JUNGLE, Blocks.JUNGLE_LOG);
        LOG_MAP.put(Biomes.MODIFIED_JUNGLE_EDGE, Blocks.JUNGLE_LOG);
        // Spruce biomes
        LOG_MAP.put(Biomes.TAIGA, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.TAIGA_HILLS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.TAIGA_MOUNTAINS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.GIANT_SPRUCE_TAIGA, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.GIANT_TREE_TAIGA, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.GIANT_TREE_TAIGA_HILLS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.MOUNTAINS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.WOODED_MOUNTAINS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.GRAVELLY_MOUNTAINS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.SNOWY_BEACH, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.SNOWY_MOUNTAINS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.SNOWY_TAIGA, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.SNOWY_TAIGA_HILLS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.SNOWY_TAIGA_MOUNTAINS, Blocks.SPRUCE_LOG);
        LOG_MAP.put(Biomes.SNOWY_TUNDRA, Blocks.SPRUCE_LOG);
        // Olive biomes
        LOG_MAP.put(GFWorldGen.OLIVE_FOREST, GFRegistry.BlockReg.OLIVE_LOG);
        // Nether biomes
        LOG_MAP.put(Biomes.WARPED_FOREST, Blocks.WARPED_STEM);
        LOG_MAP.put(Biomes.CRIMSON_FOREST, Blocks.CRIMSON_STEM);
        LOG_MAP.put(Biomes.SOUL_SAND_VALLEY, GFRegistry.BlockReg.POMEGRANATE_LOG);
    }
}
