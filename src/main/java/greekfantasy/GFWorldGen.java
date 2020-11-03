package greekfantasy;

import greekfantasy.block.OliveTree;
import greekfantasy.structure.feature.AraCampFeature;
import greekfantasy.structure.feature.HarpyNestFeature;
import greekfantasy.structure.feature.OliveTreeFeature;
import greekfantasy.structure.feature.ReedsFeature;
import greekfantasy.structure.feature.SatyrCampFeature;
import greekfantasy.structure.feature.SmallNetherShrineFeature;
import greekfantasy.structure.feature.SmallShrineFeature;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features.Placements;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

public final class GFWorldGen {
  
  @ObjectHolder(GreekFantasy.MODID + ":harpy_nest")
  public static final Feature<NoFeatureConfig> HARPY_NEST = null;
  @ObjectHolder(GreekFantasy.MODID + ":small_shrine")
  public static final Feature<NoFeatureConfig> SMALL_SHRINE = null;
  @ObjectHolder(GreekFantasy.MODID + ":small_nether_shrine")
  public static final Feature<NoFeatureConfig> SMALL_NETHER_SHRINE = null;
  @ObjectHolder(GreekFantasy.MODID + ":ara_camp")
  public static final Feature<NoFeatureConfig> ARA_CAMP = null;
  @ObjectHolder(GreekFantasy.MODID + ":satyr_camp")
  public static final Feature<NoFeatureConfig> SATYR_CAMP = null;
  @ObjectHolder(GreekFantasy.MODID + ":olive_tree")
  public static final Feature<BaseTreeFeatureConfig> OLIVE_TREE = null;
  @ObjectHolder(GreekFantasy.MODID + ":reeds")
  public static final Feature<BlockClusterFeatureConfig> REEDS = null;
  
  @ObjectHolder(GreekFantasy.MODID + ":olive_forest")
  public static final Biome OLIVE_FOREST_BIOME = null;
  
  public static RegistryKey<Biome> OLIVE_FOREST;
  

  private static final RuleTest ruleTestStone = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
  
  private GFWorldGen() { }
  
  @SubscribeEvent
  public static void registerBiomes(final RegistryEvent.Register<Biome> event) {
    final Biome oliveForestBiome = makeOliveForest(0.1F, 0.2F)
        .setRegistryName(new ResourceLocation(GreekFantasy.MODID, "olive_forest"));
    event.getRegistry().register(oliveForestBiome);
  }

  @SubscribeEvent
  public static void registerStructures(final RegistryEvent.Register<Structure<?>> event) {
    GreekFantasy.LOGGER.debug("registerStructures");
    // TODO
  }

