package greekfantasy;

import greekfantasy.entity.misc.SpearEntity;
import greekfantasy.item.BidentItem;
import greekfantasy.item.ClubItem;
import greekfantasy.item.GFArmorMaterials;
import greekfantasy.item.GFTiers;
import greekfantasy.item.KnifeItem;
import greekfantasy.item.SnakeskinArmorItem;
import greekfantasy.item.SpearItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class GFRegistry {

    private static final String MODID = GreekFantasy.MODID;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static void register() {
        BlockReg.register();
        ItemReg.register();
        PotionReg.register();
        MobEffectReg.register();
        EnchantmentReg.register();
        EntityReg.register();
        MenuReg.register();
        RecipeReg.register();
    }


    public static final class BlockReg {

        public static void register() {
            BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

    }

    public static final class ItemReg {

        public static final CreativeModeTab GF_TAB = new CreativeModeTab(GreekFantasy.MODID) {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(PANFLUTE.get());
            }
        };

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
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.EPIC))); // TODO food
        public static final RegistryObject<Item> HORN_OF_PLENTY = ITEMS.register("horn_of_plenty", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON))); // TODO ability
        public static final RegistryObject<Item> GOLDEN_FLEECE = ITEMS.register("golden_fleece", () ->
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
                new ArmorItem(GFArmorMaterials.STYXIAN, EquipmentSlot.HEAD,
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
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.HEAD, new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)));
        public static final RegistryObject<Item> SNAKESKIN_CHESTPLATE = ITEMS.register("snakeskin_chestplate", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.CHEST, new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)));
        public static final RegistryObject<Item> SNAKESKIN_LEGGINGS = ITEMS.register("snakeskin_leggings", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.LEGS, new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)));
        public static final RegistryObject<Item> SNAKESKIN_BOOTS = ITEMS.register("snakeskin_boots", () ->
                new SnakeskinArmorItem(GFArmorMaterials.SNAKESKIN, EquipmentSlot.FEET, new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE)));

        //// MISC ITEMS ////
        // TODO instruments
        public static final RegistryObject<Item> PANFLUTE = ITEMS.register("panflute", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> LYRE = ITEMS.register("wooden_lyre", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1)));
        public static final RegistryObject<Item> GOLDEN_LYRE = ITEMS.register("golden_lyre", () ->
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.UNCOMMON).stacksTo(1)));
        public static final RegistryObject<Item> OLIVES = ITEMS.register("olives", () ->
                new Item(new Item.Properties().tab(GF_TAB))); // TODO: food, places sapling
        public static final RegistryObject<Item> OLIVE_OIL = ITEMS.register("olive_oil", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(16)));
        public static final RegistryObject<Item> OLIVE_SALVE = ITEMS.register("olive_salve", () ->
                new Item(new Item.Properties().tab(GF_TAB).stacksTo(1))); // TODO salve
        public static final RegistryObject<Item> POMEGRANATE = ITEMS.register("pomegranate", () ->
                new Item(new Item.Properties().tab(GF_TAB))); // TODO: food, places sapling

        //// CRAFTING MATERIALS ////
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
                new Item(new Item.Properties().tab(GF_TAB).rarity(Rarity.RARE).stacksTo(1))); // TODO item block

        //// ITEM BLOCKS ////
        public static final RegistryObject<Item> OLIVE_SAPLING = ITEMS.register("olive_sapling", () ->
                new Item(new Item.Properties().tab(GF_TAB))); // TODO sapling
        public static final RegistryObject<Item> GOLDEN_SAPLING = ITEMS.register("golden_sapling", () ->
                new Item(new Item.Properties().tab(GF_TAB))); // TODO sapling
    }

    public static final class EntityReg {

        public static void register() {
            ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
            // event listeners
            FMLJavaModLoadingContext.get().getModEventBus().addListener(GFRegistry.EntityReg::registerEntityAttributes);
        }


        private static void registerEntityAttributes(EntityAttributeCreationEvent event) {

        }

        public static final RegistryObject<EntityType<?>> SPEAR = ENTITY_TYPES.register("spear", () ->
                EntityType.Builder.<SpearEntity>of(SpearEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).noSummon().clientTrackingRange(4).updateInterval(20)
                    .build("spear"));
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

    }


    public static final class EnchantmentReg {

        public static void register() {
            ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

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
