package greekfantasy;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.block.CappedPillarBlock;
import greekfantasy.block.MysteriousBoxBlock;
import greekfantasy.block.NestBlock;
import greekfantasy.block.StatueBlock;
import greekfantasy.block.VaseBlock;
import greekfantasy.effect.StunnedEffect;
import greekfantasy.enchantment.OverstepEnchantment;
import greekfantasy.entity.*;
import greekfantasy.gui.StatueContainer;
import greekfantasy.item.ClubItem;
import greekfantasy.item.PanfluteItem;
import greekfantasy.item.UnicornHornItem;
import greekfantasy.item.WingedSandalsItem;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.tileentity.VaseTileEntity;
import greekfantasy.util.StatuePose;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(GreekFantasy.MODID)
public final class GFRegistry {

  //ENTITY TYPES

  public static EntityType<AraEntity> ARA_ENTITY;
  public static EntityType<CentaurEntity> CENTAUR_ENTITY;
  public static EntityType<CerastesEntity> CERASTES_ENTITY;
  public static EntityType<CerberusEntity> CERBERUS_ENTITY;
  public static EntityType<CyclopesEntity> CYCLOPES_ENTITY;
  public static EntityType<CyprianEntity> CYPRIAN_ENTITY;
  public static EntityType<DryadEntity> DRYAD_ENTITY;
  public static EntityType<ElpisEntity> ELPIS_ENTITY;
  public static EntityType<EmpusaEntity> EMPUSA_ENTITY;
  public static EntityType<GeryonEntity> GERYON_ENTITY;
  public static EntityType<GiganteEntity> GIGANTE_ENTITY;
  public static EntityType<GorgonEntity> GORGON_ENTITY;
  public static EntityType<HarpyEntity> HARPY_ENTITY;
  public static EntityType<MinotaurEntity> MINOTAUR_ENTITY;
  public static EntityType<NaiadEntity> NAIAD_ENTITY;
  public static EntityType<OrthusEntity> ORTHUS_ENTITY;
  public static EntityType<SatyrEntity> SATYR_ENTITY;
  public static EntityType<ShadeEntity> SHADE_ENTITY;
  public static EntityType<SirenEntity> SIREN_ENTITY;
  public static EntityType<UnicornEntity> UNICORN_ENTITY;

  // OBJECT HOLDERS //

  @ObjectHolder("panflute")
  public static final Item PANFLUTE = null;
  @ObjectHolder("iron_club")
  public static final Item IRON_CLUB = null;
  @ObjectHolder("stone_club")
  public static final Item STONE_CLUB = null;
  @ObjectHolder("wooden_club")
  public static final Item WOODEN_CLUB = null;
//  @ObjectHolder("winged_sandals")
//  public static final Item WINGED_SANDALS = null;
//  @ObjectHolder("ambrosia")
//  public static final Item AMBROSIA = null;
//  @ObjectHolder("unicorn_horn")
//  public static final Item UNICORN_HORN = null;
//  @ObjectHolder("horn")
//  public static final Item HORN = null;
  @ObjectHolder("magic_feather")
  public static final Item MAGIC_FEATHER = null;
  @ObjectHolder("golden_bridle")
  public static final Item GOLDEN_BRIDLE = null;
  
  @ObjectHolder("nest")
  public static final Block NEST_BLOCK = null;
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
  @ObjectHolder("terracotta_vase")
  public static final Block TERRACOTTA_VASE = null;
  @ObjectHolder("mysterious_box")
  public static final Block MYSTERIOUS_BOX = null;
  
  @ObjectHolder("statue_te")
  public static final TileEntityType<StatueTileEntity> STATUE_TE = null;
  @ObjectHolder("vase_te")
  public static final TileEntityType<VaseTileEntity> VASE_TE = null;

  @ObjectHolder("statue_container")
  public static final ContainerType<StatueContainer> STATUE_CONTAINER = null;
  
  @ObjectHolder("stunned")
  public static final Effect STUNNED_EFFECT = null;
  @ObjectHolder("petrified")
  public static final Effect PETRIFIED_EFFECT = null;
  
  @ObjectHolder("overstep")
  public static final Enchantment OVERSTEP_ENCHANTMENT = null;
  
  @ObjectHolder("gorgon_face")
  public static final BasicParticleType GORGON_PARTICLE = new BasicParticleType(true);

  public static ItemGroup GREEK_GROUP = new ItemGroup("greekfantasy") {
    @Override
    public ItemStack createIcon() { return new ItemStack(PANFLUTE); }
  };

