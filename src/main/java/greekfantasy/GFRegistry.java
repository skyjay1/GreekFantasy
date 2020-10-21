package greekfantasy;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.block.CappedPillarBlock;
import greekfantasy.block.MysteriousBoxBlock;
import greekfantasy.block.NestBlock;
import greekfantasy.block.OliveTree;
import greekfantasy.block.ReedsBlock;
import greekfantasy.block.StatueBlock;
import greekfantasy.block.VaseBlock;
import greekfantasy.effect.StunnedEffect;
import greekfantasy.enchantment.OverstepEnchantment;
import greekfantasy.entity.*;
import greekfantasy.gui.StatueContainer;
import greekfantasy.item.ClubItem;
import greekfantasy.item.DragonToothItem;
import greekfantasy.item.HealingRodItem;
import greekfantasy.item.HelmOfDarknessItem;
import greekfantasy.item.PanfluteItem;
import greekfantasy.item.SwordOfTheHuntItem;
import greekfantasy.item.ThunderboltItem;
import greekfantasy.item.UnicornHornItem;
import greekfantasy.item.WingedSandalsItem;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.tileentity.VaseTileEntity;
import greekfantasy.util.StatuePose;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
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
import net.minecraft.world.IBlockReader;
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
  
  private static final String MODID = GreekFantasy.MODID;

  //ENTITY TYPES

  public static EntityType<AraEntity> ARA_ENTITY;
  public static EntityType<CentaurEntity> CENTAUR_ENTITY;
  public static EntityType<CerastesEntity> CERASTES_ENTITY;
  public static EntityType<CerberusEntity> CERBERUS_ENTITY;
  public static EntityType<CyclopesEntity> CYCLOPES_ENTITY;
  public static EntityType<CyprianEntity> CYPRIAN_ENTITY;
  public static EntityType<DragonToothEntity> DRAGON_TOOTH_ENTITY;
  public static EntityType<DryadEntity> DRYAD_ENTITY;
  public static EntityType<ElpisEntity> ELPIS_ENTITY;
  public static EntityType<EmpusaEntity> EMPUSA_ENTITY;
  public static EntityType<GeryonEntity> GERYON_ENTITY;
  public static EntityType<GiganteEntity> GIGANTE_ENTITY;
  public static EntityType<GorgonEntity> GORGON_ENTITY;
  public static EntityType<HarpyEntity> HARPY_ENTITY;
  public static EntityType<HealingSpellEntity> HEALING_SPELL_ENTITY;
  public static EntityType<MadCowEntity> MAD_COW_ENTITY;
  public static EntityType<MinotaurEntity> MINOTAUR_ENTITY;
  public static EntityType<NaiadEntity> NAIAD_ENTITY;
  public static EntityType<OrthusEntity> ORTHUS_ENTITY;
  public static EntityType<SatyrEntity> SATYR_ENTITY;
  public static EntityType<ShadeEntity> SHADE_ENTITY;
  public static EntityType<SirenEntity> SIREN_ENTITY;
  public static EntityType<SpartiEntity> SPARTI_ENTITY;
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
  @ObjectHolder("flint_knife")
  public static final Item FLINT_KNIFE = null;
  @ObjectHolder("dragon_tooth")
  public static final Item DRAGON_TOOTH = null;
  @ObjectHolder("ichor")
  public static final Item ICHOR = null;
