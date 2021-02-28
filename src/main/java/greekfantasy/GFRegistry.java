 package greekfantasy;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.block.*;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.effect.*;
import greekfantasy.enchantment.*;
import greekfantasy.entity.*;
import greekfantasy.entity.misc.*;
import greekfantasy.feature.GoldenAppleTree;
import greekfantasy.feature.OliveTree;
import greekfantasy.gui.StatueContainer;
import greekfantasy.gui.DeityContainer;
import greekfantasy.item.*;
import greekfantasy.loot.AutosmeltOrCobbleModifier;
import greekfantasy.loot.CropMultiplierModifier;
import greekfantasy.tileentity.*;
import greekfantasy.tileentity.MobHeadTileEntity.HeadType;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(GreekFantasy.MODID)
public final class GFRegistry {
  
  private static final String MODID = GreekFantasy.MODID;

  // ENTITY TYPES //

  public static EntityType<AraEntity> ARA_ENTITY = buildEntityType(AraEntity::new, "ara", 0.67F, 1.8F, EntityClassification.CREATURE, b -> {});
  public static EntityType<ArionEntity> ARION_ENTITY = buildEntityType(ArionEntity::new, "arion", 1.39F, 1.98F, EntityClassification.CREATURE, b -> b.immuneToFire());
  public static EntityType<CentaurEntity> CENTAUR_ENTITY = buildEntityType(CentaurEntity::new, "centaur", 1.39F, 2.49F, EntityClassification.CREATURE, b -> {});
  public static EntityType<CerastesEntity> CERASTES_ENTITY = buildEntityType(CerastesEntity::new, "cerastes", 0.98F, 0.94F, EntityClassification.CREATURE, b -> {});
  public static EntityType<CerberusEntity> CERBERUS_ENTITY = buildEntityType(CerberusEntity::new, "cerberus", 1.98F, 1.9F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<CharybdisEntity> CHARYBDIS_ENTITY = buildEntityType(CharybdisEntity::new, "charybdis", 5.9F, 7.9F, EntityClassification.WATER_CREATURE, b -> b.immuneToFire());
  public static EntityType<CirceEntity> CIRCE_ENTITY = buildEntityType(CirceEntity::new, "circe", 0.67F, 1.8F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<CretanEntity> CRETAN_ENTITY = buildEntityType(CretanEntity::new, "cretan", 0.98F, 3.395F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<CurseEntity> CURSE_ENTITY = buildEntityType(CurseEntity::new, "curse", 0.25F, 0.25F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning().trackingRange(4).func_233608_b_(10));
  public static EntityType<CyclopesEntity> CYCLOPES_ENTITY = buildEntityType(CyclopesEntity::new, "cyclopes", 0.99F, 2.92F, EntityClassification.MONSTER, b -> {});
  public static EntityType<CyprianEntity> CYPRIAN_ENTITY = buildEntityType(CyprianEntity::new, "cyprian", 1.39F, 2.49F, EntityClassification.CREATURE, b -> {});
  public static EntityType<DragonToothEntity> DRAGON_TOOTH_ENTITY = buildEntityType(DragonToothEntity::new, "dragon_tooth", 0.25F, 0.25F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning().trackingRange(4).func_233608_b_(10));
  public static EntityType<DrakainaEntity> DRAKAINA_ENTITY = buildEntityType(DrakainaEntity::new, "drakaina", 0.9F, 1.9F, EntityClassification.MONSTER, b -> {});
  public static EntityType<DryadEntity> DRYAD_ENTITY = buildEntityType(DryadEntity::new, "dryad", 0.48F, 1.8F, EntityClassification.CREATURE, b -> {});
  public static EntityType<ElpisEntity> ELPIS_ENTITY = buildEntityType(ElpisEntity::new, "elpis", 0.4F, 0.8F, EntityClassification.CREATURE, b -> b.immuneToFire());
  public static EntityType<EmpusaEntity> EMPUSA_ENTITY = buildEntityType(EmpusaEntity::new, "empusa", 0.67F, 1.8F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<FuryEntity> FURY_ENTITY = buildEntityType(FuryEntity::new, "fury", 0.67F, 1.4F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<GeryonEntity> GERYON_ENTITY = buildEntityType(GeryonEntity::new, "geryon", 1.98F, 4.96F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<GiantBoarEntity> GIANT_BOAR_ENTITY = buildEntityType(GiantBoarEntity::new, "giant_boar", 2.653F, 2.66F, EntityClassification.CREATURE, b -> b.immuneToFire());
  public static EntityType<GiganteEntity> GIGANTE_ENTITY = buildEntityType(GiganteEntity::new, "gigante", 1.98F, 4.79F, EntityClassification.CREATURE, b -> {});
  public static EntityType<GorgonEntity> GORGON_ENTITY = buildEntityType(GorgonEntity::new, "gorgon", 0.9F, 1.9F, EntityClassification.MONSTER, b -> {});
  public static EntityType<HarpyEntity> HARPY_ENTITY = buildEntityType(HarpyEntity::new, "harpy", 0.7F, 1.8F, EntityClassification.MONSTER, b -> {});
  public static EntityType<HealingSpellEntity> HEALING_SPELL_ENTITY = buildEntityType(HealingSpellEntity::new, "healing_spell", 0.25F, 0.25F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning().trackingRange(4).func_233608_b_(10));
//  public static EntityType<HydraEntity> HYDRA_ENTITY = buildEntityType(HydraEntity::new, "hydra", 1.4F, 1.2F, EntityClassification.MONSTER, b -> b.immuneToFire());
//  public static EntityType<HydraHeadEntity> HYDRA_HEAD_ENTITY = buildEntityType(HydraHeadEntity::new, "hydra_head", 0.5F, 0.9F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning());
  public static EntityType<MadCowEntity> MAD_COW_ENTITY = buildEntityType(MadCowEntity::new, "mad_cow", 0.9F, 1.4F, EntityClassification.CREATURE, b -> {});
  public static EntityType<MinotaurEntity> MINOTAUR_ENTITY = buildEntityType(MinotaurEntity::new, "minotaur", 0.7F, 1.94F, EntityClassification.MONSTER, b -> {});
  public static EntityType<NaiadEntity> NAIAD_ENTITY = buildEntityType(NaiadEntity::new, "naiad", 0.48F, 1.8F, EntityClassification.WATER_CREATURE, b -> {});
  public static EntityType<OrthusEntity> ORTHUS_ENTITY = buildEntityType(OrthusEntity::new, "orthus", 0.6F, 0.85F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<OrthusHeadItemEntity> ORTHUS_HEAD_ITEM_ENTITY = buildEntityType(OrthusHeadItemEntity::new, "orthus_head_item", 0.25F, 0.25F, EntityClassification.MISC, b -> b.disableSummoning().trackingRange(6).func_233608_b_(20));
  public static EntityType<PegasusEntity> PEGASUS_ENTITY = buildEntityType(PegasusEntity::new, "pegasus", 1.39F, 1.98F, EntityClassification.CREATURE, b -> {});
  public static EntityType<PoisonSpitEntity> POISON_SPIT_ENTITY = buildEntityType(PoisonSpitEntity::new, "poison_spit", 0.25F, 0.25F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning().trackingRange(4).func_233608_b_(10));
  public static EntityType<PythonEntity> PYTHON_ENTITY = buildEntityType(PythonEntity::new, "python", 1.4F, 1.9F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<SatyrEntity> SATYR_ENTITY = buildEntityType(SatyrEntity::new, "satyr", 0.67F, 1.8F, EntityClassification.CREATURE, b -> {});
  public static EntityType<ShadeEntity> SHADE_ENTITY = buildEntityType(ShadeEntity::new, "shade", 0.67F, 1.8F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<SirenEntity> SIREN_ENTITY = buildEntityType(SirenEntity::new, "siren", 0.6F, 1.9F, EntityClassification.WATER_CREATURE, b -> {});
  public static EntityType<SpartiEntity> SPARTI_ENTITY = buildEntityType(SpartiEntity::new, "sparti", 0.6F, 1.98F, EntityClassification.CREATURE, b -> {});
  public static EntityType<SpearEntity> SPEAR_ENTITY = buildEntityType(SpearEntity::new, "spear", 0.5F, 0.5F, EntityClassification.MISC, b -> b.disableSummoning().trackingRange(4).func_233608_b_(20));
  public static EntityType<SwineSpellEntity> SWINE_SPELL_ENTITY = buildEntityType(SwineSpellEntity::new, "swine_spell", 0.25F, 0.25F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning().trackingRange(4).func_233608_b_(10));
  public static EntityType<TalosEntity> TALOS_ENTITY = buildEntityType(TalosEntity::new, "talos", 1.98F, 4.96F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<UnicornEntity> UNICORN_ENTITY = buildEntityType(UnicornEntity::new, "unicorn", 1.39F, 1.98F, EntityClassification.CREATURE, b -> {});
  public static EntityType<WhirlEntity> WHIRL_ENTITY = buildEntityType(WhirlEntity::new, "whirl", 2.9F, 5.0F, EntityClassification.WATER_CREATURE, b -> {});

  // OBJECT HOLDERS //

  // Item //
  @ObjectHolder("panflute")
  public static final Item PANFLUTE = null;
  @ObjectHolder("wooden_lyre")
  public static final Item WOODEN_LYRE = null;
  @ObjectHolder("gold_lyre")
  public static final Item GOLD_LYRE = null;
  @ObjectHolder("iron_club")
  public static final Item IRON_CLUB = null;
  @ObjectHolder("stone_club")
  public static final Item STONE_CLUB = null;
  @ObjectHolder("wooden_club")
  public static final Item WOODEN_CLUB = null;
  @ObjectHolder("flint_knife")
  public static final Item FLINT_KNIFE = null;
  @ObjectHolder("dragon_tooth")
  public static final Item DRAGON_TOOTH = null;
  @ObjectHolder("ichor")
  public static final Item ICHOR = null;
  @ObjectHolder("horn")
  public static final Item HORN = null;
  @ObjectHolder("thunderbolt")
  public static final Item THUNDERBOLT = null;
  @ObjectHolder("helm_of_darkness")
  public static final Item HELM_OF_DARKNESS = null;
  @ObjectHolder("winged_sandals")
  public static final Item WINGED_SANDALS = null;
  @ObjectHolder("magic_feather")
  public static final Item MAGIC_FEATHER = null;
  @ObjectHolder("golden_bridle")
  public static final Item GOLDEN_BRIDLE = null;
  @ObjectHolder("snakeskin")
  public static final Item SNAKESKIN = null;
  @ObjectHolder("purified_snakeskin")
  public static final Item PURIFIED_SNAKESKIN = null;
  @ObjectHolder("tough_snakeskin")
  public static final Item TOUGH_SNAKESKIN = null;
  @ObjectHolder("styxian_shard")
  public static final Item STYXIAN_SHARD = null;
  @ObjectHolder("boar_ear")
  public static final Item BOAR_EAR = null;
  @ObjectHolder("swine_wand")
  public static final Item SWINE_WAND = null;
  @ObjectHolder("golden_ball")
  public static final Item GOLDEN_BALL = null;
  @ObjectHolder("golden_string")
  public static final Item GOLDEN_STRING = null;
  @ObjectHolder("cursed_bow")
  public static final Item CURSED_BOW = null;
  @ObjectHolder("artemis_bow")
  public static final Item ARTEMIS_BOW = null;
  @ObjectHolder("apollo_bow")
  public static final Item APOLLO_BOW = null;
  @ObjectHolder("bident")
  public static final Item BIDENT = null;
  @ObjectHolder("wooden_spear")
  public static final Item WOODEN_SPEAR = null;
  @ObjectHolder("stone_spear")
  public static final Item STONE_SPEAR = null;
  @ObjectHolder("iron_spear")
  public static final Item IRON_SPEAR = null;
  
  // Block //
  @ObjectHolder("reeds")
  public static final Block REEDS = null;
  @ObjectHolder("olive_log")
  public static final Block OLIVE_LOG = null;
  @ObjectHolder("stripped_olive_log")
  public static final Block STRIPPED_OLIVE_LOG = null;
  @ObjectHolder("olive_wood")
  public static final Block OLIVE_WOOD = null;
  @ObjectHolder("stripped_olive_wood")
  public static final Block STRIPPED_OLIVE_WOOD = null;
  @ObjectHolder("olive_planks")
  public static final Block OLIVE_PLANKS = null;
  @ObjectHolder("olive_slab")
  public static final Block OLIVE_SLAB = null;
  @ObjectHolder("olive_stairs")
  public static final Block OLIVE_STAIRS = null;
  @ObjectHolder("olive_leaves")
  public static final Block OLIVE_LEAVES = null;
  @ObjectHolder("olive_sapling")
  public static final Block OLIVE_SAPLING = null;
  @ObjectHolder("golden_apple_leaves")
  public static final Block GOLDEN_APPLE_LEAVES = null;
  @ObjectHolder("golden_apple_sapling")
  public static final Block GOLDEN_APPLE_SAPLING = null;
  @ObjectHolder("nest")
  public static final Block NEST_BLOCK = null;
  @ObjectHolder("wild_rose")
  public static final Block WILD_ROSE = null;
  @ObjectHolder("limestone")
  public static final Block LIMESTONE = null;
  @ObjectHolder("limestone_slab")
  public static final Block LIMESTONE_SLAB = null;
  @ObjectHolder("limestone_stairs")
  public static final Block LIMESTONE_STAIRS = null;
  @ObjectHolder("polished_limestone")
  public static final Block POLISHED_LIMESTONE = null;
  @ObjectHolder("polished_limestone_slab")
  public static final Block POLISHED_LIMESTONE_SLAB = null;
  @ObjectHolder("polished_limestone_stairs")
  public static final Block POLISHED_LIMESTONE_STAIRS = null;
  @ObjectHolder("limestone_pillar")
  public static final Block LIMESTONE_PILLAR = null;
  @ObjectHolder("marble")
  public static final Block MARBLE = null;
  @ObjectHolder("marble_slab")
  public static final Block MARBLE_SLAB = null;
  @ObjectHolder("marble_stairs")
  public static final Block MARBLE_STAIRS = null;
  @ObjectHolder("polished_marble")
  public static final Block POLISHED_MARBLE = null; 
  @ObjectHolder("polished_marble_slab")
  public static final Block POLISHED_MARBLE_SLAB = null; 
  @ObjectHolder("polished_marble_stairs")
  public static final Block POLISHED_MARBLE_STAIRS = null;
  @ObjectHolder("marble_pillar")
  public static final Block MARBLE_PILLAR = null;
  @ObjectHolder("cretan_stone")
  public static final Block CRETAN_STONE = null;
  @ObjectHolder("chiseled_cretan_stone")
  public static final Block CHISELED_CRETAN_STONE = null;
  @ObjectHolder("cretan_stone_brick")
  public static final Block CRETAN_STONE_BRICK = null;
  @ObjectHolder("chiseled_cretan_stone_brick")
  public static final Block CHISELED_CRETAN_STONE_BRICK = null;
  @ObjectHolder("cracked_cretan_stone_brick")
  public static final Block CRACKED_CRETAN_STONE_BRICK = null;
  @ObjectHolder("polished_cretan_stone")
  public static final Block POLISHED_CRETAN_STONE = null;
  @ObjectHolder("cracked_polished_cretan_stone")
  public static final Block CRACKED_POLISHED_CRETAN_STONE = null;
  @ObjectHolder("limestone_statue")
  public static final Block LIMESTONE_STATUE = null;
  @ObjectHolder("marble_statue")
  public static final Block MARBLE_STATUE = null;
  @ObjectHolder("palladium")
  public static final Block PALLADIUM = null;
  @ObjectHolder("mysterious_box")
  public static final Block MYSTERIOUS_BOX = null;
  @ObjectHolder("gigante_head")
  public static final Block GIGANTE_HEAD = null;
  @ObjectHolder("orthus_head")
  public static final Block ORTHUS_HEAD = null;
  @ObjectHolder("cerberus_head")
  public static final Block CERBERUS_HEAD = null;
  @ObjectHolder("ichor_infused_block")
  public static final Block ICHOR_INFUSED_BLOCK = null;
  @ObjectHolder("golden_string")
  public static final Block GOLDEN_STRING_BLOCK = null;
  
  // Vase //
  @ObjectHolder("terracotta_vase")
  public static final Block TERRACOTTA_VASE = null;
  @ObjectHolder("white_terracotta_vase")
  public static final Block WHITE_TERRACOTTA_VASE = null;
  @ObjectHolder("orange_terracotta_vase")
  public static final Block ORANGE_TERRACOTTA_VASE = null;
  @ObjectHolder("magenta_terracotta_vase")
  public static final Block MAGENTA_TERRACOTTA_VASE = null;
  @ObjectHolder("light_blue_terracotta_vase")
  public static final Block LIGHT_BLUE_TERRACOTTA_VASE = null;
  @ObjectHolder("yellow_terracotta_vase")
  public static final Block YELLOW_TERRACOTTA_VASE = null;
  @ObjectHolder("lime_terracotta_vase")
  public static final Block LIME_TERRACOTTA_VASE = null;
  @ObjectHolder("pink_terracotta_vase")
  public static final Block PINK_TERRACOTTA_VASE = null;
  @ObjectHolder("gray_terracotta_vase")
  public static final Block GRAY_TERRACOTTA_VASE = null;
  @ObjectHolder("light_gray_terracotta_vase")
  public static final Block LIGHT_GRAY_TERRACOTTA_VASE = null;
  @ObjectHolder("cyan_terracotta_vase")
  public static final Block CYAN_TERRACOTTA_VASE = null;
  @ObjectHolder("purple_terracotta_vase")
  public static final Block PURPLE_TERRACOTTA_VASE = null;
  @ObjectHolder("blue_terracotta_vase")
  public static final Block BLUE_TERRACOTTA_VASE = null;
  @ObjectHolder("brown_terracotta_vase")
  public static final Block BROWN_TERRACOTTA_VASE = null;
  @ObjectHolder("green_terracotta_vase")
  public static final Block GREEN_TERRACOTTA_VASE = null;
  @ObjectHolder("red_terracotta_vase")
  public static final Block RED_TERRACOTTA_VASE = null;
  @ObjectHolder("black_terracotta_vase")
  public static final Block BLACK_TERRACOTTA_VASE = null;
  
  // Altar //
  @ObjectHolder("altar_aphrodite")
  public static final Block ALTAR_APHRODITE = null;
  @ObjectHolder("altar_apollo")
  public static final Block ALTAR_APOLLO = null;
  @ObjectHolder("altar_ares")
  public static final Block ALTAR_ARES = null;
  @ObjectHolder("altar_artemis")
  public static final Block ALTAR_ARTEMIS = null;
  @ObjectHolder("altar_athena")
  public static final Block ALTAR_ATHENA = null;
  @ObjectHolder("altar_demeter")
  public static final Block ALTAR_DEMETER = null;
  @ObjectHolder("altar_dionysus")
  public static final Block ALTAR_DIONYSUS = null;
  @ObjectHolder("altar_hades")
  public static final Block ALTAR_HADES = null;
  @ObjectHolder("altar_hecate")
  public static final Block ALTAR_HECATE = null;
  @ObjectHolder("altar_hephaestus")
  public static final Block ALTAR_HEPHAESTUS = null;
  @ObjectHolder("altar_hera")
  public static final Block ALTAR_HERA = null;
  @ObjectHolder("altar_hermes")
  public static final Block ALTAR_HERMES = null;
  @ObjectHolder("altar_hestia")
  public static final Block ALTAR_HESTIA = null;
  @ObjectHolder("altar_persephone")
  public static final Block ALTAR_PERSEPHONE = null;
  @ObjectHolder("altar_poseidon")
  public static final Block ALTAR_POSEIDON = null;
  @ObjectHolder("altar_zeus")
  public static final Block ALTAR_ZEUS = null;

  // Tile Entity //  
  @ObjectHolder("statue_te")
  public static final TileEntityType<StatueTileEntity> STATUE_TE = null;
  @ObjectHolder("vase_te")
  public static final TileEntityType<VaseTileEntity> VASE_TE = null;
  @ObjectHolder("mob_head_te")
  public static final TileEntityType<MobHeadTileEntity> BOSS_HEAD_TE = null;

  // Container Type //
  @ObjectHolder("statue_container")
  public static final ContainerType<StatueContainer> STATUE_CONTAINER = null;
  @ObjectHolder("deity_container")
  public static final ContainerType<DeityContainer> DEITY_CONTAINER = null;
  
  // Effect //
  @ObjectHolder("stunned")
  public static final Effect STUNNED_EFFECT = null;
  @ObjectHolder("petrified")
  public static final Effect PETRIFIED_EFFECT = null;
  @ObjectHolder("mirror")
  public static final Effect MIRROR_EFFECT = null;
  @ObjectHolder("swine")
  public static final Effect SWINE_EFFECT = null;
  
  // Enchantment //
  @ObjectHolder("overstep")
  public static final Enchantment OVERSTEP_ENCHANTMENT = null;
  @ObjectHolder("smashing")
  public static final Enchantment SMASHING_ENCHANTMENT = null;
  @ObjectHolder("mirror")
  public static final Enchantment MIRROR_ENCHANTMENT = null;
  @ObjectHolder("poison")
  public static final Enchantment POISON_ENCHANTMENT = null;
  @ObjectHolder("flying")
  public static final Enchantment FLYING_ENCHANTMENT = null;
  @ObjectHolder("lord_of_the_sea")
  public static final Enchantment LORD_OF_THE_SEA_ENCHANTMENT = null;
  @ObjectHolder("fireflash")
  public static final Enchantment FIREFLASH_ENCHANTMENT = null;
  @ObjectHolder("daybreak")
  public static final Enchantment DAYBREAK_ENCHANTMENT = null;
  @ObjectHolder("raising")
  public static final Enchantment RAISING_ENCHANTMENT = null;
  
  // Potion //
  @ObjectHolder("mirror")
  public static final Potion MIRROR_POTION = null;
  @ObjectHolder("long_mirror")
  public static final Potion LONG_MIRROR_POTION = null;
  @ObjectHolder("swine")
  public static final Potion SWINE_POTION = null;
  @ObjectHolder("long_swine")
  public static final Potion LONG_SWINE_POTION = null;
  
  // Particle Type //
  @ObjectHolder("gorgon_face")
  public static final BasicParticleType GORGON_PARTICLE = new BasicParticleType(true);

  protected static ItemGroup GREEK_GROUP = new ItemGroup("greekfantasy") {
    @Override
    public ItemStack createIcon() { return new ItemStack(PANFLUTE); }
  };
  
  protected static final Predicate<IServerWorld> DIMENSION_MOB_PLACEMENT = world -> {
    return GreekFantasy.CONFIG.IS_SPAWNS_WHITELIST.get() == GreekFantasy.CONFIG.SPAWNS_DIMENSION_WHITELIST.get().contains(world.getWorld().getDimensionKey().getLocation().toString());
  };

  // REGISTRY METHODS //

  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    GreekFantasy.LOGGER.debug("registerEntities");
    // entity types have already been created, now they are actually registered (along with placements)
    registerEntityType(event, ARA_ENTITY, "ara", AraEntity::getAttributes, AraEntity::canAraSpawnOn);
    registerEntityType(event, ARION_ENTITY, "arion", ArionEntity::getAttributes, ArionEntity::canSpawnOn);
    registerEntityType(event, CENTAUR_ENTITY, "centaur", CentaurEntity::getAttributes, CentaurEntity::canSpawnOn);
    registerEntityType(event, CERASTES_ENTITY, "cerastes", CerastesEntity::getAttributes, CerastesEntity::canCerastesSpawnOn);
    registerEntityType(event, CERBERUS_ENTITY, "cerberus", CerberusEntity::getAttributes, null);
    registerEntityType(event, CHARYBDIS_ENTITY, "charybdis", CharybdisEntity::getAttributes, null);
    registerEntityType(event, CIRCE_ENTITY, "circe", CirceEntity::getAttributes, null);
    registerEntityType(event, CRETAN_ENTITY, "cretan", CretanEntity::getAttributes, null);
    registerEntityType(event, CYPRIAN_ENTITY, "cyprian", CyprianEntity::getAttributes, CyprianEntity::canSpawnOn);
    registerEntityType(event, CYCLOPES_ENTITY, "cyclopes", CyclopesEntity::getAttributes, CyclopesEntity::canCyclopesSpawnOn);
    registerEntityType(event, DRAKAINA_ENTITY, "drakaina", DrakainaEntity::getAttributes, DrakainaEntity::canMonsterSpawnInLight);
    registerEntityType(event, DRYAD_ENTITY, "dryad", DryadEntity::getAttributes, DryadEntity::canSpawnOn);
    registerEntityType(event, ELPIS_ENTITY, "elpis", ElpisEntity::getAttributes, null);
    registerEntityType(event, EMPUSA_ENTITY, "empusa", EmpusaEntity::getAttributes, EmpusaEntity::canMonsterSpawnInLight);
    registerEntityType(event, FURY_ENTITY, "fury", FuryEntity::getAttributes, FuryEntity::canSpawnOn);
    registerEntityType(event, GERYON_ENTITY, "geryon", GeryonEntity::getAttributes, null);
    registerEntityType(event, GIANT_BOAR_ENTITY, "giant_boar", GiantBoarEntity::getAttributes, null);
    registerEntityType(event, GIGANTE_ENTITY, "gigante", GiganteEntity::getAttributes, GiganteEntity::canSpawnOn);
    registerEntityType(event, GORGON_ENTITY, "gorgon", GorgonEntity::getAttributes, GorgonEntity::canMonsterSpawn);
    registerEntityType(event, HARPY_ENTITY, "harpy", HarpyEntity::getAttributes, HarpyEntity::canMonsterSpawn);
//    registerEntityType(event, HYDRA_ENTITY, HydraEntity::getAttributes, null);
    registerEntityType(event, MAD_COW_ENTITY, "mad_cow", MadCowEntity::getAttributes, MadCowEntity::canSpawnOn);
    registerEntityType(event, MINOTAUR_ENTITY, "minotaur", MinotaurEntity::getAttributes, MinotaurEntity::canMonsterSpawnInLight);
    registerEntityType(event, NAIAD_ENTITY, "naiad", NaiadEntity::getAttributes, NaiadEntity::canNaiadSpawnOn);
    registerEntityType(event, ORTHUS_ENTITY, "orthus", OrthusEntity::getAttributes, OrthusEntity::canSpawnOn);
    registerEntityType(event, PEGASUS_ENTITY, "pegasus", PegasusEntity::getAttributes, PegasusEntity::canSpawnOn);
    registerEntityType(event, PYTHON_ENTITY, "python", PythonEntity::getAttributes, null);
    registerEntityType(event, SATYR_ENTITY, "satyr", SatyrEntity::getAttributes, SatyrEntity::canSpawnOn);
    registerEntityType(event, SHADE_ENTITY, "shade", ShadeEntity::getAttributes, ShadeEntity::canMonsterSpawnInLight);
    registerEntityType(event, SIREN_ENTITY, "siren", SirenEntity::getAttributes, SirenEntity::canSirenSpawnOn);
    registerEntityType(event, SPARTI_ENTITY, "sparti", SpartiEntity::getAttributes, null);
    registerEntityType(event, TALOS_ENTITY, "talos", TalosEntity::getAttributes, null);
    registerEntityType(event, UNICORN_ENTITY, "unicorn", UnicornEntity::getAttributes, UnicornEntity::canSpawnOn);
    registerEntityType(event, WHIRL_ENTITY, "whirl", WhirlEntity::getAttributes, WhirlEntity::canWhirlSpawnOn);
    event.getRegistry().register(SPEAR_ENTITY.setRegistryName(MODID, "spear"));
    event.getRegistry().register(CURSE_ENTITY.setRegistryName(MODID, "curse"));
    event.getRegistry().register(DRAGON_TOOTH_ENTITY.setRegistryName(MODID, "dragon_tooth"));
    event.getRegistry().register(HEALING_SPELL_ENTITY.setRegistryName(MODID, "healing_spell"));
//    event.getRegistry().register(HYDRA_HEAD_ENTITY);
    event.getRegistry().register(ORTHUS_HEAD_ITEM_ENTITY.setRegistryName(MODID, "orthus_head_item"));
    event.getRegistry().register(POISON_SPIT_ENTITY.setRegistryName(MODID, "poison_spit"));
    event.getRegistry().register(SWINE_SPELL_ENTITY.setRegistryName(MODID, "swine_spell"));
  }
  
  @SubscribeEvent
  public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
    GreekFantasy.LOGGER.debug("registerTileEntities");
    event.getRegistry().register(
        TileEntityType.Builder.create(StatueTileEntity::new, 
            LIMESTONE_STATUE, MARBLE_STATUE, PALLADIUM, 
            ALTAR_APHRODITE, ALTAR_APOLLO, ALTAR_ARES, ALTAR_ARTEMIS, ALTAR_ATHENA, ALTAR_DEMETER,
            ALTAR_DIONYSUS, ALTAR_HADES, ALTAR_HECATE, ALTAR_HEPHAESTUS, ALTAR_HERA, ALTAR_HERMES, 
            ALTAR_HESTIA, ALTAR_PERSEPHONE, ALTAR_POSEIDON, ALTAR_ZEUS)
        .build(null).setRegistryName(MODID, "statue_te")
    );
    event.getRegistry().register(
        TileEntityType.Builder.create(VaseTileEntity::new, TERRACOTTA_VASE, 
            WHITE_TERRACOTTA_VASE, ORANGE_TERRACOTTA_VASE, MAGENTA_TERRACOTTA_VASE, LIGHT_BLUE_TERRACOTTA_VASE, 
            YELLOW_TERRACOTTA_VASE, LIME_TERRACOTTA_VASE, PINK_TERRACOTTA_VASE, GRAY_TERRACOTTA_VASE, 
            LIGHT_GRAY_TERRACOTTA_VASE, CYAN_TERRACOTTA_VASE, PURPLE_TERRACOTTA_VASE, BLUE_TERRACOTTA_VASE, 
            BROWN_TERRACOTTA_VASE, GREEN_TERRACOTTA_VASE, RED_TERRACOTTA_VASE, BLACK_TERRACOTTA_VASE)
        .build(null).setRegistryName(MODID, "vase_te")
    );
    event.getRegistry().register(
        TileEntityType.Builder.create(MobHeadTileEntity::new, GIGANTE_HEAD, ORTHUS_HEAD, CERBERUS_HEAD)
        .build(null).setRegistryName(MODID, "mob_head_te")
    );
  }
  
  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    GreekFantasy.LOGGER.debug("registerContainers");
    ContainerType<StatueContainer> statueContainer = IForgeContainerType.create((windowId, inv, data) -> {
      final boolean isFemale = data.readBoolean();
      final BlockPos blockpos = data.readBlockPos();
      final CompoundNBT poseTag = data.readCompoundTag();
      final String name = data.readString();
      final StatuePose pose = new StatuePose(poseTag);
      final Direction facing = Direction.byHorizontalIndex(data.readByte());
      return new StatueContainer(windowId, inv, new Inventory(2), pose, isFemale, name, blockpos, facing);
    });
    ContainerType<DeityContainer> deityContainer = IForgeContainerType.create((windowId, inv, data) -> {
      final IFavor favor = GreekFantasy.FAVOR.getDefaultInstance();
      GreekFantasy.FAVOR.readNBT(favor, null, data.readCompoundTag());
      return new DeityContainer(windowId, inv, favor);
    });
    event.getRegistry().register(statueContainer.setRegistryName(MODID, "statue_container"));
    event.getRegistry().register(deityContainer.setRegistryName(MODID, "deity_container"));
  }

  @SubscribeEvent
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    GreekFantasy.LOGGER.debug("registerBlocks");
        
    registerLogLeavesPlanksEtc(event, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD), "olive");
    registerLeaves(event, "golden_apple");
    
    registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.create(Material.ROCK, MaterialColor.QUARTZ).setRequiresTool().hardnessAndResistance(1.5F, 6.0F), "marble");
    registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.create(Material.ROCK, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(1.5F, 6.0F), "limestone");
    
    registerBlockPolishedChiseledAndBricks(event, AbstractBlock.Properties.create(Material.ROCK, MaterialColor.GRAY).setRequiresTool().hardnessAndResistance(99.0F, 1200.0F), "cretan_stone");
    
    event.getRegistry().registerAll(
        new ReedsBlock(AbstractBlock.Properties.create(Material.OCEAN_PLANT).doesNotBlockMovement().zeroHardnessAndResistance().tickRandomly().sound(SoundType.CROP))
          .setRegistryName(MODID, "reeds"),
        new SaplingBlock(new OliveTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly()
            .zeroHardnessAndResistance().notSolid().sound(SoundType.PLANT))
          .setRegistryName(MODID, "olive_sapling"),
        new SaplingBlock(new GoldenAppleTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly()
            .zeroHardnessAndResistance().notSolid().sound(SoundType.PLANT))
          .setRegistryName(MODID, "golden_apple_sapling"),
        new NestBlock(AbstractBlock.Properties.create(Material.ORGANIC, MaterialColor.BROWN).hardnessAndResistance(0.5F).sound(SoundType.PLANT)
            .setNeedsPostProcessing((s, r, p) -> true).notSolid())
          .setRegistryName(MODID, "nest"),
        new WildRoseBlock(Effects.SATURATION, 9, AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.PLANT))
          .setRegistryName(GreekFantasy.MODID, "wild_rose"),
        new CappedPillarBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.QUARTZ).setRequiresTool().hardnessAndResistance(1.5F, 6.0F)
            .setNeedsPostProcessing((s, r, p) -> true).notSolid())
          .setRegistryName(MODID, "marble_pillar"),
        new CappedPillarBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(1.5F, 6.0F)
            .setNeedsPostProcessing((s, r, p) -> true).notSolid())
          .setRegistryName(MODID, "limestone_pillar"),
        new StatueBlock(StatueBlock.StatueMaterial.MARBLE)
          .setRegistryName(MODID, "marble_statue"),
        new StatueBlock(StatueBlock.StatueMaterial.LIMESTONE)
          .setRegistryName(MODID, "limestone_statue"),
        new StatueBlock(StatueBlock.StatueMaterial.WOOD, te -> {
          te.setStatuePose(StatuePoses.STANDING_HOLDING_DRAMATIC);
          te.setStatueFemale(true);
          te.setItem(new ItemStack(Items.SOUL_TORCH), HandSide.RIGHT);
        }).setRegistryName(MODID, "palladium"),
        new MysteriousBoxBlock(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(0.8F, 2.0F).sound(SoundType.WOOD).notSolid())
          .setRegistryName(MODID, "mysterious_box"),
        new MobHeadBlock(HeadType.GIGANTE, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "gigante_head"),
        new OrthusHeadBlock(HeadType.ORTHUS, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "orthus_head"),
        new MobHeadBlock(HeadType.CERBERUS, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "cerberus_head"),
        new IchorInfusedBlock(AbstractBlock.Properties.from(Blocks.GOLD_BLOCK))
          .setRegistryName(MODID, "ichor_infused_block"),
        new GoldenStringBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).setLightLevel(b -> 8).zeroHardnessAndResistance().doesNotBlockMovement().notSolid())
          .setRegistryName(MODID, "golden_string")
    );
    
    // Altar blocks
    event.getRegistry().registerAll(
      makeDeityStatue("aphrodite", StatueBlock.StatueMaterial.MARBLE, StatuePoses.APHRODITE_POSE)
        .setRegistryName(MODID, "altar_aphrodite"),
      makeDeityStatue("apollo", StatueBlock.StatueMaterial.MARBLE, StatuePoses.APOLLO_POSE)
        .setRegistryName(MODID, "altar_apollo"),
      makeDeityStatue("ares", StatueBlock.StatueMaterial.MARBLE, StatuePoses.ARES_POSE)
        .setRegistryName(MODID, "altar_ares"),
      makeDeityStatue("artemis", StatueBlock.StatueMaterial.MARBLE, StatuePoses.ARTEMIS_POSE)
        .setRegistryName(MODID, "altar_artemis"),
      makeDeityStatue("athena", StatueBlock.StatueMaterial.MARBLE, StatuePoses.ATHENA_POSE)
        .setRegistryName(MODID, "altar_athena"),
      makeDeityStatue("demeter", StatueBlock.StatueMaterial.MARBLE, StatuePoses.ARES_POSE)
        .setRegistryName(MODID, "altar_demeter"),
      makeDeityStatue("dionysus", StatueBlock.StatueMaterial.MARBLE, StatuePoses.ATHENA_POSE)
        .setRegistryName(MODID, "altar_dionysus"),
      makeDeityStatue("hades", StatueBlock.StatueMaterial.MARBLE, StatuePoses.HADES_POSE)
        .setRegistryName(MODID, "altar_hades"),
      makeDeityStatue("hecate", StatueBlock.StatueMaterial.MARBLE, StatuePoses.HECATE_POSE)
        .setRegistryName(MODID, "altar_hecate"),
      makeDeityStatue("hephaestus", StatueBlock.StatueMaterial.MARBLE, StatuePoses.HEPHAESTUS_POSE)
        .setRegistryName(MODID, "altar_hephaestus"),
      makeDeityStatue("hera", StatueBlock.StatueMaterial.MARBLE, StatuePoses.HERA_POSE)
        .setRegistryName(MODID, "altar_hera"),
      makeDeityStatue("hermes", StatueBlock.StatueMaterial.MARBLE, StatuePoses.HERMES_POSE)
        .setRegistryName(MODID, "altar_hermes"),
      makeDeityStatue("hestia", StatueBlock.StatueMaterial.MARBLE, StatuePoses.HERA_POSE)
        .setRegistryName(MODID, "altar_hestia"),
      makeDeityStatue("persephone", StatueBlock.StatueMaterial.MARBLE, StatuePoses.PERSEPHONE_POSE)
        .setRegistryName(MODID, "altar_persephone"),
      makeDeityStatue("poseidon", StatueBlock.StatueMaterial.MARBLE, StatuePoses.POSEIDON_POSE)
        .setRegistryName(MODID, "altar_poseidon"),
      makeDeityStatue("zeus", StatueBlock.StatueMaterial.MARBLE, StatuePoses.ZEUS_POSE)
        .setRegistryName(MODID, "altar_zeus")
    );
    
    // Vase blocks
    event.getRegistry().register(new VaseBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(0.5F, 1.0F).notSolid())
        .setRegistryName(MODID, "terracotta_vase"));
    for(final DyeColor d : DyeColor.values()) {
      event.getRegistry().register(new VaseBlock(AbstractBlock.Properties.create(Material.ROCK, d.getMapColor()).hardnessAndResistance(0.5F, 1.0F).notSolid())
          .setRegistryName(MODID, d.getString() + "_terracotta_vase"));
    }
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    GreekFantasy.LOGGER.debug("registerItems");
    final boolean nerfAmbrosia = GreekFantasy.CONFIG.NERF_AMBROSIA.get();
    // items
    event.getRegistry().registerAll(
        new PanfluteItem(new Item.Properties().group(GREEK_GROUP).maxStackSize(1))
          .setRegistryName(MODID, "panflute"),
        new LyreItem(SoundEvents.BLOCK_NOTE_BLOCK_HARP, new Item.Properties().group(GREEK_GROUP).maxStackSize(1))
          .setRegistryName(MODID, "wooden_lyre"),
        new LyreItem(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, new Item.Properties().group(GREEK_GROUP).maxStackSize(1))
          .setRegistryName(MODID, "gold_lyre"),
        new FlintKnifeItem(ItemTier.WOOD, 3, -2.0F, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "flint_knife"),
        new ClubItem(ItemTier.IRON, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "iron_club"),
        new ClubItem(ItemTier.STONE, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "stone_club"),
        new ClubItem(ItemTier.WOOD, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "wooden_club"),
        new IvorySwordItem(ItemTier.DIAMOND, 3, -2.2F, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "ivory_sword"),
        new ConchItem(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP))
          .setRegistryName(MODID, "conch"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "talos_heart"),
        new ThunderboltItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.THUNDERBOLT_DURABILITY.get()))
          .setRegistryName(MODID, "thunderbolt"),
        new BagOfWindItem(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.BAG_OF_WIND_DURABILITY.get()))
          .setRegistryName(MODID, "bag_of_wind"),
        new UnicornHornItem(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.UNICORN_HORN_DURABILITY.get()))
          .setRegistryName(MODID, "unicorn_horn"),
        new HealingRodItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.HEALING_ROD_DURABILITY.get()))
          .setRegistryName(MODID, "healing_rod"),
        new DragonToothItem(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP))
          .setRegistryName(MODID, "dragon_tooth"),
        new SwineWandItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.SWINE_WAND_DURABILITY.get()))
          .setRegistryName(MODID, "swine_wand"),
        new EnchantedBowItem.CursedBowItem(new Item.Properties().maxDamage(384).group(GREEK_GROUP))
          .setRegistryName(MODID, "cursed_bow"),
        new EnchantedBowItem.ApolloBowItem(new Item.Properties().maxDamage(620).group(GREEK_GROUP))
          .setRegistryName(MODID, "apollo_bow"),
        new EnchantedBowItem.ArtemisBowItem(new Item.Properties().maxDamage(562).group(GREEK_GROUP))
          .setRegistryName(MODID, "artemis_bow"),
        new BidentItem(ItemTier.DIAMOND, new Item.Properties().group(GREEK_GROUP)
            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("bident")))
          .setRegistryName(MODID, "bident"),
        new SpearItem(ItemTier.WOOD, new Item.Properties().group(GREEK_GROUP)
            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("wooden_spear")))
          .setRegistryName(MODID, "wooden_spear"),
        new SpearItem(ItemTier.STONE, new Item.Properties().group(GREEK_GROUP)
            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("stone_spear")))
          .setRegistryName(MODID, "stone_spear"),
        new SpearItem(ItemTier.IRON, new Item.Properties().group(GREEK_GROUP)
            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("iron_spear")))
          .setRegistryName(MODID, "iron_spear"),
        new MirrorItem(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "mirror"),
        new SnakeskinArmorItem(EquipmentSlotType.HEAD, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "snakeskin_helmet"),
        new SnakeskinArmorItem(EquipmentSlotType.CHEST, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "snakeskin_chestplate"),
        new SnakeskinArmorItem(EquipmentSlotType.LEGS, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "snakeskin_leggings"),
        new SnakeskinArmorItem(EquipmentSlotType.FEET, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "snakeskin_boots"),
        new WingedSandalsItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP))
          .setRegistryName(MODID, "winged_sandals"),
        new HelmOfDarknessItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP))
          .setRegistryName(MODID, "helm_of_darkness"),
        new GorgonBloodItem(new Item.Properties().maxStackSize(16).containerItem(Items.GLASS_BOTTLE).group(GREEK_GROUP))
          .setRegistryName(MODID, "gorgon_blood"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "horn")
    );
    
    event.getRegistry().registerAll(
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "boar_ear"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "boar_tusk"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "golden_bridle"),
        new AmbrosiaItem(new Item.Properties().food(nerfAmbrosia ? Foods.GOLDEN_APPLE : Foods.ENCHANTED_GOLDEN_APPLE)
            .group(GREEK_GROUP).rarity(nerfAmbrosia ? Rarity.RARE : Rarity.EPIC)) {
        }.setRegistryName(MODID, "ambrosia"),
        new HornOfPlentyItem(new Item.Properties().maxDamage(24).rarity(Rarity.RARE).group(GREEK_GROUP))
          .setRegistryName(MODID, "horn_of_plenty"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "magic_feather"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "snakeskin"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "purified_snakeskin"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "tough_snakeskin"),
        new Item(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP)){
          @Override
          public boolean hasEffect(ItemStack stack) { return true; }
        }.setRegistryName(MODID, "ichor"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "dog_claw"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "fiery_hide"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "styxian_shard"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "snake_fang"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "cursed_hair"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "fiery_bat_wing"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "golden_string"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "golden_ball")
    );
    
    // block items
    registerItemBlocks(event, REEDS, OLIVE_SAPLING, GOLDEN_APPLE_SAPLING, NEST_BLOCK, WILD_ROSE, 
        GOLDEN_APPLE_LEAVES, OLIVE_LOG, STRIPPED_OLIVE_LOG, OLIVE_WOOD, STRIPPED_OLIVE_WOOD, 
        OLIVE_PLANKS, OLIVE_SLAB, OLIVE_STAIRS, OLIVE_LEAVES, 
        MARBLE, MARBLE_SLAB, MARBLE_STAIRS, POLISHED_MARBLE, POLISHED_MARBLE_SLAB, 
        POLISHED_MARBLE_STAIRS, MARBLE_PILLAR, MARBLE_STATUE, PALLADIUM, 
        CRETAN_STONE, CHISELED_CRETAN_STONE, CRETAN_STONE_BRICK, CHISELED_CRETAN_STONE_BRICK,
        CRACKED_CRETAN_STONE_BRICK, POLISHED_CRETAN_STONE, CRACKED_POLISHED_CRETAN_STONE,
        ALTAR_APHRODITE, ALTAR_APOLLO, ALTAR_ARES, ALTAR_ARTEMIS, ALTAR_ATHENA, ALTAR_DEMETER,
        ALTAR_DIONYSUS, ALTAR_HADES, ALTAR_HECATE, ALTAR_HEPHAESTUS, ALTAR_HERA, ALTAR_HERMES, 
        ALTAR_HESTIA, ALTAR_PERSEPHONE, ALTAR_POSEIDON, ALTAR_ZEUS,
        LIMESTONE, LIMESTONE_SLAB, LIMESTONE_STAIRS, POLISHED_LIMESTONE, POLISHED_LIMESTONE_SLAB, 
        POLISHED_LIMESTONE_STAIRS, LIMESTONE_PILLAR, LIMESTONE_STATUE, ICHOR_INFUSED_BLOCK,
        TERRACOTTA_VASE, WHITE_TERRACOTTA_VASE, ORANGE_TERRACOTTA_VASE, MAGENTA_TERRACOTTA_VASE, 
        LIGHT_BLUE_TERRACOTTA_VASE, YELLOW_TERRACOTTA_VASE, LIME_TERRACOTTA_VASE, 
        PINK_TERRACOTTA_VASE, GRAY_TERRACOTTA_VASE, LIGHT_GRAY_TERRACOTTA_VASE, 
        CYAN_TERRACOTTA_VASE, PURPLE_TERRACOTTA_VASE, BLUE_TERRACOTTA_VASE,
        BROWN_TERRACOTTA_VASE, GREEN_TERRACOTTA_VASE, RED_TERRACOTTA_VASE, BLACK_TERRACOTTA_VASE);
    
    event.getRegistry().register(new MobHeadItem(GIGANTE_HEAD, new Item.Properties()
        .group(GREEK_GROUP).setISTER(() -> greekfantasy.client.render.tileentity.ClientISTERProvider::bakeGiganteHeadISTER))
        .setRegistryName(MODID, "gigante_head"));
    event.getRegistry().register(new OrthusHeadItem(ORTHUS_HEAD, new Item.Properties()
        .group(GREEK_GROUP).setISTER(() -> greekfantasy.client.render.tileentity.ClientISTERProvider::bakeOrthusHeadISTER))
        .setRegistryName(MODID, "orthus_head"));
    event.getRegistry().register(new MobHeadItem(CERBERUS_HEAD, new Item.Properties()
        .group(GREEK_GROUP).setISTER(() -> greekfantasy.client.render.tileentity.ClientISTERProvider::bakeCerberusHeadISTER))
        .setRegistryName(MODID, "cerberus_head"));
    
    // mysterious box item
    event.getRegistry().register(new BlockItem(MYSTERIOUS_BOX, new Item.Properties().group(GREEK_GROUP)) {
      @OnlyIn(Dist.CLIENT)
      public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("block.greekfantasy.mysterious_box.tooltip").mergeStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
      }
    }.setRegistryName(MODID, "mysterious_box"));
    
    // spawn eggs
    registerSpawnEgg(event, ARA_ENTITY, "ara", 0xffffff, 0xbbbbbb);
    registerSpawnEgg(event, ARION_ENTITY, "arion", 0xdfc014, 0xb58614);
    registerSpawnEgg(event, CENTAUR_ENTITY, "centaur", 0x734933, 0x83251f);
    registerSpawnEgg(event, CERASTES_ENTITY, "cerastes", 0x847758, 0x997c4d);
    registerSpawnEgg(event, CHARYBDIS_ENTITY, "charybdis", 0x2e5651, 0x411e5e);
    registerSpawnEgg(event, CIRCE_ENTITY, "circe", 0x844797, 0xe8c669);
    registerSpawnEgg(event, CRETAN_ENTITY, "cretan", 0x2a2a2a, 0x734933);
    registerSpawnEgg(event, CYCLOPES_ENTITY, "cyclopes", 0xda662c, 0x2c1e0e);
    registerSpawnEgg(event, CYPRIAN_ENTITY, "cyprian", 0x443626, 0x83251f);
    registerSpawnEgg(event, DRAKAINA_ENTITY, "drakaina", 0x724e36, 0x398046);
    registerSpawnEgg(event, DRYAD_ENTITY, "dryad", 0x443626, 0xfed93f);
    registerSpawnEgg(event, ELPIS_ENTITY, "elpis", 0xe7aae4, 0xeeeeee);
    registerSpawnEgg(event, EMPUSA_ENTITY, "empusa", 0x222222, 0x83251f);
    registerSpawnEgg(event, FURY_ENTITY, "fury", 0xbd4444, 0x6c2426);
    registerSpawnEgg(event, GIANT_BOAR_ENTITY, "giant_boar", 0x5b433a, 0xe8a074);
    registerSpawnEgg(event, GIGANTE_ENTITY, "gigante", 0xd3dba7, 0x6a602b);
    registerSpawnEgg(event, GORGON_ENTITY, "gorgon", 0x3a8228, 0xbcbcbc);
    registerSpawnEgg(event, HARPY_ENTITY, "harpy", 0x724e36, 0x332411);
    registerSpawnEgg(event, MAD_COW_ENTITY, "mad_cow", 0x443626, 0xcf9797);
    registerSpawnEgg(event, MINOTAUR_ENTITY, "minotaur", 0x443626, 0x734933);
    registerSpawnEgg(event, NAIAD_ENTITY, "naiad",  0x7caba1, 0xe67830);
    registerSpawnEgg(event, ORTHUS_ENTITY, "orthus", 0x493569, 0xe42e2e);
    registerSpawnEgg(event, PEGASUS_ENTITY, "pegasus", 0x916535, 0xe8e8e8);
    registerSpawnEgg(event, PYTHON_ENTITY, "python", 0x3a8228, 0x1e4c11);
    registerSpawnEgg(event, SATYR_ENTITY, "satyr", 0x54371d, 0xa16648);
    registerSpawnEgg(event, SHADE_ENTITY, "shade", 0x222222, 0x000000);
    registerSpawnEgg(event, SIREN_ENTITY, "siren", 0x729f92, 0x398046);
    registerSpawnEgg(event, UNICORN_ENTITY, "unicorn", 0xeeeeee, 0xe8e8e8);
    registerSpawnEgg(event, WHIRL_ENTITY, "whirl", 0x1EF6FF, 0xededed);
    
