package greekfantasy;

import greekfantasy.block.MysteriousBoxBlock;
import greekfantasy.block.OilLampBlock;
import greekfantasy.block.PillarBlock;
import greekfantasy.block.VaseBlock;
import greekfantasy.block.WildRoseBlock;
import greekfantasy.blockentity.VaseBlockEntity;
import greekfantasy.enchantment.DeityEnchantment;
import greekfantasy.enchantment.HuntingEnchantment;
import greekfantasy.enchantment.MirroringEnchantment;
import greekfantasy.enchantment.OverstepEnchantment;
import greekfantasy.enchantment.PoisoningEnchantment;
import greekfantasy.enchantment.SilkstepEnchantment;
import greekfantasy.enchantment.SmashingEnchantment;
import greekfantasy.entity.misc.SpearEntity;
import greekfantasy.entity.monster.DrakainaEntity;
import greekfantasy.item.BidentItem;
import greekfantasy.item.ClubItem;
import greekfantasy.item.GFArmorMaterials;
import greekfantasy.item.GFTiers;
import greekfantasy.item.KnifeItem;
import greekfantasy.item.SnakeskinArmorItem;
import greekfantasy.item.SpearItem;
import greekfantasy.mob_effect.CurseOfCirceEffect;
import greekfantasy.mob_effect.MirroringEffect;
import greekfantasy.mob_effect.PrisonerOfHadesEffect;
import greekfantasy.mob_effect.StunnedEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public final class GFRegistry {

    private static final String MODID = GreekFantasy.MODID;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static void register() {
        BlockReg.register();
        ItemReg.register();
        PotionReg.register();
        MobEffectReg.register();
        EnchantmentReg.register();
        EntityReg.register();
        BlockEntityReg.register();
        MenuReg.register();
        RecipeReg.register();
    }


    public static final class BlockReg {

        public static void register() {
            BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
            // register marble and limestone
            registerBlockPolishedEtc("marble", Block.Properties.of(Material.STONE, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
            registerBlockPolishedEtc("limestone", Block.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
            // register olive and pomegranate blocks
            registerLogsPlanksEtc("olive", 2.0F, 3.0F, MaterialColor.WOOD, MaterialColor.SAND, 5, 5, 20);
            registerLogsPlanksEtc("pomegranate", 2.2F, 3.0F, MaterialColor.TERRACOTTA_PURPLE, MaterialColor.CRIMSON_STEM, 0, 0, 0);
            // register leaves
            registerLeaves("olive", 30, 60);
            registerLeaves("pomegranate", 0, 0);
            registerLeaves("golden", 30, 60);
            // register terracotta vases
            for(DyeColor dyeColor : DyeColor.values()) {
                RegistryObject<Block> VASE = BLOCKS.register(dyeColor.getSerializedName() + "_terracotta_vase", () ->
                        new VaseBlock(BlockBehaviour.Properties.of(Material.STONE, dyeColor.getMaterialColor())
                                .strength(0.5F, 1.0F).noOcclusion()));
                GFRegistry.ItemReg.registerItemBlock(VASE);
            }
        }

        public static final RegistryObject<Block> BRONZE_BLOCK = BLOCKS.register("bronze_block", () ->
                new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                        .requiresCorrectToolForDrops().strength(3.0F, 6.0F)
                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> ICHOR_INFUSED_BLOCK = BLOCKS.register("ichor_infused_block", () ->
                new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD)
                        .requiresCorrectToolForDrops().strength(3.0F, 6.0F)
                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> MYSTERIOUS_BOX = BLOCKS.register("mysterious_box", () ->
                new MysteriousBoxBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(0.8F, 3.0F).sound(SoundType.WOOD).noOcclusion()));
        public static final RegistryObject<Block> GIGANTE_HEAD = BLOCKS.register("gigante_head", () ->
                new Block(BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion()));
        public static final RegistryObject<Block> ORTHUS_HEAD = BLOCKS.register("orthus_head", () ->
                new Block(BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion()));
        public static final RegistryObject<Block> CERBERUS_HEAD = BLOCKS.register("cerberus_head", () ->
                new Block(BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion()));
        public static final RegistryObject<Block> OIL_LAMP = BLOCKS.register("oil_lamp", () ->
                new OilLampBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                        .noOcclusion().lightLevel(b -> b.getValue(OilLampBlock.LIT) ? 11 : 0).strength(0.2F, 0.1F)));
        public static final RegistryObject<Block> OIL = BLOCKS.register("olive_oil", () ->
                new Block(BlockBehaviour.Properties.of(Material.FIRE)
                        .noOcclusion().noCollission().instabreak()
                        .randomTicks().lightLevel((state) -> 11).sound(SoundType.WET_GRASS)));
        public static final RegistryObject<Block> GOLDEN_STRING = BLOCKS.register("golden_string", () ->
                new Block(BlockBehaviour.Properties.of(Material.DECORATION)
                        .lightLevel(b -> 8).instabreak().noCollission().noOcclusion()));
        // TODO sapling trees
        public static final RegistryObject<Block> OLIVE_SAPLING = BLOCKS.register("olive_sapling", () ->
                new SaplingBlock(new OakTreeGrower(), BlockBehaviour.Properties.of(Material.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> POMEGRANATE_SAPLING = BLOCKS.register("pomegranate_sapling", () ->
                new SaplingBlock(new OakTreeGrower(), BlockBehaviour.Properties.of(Material.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> GOLDEN_SAPLING = BLOCKS.register("golden_sapling", () ->
                new SaplingBlock(new OakTreeGrower(), BlockBehaviour.Properties.of(Material.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> NEST = BLOCKS.register("nest", () ->
                new Block(Block.Properties.of(Material.GRASS, MaterialColor.COLOR_BROWN)
                        .strength(0.5F).sound(SoundType.GRASS)
                        .hasPostProcess((s, r, p) -> true).noOcclusion()));
        public static final RegistryObject<Block> WILD_ROSE = BLOCKS.register("wild_rose", () ->
                new WildRoseBlock(MobEffects.SATURATION, 9, Block.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
        public static final RegistryObject<Block> REEDS = BLOCKS.register("reeds", () ->
                new Block(Block.Properties.of(Material.WATER_PLANT).noCollission().instabreak().randomTicks().sound(SoundType.CROP)));



        /**
         * Registers all of the following: log, stripped log, wood, stripped wood, planks, stairs, slab, door, trapdoor
         * @param registryName the base registry name
         * @param strength the destroy time
         * @param hardness the explosion resistance
         * @param side the material color of the side
         * @param top the material color of the top
         * @param fireSpread the fire spread chance. The higher the number returned, the faster fire will spread around this block.
         * @param logFlammability Chance that fire will spread and consume the log. 300 being a 100% chance, 0, being a 0% chance.
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
                    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                    @Override
                    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return logFlammability; }
                }
            );
            final RegistryObject<Block> strippedWood = BLOCKS.register("stripped_" + registryName + "_wood", () ->
                new RotatedPillarBlock(woodProperties) {
                    @Override
                    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                    @Override
                    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return logFlammability; }
                }
            );
            final RegistryObject<Block> log = BLOCKS.register(registryName + "_log", () ->
                new RotatedPillarBlock(logProperties) {
                    @Override
                    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                    @Override
                    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return logFlammability; }
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
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return logFlammability; }
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
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return planksFlammability; }
                    }
            );
            final RegistryObject<Block> slab = BLOCKS.register(registryName + "_slab", () ->
                    new SlabBlock(woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return planksFlammability; }
                    }
            );
            final RegistryObject<Block> stairs = BLOCKS.register(registryName + "_stairs", () ->
                    new StairBlock(() -> planks.get().defaultBlockState(), woodProperties) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                        @Override
                        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return planksFlammability; }
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
         * @param registryName the base registry name
         * @param fireSpread the fire spread chance. The higher the number returned, the faster fire will spread around this block.
         * @param flammability Chance that fire will spread and consume the block. 300 being a 100% chance, 0, being a 0% chance.
         */
        private static void registerLeaves(final String registryName, final int fireSpread, final int flammability) {
            final RegistryObject<Block> leaves = BLOCKS.register(registryName + "_leaves", () ->
                new LeavesBlock(Block.Properties
                        .of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS)
                        .noOcclusion().isValidSpawn(GFRegistry.BlockReg::allowsSpawnOnLeaves).isSuffocating((s, r, p) -> false)
                        .isViewBlocking((s, r, p) -> false)) {
                    @Override
                    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return fireSpread; }
                    @Override
                    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return flammability; }
                }
            );
            // block items
            GFRegistry.ItemReg.registerItemBlock(leaves);
        }

        /**
         * Registers the following: block, slab, stairs, pillar, polished block, polished slab, polished stairs
         * @param registryName the base registry name.
         * @param properties the block properties
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
         * @param registryName the base registry name
         * @param properties the block properties
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

        public static void register() {
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        //// LEGENDARY WEAPONS ////
        public static final RegistryObject<Item> THUNDERBOLT = ITEMS.register("thunderbolt", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).stacksTo(1)));
        public static final RegistryObject<Item> WAND_OF_CIRCE = ITEMS.register("wand_of_circe", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1)));
        public static final RegistryObject<Item> AVERNAL_BOW = ITEMS.register("avernal_bow", () ->
                new BowItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).stacksTo(1)));
        public static final RegistryObject<Item> APOLLO_BOW = ITEMS.register("apollo_bow", () ->
                new BowItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC).stacksTo(1)));
        public static final RegistryObject<Item> ARTEMIS_BOW = ITEMS.register("artemis_bow", () ->
                new BowItem(new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC).stacksTo(1)));

        //// WEAPONS ////
        public static final RegistryObject<Item> WOODEN_CLUB = ITEMS.register("wooden_club", () ->
                new ClubItem(Tiers.WOOD, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> STONE_CLUB = ITEMS.register("stone_club", () ->
                new ClubItem(Tiers.STONE, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> IRON_CLUB = ITEMS.register("iron_club", () ->
                new ClubItem(Tiers.IRON, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        // TODO custom tier for bident? for repair material
        public static final RegistryObject<Item> BIDENT = ITEMS.register("bident", () ->
                new BidentItem(Tiers.DIAMOND, new Item.Properties().rarity(Rarity.UNCOMMON).tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> WOODEN_SPEAR = ITEMS.register("wooden_spear", () ->
                new SpearItem(Tiers.WOOD, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> FLINT_SPEAR = ITEMS.register("flint_spear", () ->
                new SpearItem(GFTiers.FLINT, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> STONE_SPEAR = ITEMS.register("stone_spear", () ->
                new SpearItem(Tiers.STONE, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> IRON_SPEAR = ITEMS.register("iron_spear", () ->
                new SpearItem(Tiers.IRON, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> GOLDEN_SPEAR = ITEMS.register("golden_spear", () ->
                new SpearItem(Tiers.GOLD, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> DIAMOND_SPEAR = ITEMS.register("diamond_spear", () ->
                new SpearItem(Tiers.DIAMOND, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> NETHERITE_SPEAR = ITEMS.register("netherite_spear", () ->
                new SpearItem(Tiers.NETHERITE, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> FLINT_KNIFE = ITEMS.register("flint_knife", () ->
                new KnifeItem(GFTiers.FLINT, 3, -1.7F, -1.0F, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> IVORY_SWORD = ITEMS.register("ivory_sword", () ->
                new SwordItem(GFTiers.IVORY, 3, -2.2F, new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> DISCUS = ITEMS.register("discus", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(16))); // TODO ability
        public static final RegistryObject<Item> GREEK_FIRE = ITEMS.register("greek_fire", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(16))); // TODO ability
        public static final RegistryObject<Item> WEB_BALL = ITEMS.register("web_ball", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(16))); // TODO ability

        //// LEGENDARY TOOLS AND ITEMS ////
        public static final RegistryObject<Item> DRAGON_TOOTH = ITEMS.register("dragon_tooth", () ->
                new Item(new Item.Properties().tab(GF_TAB).durability(24).rarity(Rarity.RARE))); // TODO ability
        public static final RegistryObject<Item> MIRROR = ITEMS.register("mirror", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> CONCH = ITEMS.register("conch", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1))); // TODO ability
        public static final RegistryObject<Item> UNICORN_HORN = ITEMS.register("unicorn_horn", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).stacksTo(1))); // TODO ability
        public static final RegistryObject<Item> HEART_OF_TALOS = ITEMS.register("heart_of_talos", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(16)));
        public static final RegistryObject<Item> BAG_OF_WIND = ITEMS.register("bag_of_wind", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1))); // TODO ability
        public static final RegistryObject<Item> STAFF_OF_HEALING = ITEMS.register("staff_of_healing", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1))); // TODO ability
        public static final RegistryObject<Item> AMBROSIA = ITEMS.register("ambrosia", () ->
                new Item(new Item.Properties().tab(GF_TAB).food(AMBROSIA_FOOD).rarity(Rarity.EPIC)));
        public static final RegistryObject<Item> HORN_OF_PLENTY = ITEMS.register("horn_of_plenty", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON))); // TODO ability
        public static final RegistryObject<Item> GOLDEN_FLEECE = ITEMS.register("golden_fleece", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GOLDEN_BALL = ITEMS.register("golden_ball", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> ICHOR = ITEMS.register("ichor", () ->
                new Item(new Item.Properties().tab(GF_TAB)) {
                    @Override
                    public boolean isFoil(ItemStack stack) { return true; }
                });
        private static final BannerPattern SPIDER_PATTERN = BannerPattern.create("greekfantasy_spider", "greekfantasy_spider", "greekfantasy_spider", true);
        public static final RegistryObject<Item> SPIDER_BANNER_PATTERN = ITEMS.register("spider_banner_pattern", () ->
                new BannerPatternItem(SPIDER_PATTERN, new Item.Properties().tab(GF_TAB).stacksTo(1).rarity(Rarity.RARE)));

        //// LEGENDARY ARMOR ////
        public static final RegistryObject<Item> HELM_OF_DARKNESS = ITEMS.register("helm_of_darkness", () ->
                new ArmorItem(GFArmorMaterials.AVERNAL, EquipmentSlot.HEAD,
                        new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC))); // TODO ability
        public static final RegistryObject<Item> WINGED_SANDALS = ITEMS.register("winged_sandals", () ->
                new ArmorItem(GFArmorMaterials.WINGED, EquipmentSlot.FEET,
                        new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC))); // TODO ability
        public static final RegistryObject<Item> NEMEAN_LION_HIDE = ITEMS.register("nemean_lion_hide", () ->
                new ArmorItem(GFArmorMaterials.NEMEAN, EquipmentSlot.HEAD,
                        new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).setNoRepair())); // TODO ability

        //// ARMOR ////
        public static final RegistryObject<Item> HELLENIC_HELMET = ITEMS.register("hellenic_helmet", () ->
                new ArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.HEAD, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> HELLENIC_CHESTPLATE = ITEMS.register("hellenic_chestplate", () ->
                new ArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.CHEST, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> HELLENIC_LEGGINGS = ITEMS.register("hellenic_leggings", () ->
                new ArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.LEGS, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> HELLENIC_BOOTS = ITEMS.register("hellenic_boots", () ->
                new ArmorItem(GFArmorMaterials.HELLENIC, EquipmentSlot.FEET, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_HELMET = ITEMS.register("snakeskin_helmet", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.HEAD, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_CHESTPLATE = ITEMS.register("snakeskin_chestplate", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.CHEST, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_LEGGINGS = ITEMS.register("snakeskin_leggings", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.LEGS, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> SNAKESKIN_BOOTS = ITEMS.register("snakeskin_boots", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.FEET, new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));

        //// MISC ITEMS ////
        // TODO instruments
        public static final RegistryObject<Item> PANFLUTE = ITEMS.register("panflute", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> LYRE = ITEMS.register("wooden_lyre", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> GOLDEN_LYRE = ITEMS.register("golden_lyre", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).stacksTo(1)));
        public static final RegistryObject<Item> OLIVES = ITEMS.register("olives", () ->
                new Item(new Item.Properties().tab(GF_TAB).food(OLIVES_FOOD)));
        public static final RegistryObject<Item> OLIVE_OIL = ITEMS.register("olive_oil", () ->
                new BlockItem(BlockReg.OIL.get(), new Item.Properties().tab(GF_TAB).stacksTo(16)));
        public static final RegistryObject<Item> OLIVE_SALVE = ITEMS.register("olive_salve", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1))); // TODO salve
        public static final RegistryObject<Item> POMEGRANATE = ITEMS.register("pomegranate", () ->
                new Item(new Item.Properties().tab(GF_TAB).food(POMEGRANATE_FOOD))); // TODO Prisoner effect

        //// CRAFTING MATERIALS ////
        public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
            public static final RegistryObject<Item> BRONZE_NUGGET = ITEMS.register("bronze_nugget", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> HORN = ITEMS.register("horn", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_FEATHER = ITEMS.register("avernal_feather", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_HAIR = ITEMS.register("avernal_hair", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_WING = ITEMS.register("avernal_wing", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_HIDE = ITEMS.register("avernal_hide", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_CLAW = ITEMS.register("avernal_claw", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> AVERNAL_SHARD = ITEMS.register("avernal_shard", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> ICHOR_INFUSED_GEAR = ITEMS.register("ichor_infused_gear", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GOLDEN_STRING = ITEMS.register("golden_string", () ->
                new BlockItem(BlockReg.GOLDEN_STRING.get(), new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GORGON_BLOOD = ITEMS.register("gorgon_blood", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE)));
        public static final RegistryObject<Item> BOAR_EAR = ITEMS.register("boar_ear", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> BOAR_TUSK = ITEMS.register("boar_tusk", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> GOLDEN_BRIDLE = ITEMS.register("golden_bridle", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> SNAKESKIN = ITEMS.register("snakeskin", () ->
                new Item(new Item.Properties().tab(GF_TAB)));
        public static final RegistryObject<Item> TOUGH_SNAKESKIN = ITEMS.register("tough_snakeskin", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON)));
        public static final RegistryObject<Item> DEADLY_FANG = ITEMS.register("deadly_fang", () ->
                new Item(new Item.Properties().tab(GF_TAB)));

        //// LEGENDARY ITEM BLOCKS ////
        public static final RegistryObject<Item> PALLADIUM = ITEMS.register("palladium", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1))); // TODO places palladium

        //// ITEM BLOCKS ////
        public static final RegistryObject<BlockItem> BRONZE_BLOCK = registerItemBlock(BlockReg.BRONZE_BLOCK);
        public static final RegistryObject<BlockItem> ICHOR_INFUSED_BLOCK = registerItemBlock(BlockReg.ICHOR_INFUSED_BLOCK);
        public static final RegistryObject<BlockItem> MYSTERIOUS_BOX = registerItemBlock(BlockReg.MYSTERIOUS_BOX);
        public static final RegistryObject<BlockItem> GIGANTE_HEAD = registerItemBlock(BlockReg.GIGANTE_HEAD);
        public static final RegistryObject<BlockItem> ORTHUS_HEAD = registerItemBlock(BlockReg.ORTHUS_HEAD);
        public static final RegistryObject<BlockItem> CERBERUS_HEAD = registerItemBlock(BlockReg.CERBERUS_HEAD);
        public static final RegistryObject<BlockItem> OIL_LAMP = registerItemBlock(BlockReg.OIL_LAMP);
        public static final RegistryObject<BlockItem> OLIVE_SAPLING = registerItemBlock(BlockReg.OLIVE_SAPLING);
        public static final RegistryObject<BlockItem> POMEGRANATE_SAPLING = registerItemBlock(BlockReg.POMEGRANATE_SAPLING);
        public static final RegistryObject<BlockItem> GOLDEN_SAPLING = registerItemBlock(BlockReg.GOLDEN_SAPLING);
        public static final RegistryObject<BlockItem> WILD_ROSE = registerItemBlock(BlockReg.WILD_ROSE);
        public static final RegistryObject<BlockItem> NEST = registerItemBlock(BlockReg.NEST);


        /**
         * Registers an item for the given block
         * @param blockSupplier the block supplier
         * @return the BlockItem registry object
         */
        private static RegistryObject<BlockItem> registerItemBlock(final RegistryObject<? extends Block> blockSupplier) {
            return ITEMS.register(blockSupplier.getId().getPath(), itemBlock(blockSupplier));
        }

        /**
         * Creates a block item supplier for the given block
         * @param blockSupplier the block supplier
         * @return a supplier for the block item
         */
        private static Supplier<BlockItem> itemBlock(final RegistryObject<? extends Block> blockSupplier) {
            return () -> new BlockItem(blockSupplier.get(), new Item.Properties().tab(GF_TAB));
        }
    }

    public static final class EntityReg {

        public static void register() {
            ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
            // event listeners
            FMLJavaModLoadingContext.get().getModEventBus().addListener(GFRegistry.EntityReg::registerEntityAttributes);
        }

        private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            event.put(DRAKAINA.get(), DrakainaEntity.createAttributes().build());
        }

        // creature
        public static final RegistryObject<EntityType<? extends DrakainaEntity>> DRAKAINA = ENTITY_TYPES.register("drakaina", () ->
                EntityType.Builder.of(DrakainaEntity::new, MobCategory.MONSTER)
                        .sized(0.9F, 1.9F).clientTrackingRange(8)
                        .build("drakaina"));
        // other
        public static final RegistryObject<EntityType<? extends SpearEntity>> SPEAR = ENTITY_TYPES.register("spear", () ->
                EntityType.Builder.<SpearEntity>of(SpearEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).noSummon().clientTrackingRange(4).updateInterval(20)
                    .build("spear"));
    }

    public static final class BlockEntityReg {

        public static void register() {
            BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<BlockEntityType<VaseBlockEntity>> VASE = BLOCK_ENTITY_TYPES.register("vase", () -> {
            // create set of vase blocks using registry objects
            Set<Block> vaseBlocks = new HashSet<>();
            for(final DyeColor dyeColor : DyeColor.values()) {
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


    }


    public static final class EnchantmentReg {

        public static void register() {
            ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

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

    public static final class RecipeReg {

        public static void register() {
            RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

    }

    public static final class MenuReg {

        public static void register() {
            MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

    }

}
