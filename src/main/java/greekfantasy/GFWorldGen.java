package greekfantasy;

import greekfantasy.structure.feature.AraCampFeature;
import greekfantasy.structure.feature.HarpyNestFeature;
import greekfantasy.structure.feature.SatyrCampFeature;
import greekfantasy.structure.feature.SmallShrineFeature;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features.Placements;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
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
  @ObjectHolder(GreekFantasy.MODID + ":ara_camp")
  public static final Feature<NoFeatureConfig> ARA_CAMP = null;
  @ObjectHolder(GreekFantasy.MODID + ":satyr_camp")
  public static final Feature<NoFeatureConfig> SATYR_CAMP = null;
  
  
  private static final RuleTest ruleTestStone = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
  
  private GFWorldGen() { }
  
  

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
        new AraCampFeature(NoFeatureConfig.field_236558_a_)
          .setRegistryName(GreekFantasy.MODID, "ara_camp"));
    event.getRegistry().register(
        new SatyrCampFeature(NoFeatureConfig.field_236558_a_)
          .setRegistryName(GreekFantasy.MODID, "satyr_camp"));
  }

  // OTHER SETUP METHODS //

  public static void addBiomeFeatures(final BiomeLoadingEvent event) {
    if(event.getCategory() != Biome.Category.NETHER && event.getCategory() != Biome.Category.THEEND) {
      // Marble
      event.getGeneration().withFeature(
        GenerationStage.Decoration.UNDERGROUND_DECORATION,
        Feature.ORE.withConfiguration(new OreFeatureConfig(ruleTestStone, GFRegistry.MARBLE.getDefaultState(), 33))
          // unmapped methods copied from world.gen.feature.Features
		  // 33 = vein size, 80 = maxY, func_242728_a = spreadHorizontally, func_242731_b = repeat, 
          .func_242733_d(80).func_242728_a().func_242731_b(10)
      );
      // Limestone
      event.getGeneration().withFeature(
        GenerationStage.Decoration.UNDERGROUND_DECORATION,
        Feature.ORE.withConfiguration(new OreFeatureConfig(ruleTestStone, GFRegistry.LIMESTONE.getDefaultState(), 33))
          // I have no idea what these functions are, I copied them from world.gen.feature.Features
          .func_242733_d(80).func_242728_a().func_242731_b(10)
      );
      
      // Harpy Nest
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          // NoFeatureConfig.NO_FEATURE_CONFIG
          HARPY_NEST.withConfiguration(NoFeatureConfig.field_236559_b_)
          // These are copied from Features.DESERT_WELL
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT.func_242728_a()).func_242729_a(GreekFantasy.CONFIG.HARPY_NEST_SPREAD.get())
      );
      // Small Shrine
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          SMALL_SHRINE.withConfiguration(NoFeatureConfig.field_236559_b_)
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT.func_242728_a()).func_242729_a(GreekFantasy.CONFIG.SMALL_SHRINE_SPREAD.get())
      );
      // Ara Camp
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          ARA_CAMP.withConfiguration(NoFeatureConfig.field_236559_b_)
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT.func_242728_a()).func_242729_a(GreekFantasy.CONFIG.ARA_CAMP_SPREAD.get())
      );
      // Ara Camp
      event.getGeneration().withFeature(
          GenerationStage.Decoration.SURFACE_STRUCTURES, 
          SATYR_CAMP.withConfiguration(NoFeatureConfig.field_236559_b_)
          .withPlacement(Placements.HEIGHTMAP_PLACEMENT.func_242728_a()).func_242729_a(GreekFantasy.CONFIG.SATYR_CAMP_SPREAD.get())
      );
    }
  }
  
  public static void addBiomeSpawns(final BiomeLoadingEvent event) {
    final String name = event.getName().getPath();
    final boolean isForest = name.contains("forest") || name.contains("taiga") || name.contains("jungle") || name.contains("savanna") || name.contains("wooded");
    final Biome.Category category = event.getCategory();
    if(category == Biome.Category.NETHER) {
      // register nether spawns
      registerSpawns(event.getSpawns(), GFRegistry.CERBERUS_ENTITY, GreekFantasy.CONFIG.CERBERUS_SPAWN_WEIGHT.get(), 1, 1);
      registerSpawns(event.getSpawns(), GFRegistry.ORTHUS_ENTITY, GreekFantasy.CONFIG.ORTHUS_SPAWN_WEIGHT.get(), 1, 4);
    } else if(category != Biome.Category.THEEND) {
      // register overworld spawns
      registerSpawns(event.getSpawns(), GFRegistry.ARA_ENTITY, GreekFantasy.CONFIG.ARA_SPAWN_WEIGHT.get(), 2, 5);
      registerSpawns(event.getSpawns(), GFRegistry.EMPUSA_ENTITY, GreekFantasy.CONFIG.EMPUSA_SPAWN_WEIGHT.get(), 1, 3);
      registerSpawns(event.getSpawns(), GFRegistry.GORGON_ENTITY, GreekFantasy.CONFIG.GORGON_SPAWN_WEIGHT.get(), 1, 3);
      registerSpawns(event.getSpawns(), GFRegistry.MINOTAUR_ENTITY, GreekFantasy.CONFIG.MINOTAUR_SPAWN_WEIGHT.get(), 2, 5);
      registerSpawns(event.getSpawns(), GFRegistry.SHADE_ENTITY, GreekFantasy.CONFIG.SHADE_SPAWN_WEIGHT.get(), 1, 1);
      // desert spawns
      if(category == Biome.Category.DESERT) {
        registerSpawns(event.getSpawns(), GFRegistry.CERASTES_ENTITY, GreekFantasy.CONFIG.CERASTES_SPAWN_WEIGHT.get(), 1, 1);
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
}
