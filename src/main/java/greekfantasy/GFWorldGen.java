package greekfantasy;

import greekfantasy.feature.*;
import greekfantasy.util.BiomeWhitelistConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features.Placements;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

public final class GFWorldGen {
  
  private static final String MODID = GreekFantasy.MODID;
  
  @ObjectHolder(MODID + ":harpy_nest")
  public static final Feature<NoFeatureConfig> HARPY_NEST_FEATURE = null;
  @ObjectHolder(MODID + ":small_shrine")
  public static final Feature<NoFeatureConfig> SMALL_SHRINE_FEATURE = null;
  @ObjectHolder(MODID + ":small_nether_shrine")
  public static final Feature<NoFeatureConfig> SMALL_NETHER_SHRINE_FEATURE = null;
  @ObjectHolder(MODID + ":cyclopes_cave")
  public static final Feature<NoFeatureConfig> CYCLOPES_CAVE_FEATURE = null;
  @ObjectHolder(MODID + ":ara_camp")
  public static final Feature<NoFeatureConfig> ARA_CAMP_FEATURE = null;
  @ObjectHolder(MODID + ":satyr_camp")
  public static final Feature<NoFeatureConfig> SATYR_CAMP_FEATURE = null;
  @ObjectHolder(MODID + ":python_pit")
  public static final Feature<NoFeatureConfig> PYTHON_PIT_FEATURE = null;
  @ObjectHolder(MODID + ":lion_den")
  public static final Feature<NoFeatureConfig> LION_DEN_FEATURE = null;
  @ObjectHolder(MODID + ":arachne_pit")
  public static final Feature<NoFeatureConfig> ARACHNE_PIT_FEATURE = null;
  @ObjectHolder(MODID + ":olive_tree")
  public static final Feature<BaseTreeFeatureConfig> OLIVE_TREE_FEATURE = null;
  @ObjectHolder(MODID + ":pomegranate_tree")
  public static final Feature<BaseTreeFeatureConfig> POMEGRANATE_TREE_FEATURE = null;
  @ObjectHolder(MODID + ":golden_apple_tree")
  public static final Feature<BaseTreeFeatureConfig> GOLDEN_APPLE_TREE_FEATURE = null;
  @ObjectHolder(MODID + ":reeds")
  public static final Feature<BlockClusterFeatureConfig> REEDS_FEATURE = null;
  
  @ObjectHolder(MODID + ":olive_forest")
  public static final Biome OLIVE_FOREST_BIOME = null;
  
