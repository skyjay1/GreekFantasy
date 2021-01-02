package greekfantasy;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.block.*;
import greekfantasy.effect.*;
import greekfantasy.enchantment.*;
import greekfantasy.entity.*;
import greekfantasy.entity.misc.*;
import greekfantasy.favor.Favor;
import greekfantasy.favor.IFavor;
import greekfantasy.gui.StatueContainer;
import greekfantasy.gui.DeityContainer;
import greekfantasy.item.*;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
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
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.extensions.IForgeContainerType;
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
  public static EntityType<PoisonSpitEntity> POISON_SPIT_ENTITY = buildEntityType(PoisonSpitEntity::new, "poison_spit", 0.25F, 0.25F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning().trackingRange(4).func_233608_b_(10));
  public static EntityType<PythonEntity> PYTHON_ENTITY = buildEntityType(PythonEntity::new, "python", 1.4F, 1.9F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<SatyrEntity> SATYR_ENTITY = buildEntityType(SatyrEntity::new, "satyr", 0.67F, 1.8F, EntityClassification.CREATURE, b -> {});
  public static EntityType<ShadeEntity> SHADE_ENTITY = buildEntityType(ShadeEntity::new, "shade", 0.67F, 1.8F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<SirenEntity> SIREN_ENTITY = buildEntityType(SirenEntity::new, "siren", 0.6F, 1.9F, EntityClassification.WATER_CREATURE, b -> {});
  public static EntityType<SpartiEntity> SPARTI_ENTITY = buildEntityType(SpartiEntity::new, "sparti", 0.6F, 1.98F, EntityClassification.CREATURE, b -> {});
  public static EntityType<SwineSpellEntity> SWINE_SPELL_ENTITY = buildEntityType(SwineSpellEntity::new, "swine_spell", 0.25F, 0.25F, EntityClassification.MISC, b -> b.immuneToFire().disableSummoning().trackingRange(4).func_233608_b_(10));
  public static EntityType<TalosEntity> TALOS_ENTITY = buildEntityType(TalosEntity::new, "talos", 1.98F, 4.96F, EntityClassification.MONSTER, b -> b.immuneToFire());
  public static EntityType<UnicornEntity> UNICORN_ENTITY = buildEntityType(UnicornEntity::new, "unicorn", 1.39F, 1.98F, EntityClassification.CREATURE, b -> {});

  // OBJECT HOLDERS //

  // Item //
  @ObjectHolder("panflute")
  public static final Item PANFLUTE = null;
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
  @ObjectHolder("helm_of_darkness")
  public static final Item HELM_OF_DARKNESS = null;
  @ObjectHolder("magic_feather")
  public static final Item MAGIC_FEATHER = null;
  @ObjectHolder("golden_bridle")
  public static final Item GOLDEN_BRIDLE = null;
  @ObjectHolder("snakeskin")
  public static final Item SNAKESKIN = null;
  @ObjectHolder("purified_snakeskin")
  public static final Item PURIFIED_SNAKESKIN = null;
  @ObjectHolder("styxian_shard")
  public static final Item STYXIAN_SHARD = null;
  @ObjectHolder("boar_ear")
  public static final Item BOAR_EAR = null;
  
  // Block //
  @ObjectHolder("reeds")
  public static final Block REEDS = null;
  @ObjectHolder("olive_log")
  public static final Block OLIVE_LOG = null;
  @ObjectHolder("olive_wood")
  public static final Block OLIVE_WOOD = null;
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
  @ObjectHolder("limestone_statue")
  public static final Block LIMESTONE_STATUE = null;
  @ObjectHolder("marble_statue")
  public static final Block MARBLE_STATUE = null;
  @ObjectHolder("palladium")
  public static final Block PALLADIUM = null;
  @ObjectHolder("terracotta_vase")
  public static final Block TERRACOTTA_VASE = null;
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
  
  // Altar //
  @ObjectHolder("altar_zeus")
  public static final Block ALTAR_ZEUS = null;
  @ObjectHolder("altar_hades")
  public static final Block ALTAR_HADES = null;
  @ObjectHolder("altar_poseidon")
  public static final Block ALTAR_POSEIDON = null;

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
    registerEntityType(event, ARA_ENTITY, AraEntity::getAttributes, AraEntity::canAraSpawnOn);
    registerEntityType(event, ARION_ENTITY, ArionEntity::getAttributes, ArionEntity::canSpawnOn);
    registerEntityType(event, CENTAUR_ENTITY, CentaurEntity::getAttributes, CentaurEntity::canSpawnOn);
    registerEntityType(event, CERASTES_ENTITY, CerastesEntity::getAttributes, CerastesEntity::canCerastesSpawnOn);
    registerEntityType(event, CERBERUS_ENTITY, CerberusEntity::getAttributes, null);
    registerEntityType(event, CHARYBDIS_ENTITY, CharybdisEntity::getAttributes, CharybdisEntity::canCharybdisSpawnOn);
    registerEntityType(event, CYPRIAN_ENTITY, CyprianEntity::getAttributes, CyprianEntity::canSpawnOn);
    registerEntityType(event, CYCLOPES_ENTITY, CyclopesEntity::getAttributes, CyclopesEntity::canCyclopesSpawnOn);
    registerEntityType(event, DRAKAINA_ENTITY, DrakainaEntity::getAttributes, DrakainaEntity::canMonsterSpawnInLight);
    registerEntityType(event, DRYAD_ENTITY, DryadEntity::getAttributes, DryadEntity::canSpawnOn);
    registerEntityType(event, ELPIS_ENTITY, ElpisEntity::getAttributes, null);
    registerEntityType(event, EMPUSA_ENTITY, EmpusaEntity::getAttributes, EmpusaEntity::canMonsterSpawnInLight);
    registerEntityType(event, FURY_ENTITY, FuryEntity::getAttributes, FuryEntity::canSpawnOn);
    registerEntityType(event, GERYON_ENTITY, GeryonEntity::getAttributes, null);
    registerEntityType(event, GIANT_BOAR_ENTITY, GiantBoarEntity::getAttributes, null);
    registerEntityType(event, GIGANTE_ENTITY, GiganteEntity::getAttributes, GiganteEntity::canGiganteSpawnOn);
    registerEntityType(event, GORGON_ENTITY, GorgonEntity::getAttributes, GorgonEntity::canMonsterSpawn);
    registerEntityType(event, HARPY_ENTITY, HarpyEntity::getAttributes, HarpyEntity::canMonsterSpawn);
//    registerEntityType(event, HYDRA_ENTITY, HydraEntity::getAttributes, null);
    registerEntityType(event, MAD_COW_ENTITY, MadCowEntity::getAttributes, MadCowEntity::canSpawnOn);
    registerEntityType(event, MINOTAUR_ENTITY, MinotaurEntity::getAttributes, MinotaurEntity::canMonsterSpawnInLight);
    registerEntityType(event, NAIAD_ENTITY, NaiadEntity::getAttributes, NaiadEntity::canNaiadSpawnOn);
    registerEntityType(event, ORTHUS_ENTITY, OrthusEntity::getAttributes, OrthusEntity::canSpawnOn);
    registerEntityType(event, PYTHON_ENTITY, PythonEntity::getAttributes, null);
    registerEntityType(event, SATYR_ENTITY, SatyrEntity::getAttributes, SatyrEntity::canSpawnOn);
    registerEntityType(event, SHADE_ENTITY, ShadeEntity::getAttributes, ShadeEntity::canMonsterSpawnInLight);
    registerEntityType(event, SIREN_ENTITY, SirenEntity::getAttributes, SirenEntity::canSirenSpawnOn);
    registerEntityType(event, SPARTI_ENTITY, SpartiEntity::getAttributes, null);
    registerEntityType(event, TALOS_ENTITY, TalosEntity::getAttributes, null);
    registerEntityType(event, UNICORN_ENTITY, UnicornEntity::getAttributes, UnicornEntity::canSpawnOn);
    event.getRegistry().register(CURSE_ENTITY);
    event.getRegistry().register(DRAGON_TOOTH_ENTITY);
    event.getRegistry().register(HEALING_SPELL_ENTITY);
//    event.getRegistry().register(HYDRA_HEAD_ENTITY);
    event.getRegistry().register(ORTHUS_HEAD_ITEM_ENTITY);
    event.getRegistry().register(POISON_SPIT_ENTITY);
    event.getRegistry().register(SWINE_SPELL_ENTITY);
  }
  
  @SubscribeEvent
  public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
    GreekFantasy.LOGGER.debug("registerTileEntities");
    event.getRegistry().register(
        TileEntityType.Builder.create(StatueTileEntity::new, LIMESTONE_STATUE, MARBLE_STATUE, PALLADIUM, ALTAR_HADES, ALTAR_POSEIDON, ALTAR_ZEUS)
        .build(null).setRegistryName(MODID, "statue_te")
    );
    event.getRegistry().register(
        TileEntityType.Builder.create(VaseTileEntity::new, TERRACOTTA_VASE)
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
    
    registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.create(Material.ROCK, MaterialColor.QUARTZ).setRequiresTool().hardnessAndResistance(1.5F, 6.0F), "marble");
    registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.create(Material.ROCK, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(1.5F, 6.0F), "limestone");
    
    event.getRegistry().registerAll(
        new ReedsBlock(AbstractBlock.Properties.create(Material.OCEAN_PLANT).doesNotBlockMovement().zeroHardnessAndResistance().tickRandomly().sound(SoundType.CROP))
          .setRegistryName(MODID, "reeds"),
        new SaplingBlock(new OliveTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly()
            .zeroHardnessAndResistance().notSolid().sound(SoundType.PLANT))
          .setRegistryName(MODID, "olive_sapling"),
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
        makeDeityStatue("hades", StatuePoses.ZEUS_POSE)
          .setRegistryName(MODID, "altar_hades"),
        makeDeityStatue("poseidon", StatuePoses.ZEUS_POSE)
          .setRegistryName(MODID, "altar_poseidon"),
        makeDeityStatue("zeus", StatuePoses.ZEUS_POSE)
          .setRegistryName(MODID, "altar_zeus"),
        new VaseBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(0.5F, 1.0F).notSolid())
          .setRegistryName(MODID, "terracotta_vase"),
        new MysteriousBoxBlock(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(0.8F, 2.0F).sound(SoundType.WOOD).notSolid())
          .setRegistryName(MODID, "mysterious_box"),
        new MobHeadBlock(HeadType.GIGANTE, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "gigante_head"),
        new OrthusHeadBlock(HeadType.ORTHUS, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "orthus_head"),
        new MobHeadBlock(HeadType.CERBERUS, AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "cerberus_head"),
        new IchorInfusedBlock(AbstractBlock.Properties.from(Blocks.GOLD_BLOCK))
          .setRegistryName(MODID, "ichor_infused_block")
    );
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    GreekFantasy.LOGGER.debug("registerItems");
    final boolean nerfAmbrosia = GreekFantasy.CONFIG.NERF_AMBROSIA.get();
    // items
    event.getRegistry().registerAll(
        new PanfluteItem(new Item.Properties().group(GREEK_GROUP).maxStackSize(1))
          .setRegistryName(MODID, "panflute"),
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
        new WingedSandalsItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP))
          .setRegistryName(MODID, "winged_sandals"),
        new HelmOfDarknessItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP))
          .setRegistryName(MODID, "helm_of_darkness"),
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
        new MirrorItem(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "mirror"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "horn"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "boar_ear"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "boar_tusk"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "golden_bridle"));
    
    event.getRegistry().registerAll(
        new Item(new Item.Properties().food(nerfAmbrosia ? Foods.GOLDEN_APPLE : Foods.ENCHANTED_GOLDEN_APPLE)
            .group(GREEK_GROUP).rarity(nerfAmbrosia ? Rarity.RARE : Rarity.EPIC).containerItem(GFRegistry.HORN))
            .setRegistryName(MODID, "ambrosia"),
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
          .setRegistryName(MODID, "snake_fang")
    );
    
    // block items
    registerItemBlocks(event, REEDS, OLIVE_SAPLING, NEST_BLOCK, WILD_ROSE, 
        OLIVE_LOG, OLIVE_WOOD, OLIVE_PLANKS, OLIVE_SLAB, OLIVE_STAIRS, OLIVE_LEAVES, 
        MARBLE, MARBLE_SLAB, MARBLE_STAIRS, POLISHED_MARBLE, POLISHED_MARBLE_SLAB, 
        POLISHED_MARBLE_STAIRS, MARBLE_PILLAR, MARBLE_STATUE, PALLADIUM, 
        ALTAR_ZEUS, ALTAR_HADES, ALTAR_POSEIDON,
        LIMESTONE, LIMESTONE_SLAB, LIMESTONE_STAIRS, POLISHED_LIMESTONE, POLISHED_LIMESTONE_SLAB, 
        POLISHED_LIMESTONE_STAIRS, LIMESTONE_PILLAR, LIMESTONE_STATUE, 
        TERRACOTTA_VASE, ICHOR_INFUSED_BLOCK);
    
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
    registerSpawnEgg(event, ARA_ENTITY, 0xffffff, 0xbbbbbb);
    registerSpawnEgg(event, ARION_ENTITY, 0xdfc014, 0xb58614);
    registerSpawnEgg(event, CENTAUR_ENTITY, 0x734933, 0x83251f);
    registerSpawnEgg(event, CERASTES_ENTITY, 0x847758, 0x997c4d);
    registerSpawnEgg(event, CHARYBDIS_ENTITY, 0x2e5651, 0x411e5e);
    registerSpawnEgg(event, CYCLOPES_ENTITY, 0xda662c, 0x2c1e0e);
    registerSpawnEgg(event, CYPRIAN_ENTITY, 0x443626, 0x83251f);
    registerSpawnEgg(event, DRAKAINA_ENTITY, 0x724e36, 0x398046);
    registerSpawnEgg(event, DRYAD_ENTITY, 0x443626, 0xfed93f);
    registerSpawnEgg(event, ELPIS_ENTITY, 0xe7aae4, 0xeeeeee);
    registerSpawnEgg(event, EMPUSA_ENTITY, 0x222222, 0x83251f);
    registerSpawnEgg(event, FURY_ENTITY, 0xbd4444, 0x6c2426);
    registerSpawnEgg(event, GIANT_BOAR_ENTITY, 0x5b433a, 0xe8a074);
    registerSpawnEgg(event, GIGANTE_ENTITY, 0xd3dba7, 0x6a602b);
    registerSpawnEgg(event, GORGON_ENTITY, 0x3a8228, 0xbcbcbc);
    registerSpawnEgg(event, HARPY_ENTITY, 0x724e36, 0x332411);
    registerSpawnEgg(event, MAD_COW_ENTITY, 0x443626, 0xcf9797);
    registerSpawnEgg(event, MINOTAUR_ENTITY, 0x443626, 0x734933);
    registerSpawnEgg(event, NAIAD_ENTITY, 0x7caba1, 0xe67830);
    registerSpawnEgg(event, ORTHUS_ENTITY, 0x493569, 0xe42e2e);
    registerSpawnEgg(event, PYTHON_ENTITY, 0x3a8228, 0x1e4c11);
    registerSpawnEgg(event, SATYR_ENTITY, 0x54371d, 0xa16648);
    registerSpawnEgg(event, SHADE_ENTITY, 0x222222, 0x000000);
    registerSpawnEgg(event, SIREN_ENTITY, 0x729f92, 0x398046);
    registerSpawnEgg(event, UNICORN_ENTITY, 0xeeeeee, 0xe8e8e8);
    
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
    event.getRegistry().register(new OverstepEnchantment(Enchantment.Rarity.UNCOMMON)
        .setRegistryName(MODID, "overstep"));
    event.getRegistry().register(new SmashingEnchantment(Enchantment.Rarity.RARE)
        .setRegistryName(MODID, "smashing"));
    event.getRegistry().register(new HuntingEnchantment(Enchantment.Rarity.COMMON)
        .setRegistryName(MODID, "hunting"));
    event.getRegistry().register(new MirrorEnchantment(Enchantment.Rarity.VERY_RARE)
        .setRegistryName(MODID, "mirror"));
  }

  @SubscribeEvent
  public static void registerParticleTypes(final RegistryEvent.Register<ParticleType<?>> event) {
    GreekFantasy.LOGGER.debug("registerParticleTypes");
    event.getRegistry().register(new BasicParticleType(true).setRegistryName(MODID, "gorgon_face"));
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
    entityType.setRegistryName(MODID, name);
    return entityType;
  }

  /**
   * Registers the given entity type and its associated attributes and placement settings
   * @param <T> a class that inherits from MobEntity
   * @param event the registry event
   * @param entityType the entity type to register
   * @param mapSupplier the attribute supplier, usually a reference to a static method.
   * If this value is null, no attributes will be registered.
   * @param placementPredicate the spawn placement predicate, usually a reference to a static method.
   * If this value is null, no placement will be registered.
   **/
  private static <T extends MobEntity> void registerEntityType(final RegistryEvent.Register<EntityType<?>> event,
      final EntityType<T> entityType, final Supplier<AttributeModifierMap.MutableAttribute> mapSupplier, 
      @Nullable final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate) {
    // register the entity type
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
  private static Block makeDeityStatue(final String deityName, final StatuePose pose) {
    final ResourceLocation deityId = new ResourceLocation(MODID, deityName);
    final StatueBlock.StatueMaterial material = StatueBlock.StatueMaterial.LIMESTONE;
    return new StatueBlock(material, te -> {
        te.setStatuePose(pose);
        te.setDeityName(deityId.toString());
        // DEBUG
        GreekFantasy.LOGGER.debug("StatueTileEntityInit consumer - " + te.getDeity().toString());
        te.setItem(te.getDeity().getRightHandItem(), HandSide.RIGHT);
        te.setItem(te.getDeity().getLeftHandItem(), HandSide.LEFT);
      }, Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY).hardnessAndResistance(15.0F, 6000.0F).sound(SoundType.STONE).notSolid().setLightLevel(b -> material.getLightLevel()), deityId);
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
    };
    // planks block
    final Block wood = new RotatedPillarBlock(properties){
      @Override
      public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
    };
    final Block planks = new Block(properties) {
      @Override
      public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 5; }
      @Override
      public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) { return 20; }
    };
    // register log, planks, and others
    event.getRegistry().registerAll(     
        log.setRegistryName(MODID, registryName + "_log"), 
        wood.setRegistryName(MODID, registryName + "_wood"),
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
        }.setRegistryName(MODID, registryName + "_stairs"),
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
    
  private static void registerItemBlock(final RegistryEvent.Register<Item> event, final Block block) {
    event.getRegistry().register(new BlockItem(block, new Item.Properties().group(GREEK_GROUP)).setRegistryName(block.getRegistryName()));
  }
  
  private static void registerItemBlocks(final RegistryEvent.Register<Item> event, final Block... blocks) {
    for(final Block b : blocks) {
      registerItemBlock(event, b);
    }
  }
  
  private static void registerSpawnEgg(final RegistryEvent.Register<Item> event, final EntityType<?> entity, 
      final int colorBase, final int colorSpots) {
    event.getRegistry().register(new SpawnEggItem(entity, colorBase, colorSpots, new Item.Properties().group(GREEK_GROUP))
        .setRegistryName(GreekFantasy.MODID, entity.getRegistryName().getPath() + "_spawn_egg"));
  }

  private static Boolean allowsSpawnOnLeaves(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
    return entity == EntityType.OCELOT || entity == EntityType.PARROT || entity == DRYAD_ENTITY;
  }

}
