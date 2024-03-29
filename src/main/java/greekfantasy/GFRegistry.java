package greekfantasy;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import greekfantasy.block.CerberusHeadBlock;
import greekfantasy.block.GoldenStringBlock;
import greekfantasy.block.MobHeadBlock;
import greekfantasy.block.MysteriousBoxBlock;
import greekfantasy.block.NestBlock;
import greekfantasy.block.OilLampBlock;
import greekfantasy.block.OliveOilBlock;
import greekfantasy.block.OrthusHeadBlock;
import greekfantasy.block.PillarBlock;
import greekfantasy.block.PomegranateSaplingBlock;
import greekfantasy.block.ReedsBlock;
import greekfantasy.block.VaseBlock;
import greekfantasy.block.WildRoseBlock;
import greekfantasy.blockentity.MobHeadBlockEntity;
import greekfantasy.blockentity.VaseBlockEntity;
import greekfantasy.enchantment.BaneOfSerpentsEnchantment;
import greekfantasy.enchantment.DeityEnchantment;
import greekfantasy.enchantment.HuntingEnchantment;
import greekfantasy.enchantment.MirroringEnchantment;
import greekfantasy.enchantment.OverstepEnchantment;
import greekfantasy.enchantment.PoisoningEnchantment;
import greekfantasy.enchantment.SilkstepEnchantment;
import greekfantasy.enchantment.SmashingEnchantment;
import greekfantasy.entity.Arion;
import greekfantasy.entity.Automaton;
import greekfantasy.entity.Centaur;
import greekfantasy.entity.Cerastes;
import greekfantasy.entity.Dryad;
import greekfantasy.entity.Elpis;
import greekfantasy.entity.Gigante;
import greekfantasy.entity.GoldenRam;
import greekfantasy.entity.Lampad;
import greekfantasy.entity.Makhai;
import greekfantasy.entity.Naiad;
import greekfantasy.entity.Orthus;
import greekfantasy.entity.Palladium;
import greekfantasy.entity.Pegasus;
import greekfantasy.entity.Satyr;
import greekfantasy.entity.Sparti;
import greekfantasy.entity.Triton;
import greekfantasy.entity.Unicorn;
import greekfantasy.entity.Whirl;
import greekfantasy.entity.boss.Arachne;
import greekfantasy.entity.boss.BronzeBull;
import greekfantasy.entity.boss.Cerberus;
import greekfantasy.entity.boss.Charybdis;
import greekfantasy.entity.boss.CretanMinotaur;
import greekfantasy.entity.boss.Geryon;
import greekfantasy.entity.boss.GiantBoar;
import greekfantasy.entity.boss.Hydra;
import greekfantasy.entity.boss.HydraHead;
import greekfantasy.entity.boss.NemeanLion;
import greekfantasy.entity.boss.Python;
import greekfantasy.entity.boss.Scylla;
import greekfantasy.entity.boss.Talos;
import greekfantasy.entity.misc.BronzeFeather;
import greekfantasy.entity.misc.Curse;
import greekfantasy.entity.misc.CurseOfCirce;
import greekfantasy.entity.misc.Discus;
import greekfantasy.entity.misc.DragonTooth;
import greekfantasy.entity.misc.DragonToothHook;
import greekfantasy.entity.misc.GreekFire;
import greekfantasy.entity.misc.HealingSpell;
import greekfantasy.entity.misc.PoisonSpit;
import greekfantasy.entity.misc.Spear;
import greekfantasy.entity.misc.ThrowingAxe;
import greekfantasy.entity.misc.WaterSpell;
import greekfantasy.entity.misc.WebBall;
import greekfantasy.entity.monster.Ara;
import greekfantasy.entity.monster.BabySpider;
import greekfantasy.entity.monster.Circe;
import greekfantasy.entity.monster.Cyclops;
import greekfantasy.entity.monster.Cyprian;
import greekfantasy.entity.monster.Drakaina;
import greekfantasy.entity.monster.Empusa;
import greekfantasy.entity.monster.Fury;
import greekfantasy.entity.monster.Gorgon;
import greekfantasy.entity.monster.Harpy;
import greekfantasy.entity.monster.MadCow;
import greekfantasy.entity.monster.Minotaur;
import greekfantasy.entity.monster.Shade;
import greekfantasy.entity.monster.Siren;
import greekfantasy.entity.monster.Stymphalian;
import greekfantasy.item.BagOfWindItem;
import greekfantasy.item.BidentItem;
import greekfantasy.item.BronzeFeatherItem;
import greekfantasy.item.BronzeScrapItem;
import greekfantasy.item.CerberusHeadItem;
import greekfantasy.item.ClubItem;
import greekfantasy.item.ConchItem;
import greekfantasy.item.DragonToothRodItem;
import greekfantasy.item.EnchantedBowItem;
import greekfantasy.item.GiganteHeadItem;
import greekfantasy.item.GreekFireItem;
import greekfantasy.item.HasCraftRemainderItem;
import greekfantasy.item.DiscusItem;
import greekfantasy.item.DragonToothItem;
import greekfantasy.item.GFArmorMaterials;
import greekfantasy.item.GFTiers;
import greekfantasy.item.GoldenBallItem;
import greekfantasy.item.GorgonBloodItem;
import greekfantasy.item.HellenicArmorItem;
import greekfantasy.item.HelmOfDarknessItem;
import greekfantasy.item.HornOfPlentyItem;
import greekfantasy.item.InstrumentItem;
import greekfantasy.item.IvorySwordItem;
import greekfantasy.item.KnifeItem;
import greekfantasy.item.NemeanLionHideItem;
import greekfantasy.item.OliveOilItem;
import greekfantasy.item.OliveSalveItem;
import greekfantasy.item.OrthusHeadItem;
import greekfantasy.item.PalladiumItem;
import greekfantasy.item.QuestItem;
import greekfantasy.item.SnakeskinArmorItem;
import greekfantasy.item.SpearItem;
import greekfantasy.item.StaffOfHealingItem;
import greekfantasy.item.ThrowingAxeItem;
import greekfantasy.item.ThunderboltItem;
import greekfantasy.item.ThyrsusItem;
import greekfantasy.item.UnicornHornItem;
import greekfantasy.item.WandOfCirceItem;
import greekfantasy.item.WebBallItem;
import greekfantasy.item.WingedSandalsItem;
import greekfantasy.mob_effect.CurseOfCirceEffect;
import greekfantasy.mob_effect.MirroringEffect;
import greekfantasy.mob_effect.SlowSwimEffect;
import greekfantasy.mob_effect.PrisonerOfHadesEffect;
import greekfantasy.mob_effect.StunnedEffect;
import greekfantasy.util.AddSpawnsStructureModifier;
import greekfantasy.util.BronzeScrapLootModifier;
import greekfantasy.util.QuestLootModifier;
import greekfantasy.util.ReplaceDropsLootModifier;
import greekfantasy.util.SalveRecipe;
import greekfantasy.util.SpawnRulesUtil;
import greekfantasy.worldgen.CentaurStructureProcessor;
import greekfantasy.worldgen.DimensionFilter;
import greekfantasy.worldgen.GoldenTreeGrower;
import greekfantasy.worldgen.HarpyNestFeature;
import greekfantasy.worldgen.LocStructureProcessor;
import greekfantasy.worldgen.OceanVillageStructure;
import greekfantasy.worldgen.OceanVillageStructureProcessor;
import greekfantasy.worldgen.OliveTreeFeature;
import greekfantasy.worldgen.OliveTreeGrower;
import greekfantasy.worldgen.PomegranateTreeGrower;
import greekfantasy.worldgen.SatyrStructureProcessor;
import greekfantasy.worldgen.maze.MazePiece;
import greekfantasy.worldgen.maze.MazeStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "RedundantTypeArguments"})
public final class GFRegistry {

