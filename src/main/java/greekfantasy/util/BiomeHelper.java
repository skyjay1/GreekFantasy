package greekfantasy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import greekfantasy.entity.DryadEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public final class BiomeHelper {
  
  private BiomeHelper() { }
  
  private static Map<RegistryKey<Biome>, DryadEntity.Variant> DRYAD_MAP = new HashMap<>();
  private static Map<RegistryKey<Biome>, Block> LOG_MAP = new HashMap<>();
  
  public static DryadEntity.Variant getVariantForBiome(final Optional<RegistryKey<Biome>> biome) {
    if(DRYAD_MAP.isEmpty()) {
      initDryadMap();
    }
    return biome.flatMap(b -> Optional.ofNullable(DRYAD_MAP.get(b))).orElse(DryadEntity.Variant.OAK);
  }
  
  public static BlockState getLogForBiome(final Optional<RegistryKey<Biome>> biome) {
    if(LOG_MAP.isEmpty()) {
      initLogMap();
    }
    return (biome.flatMap(b -> Optional.ofNullable(LOG_MAP.get(b))).orElse(Blocks.OAK_LOG)).getDefaultState();
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
  }
}