//    registerSpawnEgg(event, HYDRA_ENTITY, 0xeeeeee, 0xe8e8e8);
  }
  
  @SubscribeEvent
  public static void registerEffects(final RegistryEvent.Register<Effect> event) {
    GreekFantasy.LOGGER.debug("registerEffects");
    event.getRegistry().registerAll(
        new StunnedEffect().setRegistryName(MODID, "stunned"),
        new StunnedEffect().setRegistryName(MODID, "petrified"),
        new MirrorEffect().setRegistryName(MODID, "mirror"),
        new SwineEffect().setRegistryName(MODID, "swine")
    );
  }
  
  @SubscribeEvent
  public static void registerPotions(final RegistryEvent.Register<Potion> event) {
    GreekFantasy.LOGGER.debug("registerPotions");
    event.getRegistry().registerAll(
        new Potion(new EffectInstance(MIRROR_EFFECT, 3600)).setRegistryName(MODID, "mirror"),
        new Potion("mirror", new EffectInstance(MIRROR_EFFECT, 9600)).setRegistryName(MODID, "long_mirror"),
        new Potion(new EffectInstance(SWINE_EFFECT, 3600)).setRegistryName(MODID, "swine"),
        new Potion("swine", new EffectInstance(SWINE_EFFECT, 9600)).setRegistryName(MODID, "long_swine")
    );
  }
  
  @SubscribeEvent
  public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
    GreekFantasy.LOGGER.debug("registerEnchantments");
    event.getRegistry().registerAll(
        new OverstepEnchantment(Enchantment.Rarity.UNCOMMON)
          .setRegistryName(MODID, "overstep"),
        new SmashingEnchantment(Enchantment.Rarity.RARE)
          .setRegistryName(MODID, "smashing"),
        new HuntingEnchantment(Enchantment.Rarity.COMMON)
          .setRegistryName(MODID, "hunting"),
        new PoisonEnchantment(Enchantment.Rarity.RARE)
          .setRegistryName(MODID, "poison"),
        new MirrorEnchantment(Enchantment.Rarity.VERY_RARE)
          .setRegistryName(MODID, "mirror"),
        new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET, EquipmentSlotType.FEET, 
              TextFormatting.GOLD, 1, e -> e.getItem() == GFRegistry.WINGED_SANDALS)
          .setRegistryName(MODID, "flying"),
        new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.TRIDENT, EquipmentSlotType.MAINHAND, 
              TextFormatting.DARK_AQUA, 1, e -> e.getItem() == Items.TRIDENT)
          .setRegistryName(MODID, "lord_of_the_sea"),
        new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, 
              TextFormatting.GOLD, 3, e -> e.getItem() == GFRegistry.THUNDERBOLT)
          .setRegistryName(MODID, "fireflash"),
        new DeityEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentType.BREAKABLE, EquipmentSlotType.MAINHAND, 
              TextFormatting.YELLOW, 1, e -> e.getItem() == Items.CLOCK)
          .setRegistryName(MODID, "daybreak"),
        new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, 
              TextFormatting.RED, 1, e -> e.getItem() == GFRegistry.BIDENT)
          .setRegistryName(MODID, "raising")
    );
  }

  @SubscribeEvent
  public static void registerParticleTypes(final RegistryEvent.Register<ParticleType<?>> event) {
    GreekFantasy.LOGGER.debug("registerParticleTypes");
    event.getRegistry().register(new BasicParticleType(true).setRegistryName(MODID, "gorgon_face"));
  }
  
  @SubscribeEvent
  public static void registerLootModifiers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
    event.getRegistry().registerAll(
        new AutosmeltOrCobbleModifier.Serializer().setRegistryName(MODID, "autosmelt_or_cobble"),
        new CropMultiplierModifier.Serializer().setRegistryName(MODID, "crop_multiplier")
    );
  }
  
  public static void finishBrewingRecipes() {
    final ItemStack awkward = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD);
    // Mirror potion recipes
    if(GreekFantasy.CONFIG.isMirrorPotionEnabled()) {
      final ItemStack mirror = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), MIRROR_POTION);
      final ItemStack splashMirror = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), MIRROR_POTION);
      final ItemStack lingeringMirror = PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), MIRROR_POTION);
      // Add brewing recipes for Mirror potion
      BrewingRecipeRegistry.addRecipe(
          Ingredient.fromStacks(awkward), 
          Ingredient.fromStacks(new ItemStack(SNAKESKIN)), mirror);
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(mirror), Ingredient.fromStacks(new ItemStack(Items.REDSTONE)), 
          PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), LONG_MIRROR_POTION));
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(mirror), Ingredient.fromStacks(new ItemStack(Items.GUNPOWDER)), splashMirror);
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(mirror), Ingredient.fromStacks(new ItemStack(Items.DRAGON_BREATH)), lingeringMirror);
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(splashMirror), Ingredient.fromStacks(new ItemStack(Items.REDSTONE)), 
          PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), LONG_MIRROR_POTION));
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(lingeringMirror), Ingredient.fromStacks(new ItemStack(Items.REDSTONE)), 
          PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), LONG_MIRROR_POTION));
    }
    // Swine potion recipes
    if(GreekFantasy.CONFIG.isSwinePotionEnabled()) {
      final ItemStack swine = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), SWINE_POTION);
      final ItemStack splashSwine = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), SWINE_POTION);
      final ItemStack lingeringSwine = PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), SWINE_POTION);
      // Add brewing recipes for Swine potion
      BrewingRecipeRegistry.addRecipe(
          Ingredient.fromStacks(awkward), 
          Ingredient.fromStacks(new ItemStack(BOAR_EAR)), swine);
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(swine), Ingredient.fromStacks(new ItemStack(Items.REDSTONE)), 
          PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), LONG_SWINE_POTION));
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(swine), Ingredient.fromStacks(new ItemStack(Items.GUNPOWDER)), splashSwine);
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(swine), Ingredient.fromStacks(new ItemStack(Items.DRAGON_BREATH)), lingeringSwine);
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(splashSwine), Ingredient.fromStacks(new ItemStack(Items.REDSTONE)), 
          PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), LONG_SWINE_POTION));
      BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(lingeringSwine), Ingredient.fromStacks(new ItemStack(Items.REDSTONE)), 
          PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), LONG_SWINE_POTION));
    }
  }

  // HELPER METHODS //
  
  /**
   * Builds and returns (but does not register) an entity type with the given information
   * @param <T> a class that inherits from Entity
   * @param factoryIn the entity factory, usually [EntityClass]::new
   * @param name the entity name for use in registration later
   * @param width the horizontal size of the entity
   * @param height the vertical size of the entity
   * @param classification the entity classification
   * @param builderSpecs a consumer to add other arguments to the builder before the entity type is built
   * @return an entity type
   **/
  private static <T extends Entity> EntityType<T> buildEntityType(final IFactory<T> factoryIn, final String name, final float width, final float height, 
      final EntityClassification classification, final Consumer<EntityType.Builder<T>> builderSpecs) {
    EntityType.Builder<T> entityTypeBuilder = EntityType.Builder.create(factoryIn, classification).size(width, height).trackingRange(8);
    builderSpecs.accept(entityTypeBuilder);
    EntityType<T> entityType = entityTypeBuilder.build(name);
    return entityType;
  }

  /**
   * Registers the given entity type and its associated attributes and placement settings
   * @param <T> a class that inherits from MobEntity
   * @param event the registry event
   * @param entityType the entity type to register
   * @param name the registry name suffix (not including mod id)
   * @param mapSupplier the attribute supplier, usually a reference to a static method.
   * If this value is null, no attributes will be registered.
   * @param placementPredicate the spawn placement predicate, usually a reference to a static method.
   * If this value is null, no placement will be registered.
   **/
  private static <T extends MobEntity> void registerEntityType(final RegistryEvent.Register<EntityType<?>> event,
      final EntityType<T> entityType, final String name, final Supplier<AttributeModifierMap.MutableAttribute> mapSupplier, 
      @Nullable final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate) {
    // register the entity type
    entityType.setRegistryName(MODID, name);
    event.getRegistry().register(entityType);
    // register attributes
    if(mapSupplier != null) {
      GlobalEntityTypeAttributes.put(entityType, mapSupplier.get().create());
    }
    // register placement (not used unless spawn information is registered with a biome)
    if(placementPredicate != null) {
      final PlacementType placementType = entityType.getClassification() == EntityClassification.WATER_CREATURE ? PlacementType.IN_WATER : PlacementType.ON_GROUND;
      // wrap the spawn predicate in one that also checks dimension predicate
      final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placement = (entity, world, reason, pos, rand) -> DIMENSION_MOB_PLACEMENT.test(world) && placementPredicate.test(entity, world, reason, pos, rand);
      EntitySpawnPlacementRegistry.register(entityType, placementType, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, placement);
    }
  }
  
  /**
   * Creates a StatueBlock that is associated with a specific IDeity
   * @param deityName the deity name
   * @param pose the statue's initial pose
   * @return the StatueBlock (registry name is not set)
   */
  private static Block makeDeityStatue(final String deityName, final StatueBlock.StatueMaterial statueMaterial, final StatuePose pose) {
    final ResourceLocation deityId = new ResourceLocation(MODID, deityName);
    return new StatueBlock(statueMaterial, te -> {
        te.setStatuePose(pose);
        te.setDeityName(deityId.toString());
        te.setItem(te.getDeity().getRightHandItem(), HandSide.RIGHT);
        te.setItem(te.getDeity().getLeftHandItem(), HandSide.LEFT);
        te.setStatueFemale(te.getDeity().isFemale());
      }, Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY).hardnessAndResistance(15.0F, 6000.0F).sound(SoundType.STONE).notSolid().setLightLevel(b -> statueMaterial.getLightLevel()), deityId);
  }
  
  private static void registerLogLeavesPlanksEtc(final RegistryEvent.Register<Block> event, final Block.Properties properties, final String registryName) {
    // log block
    final Block log = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, (state) -> {
      return state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.SAND : MaterialColor.WOOD;
    }).hardnessAndResistance(2.0F).sound(SoundType.WOOD)) {
      @Override
      public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
          if (toolType == ToolType.AXE) {
            return GFRegistry.STRIPPED_OLIVE_LOG.getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS));
          }
          return super.getToolModifiedState(state, world, pos, player, stack, toolType);
      }
    };
    // stripped log block
    final Block strippedLog = new RotatedPillarBlock(properties){
      @Override
      public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
    };
    // wood block
    final Block wood = new RotatedPillarBlock(properties){
      @Override
      public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
          if (toolType == ToolType.AXE) {
            return GFRegistry.STRIPPED_OLIVE_WOOD.getDefaultState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS));
          }
          return super.getToolModifiedState(state, world, pos, player, stack, toolType);
      }
    };
    // stripped wood block
    final Block strippedWood = new RotatedPillarBlock(properties){
      @Override
      public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
    };
    // planks block
    final Block planks = new Block(properties) {
      @Override
      public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 20; }
    };
    // register log, planks, and others
    event.getRegistry().registerAll(     
        log.setRegistryName(MODID, registryName + "_log"),
        strippedLog.setRegistryName(MODID, "stripped_" + registryName + "_log"),
        wood.setRegistryName(MODID, registryName + "_wood"),
        strippedWood.setRegistryName(MODID, "stripped_" + registryName + "_wood"),
        planks.setRegistryName(MODID, registryName + "_planks"),
        new SlabBlock(properties) {
          @Override
          public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
          @Override
          public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 20; }
        }.setRegistryName(MODID, registryName + "_slab"),
        new StairsBlock(() -> planks.getDefaultState(), properties){
          @Override
          public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
          @Override
          public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 20; }
        }.setRegistryName(MODID, registryName + "_stairs")
    );
    registerLeaves(event, registryName);
  }
  
  private static void registerLeaves(final RegistryEvent.Register<Block> event, final String registryName) {
    event.getRegistry().register(
        new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT)
            .notSolid().setAllowsSpawn(GFRegistry::allowsSpawnOnLeaves).setSuffocates((s, r, p) -> false).setBlocksVision((s, r, p) -> false)) {
          @Override
          public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 30; }
          @Override
          public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 60; }
        }.setRegistryName(MODID, registryName + "_leaves")
    );
  }

  private static void registerBlockPolishedSlabAndStairs(final RegistryEvent.Register<Block> event, final Block.Properties properties, final String registryName) {
    final Block raw = new Block(properties).setRegistryName(MODID, registryName);
    final Block polished = new Block(properties).setRegistryName(MODID, "polished_" + registryName);
    // raw, slab, and stairs
    event.getRegistry().register(raw);
    event.getRegistry().register(new SlabBlock(properties).setRegistryName(MODID, registryName + "_slab"));
    event.getRegistry().register(new StairsBlock(() -> raw.getDefaultState(), properties).setRegistryName(MODID, registryName + "_stairs"));
    // polished, slab, and stairs
    event.getRegistry().register(polished);
    event.getRegistry().register(new SlabBlock(properties).setRegistryName(MODID, "polished_" + registryName + "_slab"));
    event.getRegistry().register(new StairsBlock(() -> polished.getDefaultState(), properties).setRegistryName(MODID, "polished_" + registryName + "_stairs"));
  }
  
  private static void registerBlockPolishedChiseledAndBricks(final RegistryEvent.Register<Block> event, final Block.Properties properties, final String registryName) {
    // raw, polished, chiseled, brick, and chiseled_brick
    event.getRegistry().register(new Block(properties).setRegistryName(MODID, registryName));
    event.getRegistry().register(new Block(properties).setRegistryName(MODID, "chiseled_" + registryName));
    event.getRegistry().register(new Block(properties).setRegistryName(MODID, "polished_" + registryName));
    event.getRegistry().register(new Block(properties).setRegistryName(MODID, "cracked_polished_" + registryName));
    event.getRegistry().register(new Block(properties).setRegistryName(MODID, registryName + "_brick"));
    event.getRegistry().register(new Block(properties).setRegistryName(MODID, "chiseled_" + registryName + "_brick"));
    event.getRegistry().register(new Block(properties).setRegistryName(MODID, "cracked_" + registryName + "_brick"));
  }
    
  private static void registerItemBlock(final RegistryEvent.Register<Item> event, final Block block) {
    event.getRegistry().register(new BlockItem(block, new Item.Properties().group(GREEK_GROUP)).setRegistryName(block.getRegistryName()));
  }
  
  private static void registerItemBlocks(final RegistryEvent.Register<Item> event, final Block... blocks) {
    for(final Block b : blocks) {
      registerItemBlock(event, b);
    }
  }
  
  private static void registerSpawnEgg(final RegistryEvent.Register<Item> event, final EntityType<?> entity, 
      final String entityName, final int colorBase, final int colorSpots) {
    event.getRegistry().register(new SpawnEggItem(entity, colorBase, colorSpots, new Item.Properties().group(GREEK_GROUP))
        .setRegistryName(MODID, entityName + "_spawn_egg"));
  }

  private static Boolean allowsSpawnOnLeaves(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
    return entity == EntityType.OCELOT || entity == EntityType.PARROT || entity == DRYAD_ENTITY;
  }

}
