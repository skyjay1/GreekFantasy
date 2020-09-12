package greekfantasy;

import java.util.function.Supplier;

import greekfantasy.block.NestBlock;
import greekfantasy.entity.AraEntity;
import greekfantasy.entity.CentaurEntity;
import greekfantasy.entity.CerastesEntity;
import greekfantasy.entity.CyclopesEntity;
import greekfantasy.entity.CyprianCentaurEntity;
import greekfantasy.entity.EmpusaEntity;
import greekfantasy.entity.GiganteEntity;
import greekfantasy.entity.GorgonEntity;
import greekfantasy.entity.HarpyEntity;
import greekfantasy.entity.MinotaurEntity;
import greekfantasy.entity.NymphEntity;
import greekfantasy.entity.OrthusEntity;
import greekfantasy.entity.SatyrEntity;
import greekfantasy.entity.ShadeEntity;
import greekfantasy.entity.SirenEntity;
import greekfantasy.entity.UnicornEntity;
import greekfantasy.item.ClubItem;
import greekfantasy.item.PanfluteItem;
import greekfantasy.structure.HarpyNestStructure;
import greekfantasy.structure.feature.HarpyNestFeature;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(GreekFantasy.MODID)
public final class GFRegistry {

  //ENTITY TYPES

  public static EntityType<AraEntity> ARA_ENTITY;
  public static EntityType<CentaurEntity> CENTAUR_ENTITY;
  public static EntityType<CerastesEntity> CERASTES_ENTITY;
  public static EntityType<CyclopesEntity> CYCLOPES_ENTITY;
  public static EntityType<CyprianCentaurEntity> CYPRIAN_CENTAUR_ENTITY;
  public static EntityType<EmpusaEntity> EMPUSA_ENTITY;
  public static EntityType<GiganteEntity> GIGANTE_ENTITY;
  public static EntityType<GorgonEntity> GORGON_ENTITY;
  public static EntityType<HarpyEntity> HARPY_ENTITY;
  public static EntityType<MinotaurEntity> MINOTAUR_ENTITY;
  public static EntityType<NymphEntity> NYMPH_ENTITY;
  public static EntityType<OrthusEntity> ORTHUS_ENTITY;
  public static EntityType<SatyrEntity> SATYR_ENTITY;
  public static EntityType<ShadeEntity> SHADE_ENTITY;
  public static EntityType<SirenEntity> SIREN_ENTITY;
  public static EntityType<UnicornEntity> UNICORN_ENTITY;

  // OBJECT HOLDERS //

  @ObjectHolder("panflute")
  public static final Item PANFLUTE = null;

  @ObjectHolder("stone_club")
  public static final Item STONE_CLUB = null;

  @ObjectHolder("wooden_club")
  public static final Item WOODEN_CLUB = null;

  @ObjectHolder("nest")
  public static final Block NEST_BLOCK = null;