  @SubscribeEvent
  public static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
    GreekFantasy.LOGGER.debug("registerFeatures");
    event.getRegistry().register(
        new HarpyNestFeature(NoFeatureConfig.field_236558_a_)
          .setRegistryName(GreekFantasy.MODID, "harpy_nest"));
    event.getRegistry().register(
        new SmallShrineFeature(NoFeatureConfig.field_236558_a_)
          .setRegistryName(GreekFantasy.MODID, "small_shrine"));
    event.getRegistry().register(
        new SmallNetherShrineFeature(NoFeatureConfig.field_236558_a_)
          .setRegistryName(GreekFantasy.MODID, "small_nether_shrine"));
    event.getRegistry().register(
        new AraCampFeature(NoFeatureConfig.field_236558_a_)
          .setRegistryName(GreekFantasy.MODID, "ara_camp"));
    event.getRegistry().register(
        new SatyrCampFeature(NoFeatureConfig.field_236558_a_)
          .setRegistryName(GreekFantasy.MODID, "satyr_camp"));
    event.getRegistry().register(
        new OliveTreeFeature(BaseTreeFeatureConfig.CODEC)
          .setRegistryName(GreekFantasy.MODID, "olive_tree"));
    event.getRegistry().register(
        new ReedsFeature(BlockClusterFeatureConfig.field_236587_a_)
          .setRegistryName(GreekFantasy.MODID, "reeds"));
  }

  // OTHER SETUP METHODS //
  
  public static void finishBiomeSetup() {
    OLIVE_FOREST = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(GreekFantasy.MODID, "olive_forest"));
    BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(OLIVE_FOREST, 10));
  }

  public static void addBiomeFeatures(final BiomeLoadingEvent event) {
    if(event.getCategory() != Biome.Category.NETHER && event.getCategory() != Biome.Category.THEEND) {
      // Marble
      event.getGeneration().withFeature(
        GenerationStage.Decoration.UNDERGROUND_DECORATION,
        Feature.ORE.withConfiguration(new OreFeatureConfig(ruleTestStone, GFRegistry.MARBLE.getDefaultState(), 33))
          // unmapped methods copied from world.gen.feature.Features
          // 33 = vein size, 80 = maxY, func_242728_a = spreadHorizontally, func_242731_b = repeat
          .range(80).square().func_242731_b(10)
      );
      // Limestone
      event.getGeneration().withFeature(
        GenerationStage.Decoration.UNDERGROUND_DECORATION,
        Feature.ORE.withConfiguration(new OreFeatureConfig(ruleTestStone, GFRegistry.LIMESTONE.getDefaultState(), 33))
          // I have no idea what these functions are, I copied them from world.gen.feature.Features
          .range(80).square().func_242731_b(10)
      );
      
      // Harpy Nest
      event.getGeneration().withFeature(
          GenerationStage.Decoration.VEGETAL_DECORATION, 
          // NoFeatureConfig.NO_FEATURE_CONFIG
          HARPY_NEST.withConfiguration(NoFeatureConfig.field_236559_b_)
          // These are copied from Features.DESERT_WELL
          // Placements.HEIGHTMAP_PLACEMENT.func_242728_a()
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT).chance(GreekFantasy.CONFIG.HARPY_NEST_SPREAD.get())
      );
      // Small Shrine
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          SMALL_SHRINE.withConfiguration(NoFeatureConfig.field_236559_b_)
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT).chance(GreekFantasy.CONFIG.SMALL_SHRINE_SPREAD.get())
      );
      // Ara Camp
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          ARA_CAMP.withConfiguration(NoFeatureConfig.field_236559_b_)
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT).chance(GreekFantasy.CONFIG.ARA_CAMP_SPREAD.get())
      );
      // Satyr Camp
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          SATYR_CAMP.withConfiguration(NoFeatureConfig.field_236559_b_)
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT).chance(GreekFantasy.CONFIG.SATYR_CAMP_SPREAD.get())
      );
      // Reeds
      event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, 
          REEDS.withConfiguration((new BlockClusterFeatureConfig.Builder(
              new SimpleBlockStateProvider(GFRegistry.REEDS.getDefaultState()), 
              new DoublePlantBlockPlacer()))
                .tries(48)
                .replaceable()
                .xSpread(4)
                .zSpread(4)
                .build())
          .func_242731_b(8)
      );
    }
    // biome-specific features
    if(event.getCategory() == Category.FOREST && event.getClimate().temperature > 0.5F) {
      // Olive Tree
      event.getGeneration().withFeature(
          GenerationStage.Decoration.VEGETAL_DECORATION, 
          OliveTree.getConfiguredTree().withPlacement(Placements.VEGETATION_PLACEMENT).func_242731_b(40)
      );
    }
    if(new ResourceLocation(GreekFantasy.MODID, "olive_forest").equals(event.getName())) {
      event.getGeneration().withFeature(
          GenerationStage.Decoration.VEGETAL_DECORATION, 
          OliveTree.getConfiguredTree()
            .withPlacement(Placements.VEGETATION_PLACEMENT)
            .withPlacement(Placements.HEIGHTMAP_PLACEMENT)
            .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 1)))
      );
    }
      
    if(event.getName().toString().contains("swamp")) {
      // Swamp Reeds
      event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, 
          REEDS.withConfiguration((new BlockClusterFeatureConfig.Builder(
              new SimpleBlockStateProvider(GFRegistry.REEDS.getDefaultState()), 
              new DoublePlantBlockPlacer()))
                .tries(32)
                .replaceable()
                .xSpread(3)
                .ySpread(3)
                .zSpread(3)
                .build())
          .func_242731_b(4)
      );
    }
    if(event.getCategory() == Biome.Category.NETHER) {
      // Small Nether Shrine
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          SMALL_NETHER_SHRINE.withConfiguration(NoFeatureConfig.field_236559_b_)
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT).func_242731_b(GreekFantasy.CONFIG.SMALL_NETHER_SHRINE_SPREAD.get())
      );
    }    
  }
  
  public static void addBiomeSpawns(final BiomeLoadingEvent event) {
    final String name = event.getName().getPath();
    final boolean isForest = name.contains("forest") || name.contains("taiga") || name.contains("jungle") || name.contains("savanna") || name.contains("wooded");
    final Biome.Category category = event.getCategory();
    if(category == Biome.Category.NETHER) {
      // register nether spawns
      registerSpawns(event.getSpawns(), GFRegistry.ORTHUS_ENTITY, GreekFantasy.CONFIG.ORTHUS_SPAWN_WEIGHT.get(), 1, 4);
    } else if(category != Biome.Category.THEEND) {
      // register overworld spawns
      registerSpawns(event.getSpawns(), GFRegistry.EMPUSA_ENTITY, GreekFantasy.CONFIG.EMPUSA_SPAWN_WEIGHT.get(), 1, 3);
      registerSpawns(event.getSpawns(), GFRegistry.GORGON_ENTITY, GreekFantasy.CONFIG.GORGON_SPAWN_WEIGHT.get(), 1, 3);
      registerSpawns(event.getSpawns(), GFRegistry.SHADE_ENTITY, GreekFantasy.CONFIG.SHADE_SPAWN_WEIGHT.get(), 1, 1);
      // non-ocean, non-icy spawns
      if(category != Biome.Category.OCEAN && category != Biome.Category.ICY) {
        registerSpawns(event.getSpawns(), GFRegistry.ARA_ENTITY, GreekFantasy.CONFIG.ARA_SPAWN_WEIGHT.get(), 2, 5);
        registerSpawns(event.getSpawns(), GFRegistry.MAD_COW_ENTITY, GreekFantasy.CONFIG.MAD_COW_SPAWN_WEIGHT.get(), 1, 1);
        registerSpawns(event.getSpawns(), GFRegistry.MINOTAUR_ENTITY, GreekFantasy.CONFIG.MINOTAUR_SPAWN_WEIGHT.get(), 2, 5);
      }
      // desert spawns
      if(category == Biome.Category.DESERT) {
        registerSpawns(event.getSpawns(), GFRegistry.CERASTES_ENTITY, GreekFantasy.CONFIG.CERASTES_SPAWN_WEIGHT.get(), 1, 2);
        registerSpawns(event.getSpawns(), GFRegistry.HARPY_ENTITY, GreekFantasy.CONFIG.HARPY_SPAWN_WEIGHT.get(), 1, 3);
      }
      // mountains spawns
      if(category == Biome.Category.EXTREME_HILLS) {
        registerSpawns(event.getSpawns(), GFRegistry.CYCLOPES_ENTITY, GreekFantasy.CONFIG.CYCLOPES_SPAWN_WEIGHT.get(), 1, 3);
        registerSpawns(event.getSpawns(), GFRegistry.GIGANTE_ENTITY, GreekFantasy.CONFIG.GIGANTE_SPAWN_WEIGHT.get(), 1, 4);
      }
      // plains spawns
      if(category == Biome.Category.PLAINS) {
        registerSpawns(event.getSpawns(), GFRegistry.UNICORN_ENTITY, GreekFantasy.CONFIG.UNICORN_SPAWN_WEIGHT.get(), 2, 5);
      }
      // plains + taiga spawns
      if(category == Biome.Category.PLAINS || name.contains("taiga")) {
        registerSpawns(event.getSpawns(), GFRegistry.CENTAUR_ENTITY, GreekFantasy.CONFIG.CENTAUR_SPAWN_WEIGHT.get(), 2, 4);
        registerSpawns(event.getSpawns(), GFRegistry.CYPRIAN_ENTITY, GreekFantasy.CONFIG.CYPRIAN_SPAWN_WEIGHT.get(), 2, 3);
      }
      // forest spawns
      if(isForest) {
        registerSpawns(event.getSpawns(), GFRegistry.DRYAD_ENTITY, GreekFantasy.CONFIG.DRYAD_SPAWN_WEIGHT.get(), 1, 2);
        registerSpawns(event.getSpawns(), GFRegistry.SATYR_ENTITY, GreekFantasy.CONFIG.SATYR_SPAWN_WEIGHT.get(), 2, 5);
      }
      // water spawns
      if(category == Biome.Category.OCEAN) {
        registerSpawns(event.getSpawns(), GFRegistry.SIREN_ENTITY, GreekFantasy.CONFIG.SIREN_SPAWN_WEIGHT.get(), 2, 5);
      }
      if(category == Biome.Category.OCEAN || category == Biome.Category.RIVER) {
        registerSpawns(event.getSpawns(), GFRegistry.NAIAD_ENTITY, GreekFantasy.CONFIG.NAIAD_SPAWN_WEIGHT.get(), 2, 5);
      }
    }
  }
  
  private static MobSpawnInfo.Builder registerSpawns(final MobSpawnInfoBuilder builder, final EntityType<?> entity, final int weight, final int min, final int max) {
    return builder.withSpawner(entity.getClassification(), new MobSpawnInfo.Spawners(entity, weight, min, max));
  }
  
  private static Biome makeOliveForest(float depth, float scale) {
    MobSpawnInfo.Builder builder = new MobSpawnInfo.Builder();
    DefaultBiomeFeatures.withPassiveMobs(builder);
    DefaultBiomeFeatures.withBatsAndHostiles(builder);

    BiomeGenerationSettings.Builder biomeGenBuilder = (new BiomeGenerationSettings.Builder())
        .withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);
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
      .withMobSpawnSettings(builder.copy())
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