  // REGISTRY METHODS //

  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    GreekFantasy.LOGGER.debug("registerEntities");
    ARA_ENTITY = registerEntityType(event, AraEntity::new, AraEntity::getAttributes, MonsterEntity::canMonsterSpawnInLight, "ara", 0.67F, 1.8F, EntityClassification.MONSTER, true);
    CENTAUR_ENTITY = registerEntityType(event, CentaurEntity::new, CentaurEntity::getAttributes, CentaurEntity::canSpawnOn, "centaur", 1.39F, 2.49F, EntityClassification.CREATURE, false);
    CYPRIAN_ENTITY = registerEntityType(event, CyprianEntity::new, CyprianEntity::getAttributes, CyprianEntity::canSpawnOn, "cyprian", 1.39F, 2.49F, EntityClassification.CREATURE, false);
    CERASTES_ENTITY = registerEntityType(event, CerastesEntity::new, CerastesEntity::getAttributes, CerastesEntity::canCerastesSpawnOn, "cerastes", 0.98F, 0.94F, EntityClassification.CREATURE, false);
    CERBERUS_ENTITY = registerEntityType(event, CerberusEntity::new, CerberusEntity::getAttributes, CerberusEntity::canSpawnOn, "cerberus", 1.98F, 1.9F, EntityClassification.MONSTER, false);
    CYCLOPES_ENTITY = registerEntityType(event, CyclopesEntity::new, CyclopesEntity::getAttributes, CyclopesEntity::canCyclopesSpawnOn, "cyclopes", 0.99F, 2.92F, EntityClassification.MONSTER, false);
    DRYAD_ENTITY = registerEntityType(event, DryadEntity::new, DryadEntity::getAttributes, DryadEntity::canSpawnOn, "dryad", 0.48F, 1.8F, EntityClassification.CREATURE, false);
    ELPIS_ENTITY = registerEntityType(event, ElpisEntity::new, ElpisEntity::getAttributes, ElpisEntity::canSpawnOn, "elpis", 0.4F, 0.8F, EntityClassification.CREATURE, false);
    EMPUSA_ENTITY = registerEntityType(event, EmpusaEntity::new, EmpusaEntity::getAttributes, EmpusaEntity::canMonsterSpawnInLight, "empusa", 0.67F, 1.8F, EntityClassification.MONSTER, true);
    GERYON_ENTITY = registerEntityType(event, GeryonEntity::new, GeryonEntity::getAttributes, GeryonEntity::canGeryonSpawnOn, "geryon", 1.98F, 4.96F, EntityClassification.MONSTER, false);
    GIGANTE_ENTITY = registerEntityType(event, GiganteEntity::new, GiganteEntity::getAttributes, GiganteEntity::canGiganteSpawnOn, "gigante", 1.98F, 4.79F, EntityClassification.CREATURE, false);
    GORGON_ENTITY = registerEntityType(event, GorgonEntity::new, GorgonEntity::getAttributes, GorgonEntity::canMonsterSpawn, "gorgon", 0.9F, 1.9F, EntityClassification.MONSTER, false);
    HARPY_ENTITY = registerEntityType(event, HarpyEntity::new, HarpyEntity::getAttributes, HarpyEntity::canMonsterSpawn, "harpy", 0.7F, 1.8F, EntityClassification.MONSTER, false);
    MINOTAUR_ENTITY = registerEntityType(event, MinotaurEntity::new, MinotaurEntity::getAttributes, MinotaurEntity::canMonsterSpawnInLight, "minotaur", 0.7F, 1.8F, EntityClassification.MONSTER, false);
    NAIAD_ENTITY = registerEntityType(event, NaiadEntity::new, NaiadEntity::getAttributes, NaiadEntity::canNaiadSpawnOn, "naiad", 0.48F, 1.8F, EntityClassification.WATER_CREATURE, false);
    ORTHUS_ENTITY = registerEntityType(event, OrthusEntity::new, OrthusEntity::getAttributes, OrthusEntity::canSpawnOn, "orthus", 0.6F, 0.85F, EntityClassification.MONSTER, true);
    SATYR_ENTITY = registerEntityType(event, SatyrEntity::new, SatyrEntity::getAttributes, SatyrEntity::canSpawnOn, "satyr", 0.67F, 1.8F, EntityClassification.CREATURE, false);
    SHADE_ENTITY = registerEntityType(event, ShadeEntity::new, ShadeEntity::getAttributes, ShadeEntity::canMonsterSpawnInLight, "shade", 0.67F, 1.8F, EntityClassification.MONSTER, true);
    SIREN_ENTITY = registerEntityType(event, SirenEntity::new, SirenEntity::getAttributes, SirenEntity::canSirenSpawnOn, "siren", 0.6F, 1.9F, EntityClassification.WATER_CREATURE, false);
    UNICORN_ENTITY = registerEntityType(event, UnicornEntity::new, UnicornEntity::getAttributes, UnicornEntity::canSpawnOn, "unicorn", 1.39F, 1.98F, EntityClassification.CREATURE, false);
  }
  
  @SubscribeEvent
  public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
    GreekFantasy.LOGGER.debug("registerTileEntities");
    event.getRegistry().register(
        TileEntityType.Builder.create(StatueTileEntity::new, LIMESTONE_STATUE, MARBLE_STATUE)
        .build(null).setRegistryName(GreekFantasy.MODID, "statue_te")
    );
    event.getRegistry().register(
        TileEntityType.Builder.create(VaseTileEntity::new, TERRACOTTA_VASE)
        .build(null).setRegistryName(GreekFantasy.MODID, "vase_te")
    );
  }
  
  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    GreekFantasy.LOGGER.debug("registerContainers");
    ContainerType<StatueContainer> containerType = IForgeContainerType.create((windowId, inv, data) -> {
      final boolean isFemale = data.readBoolean();
      final BlockPos blockpos = data.readBlockPos();
      final CompoundNBT poseTag = data.readCompoundTag();
      final String name = data.readString();
      final StatuePose pose = new StatuePose(poseTag);
      final Direction facing = Direction.byHorizontalIndex(data.readByte());
      return new StatueContainer(windowId, inv, new Inventory(2), pose, isFemale, name, blockpos, facing);
    });
    event.getRegistry().register(containerType.setRegistryName(GreekFantasy.MODID, "statue_container"));
  }

  @SubscribeEvent
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    GreekFantasy.LOGGER.debug("registerBlocks");
    
    registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.create(Material.ROCK, MaterialColor.QUARTZ).setRequiresTool().hardnessAndResistance(1.5F, 6.0F), "marble");
    registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.create(Material.ROCK, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(1.5F, 6.0F), "limestone");

    event.getRegistry().registerAll(
        new NestBlock(AbstractBlock.Properties.create(Material.ORGANIC, MaterialColor.BROWN).hardnessAndResistance(0.5F).sound(SoundType.PLANT).notSolid())
          .setRegistryName(GreekFantasy.MODID, "nest"),
        new CappedPillarBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.QUARTZ).setRequiresTool().hardnessAndResistance(1.5F, 6.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "marble_pillar"),
        new CappedPillarBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(1.5F, 6.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "limestone_pillar"),
        new StatueBlock(StatueBlock.StatueMaterial.MARBLE)
          .setRegistryName(GreekFantasy.MODID, "marble_statue"),
        new StatueBlock(StatueBlock.StatueMaterial.LIMESTONE)
          .setRegistryName(GreekFantasy.MODID, "limestone_statue"),
        new VaseBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(0.5F, 1.0F).notSolid())
          .setRegistryName(GreekFantasy.MODID, "terracotta_vase"),
        new MysteriousBoxBlock(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(0.8F, 2.0F).sound(SoundType.WOOD).notSolid())
          .setRegistryName(GreekFantasy.MODID, "mysterious_box")
    );
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    GreekFantasy.LOGGER.debug("registerItems");
    final boolean nerfAmbrosia = GreekFantasy.CONFIG.NERF_AMBROSIA.get();
    // items
    event.getRegistry().registerAll(
        new PanfluteItem(new Item.Properties().group(GREEK_GROUP).maxDamage(100))
          .setRegistryName(GreekFantasy.MODID, "panflute"),
        new ClubItem(ItemTier.IRON, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(GreekFantasy.MODID, "iron_club"),
        new ClubItem(ItemTier.STONE, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(GreekFantasy.MODID, "stone_club"),
        new ClubItem(ItemTier.WOOD, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(GreekFantasy.MODID, "wooden_club"),
        new WingedSandalsItem(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(GreekFantasy.MODID, "winged_sandals"),
        new Item(new Item.Properties().food(nerfAmbrosia ? Foods.GOLDEN_APPLE : Foods.ENCHANTED_GOLDEN_APPLE)
          .group(GREEK_GROUP).rarity(nerfAmbrosia ? Rarity.RARE : Rarity.EPIC))
          .setRegistryName(GreekFantasy.MODID, "ambrosia"),
        new UnicornHornItem(new Item.Properties().group(GREEK_GROUP).maxStackSize(1))
          .setRegistryName(GreekFantasy.MODID, "unicorn_horn"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(GreekFantasy.MODID, "horn"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(GreekFantasy.MODID, "golden_bridle"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(GreekFantasy.MODID, "magic_feather")
    );
    
    // block items
    registerItemBlock(event, NEST_BLOCK, "nest");
    
    registerItemBlock(event, MARBLE, "marble");
    registerItemBlock(event, MARBLE_SLAB, "marble_slab");
    registerItemBlock(event, MARBLE_STAIRS, "marble_stairs");
    registerItemBlock(event, POLISHED_MARBLE, "polished_marble");
    registerItemBlock(event, POLISHED_MARBLE_SLAB, "polished_marble_slab");
    registerItemBlock(event, POLISHED_MARBLE_STAIRS, "polished_marble_stairs");
    registerItemBlock(event, MARBLE_PILLAR, "marble_pillar");

    registerItemBlock(event, LIMESTONE, "limestone");
    registerItemBlock(event, LIMESTONE_SLAB, "limestone_slab");
    registerItemBlock(event, LIMESTONE_STAIRS, "limestone_stairs");
    registerItemBlock(event, POLISHED_LIMESTONE, "polished_limestone");
    registerItemBlock(event, POLISHED_LIMESTONE_SLAB, "polished_limestone_slab");
    registerItemBlock(event, POLISHED_LIMESTONE_STAIRS, "polished_limestone_stairs");
    registerItemBlock(event, LIMESTONE_PILLAR, "limestone_pillar");
    
    registerItemBlock(event, LIMESTONE_STATUE, "limestone_statue");
    registerItemBlock(event, MARBLE_STATUE, "marble_statue");
    
    registerItemBlock(event, TERRACOTTA_VASE, "terracotta_vase");
    
    // mysterious box item
    event.getRegistry().register(new BlockItem(MYSTERIOUS_BOX, new Item.Properties().group(GREEK_GROUP)) {
      @OnlyIn(Dist.CLIENT)
      public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("block.greekfantasy.mysterious_box.tooltip").mergeStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
      }
    }.setRegistryName(GreekFantasy.MODID, "mysterious_box"));
  }
  
  @SubscribeEvent
  public static void registerEffects(final RegistryEvent.Register<Effect> event) {
    GreekFantasy.LOGGER.debug("registerEffects");
    event.getRegistry().registerAll(
        new StunnedEffect().setRegistryName(GreekFantasy.MODID, "stunned"),
        new StunnedEffect().setRegistryName(GreekFantasy.MODID, "petrified")
    );
  }
  
  @SubscribeEvent
  public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
    event.getRegistry().register(new OverstepEnchantment(Enchantment.Rarity.UNCOMMON)
        .setRegistryName(GreekFantasy.MODID, "overstep"));
  }

  @SubscribeEvent
  public static void registerParticleTypes(final RegistryEvent.Register<ParticleType<?>> event) {
    GreekFantasy.LOGGER.debug("registerParticleTypes");
    event.getRegistry().register(new BasicParticleType(true).setRegistryName(GreekFantasy.MODID, "gorgon_face"));
  }

  // HELPER METHODS //

  private static <T extends MobEntity> EntityType<T> registerEntityType(final RegistryEvent.Register<EntityType<?>> event,
      final IFactory<T> factoryIn, final Supplier<AttributeModifierMap.MutableAttribute> mapSupplier, 
      final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate, final String name,
      final float width, final float height, final EntityClassification classification, final boolean fireproof) {
    EntityType.Builder<T> entityTypeBuilder = EntityType.Builder.create(factoryIn, classification).size(width, height);
    if (fireproof) entityTypeBuilder.immuneToFire();
    EntityType<T> entityType = entityTypeBuilder.build(name);
    entityType.setRegistryName(GreekFantasy.MODID, name);
    event.getRegistry().register(entityType);
    GlobalEntityTypeAttributes.put(entityType, mapSupplier.get().create());    
    EntitySpawnPlacementRegistry.register(entityType, classification == EntityClassification.WATER_CREATURE ? PlacementType.IN_WATER : PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, placementPredicate);
    return entityType;
  }

  private static void registerBlockPolishedSlabAndStairs(final RegistryEvent.Register<Block> event, final Block.Properties properties, final String registryName) {
    final Block raw = new Block(properties).setRegistryName(GreekFantasy.MODID, registryName);
    final Block polished = new Block(properties).setRegistryName(GreekFantasy.MODID, "polished_" + registryName);
    // raw, slab, and stairs
    event.getRegistry().register(raw);
    event.getRegistry().register(new SlabBlock(properties).setRegistryName(GreekFantasy.MODID, registryName + "_slab"));
    event.getRegistry().register(new StairsBlock(() -> raw.getDefaultState(), properties).setRegistryName(GreekFantasy.MODID, registryName + "_stairs"));
    // polished, slab, and stairs
    event.getRegistry().register(polished);
    event.getRegistry().register(new SlabBlock(properties).setRegistryName(GreekFantasy.MODID, "polished_" + registryName + "_slab"));
    event.getRegistry().register(new StairsBlock(() -> polished.getDefaultState(), properties).setRegistryName(GreekFantasy.MODID, "polished_" + registryName + "_stairs"));
  }
  
  private static void registerItemBlock(final RegistryEvent.Register<Item> event, final Block block, final String registryName) {
    event.getRegistry().register(new BlockItem(block, new Item.Properties().group(GREEK_GROUP)).setRegistryName(GreekFantasy.MODID, registryName));
  }
}