    private static final String MODID = GreekFantasy.MODID;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Registry.BANNER_PATTERN.key(), MODID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registry.STRUCTURE_TYPES.key(), MODID);
    private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registry.STRUCTURE_PIECE.key(), MODID);
    private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(Registry.PLACEMENT_MODIFIERS.key(), MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    private static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, MODID);

    public static void register() {
        BlockReg.register();
        ItemReg.register();
        BannerPatternReg.register();
        PotionReg.register();
        LootModifierReg.register();
        MobEffectReg.register();
        EnchantmentReg.register();
        EntityReg.register();
        BlockEntityReg.register();
        RecipeReg.register();
        ParticleReg.register();
        StructureReg.register();
        StructureProcessorReg.register();
        FeatureReg.register();
        PlacementTypeReg.register();
        StructureModifierReg.register();
    }


    public static final class BlockReg {

        public static void register() {
            BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
            // register blocks and items together
            registerBlockPolishedEtc("limestone", Block.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
            registerBlockPolishedEtc("marble", Block.Properties.of(Material.STONE, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
            registerBlockPolishedChiseledAndBricks("cretan_stone", BlockBehaviour.Properties.of(Material.STONE, MaterialColor.CLAY).requiresCorrectToolForDrops().strength(80.0F, 3600.0F));
            registerLogsPlanksEtc("olive", 2.0F, 3.0F, MaterialColor.WOOD, MaterialColor.SAND, 5, 5, 20);
            registerLogsPlanksEtc("pomegranate", 2.2F, 3.0F, MaterialColor.TERRACOTTA_PURPLE, MaterialColor.CRIMSON_STEM, 0, 0, 0);
            registerLeaves("olive", 30, 60);
            registerLeaves("pomegranate", 0, 0);
            registerLeaves("golden", 30, 60);
            // register terracotta vase
            RegistryObject<Block> COLORLESS_VASE = BLOCKS.register("terracotta_vase", () ->
                    new VaseBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_ORANGE)
                            .strength(0.5F, 1.0F).noOcclusion()));
            GFRegistry.ItemReg.registerItemBlock(COLORLESS_VASE);
            // register colored terracotta vases
            for (DyeColor dyeColor : DyeColor.values()) {
                RegistryObject<Block> VASE = BLOCKS.register(dyeColor.getSerializedName() + "_terracotta_vase", () ->
                        new VaseBlock(BlockBehaviour.Properties.of(Material.STONE, dyeColor.getMaterialColor())
                                .strength(0.5F, 1.0F).noOcclusion()));
                GFRegistry.ItemReg.registerItemBlock(VASE);
            }
        }

        public static final RegistryObject<Block> LIMESTONE = RegistryObject.create(new ResourceLocation(MODID, "limestone"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> MARBLE = RegistryObject.create(new ResourceLocation(MODID, "marble"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> OLIVE_LOG = RegistryObject.create(new ResourceLocation(MODID, "olive_log"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> POMEGRANATE_LOG = RegistryObject.create(new ResourceLocation(MODID, "pomegranate_log"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> OLIVE_LEAVES = RegistryObject.create(new ResourceLocation(MODID, "olive_leaves"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> POMEGRANATE_LEAVES = RegistryObject.create(new ResourceLocation(MODID, "pomegranate_leaves"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> GOLDEN_LEAVES = RegistryObject.create(new ResourceLocation(MODID, "golden_leaves"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> CRETAN_STONE_BRICK = RegistryObject.create(new ResourceLocation(MODID, "cretan_stone_brick"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> POLISHED_CRETAN_STONE = RegistryObject.create(new ResourceLocation(MODID, "polished_cretan_stone"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> CRACKED_CRETAN_STONE_BRICK = RegistryObject.create(new ResourceLocation(MODID, "cracked_cretan_stone_brick"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> CRACKED_POLISHED_CRETAN_STONE = RegistryObject.create(new ResourceLocation(MODID, "cracked_polished_cretan_stone"), ForgeRegistries.BLOCKS);
        public static final RegistryObject<Block> BRONZE_BLOCK = BLOCKS.register("bronze_block", () ->
                new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BROWN)
                        .requiresCorrectToolForDrops().strength(3.0F, 6.0F)
                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> ICHOR_INFUSED_GEARBOX = BLOCKS.register("ichor_infused_gearbox", () ->
                new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                        .requiresCorrectToolForDrops().strength(3.0F, 6.0F)
                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> MYSTERIOUS_BOX = BLOCKS.register("mysterious_box", () ->
                new MysteriousBoxBlock(BlockBehaviour.Properties.of(Material.WOOD)
                        .strength(0.8F, 3.0F).sound(SoundType.WOOD).noOcclusion()));
        public static final RegistryObject<Block> GIGANTE_HEAD = BLOCKS.register("gigante_head", () ->
                new MobHeadBlock(BlockEntityReg.GIGANTE_HEAD, BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion()));
        public static final RegistryObject<Block> ORTHUS_HEAD = BLOCKS.register("orthus_head", () ->
                new OrthusHeadBlock(BlockEntityReg.ORTHUS_HEAD, BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion()));
        public static final RegistryObject<Block> CERBERUS_HEAD = BLOCKS.register("cerberus_head", () ->
                new CerberusHeadBlock(BlockEntityReg.CERBERUS_HEAD, BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion()));
        public static final RegistryObject<Block> OIL_LAMP = BLOCKS.register("oil_lamp", () ->
                new OilLampBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                        .noOcclusion().lightLevel(b -> b.getValue(OilLampBlock.LIT) ? 11 : 0).strength(0.2F, 0.1F)));
        public static final RegistryObject<Block> OLIVE_OIL = BLOCKS.register("olive_oil", () ->
                new OliveOilBlock(BlockBehaviour.Properties.of(Material.FIRE)
                        .noOcclusion().noCollission().instabreak()
                        .randomTicks().lightLevel(b -> b.getValue(OliveOilBlock.LIT) ? 11 : 0).sound(SoundType.WET_GRASS)));
        public static final RegistryObject<Block> GOLDEN_STRING = BLOCKS.register("golden_string", () ->
                new GoldenStringBlock(BlockBehaviour.Properties.of(Material.DECORATION)
                        .lightLevel(b -> 8).instabreak().noCollission().noOcclusion().sound(SoundType.WOOL)));
        public static final RegistryObject<Block> OLIVE_SAPLING = BLOCKS.register("olive_sapling", () ->
                new SaplingBlock(new OliveTreeGrower(), BlockBehaviour.Properties.of(Material.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> POMEGRANATE_SAPLING = BLOCKS.register("pomegranate_sapling", () ->
                new PomegranateSaplingBlock(new PomegranateTreeGrower(), BlockBehaviour.Properties.of(Material.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> GOLDEN_SAPLING = BLOCKS.register("golden_sapling", () ->
                new SaplingBlock(new GoldenTreeGrower(), BlockBehaviour.Properties.of(Material.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> NEST = BLOCKS.register("nest", () ->
                new NestBlock(Block.Properties.of(Material.GRASS, MaterialColor.COLOR_BROWN)
                        .strength(0.5F).sound(SoundType.GRASS)
                        .hasPostProcess((s, r, p) -> true).noOcclusion()));
        public static final RegistryObject<Block> WILD_ROSE = BLOCKS.register("wild_rose", () ->
                new WildRoseBlock(MobEffects.SATURATION, 9, Block.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> REEDS = BLOCKS.register("reeds", () ->
                new ReedsBlock(Block.Properties.of(Material.REPLACEABLE_WATER_PLANT).noCollission().instabreak()
                        .offsetType(BlockBehaviour.OffsetType.XZ).randomTicks().sound(SoundType.CROP)));
        public static final RegistryObject<Block> LIGHT = BLOCKS.register("light", () ->
                new LightBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT)));

        /**
         * Registers all of the following: log, stripped log, wood, stripped wood, planks, stairs, slab, door, trapdoor
         *
         * @param registryName       the base registry name
         * @param strength           the destroy time
         * @param hardness           the explosion resistance
         * @param side               the material color of the side
         * @param top                the material color of the top
         * @param fireSpread         the fire spread chance. The higher the number returned, the faster fire will spread around this block.
         * @param logFlammability    Chance that fire will spread and consume the log. 300 being a 100% chance, 0, being a 0% chance.
         * @param planksFlammability Chance that fire will spread and consume the plank. 300 being a 100% chance, 0, being a 0% chance.
         */
        private static void registerLogsPlanksEtc(final String registryName, final float strength, final float hardness,
                                                  final MaterialColor side, final MaterialColor top,
                                                  final int fireSpread, final int logFlammability, final int planksFlammability) {
            // create properties to apply to wood and non-rotatable variants
            final BlockBehaviour.Properties woodProperties = BlockBehaviour.Properties
                    .of(Material.WOOD, side)
                    .strength(strength, hardness).sound(SoundType.WOOD);
            // create properties to apply to log (multiple material colors)
            final BlockBehaviour.Properties logProperties = BlockBehaviour.Properties
                    .of(Material.WOOD, (state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? top : side)
                    .strength(strength, hardness).sound(SoundType.WOOD);
            // create properties to apply to doors and trapdoors
            final Block.Properties doorProperties = BlockBehaviour.Properties
                    .of(Material.WOOD, side)
                    .strength(strength, hardness).sound(SoundType.WOOD)
                    .noOcclusion().isValidSpawn((b, i, p, a) -> false);

            // register blocks
            final RegistryObject<Block> strippedLog = BLOCKS.register("stripped_" + registryName + "_log", () ->
                    new RotatedPillarBlock(woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return logFlammability;
                        }
                    }
            );
            final RegistryObject<Block> strippedWood = BLOCKS.register("stripped_" + registryName + "_wood", () ->
                    new RotatedPillarBlock(woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return logFlammability;
                        }
                    }
            );
            final RegistryObject<Block> log = BLOCKS.register(registryName + "_log", () ->
                    new RotatedPillarBlock(logProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return logFlammability;
                        }

                        @Override
                        public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
                            if (toolAction == ToolActions.AXE_STRIP) {
                                return strippedLog.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
                            }
                            return super.getToolModifiedState(state, context, toolAction, simulate);
                        }
                    }
            );
            final RegistryObject<Block> wood = BLOCKS.register(registryName + "_wood", () ->
                    new RotatedPillarBlock(woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return logFlammability;
                        }

                        @Override
                        public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
                            if (toolAction == ToolActions.AXE_STRIP) {
                                return strippedWood.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
                            }
                            return super.getToolModifiedState(state, context, toolAction, simulate);
                        }
                    }
            );
            final RegistryObject<Block> planks = BLOCKS.register(registryName + "_planks", () ->
                    new Block(woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return planksFlammability;
                        }
                    }
            );
            final RegistryObject<Block> slab = BLOCKS.register(registryName + "_slab", () ->
                    new SlabBlock(woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return planksFlammability;
                        }
                    }
            );
            final RegistryObject<Block> stairs = BLOCKS.register(registryName + "_stairs", () ->
                    new StairBlock(() -> planks.get().defaultBlockState(), woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return planksFlammability;
                        }
                    }
            );
            final RegistryObject<Block> door = BLOCKS.register(registryName + "_door", () -> new DoorBlock(doorProperties));
            final RegistryObject<Block> trapdoor = BLOCKS.register(registryName + "_trapdoor", () -> new TrapDoorBlock(doorProperties));
            // block items
            GFRegistry.ItemReg.registerItemBlock(log);
            GFRegistry.ItemReg.registerItemBlock(strippedLog);
            GFRegistry.ItemReg.registerItemBlock(wood);
            GFRegistry.ItemReg.registerItemBlock(strippedWood);
            GFRegistry.ItemReg.registerItemBlock(planks);
            GFRegistry.ItemReg.registerItemBlock(slab);
            GFRegistry.ItemReg.registerItemBlock(stairs);
            GFRegistry.ItemReg.registerItemBlock(door);
            GFRegistry.ItemReg.registerItemBlock(trapdoor);
        }

        /**
         * Registers a leaves block.
         *
         * @param registryName the base registry name
         * @param fireSpread   the fire spread chance. The higher the number returned, the faster fire will spread around this block.
         * @param flammability Chance that fire will spread and consume the block. 300 being a 100% chance, 0, being a 0% chance.
         */
        private static void registerLeaves(final String registryName, final int fireSpread, final int flammability) {
            final RegistryObject<Block> leaves = BLOCKS.register(registryName + "_leaves", () ->
                    new LeavesBlock(Block.Properties
                            .of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS)
                            .noOcclusion().isValidSpawn(GFRegistry.BlockReg::allowsSpawnOnLeaves).isSuffocating((s, r, p) -> false)
                            .isViewBlocking((s, r, p) -> false)) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                            return flammability;
                        }
                    }
            );
            // block items
            GFRegistry.ItemReg.registerItemBlock(leaves);
        }

        /**
         * Registers the following: block, slab, stairs, pillar, polished block, polished slab, polished stairs
         *
         * @param registryName the base registry name.
         * @param properties   the block properties
         */
        private static void registerBlockPolishedEtc(final String registryName, final Block.Properties properties) {
            // raw, slab, and stairs
            final RegistryObject<Block> raw = BLOCKS.register(registryName, () -> new Block(properties));
            final RegistryObject<Block> slab = BLOCKS.register(registryName + "_slab", () -> new SlabBlock(properties));
            final RegistryObject<Block> stairs = BLOCKS.register(registryName + "_stairs", () -> new StairBlock(() -> raw.get().defaultBlockState(), properties));
            // polished, slab, and stairs
            final RegistryObject<Block> polished = BLOCKS.register("polished_" + registryName, () -> new Block(properties));
            final RegistryObject<Block> polishedSlab = BLOCKS.register("polished_" + registryName + "_slab", () -> new SlabBlock(properties));
            final RegistryObject<Block> polishedStairs = BLOCKS.register("polished_" + registryName + "_stairs", () -> new StairBlock(() -> polished.get().defaultBlockState(), properties));
            // pillar
            final RegistryObject<Block> pillar = BLOCKS.register(registryName + "_pillar", () -> new PillarBlock(properties));
            // block items
            GFRegistry.ItemReg.registerItemBlock(raw);
            GFRegistry.ItemReg.registerItemBlock(slab);
            GFRegistry.ItemReg.registerItemBlock(stairs);
            GFRegistry.ItemReg.registerItemBlock(polished);
            GFRegistry.ItemReg.registerItemBlock(polishedSlab);
            GFRegistry.ItemReg.registerItemBlock(polishedStairs);
            GFRegistry.ItemReg.registerItemBlock(pillar);
        }

        /**
         * Registers the following: block, chiseled, polished, cracked polished, brick, chiseled brick, cracked brick
         *
         * @param registryName the base registry name
         * @param properties   the block properties
         */
        private static void registerBlockPolishedChiseledAndBricks(final String registryName, final Block.Properties properties) {
            // raw, polished, chiseled, brick, and chiseled_brick
            ItemReg.registerItemBlock(BLOCKS.register(registryName, () -> new Block(properties)));
            ItemReg.registerItemBlock(BLOCKS.register("chiseled_" + registryName, () -> new Block(properties)));
            ItemReg.registerItemBlock(BLOCKS.register("polished_" + registryName, () -> new Block(properties)));
            ItemReg.registerItemBlock(BLOCKS.register("cracked_polished_" + registryName, () -> new Block(properties)));
            ItemReg.registerItemBlock(BLOCKS.register(registryName + "_brick", () -> new Block(properties)));
            ItemReg.registerItemBlock(BLOCKS.register("chiseled_" + registryName + "_brick", () -> new Block(properties)));
            ItemReg.registerItemBlock(BLOCKS.register("cracked_" + registryName + "_brick", () -> new Block(properties)));
        }

        private static Boolean allowsSpawnOnLeaves(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
            return entity == EntityType.OCELOT || entity == EntityType.PARROT; // || entity == EntityReg.DRYAD_ENTITY || entity == EntityReg.LAMPAD_ENTITY;
        }

    }

    public static final class ItemReg {

        public static final CreativeModeTab GF_TAB = new CreativeModeTab(GreekFantasy.MODID) {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(PANFLUTE.get());
            }
        };

        private static final FoodProperties OLIVES_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build();
        private static final FoodProperties POMEGRANATE_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F)
                .effect(() -> new MobEffectInstance(MobEffectReg.PRISONER_OF_HADES.get(), 6000), 1.0F).build();
        private static final FoodProperties AMBROSIA_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(1.2F).alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 800, 0), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0F).build();
        private static final FoodProperties OLIVE_SALVE_FOOD = new FoodProperties.Builder().alwaysEat().build();
        private static final FoodProperties PINECONE_FOOD = new FoodProperties.Builder().nutrition(1).saturationMod(0.0125F).build();

        public static void register() {
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
            FMLJavaModLoadingContext.get().getModEventBus().addListener(GFRegistry.ItemReg::registerComposterRecipes);
        }

        private static void registerComposterRecipes(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                ComposterBlock.COMPOSTABLES.put(GOLDEN_SAPLING.get().asItem(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(OLIVES.get().asItem(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(PINECONE.get().asItem(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(POMEGRANATE_SAPLING.get().asItem(), 0.3F);
                ComposterBlock.COMPOSTABLES.put(POMEGRANATE.get().asItem(), 0.65F);
                ComposterBlock.COMPOSTABLES.put(REEDS.get().asItem(), 0.5F);
                ComposterBlock.COMPOSTABLES.put(WILD_ROSE.get().asItem(), 0.85F);
            });
        }

        //// LEGENDARY WEAPONS ////
        public static final RegistryObject<Item> THUNDERBOLT = ITEMS.register("thunderbolt", () ->
                new ThunderboltItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).durability(170)));
        public static final RegistryObject<Item> WAND_OF_CIRCE = ITEMS.register("wand_of_circe", () ->
                new WandOfCirceItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).durability(54)));
        public static final RegistryObject<Item> AVERNAL_BOW = ITEMS.register("avernal_bow", () ->
                new EnchantedBowItem.AvernalBowItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).durability(384)));
        public static final RegistryObject<Item> APOLLO_BOW = ITEMS.register("apollo_bow", () ->
                new EnchantedBowItem.ApolloBowItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC).durability(434)));
        public static final RegistryObject<Item> ARTEMIS_BOW = ITEMS.register("artemis_bow", () ->
                new EnchantedBowItem.ArtemisBowItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC).durability(434)));

        //// WEAPONS ////
        public static final RegistryObject<Item> WOODEN_CLUB = ITEMS.register("wooden_club", () ->
                new ClubItem(Tiers.WOOD, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> STONE_CLUB = ITEMS.register("stone_club", () ->
                new ClubItem(Tiers.STONE, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> IRON_CLUB = ITEMS.register("iron_club", () ->
                new ClubItem(Tiers.IRON, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> BIDENT = ITEMS.register("bident", () ->
                new BidentItem(Tiers.DIAMOND, new Item.Properties().rarity(Rarity.UNCOMMON).tab(GF_TAB).setNoRepair()));
        public static final RegistryObject<Item> WOODEN_SPEAR = ITEMS.register("wooden_spear", () ->
                new SpearItem(Tiers.WOOD, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> FLINT_SPEAR = ITEMS.register("flint_spear", () ->
                new SpearItem(GFTiers.FLINT, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> STONE_SPEAR = ITEMS.register("stone_spear", () ->
                new SpearItem(Tiers.STONE, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> IRON_SPEAR = ITEMS.register("iron_spear", () ->
                new SpearItem(Tiers.IRON, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GOLDEN_SPEAR = ITEMS.register("golden_spear", () ->
                new SpearItem(Tiers.GOLD, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> DIAMOND_SPEAR = ITEMS.register("diamond_spear", () ->
                new SpearItem(Tiers.DIAMOND, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> NETHERITE_SPEAR = ITEMS.register("netherite_spear", () ->
                new SpearItem(Tiers.NETHERITE, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> FLINT_KNIFE = ITEMS.register("flint_knife", () ->
                new KnifeItem(GFTiers.FLINT, 3, -1.7F, -1.0F, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> IVORY_SWORD = ITEMS.register("ivory_sword", () ->
                new IvorySwordItem(GFTiers.IVORY, 3, -2.2F, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> THROWING_AXE = ITEMS.register("throwing_axe", () ->
                new ThrowingAxeItem(Tiers.IRON, 6.0F, -3.1F, new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)));
        public static final RegistryObject<Item> DRAGON_TOOTH_ROD = ITEMS.register("dragon_tooth_rod", () ->
                new DragonToothRodItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC).durability(128)));
        public static final RegistryObject<Item> DISCUS = ITEMS.register("discus", () ->
                new DiscusItem(new Item.Properties().tab(GF_TAB).stacksTo(16)));
        public static final RegistryObject<Item> GREEK_FIRE = ITEMS.register("greek_fire", () ->
                new GreekFireItem(new Item.Properties().tab(GF_TAB).stacksTo(16)));
        public static final RegistryObject<Item> WEB_BALL = ITEMS.register("web_ball", () ->
                new WebBallItem(new Item.Properties().tab(GF_TAB).stacksTo(16)));

        //// LEGENDARY TOOLS AND ITEMS ////
        public static final RegistryObject<Item> BRONZE_FEATHER = ITEMS.register("bronze_feather", () ->
                new BronzeFeatherItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> DRAGON_TOOTH = ITEMS.register("dragon_tooth", () ->
                new DragonToothItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)));
        public static final RegistryObject<Item> MIRROR = ITEMS.register("mirror", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> CONCH = ITEMS.register("conch", () ->
                new ConchItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1).durability(64)));
        public static final RegistryObject<Item> UNICORN_HORN = ITEMS.register("unicorn_horn", () ->
                new UnicornHornItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).durability(44)));
        public static final RegistryObject<Item> HEART_OF_TALOS = ITEMS.register("heart_of_talos", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(16)));
        public static final RegistryObject<Item> BAG_OF_WIND = ITEMS.register("bag_of_wind", () ->
                new BagOfWindItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).durability(24)));
        public static final RegistryObject<Item> STAFF_OF_HEALING = ITEMS.register("staff_of_healing", () ->
                new StaffOfHealingItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).durability(384)));
        public static final RegistryObject<Item> THYRSUS = ITEMS.register("thyrsus", () ->
                new ThyrsusItem(GFTiers.THYRSUS, 2.5F, -2.2F, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AMBROSIA = ITEMS.register("ambrosia", () ->
                new HasCraftRemainderItem(ItemReg.HORN, new Item.Properties().tab(GF_TAB).food(AMBROSIA_FOOD).rarity(Rarity.EPIC)));
        public static final RegistryObject<Item> HORN_OF_PLENTY = ITEMS.register("horn_of_plenty", () ->
                new HornOfPlentyItem(ItemReg.HORN, new Item.Properties().tab(GF_TAB).durability(24).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> GOLDEN_FLEECE = ITEMS.register("golden_fleece", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)));
        public static final RegistryObject<Item> GOLDEN_BALL = ITEMS.register("golden_ball", () ->
                new GoldenBallItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).durability(680)));
        public static final RegistryObject<Item> ICHOR = ITEMS.register("ichor", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)) {
                    @Override
                    public boolean isFoil(ItemStack stack) {
                        return true;
                    }
                });
        public static final RegistryObject<Item> SPIDER_BANNER_PATTERN = ITEMS.register("spider_banner_pattern", () ->
                new BannerPatternItem(TagKey.create(Registry.BANNER_PATTERN_REGISTRY, new ResourceLocation(GreekFantasy.MODID, "pattern_item/spider")), new Item.Properties().tab(GF_TAB).stacksTo(1).rarity(Rarity.RARE)));

        //// LEGENDARY ARMOR ////
        public static final RegistryObject<Item> HELM_OF_DARKNESS = ITEMS.register("helm_of_darkness", () ->
                new HelmOfDarknessItem(GFArmorMaterials.AVERNAL, new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC)));
        public static final RegistryObject<Item> WINGED_SANDALS = ITEMS.register("winged_sandals", () ->
                new WingedSandalsItem(GFArmorMaterials.WINGED, new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC)));
        public static final RegistryObject<Item> NEMEAN_LION_HIDE = ITEMS.register("nemean_lion_hide", () ->
                new NemeanLionHideItem(GFArmorMaterials.NEMEAN, EquipmentSlot.HEAD,
                        new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).setNoRepair()));

        //// ARMOR ////
        public static final RegistryObject<Item> HELLENIC_HELMET = ITEMS.register("hellenic_helmet", () ->
                new HellenicArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.HEAD, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> HELLENIC_CHESTPLATE = ITEMS.register("hellenic_chestplate", () ->
                new HellenicArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.CHEST, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> HELLENIC_LEGGINGS = ITEMS.register("hellenic_leggings", () ->
                new HellenicArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.LEGS, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> HELLENIC_BOOTS = ITEMS.register("hellenic_boots", () ->
                new HellenicArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.FEET, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_HELMET = ITEMS.register("snakeskin_helmet", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.HEAD, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_CHESTPLATE = ITEMS.register("snakeskin_chestplate", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.CHEST, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_LEGGINGS = ITEMS.register("snakeskin_leggings", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.LEGS, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_BOOTS = ITEMS.register("snakeskin_boots", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.FEET, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));

        //// MISC ITEMS ////
        public static final RegistryObject<QuestItem> QUEST = ITEMS.register("quest", () ->
                new QuestItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<InstrumentItem> PANFLUTE = ITEMS.register("panflute", () ->
                new InstrumentItem(new Item.Properties().tab(GF_TAB).stacksTo(1), () -> SoundEvents.NOTE_BLOCK_FLUTE));
        public static final RegistryObject<InstrumentItem> WOODEN_LYRE = ITEMS.register("wooden_lyre", () ->
                new InstrumentItem(new Item.Properties().tab(GF_TAB).stacksTo(1), () -> SoundEvents.NOTE_BLOCK_HARP));
        public static final RegistryObject<InstrumentItem> GOLDEN_LYRE = ITEMS.register("golden_lyre", () ->
                new InstrumentItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).stacksTo(1), () -> SoundEvents.NOTE_BLOCK_GUITAR));
        public static final RegistryObject<Item> OLIVES = ITEMS.register("olives", () ->
                new Item(new Item.Properties().tab(GF_TAB).food(OLIVES_FOOD)));
        public static final RegistryObject<Item> OLIVE_OIL = ITEMS.register("olive_oil", () ->
                new OliveOilItem(BlockReg.OLIVE_OIL.get(), new Item.Properties().tab(GF_TAB).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE)));
        public static final RegistryObject<Item> OLIVE_SALVE = ITEMS.register("olive_salve", () ->
                new OliveSalveItem(new Item.Properties().tab(GF_TAB).stacksTo(1).food(OLIVE_SALVE_FOOD)));
        public static final RegistryObject<Item> POMEGRANATE = ITEMS.register("pomegranate", () ->
                new Item(new Item.Properties().tab(GF_TAB).food(POMEGRANATE_FOOD)));

        //// CRAFTING MATERIALS ////
        public static final RegistryObject<Item> AVERNAL_FEATHER = ITEMS.register("avernal_feather", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_HAIR = ITEMS.register("avernal_hair", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_WING = ITEMS.register("avernal_wing", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_HIDE = ITEMS.register("avernal_hide", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_CLAW = ITEMS.register("avernal_claw", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_SHARD = ITEMS.register("avernal_shard", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> ICHOR_INFUSED_GEAR = ITEMS.register("ichor_infused_gear", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GOLDEN_STRING = ITEMS.register("golden_string", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GORGON_BLOOD = ITEMS.register("gorgon_blood", () -> new GorgonBloodItem(new Item.Properties().tab(GF_TAB).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE)));
        public static final RegistryObject<Item> BOAR_EAR = ITEMS.register("boar_ear", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BOAR_TUSK = ITEMS.register("boar_tusk", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> DEADLY_FANG = ITEMS.register("deadly_fang", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GOLDEN_BRIDLE = ITEMS.register("golden_bridle", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> HORN = ITEMS.register("horn", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> PINECONE = ITEMS.register("pinecone", () -> new Item(new Item.Properties().tab(GF_TAB).food(PINECONE_FOOD)));
        public static final RegistryObject<Item> REEDS = ITEMS.register("reeds", () -> new BlockItem(BlockReg.REEDS.get(), new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> SCYLLA_BONE = ITEMS.register("scylla_bone", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> SNAKESKIN = ITEMS.register("snakeskin", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> TOUGH_SNAKESKIN = ITEMS.register("tough_snakeskin", () -> new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        //// BRONZE SCRAP ////
        public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_NUGGET = ITEMS.register("bronze_nugget", () -> new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_BOWL = ITEMS.register("bronze_bowl", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_COINS = ITEMS.register("bronze_coins", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_CUIRASS = ITEMS.register("bronze_cuirass", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_FIGURINE = ITEMS.register("bronze_figurine", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_GOBLET = ITEMS.register("bronze_goblet", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_HELMET = ITEMS.register("bronze_helmet", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_SHIELD = ITEMS.register("bronze_shield", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BRONZE_VASE = ITEMS.register("bronze_vase", () -> new BronzeScrapItem(new Item.Properties().tab(GF_TAB)));

        //// SPAWN EGGS ////
        public static final RegistryObject<Item> ARA_SPAWN_EGG = ITEMS.register("ara_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.ARA, 0xffffff, 0xbbbbbb, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> ARACHNE_SPAWN_EGG = ITEMS.register("arachne_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.ARACHNE, 0x9c7b50, 0xa80e0e, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> ARION_SPAWN_EGG = ITEMS.register("arion_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.ARION, 0xdfc014, 0xb58614, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> CENTAUR_SPAWN_EGG = ITEMS.register("centaur_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.CENTAUR, 0x734933, 0x83251f, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> CERASTES_SPAWN_EGG = ITEMS.register("cerastes_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.CERASTES, 0x847758, 0x997c4d, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> CIRCE_SPAWN_EGG = ITEMS.register("circe_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.CIRCE, 0x844797, 0xe8c669, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> CRETAN_MINOTAUR_SPAWN_EGG = ITEMS.register("cretan_minotaur_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.CRETAN_MINOTAUR, 0x2a2a2a, 0x734933, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> CYCLOPS_SPAWN_EGG = ITEMS.register("cyclops_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.CYCLOPS, 0xda662c, 0x2c1e0e, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> CYPRIAN_SPAWN_EGG = ITEMS.register("cyprian_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.CYPRIAN, 0x443626, 0x83251f, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> DRAKAINA_SPAWN_EGG = ITEMS.register("drakaina_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.DRAKAINA, 0x724e36, 0x398046, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> DRYAD_SPAWN_EGG = ITEMS.register("dryad_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.DRYAD, 0x443626, 0xfed93f, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> ELPIS_SPAWN_EGG = ITEMS.register("elpis_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.ELPIS, 0xe7aae4, 0xeeeeee, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> EMPUSA_SPAWN_EGG = ITEMS.register("empusa_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.EMPUSA, 0x222222, 0x83251f, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> FURY_SPAWN_EGG = ITEMS.register("fury_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.FURY, 0xbd4444, 0x6c2426, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GIANT_BOAR_SPAWN_EGG = ITEMS.register("giant_boar_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.GIANT_BOAR, 0x5b433a, 0xe8a074, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GIGANTE_SPAWN_EGG = ITEMS.register("gigante_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.GIGANTE, 0xd3dba7, 0x6a602b, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GOLDEN_RAM_SPAWN_EGG = ITEMS.register("golden_ram_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.GOLDEN_RAM, 0xdfc014, 0xd08d26, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GORGON_SPAWN_EGG = ITEMS.register("gorgon_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.GORGON, 0x3a8228, 0xbcbcbc, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> HARPY_SPAWN_EGG = ITEMS.register("harpy_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.HARPY, 0x724e36, 0x332411, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> HYDRA_SPAWN_EGG = ITEMS.register("hydra_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.HYDRA, 0x372828, 0x9d4217, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> LAMPAD_SPAWN_EGG = ITEMS.register("lampad_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.LAMPAD, 0x643026, 0xfed93f, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> MAD_COW_SPAWN_EGG = ITEMS.register("mad_cow_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.MAD_COW, 0x443626, 0xcf9797, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> MAKHAI_SPAWN_EGG = ITEMS.register("makhai_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.MAKHAI, 0x513f38, 0xf33531, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> MINOTAUR_SPAWN_EGG = ITEMS.register("minotaur_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.MINOTAUR, 0x443626, 0x734933, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> NAIAD_SPAWN_EGG = ITEMS.register("naiad_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.NAIAD, 0x7caba1, 0xe67830, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> NEMEAN_LION_SPAWN_EGG = ITEMS.register("nemean_lion_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.NEMEAN_LION, 0xd08d26, 0x7d3107, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> ORTHUS_SPAWN_EGG = ITEMS.register("orthus_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.ORTHUS, 0x493569, 0xe42e2e, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> PEGASUS_SPAWN_EGG = ITEMS.register("pegasus_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.PEGASUS, 0x916535, 0xe8e8e8, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> PYTHON_SPAWN_EGG = ITEMS.register("python_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.PYTHON, 0x3a8228, 0x1e4c11, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> SATYR_SPAWN_EGG = ITEMS.register("satyr_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.SATYR, 0x54371d, 0xa16648, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> SHADE_SPAWN_EGG = ITEMS.register("shade_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.SHADE, 0x222222, 0x000000, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> SIREN_SPAWN_EGG = ITEMS.register("siren_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.SIREN, 0x729f92, 0x398046, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> STYMPHALIAN_SPAWN_EGG = ITEMS.register("stymphalian_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.STYMPHALIAN, 0x684822, 0xc08845, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> TRITON_SPAWN_EGG = ITEMS.register("triton_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.TRITON, 0x527f72, 0x398046, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> UNICORN_SPAWN_EGG = ITEMS.register("unicorn_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.UNICORN, 0xeeeeee, 0xe8e8e8, new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> WHIRL_SPAWN_EGG = ITEMS.register("whirl_spawn_egg", () ->
                new ForgeSpawnEggItem(EntityReg.WHIRL, 0x1EF6FF, 0xededed, new Item.Properties().tab(GF_TAB)));

        // TODO spawn eggs for other bosses? bull, geryon, talos, cerberus, charybdis

        //// LEGENDARY ITEM BLOCKS ////
        public static final RegistryObject<Item> PALLADIUM = ITEMS.register("palladium", () ->
                new PalladiumItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1)));

        //// ITEM BLOCKS ////
        public static final RegistryObject<BlockItem> BRONZE_BLOCK = registerItemBlock(BlockReg.BRONZE_BLOCK);
        public static final RegistryObject<BlockItem> ICHOR_INFUSED_GEARBOX = registerItemBlock(BlockReg.ICHOR_INFUSED_GEARBOX, p -> p.rarity(Rarity.RARE));
        public static final RegistryObject<BlockItem> MYSTERIOUS_BOX = registerItemBlock(BlockReg.MYSTERIOUS_BOX, p -> p.rarity(Rarity.UNCOMMON));
        public static final RegistryObject<BlockItem> GIGANTE_HEAD = ITEMS.register("gigante_head", () -> new GiganteHeadItem(BlockReg.GIGANTE_HEAD.get(), new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<BlockItem> ORTHUS_HEAD = ITEMS.register("orthus_head", () -> new OrthusHeadItem(BlockReg.ORTHUS_HEAD.get(), new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<BlockItem> CERBERUS_HEAD = ITEMS.register("cerberus_head", () -> new CerberusHeadItem(BlockReg.CERBERUS_HEAD.get(), new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<BlockItem> OIL_LAMP = registerItemBlock(BlockReg.OIL_LAMP);
        public static final RegistryObject<BlockItem> OLIVE_SAPLING = registerItemBlock(BlockReg.OLIVE_SAPLING);
        public static final RegistryObject<BlockItem> POMEGRANATE_SAPLING = registerItemBlock(BlockReg.POMEGRANATE_SAPLING);
        public static final RegistryObject<BlockItem> GOLDEN_SAPLING = registerItemBlock(BlockReg.GOLDEN_SAPLING, p -> p.rarity(Rarity.UNCOMMON));
        public static final RegistryObject<BlockItem> WILD_ROSE = registerItemBlock(BlockReg.WILD_ROSE, p -> p.rarity(Rarity.UNCOMMON));
        public static final RegistryObject<BlockItem> NEST = registerItemBlock(BlockReg.NEST);

        /**
         * Registers an item for the given block
         *
         * @param blockSupplier the block supplier
         * @return the BlockItem registry object
         */
        private static RegistryObject<BlockItem> registerItemBlock(final RegistryObject<? extends Block> blockSupplier) {
            return ITEMS.register(blockSupplier.getId().getPath(), itemBlock(blockSupplier, p -> {}));
        }

        /**
         * Registers an item for the given block
         *
         * @param blockSupplier the block supplier
         * @param consumer the item properties consumer
         * @return the BlockItem registry object
         */
        private static RegistryObject<BlockItem> registerItemBlock(final RegistryObject<? extends Block> blockSupplier, Consumer<Item.Properties> consumer) {
            return ITEMS.register(blockSupplier.getId().getPath(), itemBlock(blockSupplier, consumer));
        }

        /**
         * Creates a block item supplier for the given block
         *
         * @param blockSupplier the block supplier
         * @return a supplier for the block item
         */
        private static Supplier<BlockItem> itemBlock(final RegistryObject<? extends Block> blockSupplier, Consumer<Item.Properties> consumer) {
            Item.Properties props = new Item.Properties().tab(GF_TAB);
            consumer.accept(props);
            return () -> new BlockItem(blockSupplier.get(), props);
        }
    }

    public static final class BannerPatternReg {
        public static void register() {
            BANNER_PATTERNS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<BannerPattern> SPIDER = BANNER_PATTERNS.register("spider", () ->
                new BannerPattern("gf:spi"));
    }

    public static final class EntityReg {

        public static void register() {
            ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
            // event listeners
            FMLJavaModLoadingContext.get().getModEventBus().addListener(GFRegistry.EntityReg::registerEntityAttributes);
        }

        private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            register(event, AUTOMATON.get(), Automaton::createAttributes, Mob::checkMobSpawnRules);
            register(event, ARA.get(), Ara::createAttributes, SpawnRulesUtil::checkMonsterSpawnRules);
            register(event, ARACHNE.get(), Arachne::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, ARION.get(), Arion::createAttributes, Mob::checkMobSpawnRules);
            register(event, BABY_SPIDER.get(), BabySpider::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, BRONZE_BULL.get(), BronzeBull::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, CENTAUR.get(), Centaur::createAttributes, Mob::checkMobSpawnRules);
            register(event, CERASTES.get(), Cerastes::createAttributes, Cerastes::checkCerastesSpawnRules);
            register(event, CERBERUS.get(), Cerberus::createAttributes, SpawnRulesUtil::checkMonsterSpawnRules);
            register(event, CHARYBDIS.get(), Charybdis::createAttributes, SpawnRulesUtil::checkWaterMonsterSpawnRules);
            register(event, CIRCE.get(), Circe::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, CRETAN_MINOTAUR.get(), CretanMinotaur::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, CYCLOPS.get(), Cyclops::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, CYPRIAN.get(), Cyprian::createAttributes, SpawnRulesUtil::checkMonsterSpawnRules);
            register(event, DRAKAINA.get(), Drakaina::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, DRYAD.get(), Dryad::createAttributes, Mob::checkMobSpawnRules);
            register(event, ELPIS.get(), Elpis::createAttributes, Mob::checkMobSpawnRules);
            register(event, EMPUSA.get(), Empusa::createAttributes, Empusa::checkEmpusaSpawnRules);
            register(event, FURY.get(), Fury::createAttributes, Monster::checkAnyLightMonsterSpawnRules);
            register(event, GERYON.get(), Geryon::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, GIANT_BOAR.get(), GiantBoar::createAttributes, SpawnRulesUtil::checkMonsterSpawnRules);
            register(event, GIGANTE.get(), Gigante::createAttributes, Mob::checkMobSpawnRules);
            register(event, GOLDEN_RAM.get(), GoldenRam::createAttributes, Mob::checkMobSpawnRules);
            register(event, GORGON.get(), Gorgon::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, HARPY.get(), Harpy::createAttributes, Monster::checkAnyLightMonsterSpawnRules);
            register(event, HYDRA.get(), Hydra::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, HYDRA_HEAD.get(), HydraHead::createAttributes, null);
            register(event, LAMPAD.get(), Lampad::createAttributes, Mob::checkMobSpawnRules);
            register(event, MAD_COW.get(), MadCow::createAttributes, SpawnRulesUtil::checkMonsterSpawnRules);
            register(event, MAKHAI.get(), Makhai::createAttributes, SpawnRulesUtil::checkAnyLightMonsterSpawnRules);
            register(event, MINOTAUR.get(), Minotaur::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, NAIAD.get(), Naiad::createAttributes, SpawnRulesUtil::checkWaterMobSpawnRules);
            register(event, NEMEAN_LION.get(), NemeanLion::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, ORTHUS.get(), Orthus::createAttributes, SpawnRulesUtil::checkMonsterSpawnRules);
            event.put(PALLADIUM.get(), Palladium.createAttributes().build());
            register(event, PEGASUS.get(), Pegasus::createAttributes, Mob::checkMobSpawnRules);
            register(event, PYTHON.get(), Python::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, SATYR.get(), Satyr::createAttributes, Mob::checkMobSpawnRules);
            register(event, SCYLLA.get(), Scylla::createAttributes, SpawnRulesUtil::checkWaterMonsterSpawnRules);
            register(event, SHADE.get(), Shade::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, SIREN.get(), Siren::createAttributes, Siren::checkSirenSpawnRules);
            register(event, SPARTI.get(), Sparti::createAttributes, Mob::checkMobSpawnRules);
            register(event, STYMPHALIAN.get(), Stymphalian::createAttributes, Monster::checkMonsterSpawnRules);
            register(event, TALOS.get(), Talos::createAttributes, SpawnRulesUtil::checkMonsterSpawnRules);
            register(event, TRITON.get(), Triton::createAttributes, SpawnRulesUtil::checkWaterMobSpawnRules);
            register(event, UNICORN.get(), Unicorn::createAttributes, Mob::checkMobSpawnRules);
            register(event, WHIRL.get(), Whirl::createAttributes, SpawnRulesUtil::checkWaterMobSpawnRules);
        }

        /**
         * Helper method to register mob entity attributes and placement predicates at the same time.
         * @param event the entity attribute creation event
         * @param entityType the entity type
         * @param attributeSupplier a supplier to the attribute builder
         * @param placementPredicate the placement predicate, can be null
         * @param <T> a mob entity
         */
        private static <T extends Mob> void register(final EntityAttributeCreationEvent event, final EntityType<T> entityType,
                                                     Supplier<AttributeSupplier.Builder> attributeSupplier,
                                                     @Nullable final SpawnPlacements.SpawnPredicate<T> placementPredicate) {
            // register attributes
            event.put(entityType, attributeSupplier.get().build());
            // register placement
            if (placementPredicate != null) {
                final SpawnPlacements.Type placementType = entityType.getCategory() == MobCategory.WATER_CREATURE ? SpawnPlacements.Type.IN_WATER : SpawnPlacements.Type.ON_GROUND;
                // wrap the spawn predicate in one that also checks dimension predicate
                final SpawnPlacements.SpawnPredicate<T> placement = (entity, level, reason, pos, rand) ->
                        GreekFantasy.CONFIG.spawnMatchesDimension(level.getLevel()) && placementPredicate.test(entity, level, reason, pos, rand);
                // actually register the placement
                SpawnPlacements.register(entityType, placementType, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, placement);
            }
        }

        // creature
        public static final RegistryObject<EntityType<? extends Automaton>> AUTOMATON = ENTITY_TYPES.register("automaton", () ->
                EntityType.Builder.of(Automaton::new, MobCategory.MISC)
                        .sized(0.94F, 2.48F)
                        .build("automaton"));
        public static final RegistryObject<EntityType<? extends Ara>> ARA = ENTITY_TYPES.register("ara", () ->
                EntityType.Builder.of(Ara::new, MobCategory.MONSTER)
                        .sized(0.67F, 1.8F)
                        .build("ara"));
        public static final RegistryObject<EntityType<? extends Arachne>> ARACHNE = ENTITY_TYPES.register("arachne", () ->
                EntityType.Builder.of(Arachne::new, MobCategory.MONSTER)
                        .sized(0.94F, 1.9F)
                        .build("arachne"));
        public static final RegistryObject<EntityType<? extends Arion>> ARION = ENTITY_TYPES.register("arion", () ->
                EntityType.Builder.of(Arion::new, MobCategory.CREATURE)
                        .sized(1.39F, 1.98F)
                        .build("arion"));
        public static final RegistryObject<EntityType<? extends BabySpider>> BABY_SPIDER = ENTITY_TYPES.register("baby_spider", () ->
                EntityType.Builder.of(BabySpider::new, MobCategory.MONSTER)
                        .sized(0.5F, 0.65F)
                        .build("baby_spider"));
        public static final RegistryObject<EntityType<? extends BronzeBull>> BRONZE_BULL = ENTITY_TYPES.register("bronze_bull", () ->
                EntityType.Builder.of(BronzeBull::new, MobCategory.MONSTER)
                        .sized(1.95F, 2.98F).fireImmune()
                        .build("bronze_bull"));
        public static final RegistryObject<EntityType<? extends Centaur>> CENTAUR = ENTITY_TYPES.register("centaur", () ->
                EntityType.Builder.of(Centaur::new, MobCategory.CREATURE)
                        .sized(1.39F, 2.49F)
                        .build("centaur"));
        public static final RegistryObject<EntityType<? extends Cerastes>> CERASTES = ENTITY_TYPES.register("cerastes", () ->
                EntityType.Builder.of(Cerastes::new, MobCategory.CREATURE)
                        .sized(0.98F, 0.94F)
                        .build("cerastes"));
        public static final RegistryObject<EntityType<? extends Cerberus>> CERBERUS = ENTITY_TYPES.register("cerberus", () ->
                EntityType.Builder.of(Cerberus::new, MobCategory.MONSTER)
                        .sized(1.98F, 1.9F).fireImmune()
                        .build("cerberus"));
        public static final RegistryObject<EntityType<? extends Charybdis>> CHARYBDIS = ENTITY_TYPES.register("charybdis", () ->
                EntityType.Builder.of(Charybdis::new, MobCategory.WATER_CREATURE)
                        .sized(5.9F, 7.9F).fireImmune()
                        .build("charybdis"));
        public static final RegistryObject<EntityType<? extends Circe>> CIRCE = ENTITY_TYPES.register("circe", () ->
                EntityType.Builder.of(Circe::new, MobCategory.MONSTER)
                        .sized(0.67F, 1.8F)
                        .build("circe"));
        public static final RegistryObject<EntityType<? extends CretanMinotaur>> CRETAN_MINOTAUR = ENTITY_TYPES.register("cretan_minotaur", () ->
                EntityType.Builder.of(CretanMinotaur::new, MobCategory.MONSTER)
                        .sized(0.989F, 3.395F).fireImmune()
                        .build("cretan_minotaur"));
        public static final RegistryObject<EntityType<? extends Cyclops>> CYCLOPS = ENTITY_TYPES.register("cyclops", () ->
                EntityType.Builder.of(Cyclops::new, MobCategory.MONSTER)
                        .sized(0.99F, 2.92F)
                        .build("cyclops"));
        public static final RegistryObject<EntityType<? extends Cyprian>> CYPRIAN = ENTITY_TYPES.register("cyprian", () ->
                EntityType.Builder.of(Cyprian::new, MobCategory.MONSTER)
                        .sized(1.39F, 2.49F)
                        .build("cyprian"));
        public static final RegistryObject<EntityType<? extends Drakaina>> DRAKAINA = ENTITY_TYPES.register("drakaina", () ->
                EntityType.Builder.of(Drakaina::new, MobCategory.MONSTER)
                        .sized(0.9F, 1.9F)
                        .build("drakaina"));
        public static final RegistryObject<EntityType<? extends DragonToothHook>> DRAGON_TOOTH_HOOK = ENTITY_TYPES.register("dragon_tooth_hook", () ->
                EntityType.Builder.<DragonToothHook>of(DragonToothHook::new, MobCategory.MISC)
                        .noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5)
                        .build("dragon_tooth_hook"));
        public static final RegistryObject<EntityType<? extends Dryad>> DRYAD = ENTITY_TYPES.register("dryad", () ->
                EntityType.Builder.of(Dryad::new, MobCategory.CREATURE)
                        .sized(0.48F, 1.8F)
                        .build("dryad"));
        public static final RegistryObject<EntityType<? extends Elpis>> ELPIS = ENTITY_TYPES.register("elpis", () ->
                EntityType.Builder.of(Elpis::new, MobCategory.CREATURE)
                        .sized(0.45F, 0.45F).fireImmune()
                        .build("elpis"));
        public static final RegistryObject<EntityType<? extends Empusa>> EMPUSA = ENTITY_TYPES.register("empusa", () ->
                EntityType.Builder.of(Empusa::new, MobCategory.MONSTER)
                        .sized(0.67F, 1.8F).fireImmune()
                        .build("empusa"));
        public static final RegistryObject<EntityType<? extends Fury>> FURY = ENTITY_TYPES.register("fury", () ->
                EntityType.Builder.of(Fury::new, MobCategory.MONSTER)
                        .sized(0.67F, 1.4F).fireImmune()
                        .build("fury"));
        public static final RegistryObject<EntityType<? extends Geryon>> GERYON = ENTITY_TYPES.register("geryon", () ->
                EntityType.Builder.of(Geryon::new, MobCategory.MONSTER)
                        .sized(1.98F, 4.96F).fireImmune()
                        .build("geryon"));
        public static final RegistryObject<EntityType<? extends Gigante>> GIGANTE = ENTITY_TYPES.register("gigante", () ->
                EntityType.Builder.of(Gigante::new, MobCategory.CREATURE)
                        .sized(1.98F, 4.79F)
                        .build("gigante"));
        public static final RegistryObject<EntityType<? extends GoldenRam>> GOLDEN_RAM = ENTITY_TYPES.register("golden_ram", () ->
                EntityType.Builder.of(GoldenRam::new, MobCategory.CREATURE)
                        .sized(0.96F, 1.56F)
                        .build("golden_ram"));
        public static final RegistryObject<EntityType<? extends Gorgon>> GORGON = ENTITY_TYPES.register("gorgon", () ->
                EntityType.Builder.of(Gorgon::new, MobCategory.MONSTER)
                        .sized(0.9F, 1.9F)
                        .build("gorgon"));
        public static final RegistryObject<EntityType<? extends Harpy>> HARPY = ENTITY_TYPES.register("harpy", () ->
                EntityType.Builder.of(Harpy::new, MobCategory.MONSTER)
                        .sized(0.7F, 1.8F)
                        .build("harpy"));
        public static final RegistryObject<EntityType<? extends Hydra>> HYDRA = ENTITY_TYPES.register("hydra", () ->
                EntityType.Builder.of(Hydra::new, MobCategory.MONSTER)
                        .sized(2.4F, 2.24F).fireImmune()
                        .build("hydra"));
        public static final RegistryObject<EntityType<? extends HydraHead>> HYDRA_HEAD = ENTITY_TYPES.register("hydra_head", () ->
                EntityType.Builder.of(HydraHead::new, MobCategory.MISC)
                        .sized(0.68F, 1.88F).noSummon()
                        .build("hydra_head"));
        public static final RegistryObject<EntityType<? extends GiantBoar>> GIANT_BOAR = ENTITY_TYPES.register("giant_boar", () ->
                EntityType.Builder.of(GiantBoar::new, MobCategory.MONSTER)
                        .sized(2.653F, 2.66F)
                        .build("giant_boar"));
        public static final RegistryObject<EntityType<? extends Lampad>> LAMPAD = ENTITY_TYPES.register("lampad", () ->
                EntityType.Builder.of(Lampad::new, MobCategory.CREATURE)
                        .sized(0.48F, 1.8F).fireImmune()
                        .build("lampad"));
        public static final RegistryObject<EntityType<? extends MadCow>> MAD_COW = ENTITY_TYPES.register("mad_cow", () ->
                EntityType.Builder.of(MadCow::new, MobCategory.MONSTER)
                        .sized(0.9F, 1.4F)
                        .build("mad_cow"));
        public static final RegistryObject<EntityType<? extends Makhai>> MAKHAI = ENTITY_TYPES.register("makhai", () ->
                EntityType.Builder.of(Makhai::new, MobCategory.CREATURE)
                        .sized(0.70F, 1.8F)
                        .build("makhai"));
        public static final RegistryObject<EntityType<? extends Minotaur>> MINOTAUR = ENTITY_TYPES.register("minotaur", () ->
                EntityType.Builder.of(Minotaur::new, MobCategory.MONSTER)
                        .sized(0.7F, 1.94F)
                        .build("minotaur"));
        public static final RegistryObject<EntityType<? extends Naiad>> NAIAD = ENTITY_TYPES.register("naiad", () ->
                EntityType.Builder.of(Naiad::new, MobCategory.WATER_CREATURE)
                        .sized(0.48F, 1.8F)
                        .build("naiad"));
        public static final RegistryObject<EntityType<? extends NemeanLion>> NEMEAN_LION = ENTITY_TYPES.register("nemean_lion", () ->
                EntityType.Builder.of(NemeanLion::new, MobCategory.MONSTER)
                        .sized(1.92F, 2.28F).fireImmune()
                        .build("nemean_lion"));
        public static final RegistryObject<EntityType<? extends Orthus>> ORTHUS = ENTITY_TYPES.register("orthus", () ->
                EntityType.Builder.of(Orthus::new, MobCategory.MONSTER)
                        .sized(0.6F, 0.85F).fireImmune()
                        .build("orthus"));
        public static final RegistryObject<EntityType<? extends Pegasus>> PEGASUS = ENTITY_TYPES.register("pegasus", () ->
                EntityType.Builder.of(Pegasus::new, MobCategory.CREATURE)
                        .sized(1.39F, 1.98F)
                        .build("pegasus"));
        public static final RegistryObject<EntityType<? extends Python>> PYTHON = ENTITY_TYPES.register("python", () ->
                EntityType.Builder.of(Python::new, MobCategory.MONSTER)
                        .sized(1.4F, 1.9F).fireImmune()
                        .build("python"));
        public static final RegistryObject<EntityType<? extends Satyr>> SATYR = ENTITY_TYPES.register("satyr", () ->
                EntityType.Builder.of(Satyr::new, MobCategory.CREATURE)
                        .sized(0.67F, 1.8F)
                        .build("satyr"));
        public static final RegistryObject<EntityType<? extends Scylla>> SCYLLA = ENTITY_TYPES.register("scylla", () ->
                EntityType.Builder.of(Scylla::new, MobCategory.WATER_CREATURE)
                        .sized(1.92F, 4.4F).fireImmune()
                        .build("scylla"));
        public static final RegistryObject<EntityType<? extends Shade>> SHADE = ENTITY_TYPES.register("shade", () ->
                EntityType.Builder.of(Shade::new, MobCategory.MONSTER)
                        .sized(0.67F, 1.8F).fireImmune()
                        .build("shade"));
        public static final RegistryObject<EntityType<? extends Siren>> SIREN = ENTITY_TYPES.register("siren", () ->
                EntityType.Builder.of(Siren::new, MobCategory.WATER_CREATURE)
                        .sized(0.6F, 1.9F)
                        .build("siren"));
        public static final RegistryObject<EntityType<? extends Sparti>> SPARTI = ENTITY_TYPES.register("sparti", () ->
                EntityType.Builder.of(Sparti::new, MobCategory.CREATURE)
                        .sized(0.6F, 1.98F)
                        .build("sparti"));
        public static final RegistryObject<EntityType<? extends Stymphalian>> STYMPHALIAN = ENTITY_TYPES.register("stymphalian", () ->
                EntityType.Builder.of(Stymphalian::new, MobCategory.MONSTER)
                        .sized(0.7F, 0.7F)
                        .build("stymphalian"));
        public static final RegistryObject<EntityType<? extends Talos>> TALOS = ENTITY_TYPES.register("talos", () ->
                EntityType.Builder.of(Talos::new, MobCategory.MONSTER)
                        .sized(1.98F, 4.96F).fireImmune()
                        .build("talos"));
        public static final RegistryObject<EntityType<? extends Triton>> TRITON = ENTITY_TYPES.register("triton", () ->
                EntityType.Builder.of(Triton::new, MobCategory.WATER_CREATURE)
                        .sized(0.6F, 1.9F)
                        .build("triton"));
        public static final RegistryObject<EntityType<? extends Unicorn>> UNICORN = ENTITY_TYPES.register("unicorn", () ->
                EntityType.Builder.of(Unicorn::new, MobCategory.CREATURE)
                        .sized(1.39F, 1.98F)
                        .build("unicorn"));
        public static final RegistryObject<EntityType<? extends Whirl>> WHIRL = ENTITY_TYPES.register("whirl", () ->
                EntityType.Builder.of(Whirl::new, MobCategory.WATER_CREATURE)
                        .sized(2.9F, 5.0F)
                        .build("whirl"));
        // other
        public static final RegistryObject<EntityType<? extends BronzeFeather>> BRONZE_FEATHER = ENTITY_TYPES.register("bronze_feather", () ->
                EntityType.Builder.<BronzeFeather>of(BronzeFeather::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("bronze_feather"));
        public static final RegistryObject<EntityType<? extends Curse>> CURSE = ENTITY_TYPES.register("curse", () ->
                EntityType.Builder.<Curse>of(Curse::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("curse"));
        public static final RegistryObject<EntityType<? extends CurseOfCirce>> CURSE_OF_CIRCE = ENTITY_TYPES.register("curse_of_circe", () ->
                EntityType.Builder.<CurseOfCirce>of(CurseOfCirce::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("curse_of_circe"));
        public static final RegistryObject<EntityType<? extends Discus>> DISCUS = ENTITY_TYPES.register("discus", () ->
                EntityType.Builder.<Discus>of(Discus::new, MobCategory.MISC)
                        .sized(0.45F, 0.45F).noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("discus"));
        public static final RegistryObject<EntityType<? extends DragonTooth>> DRAGON_TOOTH = ENTITY_TYPES.register("dragon_tooth", () ->
                EntityType.Builder.<DragonTooth>of(DragonTooth::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("dragon_tooth"));
        public static final RegistryObject<EntityType<? extends GreekFire>> GREEK_FIRE = ENTITY_TYPES.register("greek_fire", () ->
                EntityType.Builder.<GreekFire>of(GreekFire::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("greek_fire"));
        public static final RegistryObject<EntityType<? extends HealingSpell>> HEALING_SPELL = ENTITY_TYPES.register("healing_spell", () ->
                EntityType.Builder.<HealingSpell>of(HealingSpell::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("healing_spell"));
        public static final RegistryObject<EntityType<? extends Palladium>> PALLADIUM = ENTITY_TYPES.register("palladium", () ->
                EntityType.Builder.of(Palladium::new, MobCategory.MISC)
                        .sized(0.98F, 2.24F).fireImmune()
                        .build("palladium"));
        public static final RegistryObject<EntityType<? extends PoisonSpit>> POISON_SPIT = ENTITY_TYPES.register("poison_spit", () ->
                EntityType.Builder.<PoisonSpit>of(PoisonSpit::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("poison_spit"));
        public static final RegistryObject<EntityType<? extends Spear>> SPEAR = ENTITY_TYPES.register("spear", () ->
                EntityType.Builder.<Spear>of(Spear::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F).noSummon().clientTrackingRange(4).updateInterval(20)
                        .build("spear"));
        public static final RegistryObject<EntityType<? extends ThrowingAxe>> THROWING_AXE = ENTITY_TYPES.register("throwing_axe", () ->
                EntityType.Builder.<ThrowingAxe>of(ThrowingAxe::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F).noSummon().clientTrackingRange(4).updateInterval(20)
                        .build("throwing_axe"));
        public static final RegistryObject<EntityType<? extends WaterSpell>> WATER_SPELL = ENTITY_TYPES.register("water_spell", () ->
                EntityType.Builder.<WaterSpell>of(WaterSpell::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("water_spell"));
        public static final RegistryObject<EntityType<? extends WebBall>> WEB_BALL = ENTITY_TYPES.register("web_ball", () ->
                EntityType.Builder.<WebBall>of(WebBall::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F).fireImmune().noSummon().clientTrackingRange(4).updateInterval(10)
                        .build("web_ball"));
    }

    public static final class BlockEntityReg {

        public static void register() {
            BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<BlockEntityType<MobHeadBlockEntity>> CERBERUS_HEAD = BLOCK_ENTITY_TYPES.register("cerberus_head", () ->
                BlockEntityType.Builder.of((pos, state) ->
                        new MobHeadBlockEntity(BlockEntityReg.CERBERUS_HEAD.get(), pos, state),
                    BlockReg.CERBERUS_HEAD.get())
                .build(null)
        );

        public static final RegistryObject<BlockEntityType<MobHeadBlockEntity>> GIGANTE_HEAD = BLOCK_ENTITY_TYPES.register("gigante_head", () ->
                BlockEntityType.Builder.of((pos, state) ->
                        new MobHeadBlockEntity(BlockEntityReg.GIGANTE_HEAD.get(), pos, state),
                    BlockReg.GIGANTE_HEAD.get())
                .build(null)
        );

        public static final RegistryObject<BlockEntityType<MobHeadBlockEntity>> ORTHUS_HEAD = BLOCK_ENTITY_TYPES.register("orthus_head", () ->
                BlockEntityType.Builder.of((pos, state) ->
                            new MobHeadBlockEntity(BlockEntityReg.ORTHUS_HEAD.get(), pos, state),
                    BlockReg.ORTHUS_HEAD.get())
                .build(null)
        );

        public static final RegistryObject<BlockEntityType<VaseBlockEntity>> VASE = BLOCK_ENTITY_TYPES.register("vase", () -> {
            // create set of vase blocks using registry objects
            Set<Block> vaseBlocks = new HashSet<>();
            vaseBlocks.add(RegistryObject.create(new ResourceLocation(MODID, "terracotta_vase"), ForgeRegistries.BLOCKS).get());
            for (final DyeColor dyeColor : DyeColor.values()) {
                vaseBlocks.add(RegistryObject.create(new ResourceLocation(MODID, dyeColor.getSerializedName() + "_terracotta_vase"), ForgeRegistries.BLOCKS).get());
            }
            // create block entity type
            return BlockEntityType.Builder.of(VaseBlockEntity::new, vaseBlocks.toArray(new Block[0]))
                    .build(null);
        });
    }


    public static final class PotionReg {

        public static void register() {
            POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
            // add setup listener
            FMLJavaModLoadingContext.get().getModEventBus().addListener(PotionReg::registerPotionRecipes);
        }

        public static final RegistryObject<Potion> MIRRORING = POTIONS.register("mirroring",
                () -> new Potion(new MobEffectInstance(MobEffectReg.MIRRORING.get(), 3600)));
        public static final RegistryObject<Potion> LONG_MIRRORING = POTIONS.register("long_mirroring",
                () -> new Potion(new MobEffectInstance(MobEffectReg.MIRRORING.get(), 9600)));

        public static final RegistryObject<Potion> CURSE_OF_CIRCE = POTIONS.register("curse_of_circe",
                () -> new Potion(new MobEffectInstance(MobEffectReg.CURSE_OF_CIRCE.get(), 3600)));
        public static final RegistryObject<Potion> LONG_CURSE_OF_CIRCE = POTIONS.register("long_curse_of_circe",
                () -> new Potion(new MobEffectInstance(MobEffectReg.CURSE_OF_CIRCE.get(), 9600)));

        public static final RegistryObject<Potion> SLOW_SWIM = POTIONS.register("slow_swim",
                () -> new Potion(new MobEffectInstance(MobEffectReg.SLOW_SWIM.get(), 3600)));
        public static final RegistryObject<Potion> LONG_SLOW_SWIM = POTIONS.register("long_slow_swim",
                () -> new Potion(new MobEffectInstance(MobEffectReg.SLOW_SWIM.get(), 9600)));


        public static void registerPotionRecipes(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                final ItemStack awkward = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);
                // Mirroring potion
                if (GreekFantasy.CONFIG.isMirroringEffectEnabled()) {
                    // create item stacks
                    final ItemStack mirroring = PotionUtils.setPotion(new ItemStack(Items.POTION), MIRRORING.get());
                    final ItemStack splashMirroring = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), MIRRORING.get());
                    final ItemStack lingeringMirroring = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), MIRRORING.get());
                    // Add brewing recipes
                    BrewingRecipeRegistry.addRecipe(
                            Ingredient.of(awkward),
                            Ingredient.of(new ItemStack(ItemReg.SNAKESKIN.get())), mirroring);
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(mirroring), Ingredient.of(new ItemStack(Items.REDSTONE)),
                            PotionUtils.setPotion(new ItemStack(Items.POTION), LONG_MIRRORING.get()));
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(mirroring), Ingredient.of(new ItemStack(Items.GUNPOWDER)), splashMirroring);
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(mirroring), Ingredient.of(new ItemStack(Items.DRAGON_BREATH)), lingeringMirroring);
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(splashMirroring), Ingredient.of(new ItemStack(Items.REDSTONE)),
                            PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), LONG_MIRRORING.get()));
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(lingeringMirroring), Ingredient.of(new ItemStack(Items.REDSTONE)),
                            PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), LONG_MIRRORING.get()));
                }
                // Curse of Circe potion
                if (GreekFantasy.CONFIG.isCurseOfCirceEnabled()) {
                    // create item stacks
                    final ItemStack curseOfCirce = PotionUtils.setPotion(new ItemStack(Items.POTION), CURSE_OF_CIRCE.get());
                    final ItemStack splashCurseOfCirce = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), CURSE_OF_CIRCE.get());
                    final ItemStack lingeringCurseOfCirce = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), CURSE_OF_CIRCE.get());
                    // Add brewing recipes
                    BrewingRecipeRegistry.addRecipe(
                            Ingredient.of(awkward),
                            Ingredient.of(new ItemStack(ItemReg.BOAR_EAR.get())), curseOfCirce);
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(curseOfCirce), Ingredient.of(new ItemStack(Items.REDSTONE)),
                            PotionUtils.setPotion(new ItemStack(Items.POTION), LONG_CURSE_OF_CIRCE.get()));
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(curseOfCirce), Ingredient.of(new ItemStack(Items.GUNPOWDER)), splashCurseOfCirce);
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(curseOfCirce), Ingredient.of(new ItemStack(Items.DRAGON_BREATH)), lingeringCurseOfCirce);
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(splashCurseOfCirce), Ingredient.of(new ItemStack(Items.REDSTONE)),
                            PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), LONG_CURSE_OF_CIRCE.get()));
                    BrewingRecipeRegistry.addRecipe(Ingredient.of(lingeringCurseOfCirce), Ingredient.of(new ItemStack(Items.REDSTONE)),
                            PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), LONG_CURSE_OF_CIRCE.get()));
                }
                // Slow Swim potion
                // create item stacks
                final ItemStack slowSwim = PotionUtils.setPotion(new ItemStack(Items.POTION), SLOW_SWIM.get());
                final ItemStack splashSlowSwim = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), SLOW_SWIM.get());
                final ItemStack lingeringSlowSwim = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), SLOW_SWIM.get());
                // Add brewing recipes
                BrewingRecipeRegistry.addRecipe(
                        Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.TURTLE_MASTER)),
                        Ingredient.of(new ItemStack(Items.FERMENTED_SPIDER_EYE)), slowSwim);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(slowSwim), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.POTION), LONG_SLOW_SWIM.get()));
                BrewingRecipeRegistry.addRecipe(Ingredient.of(slowSwim), Ingredient.of(new ItemStack(Items.GUNPOWDER)), splashSlowSwim);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(slowSwim), Ingredient.of(new ItemStack(Items.DRAGON_BREATH)), lingeringSlowSwim);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(splashSlowSwim), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), LONG_SLOW_SWIM.get()));
                BrewingRecipeRegistry.addRecipe(Ingredient.of(lingeringSlowSwim), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), LONG_SLOW_SWIM.get()));
            });
        }

    }

    public static final class MobEffectReg {

        public static void register() {
            MOB_EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<MobEffect> CURSE_OF_CIRCE = MOB_EFFECTS.register("curse_of_circe", () -> new CurseOfCirceEffect());
        public static final RegistryObject<MobEffect> MIRRORING = MOB_EFFECTS.register("mirroring", () -> new MirroringEffect());
        public static final RegistryObject<MobEffect> PETRIFIED = MOB_EFFECTS.register("petrified", () -> new StunnedEffect());
        public static final RegistryObject<MobEffect> PRISONER_OF_HADES = MOB_EFFECTS.register("prisoner_of_hades", () -> new PrisonerOfHadesEffect());
        public static final RegistryObject<MobEffect> STUNNED = MOB_EFFECTS.register("stunned", () -> new StunnedEffect());
        public static final RegistryObject<MobEffect> SLOW_SWIM = MOB_EFFECTS.register("slow_swim", () -> new SlowSwimEffect());

    }


    public static final class EnchantmentReg {

        public static void register() {
            ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Enchantment> BANE_OF_SERPENTS = ENCHANTMENTS.register("bane_of_serpents", () ->
                new BaneOfSerpentsEnchantment(Enchantment.Rarity.UNCOMMON));
        public static final RegistryObject<Enchantment> DAYBREAK = ENCHANTMENTS.register("daybreak", () ->
                new DeityEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND, 1, i -> i.is(Items.CLOCK)));
        public static final RegistryObject<Enchantment> FLYING = ENCHANTMENTS.register("flying", () ->
                new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_FEET, EquipmentSlot.FEET, 1, i -> i.is(ItemReg.WINGED_SANDALS.get())));
        public static final RegistryObject<Enchantment> FIREFLASH = ENCHANTMENTS.register("fireflash", () ->
                new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, 1, i -> i.is(ItemReg.THUNDERBOLT.get())));
        public static final RegistryObject<Enchantment> HUNTING = ENCHANTMENTS.register("hunting", () ->
                new HuntingEnchantment(Enchantment.Rarity.COMMON));
        public static final RegistryObject<Enchantment> LORD_OF_THE_SEA = ENCHANTMENTS.register("lord_of_the_sea", () ->
                new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.TRIDENT, EquipmentSlot.MAINHAND, 1, i -> i.is(Items.TRIDENT)));
        public static final RegistryObject<Enchantment> MIRRORING = ENCHANTMENTS.register("mirroring", () ->
                new MirroringEnchantment(Enchantment.Rarity.UNCOMMON));
        public static final RegistryObject<Enchantment> OVERSTEP = ENCHANTMENTS.register("overstep", () ->
                new OverstepEnchantment(Enchantment.Rarity.UNCOMMON));
        public static final RegistryObject<Enchantment> POISONING = ENCHANTMENTS.register("poisoning", () ->
                new PoisoningEnchantment(Enchantment.Rarity.VERY_RARE));
        public static final RegistryObject<Enchantment> RAISING = ENCHANTMENTS.register("raising", () ->
                new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, 1, i -> i.is(ItemReg.BIDENT.get())));
        public static final RegistryObject<Enchantment> SILKSTEP = ENCHANTMENTS.register("silkstep", () ->
                new SilkstepEnchantment(Enchantment.Rarity.RARE));
        public static final RegistryObject<Enchantment> SMASHING = ENCHANTMENTS.register("smashing", () ->
                new SmashingEnchantment(Enchantment.Rarity.VERY_RARE));


    }

    public static final class LootModifierReg {

        public static void register() {
            LOOT_MODIFIER_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Codec<ReplaceDropsLootModifier>> REPLACE_DROPS_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register(
                "replace_drops", ReplaceDropsLootModifier.CODEC_SUPPLIER);
        public static final RegistryObject<Codec<BronzeScrapLootModifier>> BRONZE_SCRAP_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register(
                "bronze_scrap", BronzeScrapLootModifier.CODEC_SUPPLIER);
        public static final RegistryObject<Codec<QuestLootModifier>> QUEST_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register(
                "quest", QuestLootModifier.CODEC_SUPPLIER);

    }

    public static final class RecipeReg {

        public static void register() {
            RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<RecipeSerializer> OLIVE_SALVE = RECIPE_SERIALIZERS.register(SalveRecipe.Serializer.CATEGORY, () ->
                new SalveRecipe.Serializer());
    }

    public static final class ParticleReg {

        public static void register() {
            PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<SimpleParticleType> GORGON = PARTICLE_TYPES.register("gorgon", () ->
                new SimpleParticleType(true));
    }

    public static final class StructureProcessorReg {

        public static void register() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(StructureProcessorReg::registerStructureProcessors);
        }

        public static StructureProcessorType<LocStructureProcessor> LOC_PROCESSOR;
        public static StructureProcessorType<CentaurStructureProcessor> CENTAUR_PROCESSOR;
        public static StructureProcessorType<SatyrStructureProcessor> SATYR_PROCESSOR;
        public static StructureProcessorType<OceanVillageStructureProcessor> OCEAN_VILLAGE_PROCESSOR;

        private static void registerStructureProcessors(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                // register loc processor
                ResourceLocation locProcessorId = new ResourceLocation(GreekFantasy.MODID, "loc");
                LOC_PROCESSOR = StructureProcessorType.register(locProcessorId.toString(), LocStructureProcessor.CODEC);
                // register satyr processor
                ResourceLocation satyrProcessorId = new ResourceLocation(GreekFantasy.MODID, "satyr");
                SATYR_PROCESSOR = StructureProcessorType.register(satyrProcessorId.toString(), SatyrStructureProcessor.CODEC);
                // register centaur processor
                ResourceLocation centaurProcessorId = new ResourceLocation(GreekFantasy.MODID, "centaur");
                CENTAUR_PROCESSOR = StructureProcessorType.register(centaurProcessorId.toString(), CentaurStructureProcessor.CODEC);
                // register ocean village processor
                ResourceLocation oceanVillageProcessorId = new ResourceLocation(GreekFantasy.MODID, "ocean_village");
                OCEAN_VILLAGE_PROCESSOR = StructureProcessorType.register(oceanVillageProcessorId.toString(), OceanVillageStructureProcessor.CODEC);

            });
        }
    }

    public static final class StructureReg {

        public static void register() {
            STRUCTURE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
            STRUCTURE_PIECE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<StructureType<MazeStructure>> MAZE = STRUCTURE_TYPES.register("maze", () ->
                () -> MazeStructure.CODEC);
        public static final RegistryObject<StructurePieceType> MAZE_ROOM = STRUCTURE_PIECE_TYPES.register("maze", () ->
                (config, tag) -> new MazePiece(tag));
        public static final RegistryObject<StructureType<OceanVillageStructure>> OCEAN_VILLAGE = STRUCTURE_TYPES.register("ocean_village", () ->
                () -> OceanVillageStructure.CODEC);
    }

    public static final class FeatureReg {

        public static void register() {
            FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static RegistryObject<OliveTreeFeature> OLIVE_TREE_FEATURE = FEATURES.register("olive_tree", () ->
                new OliveTreeFeature(TreeConfiguration.CODEC));
        public static RegistryObject<HarpyNestFeature> HARPY_NEST_FEATURE = FEATURES.register("harpy_nest", () ->
                new HarpyNestFeature(TreeConfiguration.CODEC));
    }

    public static final class PlacementTypeReg {

        public static void register() {
            PLACEMENT_MODIFIER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<PlacementModifierType<DimensionFilter>> DIMENSION_FILTER = PLACEMENT_MODIFIER_TYPES.register("dimension", () -> () -> DimensionFilter.CODEC);
    }

    public static final class StructureModifierReg {

        public static void register() {
            STRUCTURE_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Codec<AddSpawnsStructureModifier>> ADD_SPAWNS_MODIFIER = STRUCTURE_MODIFIERS.register("add_spawn", () -> AddSpawnsStructureModifier.CODEC);
    }
}