  public static ItemGroup GREEK_GROUP = new ItemGroup("greekfantasy") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(PANFLUTE);
    }
  };

  // REGISTRY METHODS //

  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    GreekFantasy.LOGGER.info("registerEntities");
    ARA_ENTITY = registerEntityType(event, AraEntity::new, AraEntity::getAttributes, "ara", 0.7F, 1.8F, true);
    CENTAUR_ENTITY = registerEntityType(event, CentaurEntity::new, CentaurEntity::getAttributes, "centaur", 1.39F, 2.49F, false);
    CYPRIAN_CENTAUR_ENTITY = registerEntityType(event, CyprianCentaurEntity::new, CyprianCentaurEntity::getAttributes, "cyprian", 1.39F, 2.49F, false);
    CERASTES_ENTITY = registerEntityType(event, CerastesEntity::new, CerastesEntity::getAttributes, "cerastes", 0.98F, 0.94F, false);
    CYCLOPES_ENTITY = registerEntityType(event, CyclopesEntity::new, CyclopesEntity::getAttributes, "cyclopes", 1.19F, 2.79F, false);
    EMPUSA_ENTITY = registerEntityType(event, EmpusaEntity::new, EmpusaEntity::getAttributes, "empusa", 0.7F, 1.8F, true);
    GIGANTE_ENTITY = registerEntityType(event, GiganteEntity::new, GiganteEntity::getAttributes, "gigante", 1.19F, 2.79F, false);
    GORGON_ENTITY = registerEntityType(event, GorgonEntity::new, GorgonEntity::getAttributes, "gorgon", 0.9F, 1.9F, false);
    HARPY_ENTITY = registerEntityType(event, HarpyEntity::new, HarpyEntity::getAttributes, "harpy", 0.7F, 1.8F, false);
    MINOTAUR_ENTITY = registerEntityType(event, MinotaurEntity::new, MinotaurEntity::getAttributes, "minotaur", 0.7F, 1.8F, false);
    ORTHUS_ENTITY = registerEntityType(event, OrthusEntity::new, OrthusEntity::getAttributes, "orthus", 0.6F, 0.85F, true);
    NYMPH_ENTITY = registerEntityType(event, NymphEntity::new, NymphEntity::getAttributes, "nymph", 0.48F, 1.8F, false);
    SATYR_ENTITY = registerEntityType(event, SatyrEntity::new, SatyrEntity::getAttributes, "satyr", 0.7F, 1.8F, false);
    SHADE_ENTITY = registerEntityType(event, ShadeEntity::new, ShadeEntity::getAttributes, "shade", 0.7F, 1.8F, true);
    SIREN_ENTITY = registerEntityType(event, SirenEntity::new, SirenEntity::getAttributes, "siren", 0.6F, 1.9F, false);
    UNICORN_ENTITY = registerEntityType(event, UnicornEntity::new, UnicornEntity::getAttributes, "unicorn", 1.39F, 1.98F, false);
    GreekFantasy.PROXY.registerEntityRenders();
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    GreekFantasy.LOGGER.info("registerItems");
    // items
    event.getRegistry().registerAll(
        new PanfluteItem(new Item.Properties().group(GREEK_GROUP).maxDamage(100)).setRegistryName(GreekFantasy.MODID, "panflute"),
        new ClubItem(ItemTier.DIAMOND, new Item.Properties().group(GREEK_GROUP)).setRegistryName(GreekFantasy.MODID,
            "diamond_club"),
        new ClubItem(ItemTier.GOLD, new Item.Properties().group(GREEK_GROUP)).setRegistryName(GreekFantasy.MODID, "gold_club"),
        new ClubItem(ItemTier.IRON, new Item.Properties().group(GREEK_GROUP)).setRegistryName(GreekFantasy.MODID, "iron_club"),
        new ClubItem(ItemTier.NETHERITE, new Item.Properties().group(GREEK_GROUP)).setRegistryName(GreekFantasy.MODID,
            "netherite_club"),
        new ClubItem(ItemTier.STONE, new Item.Properties().group(GREEK_GROUP)).setRegistryName(GreekFantasy.MODID, "stone_club"),
        new ClubItem(ItemTier.WOOD, new Item.Properties().group(GREEK_GROUP)).setRegistryName(GreekFantasy.MODID, "wooden_club"));
    // block items
    event.getRegistry().registerAll(new BlockItem(NEST_BLOCK, new Item.Properties().group(GREEK_GROUP))
        .setRegistryName(GreekFantasy.MODID, "nest"));
  }

  @SubscribeEvent
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    GreekFantasy.LOGGER.info("registerBlocks");
    event.getRegistry().registerAll(new NestBlock().setRegistryName(GreekFantasy.MODID, "nest"));
  }

  @SubscribeEvent
  public static void registerStructures(final RegistryEvent.Register<Structure<?>> event) {
    GreekFantasy.LOGGER.info("registerStructures");
    // TODO
  }

  @SubscribeEvent
  public static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
    GreekFantasy.LOGGER.info("registerFeatures");
    event.getRegistry().register(
        new HarpyNestFeature(NoFeatureConfig.field_236558_a_).setRegistryName(GreekFantasy.MODID, HarpyNestStructure.NAME));
  }

  // OTHER SETUP METHODS //

  public static void setupFeatures() {
    for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
      if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
//        biome.func_242440_e()
//        .func_242498_c().get(GenerationStage.Decoration.SURFACE_STRUCTURES.ordinal())
//            .add(() -> new ConfiguredFeature<>(GFStructures.HARPY_NEST_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG)
//                .withPlacement(new ConfiguredPlacement<>(Placement.field_242898_b, new ChanceConfig(4))));

      }
    }
  }

  public static void setupStructures() {
//  for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
//    if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
//      biome.func_242440_e()..addStructure(GFFeatures.HARPY_NEST, IFeatureConfig.NO_FEATURE_CONFIG);
//      biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, 
//          Biome.createDecoratedFeature(GFFeatures.HARPY_NEST,
//          IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
//    }
//  }
  }

  // HELPER METHODS //

  private static <T extends LivingEntity> EntityType<T> registerEntityType(final RegistryEvent.Register<EntityType<?>> event,
      final IFactory<T> factoryIn, final Supplier<AttributeModifierMap.MutableAttribute> mapSupplier, final String name,
      final float width, final float height, final boolean fireproof) {
    EntityType.Builder<T> entityTypeBuilder = EntityType.Builder.create(factoryIn, EntityClassification.MISC).size(width, height);
    if (fireproof)
      entityTypeBuilder.immuneToFire();
    EntityType<T> entityType = entityTypeBuilder.build(name);
    entityType.setRegistryName(GreekFantasy.MODID, name);
    event.getRegistry().register(entityType);
    GlobalEntityTypeAttributes.put(entityType, mapSupplier.get().create());
    return entityType;
  }
}