//  @ObjectHolder("winged_sandals")
//  public static final Item WINGED_SANDALS = null;
//  @ObjectHolder("ambrosia")
//  public static final Item AMBROSIA = null;
//  @ObjectHolder("unicorn_horn")
//  public static final Item UNICORN_HORN = null;
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
    ARA_ENTITY = registerEntityType(event, AraEntity::new, AraEntity::getAttributes, AraEntity::canAraSpawnOn, "ara", 0.67F, 1.8F, EntityClassification.CREATURE, false);
    CENTAUR_ENTITY = registerEntityType(event, CentaurEntity::new, CentaurEntity::getAttributes, CentaurEntity::canSpawnOn, "centaur", 1.39F, 2.49F, EntityClassification.CREATURE, false);
    CYPRIAN_ENTITY = registerEntityType(event, CyprianEntity::new, CyprianEntity::getAttributes, CyprianEntity::canSpawnOn, "cyprian", 1.39F, 2.49F, EntityClassification.CREATURE, false);
    CERASTES_ENTITY = registerEntityType(event, CerastesEntity::new, CerastesEntity::getAttributes, CerastesEntity::canCerastesSpawnOn, "cerastes", 0.98F, 0.94F, EntityClassification.CREATURE, false);
    CERBERUS_ENTITY = registerEntityType(event, CerberusEntity::new, CerberusEntity::getAttributes, CerberusEntity::canSpawnOn, "cerberus", 1.98F, 1.9F, EntityClassification.MONSTER, true);
    CYCLOPES_ENTITY = registerEntityType(event, CyclopesEntity::new, CyclopesEntity::getAttributes, CyclopesEntity::canCyclopesSpawnOn, "cyclopes", 0.99F, 2.92F, EntityClassification.MONSTER, false);
    DRYAD_ENTITY = registerEntityType(event, DryadEntity::new, DryadEntity::getAttributes, DryadEntity::canSpawnOn, "dryad", 0.48F, 1.8F, EntityClassification.CREATURE, false);
    ELPIS_ENTITY = registerEntityType(event, ElpisEntity::new, ElpisEntity::getAttributes, null, "elpis", 0.4F, 0.8F, EntityClassification.CREATURE, false);
    EMPUSA_ENTITY = registerEntityType(event, EmpusaEntity::new, EmpusaEntity::getAttributes, EmpusaEntity::canMonsterSpawnInLight, "empusa", 0.67F, 1.8F, EntityClassification.MONSTER, true);
    GERYON_ENTITY = registerEntityType(event, GeryonEntity::new, GeryonEntity::getAttributes, GeryonEntity::canGeryonSpawnOn, "geryon", 1.98F, 4.96F, EntityClassification.MONSTER, false);
    GIGANTE_ENTITY = registerEntityType(event, GiganteEntity::new, GiganteEntity::getAttributes, GiganteEntity::canGiganteSpawnOn, "gigante", 1.98F, 4.79F, EntityClassification.CREATURE, false);
    GORGON_ENTITY = registerEntityType(event, GorgonEntity::new, GorgonEntity::getAttributes, GorgonEntity::canMonsterSpawn, "gorgon", 0.9F, 1.9F, EntityClassification.MONSTER, false);
    HARPY_ENTITY = registerEntityType(event, HarpyEntity::new, HarpyEntity::getAttributes, HarpyEntity::canMonsterSpawn, "harpy", 0.7F, 1.8F, EntityClassification.MONSTER, false);
    MAD_COW_ENTITY = registerEntityType(event, MadCowEntity::new, MadCowEntity::getAttributes, null, "mad_cow", 0.9F, 1.4F, EntityClassification.CREATURE, false);
    MINOTAUR_ENTITY = registerEntityType(event, MinotaurEntity::new, MinotaurEntity::getAttributes, MinotaurEntity::canMonsterSpawnInLight, "minotaur", 0.7F, 1.8F, EntityClassification.MONSTER, false);
    NAIAD_ENTITY = registerEntityType(event, NaiadEntity::new, NaiadEntity::getAttributes, NaiadEntity::canNaiadSpawnOn, "naiad", 0.48F, 1.8F, EntityClassification.WATER_CREATURE, false);
    ORTHUS_ENTITY = registerEntityType(event, OrthusEntity::new, OrthusEntity::getAttributes, OrthusEntity::canSpawnOn, "orthus", 0.6F, 0.85F, EntityClassification.MONSTER, true);
    SATYR_ENTITY = registerEntityType(event, SatyrEntity::new, SatyrEntity::getAttributes, SatyrEntity::canSpawnOn, "satyr", 0.67F, 1.8F, EntityClassification.CREATURE, false);
    SHADE_ENTITY = registerEntityType(event, ShadeEntity::new, ShadeEntity::getAttributes, ShadeEntity::canMonsterSpawnInLight, "shade", 0.67F, 1.8F, EntityClassification.MONSTER, true);
    SIREN_ENTITY = registerEntityType(event, SirenEntity::new, SirenEntity::getAttributes, SirenEntity::canSirenSpawnOn, "siren", 0.6F, 1.9F, EntityClassification.WATER_CREATURE, false);
    SPARTI_ENTITY = registerEntityType(event, SpartiEntity::new, SpartiEntity::getAttributes, null, "sparti", 0.6F, 1.98F, EntityClassification.CREATURE, false);
    UNICORN_ENTITY = registerEntityType(event, UnicornEntity::new, UnicornEntity::getAttributes, UnicornEntity::canSpawnOn, "unicorn", 1.39F, 1.98F, EntityClassification.CREATURE, false);
    // create and register misc. entity types
    EntityType<DragonToothEntity> dragonToothEntityType = EntityType.Builder.create(DragonToothEntity::new, EntityClassification.MISC)
        .size(0.25F, 0.25F).immuneToFire().build("dragon_tooth");
    event.getRegistry().register(dragonToothEntityType.setRegistryName(MODID, "dragon_tooth"));
    DRAGON_TOOTH_ENTITY = dragonToothEntityType;
    EntityType<HealingSpellEntity> healingSpellEntityType = EntityType.Builder.create(HealingSpellEntity::new, EntityClassification.MISC)
        .size(0.25F, 0.25F).immuneToFire().build("healing_spell");
    event.getRegistry().register(healingSpellEntityType.setRegistryName(MODID, "healing_spell"));
    HEALING_SPELL_ENTITY = healingSpellEntityType;
  }
  
  @SubscribeEvent
  public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
    GreekFantasy.LOGGER.debug("registerTileEntities");
    event.getRegistry().register(
        TileEntityType.Builder.create(StatueTileEntity::new, LIMESTONE_STATUE, MARBLE_STATUE)
        .build(null).setRegistryName(MODID, "statue_te")
    );
    event.getRegistry().register(
        TileEntityType.Builder.create(VaseTileEntity::new, TERRACOTTA_VASE)
        .build(null).setRegistryName(MODID, "vase_te")
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
    event.getRegistry().register(containerType.setRegistryName(MODID, "statue_container"));
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
        new VaseBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.ADOBE).hardnessAndResistance(0.5F, 1.0F).notSolid())
          .setRegistryName(MODID, "terracotta_vase"),
        new MysteriousBoxBlock(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(0.8F, 2.0F).sound(SoundType.WOOD).notSolid())
          .setRegistryName(MODID, "mysterious_box")
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
        new SwordItem(ItemTier.WOOD, 3, -2.0F, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "flint_knife"),
        new SwordOfTheHuntItem(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP))
          .setRegistryName(MODID, "sword_of_the_hunt"),
        new ClubItem(ItemTier.IRON, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "iron_club"),
        new ClubItem(ItemTier.STONE, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "stone_club"),
        new ClubItem(ItemTier.WOOD, new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "wooden_club"),
        new ThunderboltItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.THUNDERBOLT_DURABILITY.get()))
          .setRegistryName(MODID, "thunderbolt"),
        new WingedSandalsItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP))
          .setRegistryName(MODID, "winged_sandals"),
        new HelmOfDarknessItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP))
          .setRegistryName(MODID, "helm_of_darkness"),
        new Item(new Item.Properties().food(nerfAmbrosia ? Foods.GOLDEN_APPLE : Foods.ENCHANTED_GOLDEN_APPLE)
          .group(GREEK_GROUP).rarity(nerfAmbrosia ? Rarity.RARE : Rarity.EPIC))
          .setRegistryName(MODID, "ambrosia"),
        new UnicornHornItem(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.UNICORN_HORN_DURABILITY.get()))
          .setRegistryName(MODID, "unicorn_horn"),
        new HealingRodItem(new Item.Properties().rarity(Rarity.RARE).group(GREEK_GROUP)
            .maxDamage(GreekFantasy.CONFIG.HEALING_ROD_DURABILITY.get()))
          .setRegistryName(MODID, "healing_rod"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "horn"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "golden_bridle"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "magic_feather"),
        new DragonToothItem(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP))
          .setRegistryName(MODID, "dragon_tooth"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "snakeskin"),
        new Item(new Item.Properties().group(GREEK_GROUP))
          .setRegistryName(MODID, "purified_snakeskin"),
        new Item(new Item.Properties().rarity(Rarity.UNCOMMON).group(GREEK_GROUP)){
          @Override
          public boolean hasEffect(ItemStack stack) { return true; }
        }.setRegistryName(MODID, "ichor")
    );
    
    // block items
    registerItemBlock(event, REEDS, "reeds");
    registerItemBlock(event, OLIVE_LOG, "olive_log");
    registerItemBlock(event, OLIVE_WOOD, "olive_wood");
    registerItemBlock(event, OLIVE_PLANKS, "olive_planks");
    registerItemBlock(event, OLIVE_SLAB, "olive_slab");
    registerItemBlock(event, OLIVE_STAIRS, "olive_stairs");
    registerItemBlock(event, OLIVE_LEAVES, "olive_leaves");
    registerItemBlock(event, OLIVE_SAPLING, "olive_sapling");
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
    }.setRegistryName(MODID, "mysterious_box"));
  }
  
  @SubscribeEvent
  public static void registerEffects(final RegistryEvent.Register<Effect> event) {
    GreekFantasy.LOGGER.debug("registerEffects");
    event.getRegistry().registerAll(
        new StunnedEffect().setRegistryName(MODID, "stunned"),
        new StunnedEffect().setRegistryName(MODID, "petrified")
    );
  }
  
  @SubscribeEvent
  public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
    GreekFantasy.LOGGER.debug("registerEnchantments");
    event.getRegistry().register(new OverstepEnchantment(Enchantment.Rarity.UNCOMMON)
        .setRegistryName(MODID, "overstep"));
  }

  @SubscribeEvent
  public static void registerParticleTypes(final RegistryEvent.Register<ParticleType<?>> event) {
    GreekFantasy.LOGGER.debug("registerParticleTypes");
    event.getRegistry().register(new BasicParticleType(true).setRegistryName(MODID, "gorgon_face"));
  }

  // HELPER METHODS //

  private static <T extends MobEntity> EntityType<T> registerEntityType(final RegistryEvent.Register<EntityType<?>> event,
      final IFactory<T> factoryIn, final Supplier<AttributeModifierMap.MutableAttribute> mapSupplier, 
      @Nullable final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate, final String name,
      final float width, final float height, final EntityClassification classification, final boolean fireproof) {
    // make the entity type
    EntityType.Builder<T> entityTypeBuilder = EntityType.Builder.create(factoryIn, classification).size(width, height);
    if (fireproof) entityTypeBuilder.immuneToFire();
    EntityType<T> entityType = entityTypeBuilder.build(name);
    entityType.setRegistryName(MODID, name);
    // register the entity type
    event.getRegistry().register(entityType);
    // register attributes
    if(mapSupplier != null) {
      GlobalEntityTypeAttributes.put(entityType, mapSupplier.get().create());
    }
    // register placement (not used unless spawn information is registered with a biome)
    if(placementPredicate != null) {
      EntitySpawnPlacementRegistry.register(entityType, classification == EntityClassification.WATER_CREATURE ? PlacementType.IN_WATER : PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, placementPredicate);
    }
    return entityType;
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
    
  private static void registerItemBlock(final RegistryEvent.Register<Item> event, final Block block, final String registryName) {
    event.getRegistry().register(new BlockItem(block, new Item.Properties().group(GREEK_GROUP)).setRegistryName(MODID, registryName));
  }

  private static Boolean allowsSpawnOnLeaves(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
    return entity == EntityType.OCELOT || entity == EntityType.PARROT || entity == DRYAD_ENTITY;
  }
}