  public static RegistryKey<Biome> OLIVE_FOREST = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(MODID, "olive_forest"));

  private static ConfiguredFeature<?, ?> MARBLE;
  private static ConfiguredFeature<?, ?> LIMESTONE;
  private static ConfiguredFeature<?, ?> HARPY_NEST;
  private static ConfiguredFeature<?, ?> SMALL_SHRINE;
  private static ConfiguredFeature<?, ?> SMALL_NETHER_SHRINE;
  private static ConfiguredFeature<?, ?> CYCLOPES_CAVE;
  private static ConfiguredFeature<?, ?> ARA_CAMP;
  private static ConfiguredFeature<?, ?> SATYR_CAMP;
  private static ConfiguredFeature<?, ?> LION_DEN;
  private static ConfiguredFeature<?, ?> ARACHNE_PIT;
  private static ConfiguredFeature<?, ?> PYTHON_PIT;
  private static ConfiguredFeature<?, ?> OLIVE_TREE_SINGLE;
  private static ConfiguredFeature<?, ?> OLIVE_TREE_FOREST;
  private static ConfiguredFeature<?, ?> POMEGRANATE_TREE;
  private static ConfiguredFeature<?, ?> GOLDEN_APPLE_TREE;
  private static ConfiguredFeature<?, ?> REEDS;
  private static ConfiguredFeature<?, ?> SWAMP_REEDS;
  
  private static final RuleTest ruleTestStone = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
  
  private GFWorldGen() { }
  
  @SubscribeEvent
  public static void registerBiomes(final RegistryEvent.Register<Biome> event) {
    final Biome oliveForestBiome = makeOliveForest(0.1F, 0.16F)
        .setRegistryName(new ResourceLocation(MODID, "olive_forest"));
    event.getRegistry().register(oliveForestBiome);
  }

  @SubscribeEvent
  public static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
    GreekFantasy.LOGGER.debug("registerFeatures");
    event.getRegistry().registerAll(
        new HarpyNestFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "harpy_nest"),
        new SmallShrineFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "small_shrine"),
        new SmallNetherShrineFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "small_nether_shrine"),
        new CyclopesCaveFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "cyclopes_cave"),
        new AraCampFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "ara_camp"),
        new SatyrCampFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "satyr_camp"),
        new PythonPitFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "python_pit"),
        new LionDenFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "lion_den"),
        new ArachnePitFeature(NoFeatureConfig.CODEC)
          .setRegistryName(MODID, "arachne_pit"),
        new OliveTreeFeature(BaseTreeFeatureConfig.CODEC)
          .setRegistryName(MODID, "olive_tree"),
        new TreeFeature(BaseTreeFeatureConfig.CODEC)
          .setRegistryName(MODID, "pomegranate_tree"),
        new TreeFeature(BaseTreeFeatureConfig.CODEC)
          .setRegistryName(MODID, "golden_apple_tree"),
        new ReedsFeature(BlockClusterFeatureConfig.CODEC)
          .setRegistryName(MODID, "reeds"));
  }


  // OTHER SETUP METHODS //
  
  public static void finishBiomeSetup() {
    final int biomeWeight = GreekFantasy.CONFIG.OLIVE_FOREST_BIOME_WEIGHT.get();
    if(biomeWeight > 0) {
      BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(OLIVE_FOREST, biomeWeight));
      BiomeDictionary.addTypes(OLIVE_FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD);
    }
  }
  
  public static void registerConfiguredFeatures() {
    GreekFantasy.LOGGER.debug("registerConfiguredFeatures");
    MARBLE = registerFeature("marble", 
        Feature.ORE.withConfiguration(new OreFeatureConfig(ruleTestStone, GFRegistry.MARBLE.getDefaultState(), 33))
        // unmapped methods copied from world.gen.feature.Features
        // 33 = vein size, 80 = maxY, square = spreadHorizontally, count = repeat
        .range(80).square().count(10));
    LIMESTONE = registerFeature("limestone", 
        Feature.ORE.withConfiguration(new OreFeatureConfig(ruleTestStone, GFRegistry.LIMESTONE.getDefaultState(), 33))
        .range(80).square().count(10));
    HARPY_NEST = registerFeature("harpy_nest", 
        HARPY_NEST_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG) // NoFeatureConfig.NO_FEATURE_CONFIG
        .withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    SMALL_SHRINE = registerFeature("small_shrine", 
        SMALL_SHRINE_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
        .withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    SMALL_NETHER_SHRINE = registerFeature("small_nether_shrine",
        SMALL_NETHER_SHRINE_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG));
    CYCLOPES_CAVE = registerFeature("cyclopes_cave",
        CYCLOPES_CAVE_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
        .chance(3).withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    ARA_CAMP = registerFeature("ara_camp", 
        ARA_CAMP_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).chance(2)
        .withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    SATYR_CAMP = registerFeature("satyr_camp",
        SATYR_CAMP_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
        .withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    LION_DEN = registerFeature("lion_den",
        LION_DEN_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
        .chance(2).withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    PYTHON_PIT = registerFeature("python_pit",
        PYTHON_PIT_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG)
        .withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    ARACHNE_PIT = registerFeature("arachne_pit",
        ARACHNE_PIT_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG).chance(3)
        .withPlacement(Placements.HEIGHTMAP_PLACEMENT));
    OLIVE_TREE_SINGLE = registerFeature("olive_tree_single", 
        OliveTree.getConfiguredTree().withPlacement(Placements.VEGETATION_PLACEMENT).count(20));
    OLIVE_TREE_FOREST = 
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(MODID, "olive_tree"), 
          OliveTree.getConfiguredTree()
            .withPlacement(Placements.VEGETATION_PLACEMENT)
            .withPlacement(Placements.HEIGHTMAP_PLACEMENT)
            .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 1))));
    POMEGRANATE_TREE = registerFeature("pomegranate_tree",
        PomegranateTree.getConfiguredTree().withPlacement(Placements.VEGETATION_PLACEMENT).count(20));
    REEDS = registerFeature("reeds",
        REEDS_FEATURE.withConfiguration((new BlockClusterFeatureConfig.Builder(
            new SimpleBlockStateProvider(GFRegistry.REEDS.getDefaultState()), 
            new DoublePlantBlockPlacer()))
              .tries(48).replaceable()
              .xSpread(4).zSpread(4)
              .build()).count(2));
    SWAMP_REEDS = registerFeature("reeds_swamp",
        REEDS_FEATURE.withConfiguration((new BlockClusterFeatureConfig.Builder(
            new SimpleBlockStateProvider(GFRegistry.REEDS.getDefaultState()), 
            new DoublePlantBlockPlacer()))
              .tries(32).replaceable()
              .xSpread(3).ySpread(3).zSpread(3)
              .build()).count(2));
    GOLDEN_APPLE_TREE = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(MODID, "golden_apple_tree"), 
        GoldenAppleTree.getConfiguredTree().withPlacement(Placements.VEGETATION_PLACEMENT));
  }
  
  private static ConfiguredFeature<?, ?> registerFeature(final String name, ConfiguredFeature<?, ?> feature) {
    final BiomeWhitelistConfig config = GreekFantasy.CONFIG.FEATURES.get(name);
    
    if(null == config) {
      GreekFantasy.LOGGER.error("Error registering features: config for '" + name + "' not found!");
    } else if(config.chance() > 0) {
      feature = feature.chance(1000 / config.chance());
    }
    
    return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(MODID, name), feature);
  }

  public static void addBiomeFeatures(final BiomeLoadingEvent event) {
    if(event.getCategory() == Biome.Category.NETHER) {
      addFeature(event, "small_nether_shrine", GenerationStage.Decoration.SURFACE_STRUCTURES, SMALL_NETHER_SHRINE);
      addFeature(event, "pomegranate_tree", GenerationStage.Decoration.VEGETAL_DECORATION, POMEGRANATE_TREE);
    } else if(event.getCategory() != Biome.Category.THEEND) {
      // add ore features
      addFeature(event, "marble", GenerationStage.Decoration.UNDERGROUND_DECORATION, MARBLE);
      addFeature(event, "limestone", GenerationStage.Decoration.UNDERGROUND_DECORATION, LIMESTONE);
      // add custom features
      addFeature(event, "harpy_nest", GenerationStage.Decoration.VEGETAL_DECORATION, HARPY_NEST);
      addFeature(event, "small_shrine", GenerationStage.Decoration.SURFACE_STRUCTURES, SMALL_SHRINE);
      addFeature(event, "cyclopes_cave", GenerationStage.Decoration.SURFACE_STRUCTURES, CYCLOPES_CAVE);
      addFeature(event, "ara_camp",GenerationStage.Decoration.SURFACE_STRUCTURES, ARA_CAMP);
      addFeature(event, "satyr_camp", GenerationStage.Decoration.SURFACE_STRUCTURES, SATYR_CAMP);
      addFeature(event, "lion_den", GenerationStage.Decoration.SURFACE_STRUCTURES, LION_DEN);
      addFeature(event, "python_pit", GenerationStage.Decoration.UNDERGROUND_STRUCTURES, PYTHON_PIT);
      addFeature(event, "arachne_pit", GenerationStage.Decoration.UNDERGROUND_STRUCTURES, ARACHNE_PIT);
      addFeature(event, "reeds", GenerationStage.Decoration.VEGETAL_DECORATION, REEDS);
      addFeature(event, "olive_tree_single", GenerationStage.Decoration.VEGETAL_DECORATION, OLIVE_TREE_SINGLE);    
      addFeature(event, "reeds_swamp", GenerationStage.Decoration.VEGETAL_DECORATION, SWAMP_REEDS);
      // add olive forest features
      if(OLIVE_FOREST.getLocation().equals(event.getName())) {
        event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, OLIVE_TREE_FOREST);
      }
    }
  }
  
  private static void addFeature(final BiomeLoadingEvent event, final String featureName, 
      final GenerationStage.Decoration stage, final ConfiguredFeature<?, ?> feature) {
    final BiomeWhitelistConfig config = GreekFantasy.CONFIG.FEATURES.get(featureName);
    if(null == config) {
      GreekFantasy.LOGGER.error("Error registering features: config for '" + featureName + "' not found!");
    } else if(config.chance() > 0 && config.canSpawnInBiome(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName()))) {
      event.getGeneration().withFeature(stage, feature);
    }
  }
  
  public static void addBiomeSpawns(final BiomeLoadingEvent event) {
    // nether spawns
    addSpawns(event, GFRegistry.FURY_ENTITY, 3, 3);
    addSpawns(event, GFRegistry.LAMPAD_ENTITY, 1, 3);
    addSpawns(event, GFRegistry.ORTHUS_ENTITY, 1, 4);
    // overworld spawns
    addSpawns(event, GFRegistry.ARA_ENTITY, 2, 5);
    addSpawns(event, GFRegistry.CENTAUR_ENTITY, 2, 4);
    addSpawns(event, GFRegistry.CERASTES_ENTITY, 1, 2);
    addSpawns(event, GFRegistry.CYCLOPES_ENTITY, 1, 3);
    addSpawns(event, GFRegistry.CYPRIAN_ENTITY, 1, 3);
    addSpawns(event, GFRegistry.DRAKAINA_ENTITY, 1, 2);
    addSpawns(event, GFRegistry.DRYAD_ENTITY, 1, 3);
    addSpawns(event, GFRegistry.EMPUSA_ENTITY, 1, 2);
    addSpawns(event, GFRegistry.GIGANTE_ENTITY, 1, 4);
    addSpawns(event, GFRegistry.GORGON_ENTITY, 1, 2);
    addSpawns(event, GFRegistry.HARPY_ENTITY, 1, 3);
    addSpawns(event, GFRegistry.HYDRA_ENTITY, 1, 1);
    addSpawns(event, GFRegistry.MAD_COW_ENTITY, 1, 1);
    addSpawns(event, GFRegistry.MINOTAUR_ENTITY, 3, 5);
    addSpawns(event, GFRegistry.NAIAD_ENTITY, 2, 5);
    addSpawns(event, GFRegistry.PEGASUS_ENTITY, 2, 5);
    addSpawns(event, GFRegistry.SATYR_ENTITY, 2, 5);
    addSpawns(event, GFRegistry.SHADE_ENTITY, 1, 1);
    addSpawns(event, GFRegistry.SIREN_ENTITY, 2, 4);
    addSpawns(event, GFRegistry.UNICORN_ENTITY, 2, 5);
    addSpawns(event, GFRegistry.WHIRL_ENTITY, 1, 1);
  }
  
  private static void addSpawns(final BiomeLoadingEvent event, final EntityType<?> entity, final int min, final int max) {
    final String name = entity.getRegistryName().getPath();
    final BiomeWhitelistConfig config = GreekFantasy.CONFIG.MOB_SPAWNS.get(name);
    final RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName());
    if(null == config) {
      GreekFantasy.LOGGER.error("Error registering spawns: config for '" + name + "' not found!");
    } else if(config.chance() > 0 && config.canSpawnInBiome(key)) {
      event.getSpawns().withSpawner(entity.getClassification(), new MobSpawnInfo.Spawners(entity, config.chance(), min, max));
    }
  }
  
  private static Biome makeOliveForest(float depth, float scale) {
    MobSpawnInfo.Builder builder = new MobSpawnInfo.Builder();
    DefaultBiomeFeatures.withPassiveMobs(builder);
    DefaultBiomeFeatures.withBatsAndHostiles(builder);

    BiomeGenerationSettings.Builder biomeGenBuilder = (new BiomeGenerationSettings.Builder())
        .withSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
    DefaultBiomeFeatures.withStrongholdAndMineshaft(biomeGenBuilder);
    biomeGenBuilder.withStructure(StructureFeatures.RUINED_PORTAL);
    
    DefaultBiomeFeatures.withCavesAndCanyons(biomeGenBuilder);
    
    DefaultBiomeFeatures.withLavaAndWaterLakes(biomeGenBuilder);
    DefaultBiomeFeatures.withMonsterRoom(biomeGenBuilder);
    DefaultBiomeFeatures.withAllForestFlowerGeneration(biomeGenBuilder);
    DefaultBiomeFeatures.withCommonOverworldBlocks(biomeGenBuilder);
    DefaultBiomeFeatures.withOverworldOres(biomeGenBuilder);
    DefaultBiomeFeatures.withDisks(biomeGenBuilder);
    
    DefaultBiomeFeatures.withDefaultFlowers(biomeGenBuilder);
    DefaultBiomeFeatures.withForestGrass(biomeGenBuilder);
    DefaultBiomeFeatures.withNormalMushroomGeneration(biomeGenBuilder);
    DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomeGenBuilder);
    DefaultBiomeFeatures.withLavaAndWaterSprings(biomeGenBuilder);
    DefaultBiomeFeatures.withFrozenTopLayer(biomeGenBuilder);
    
    return (new Biome.Builder())
      .precipitation(Biome.RainType.RAIN)
      .category(Biome.Category.FOREST)
      .depth(depth)
      .scale(scale)
      .temperature(0.6F)
      .downfall(0.6F)
      .setEffects((new BiomeAmbience.Builder())
        .withFoliageColor(10729111)
        .withGrassColor(8955507)
        .setWaterColor(4159204)
        .setWaterFogColor(329011)
        .setFogColor(12638463)
        .withSkyColor(getSkyColorWithTemperatureModifier(0.6F))
        .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
        .build())
      .withMobSpawnSettings(builder.build())
      .withGenerationSettings(biomeGenBuilder.build())
      .build();
  }
  
  private static int getSkyColorWithTemperatureModifier(float t) {
    float f1 = t;
    f1 /= 3.0F;
    f1 = MathHelper.clamp(f1, -1.0F, 1.0F);
    return MathHelper.hsvToRGB(0.62222224F - f1 * 0.05F, 0.5F + f1 * 0.1F, 1.0F);
  }
}
