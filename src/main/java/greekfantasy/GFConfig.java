package greekfantasy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import greekfantasy.worldgen.BiomeListConfigSpec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GFConfig {

    // items
    public final ForgeConfigSpec.IntValue BAG_OF_WIND_DURATION;
    public final ForgeConfigSpec.IntValue BAG_OF_WIND_COOLDOWN;
    public final ForgeConfigSpec.IntValue DRAGON_TOOTH_SPARTI_COUNT;
    public final ForgeConfigSpec.IntValue DRAGON_TOOTH_SPARTI_LIFESPAN;
    public final ForgeConfigSpec.IntValue STAFF_OF_HEALING_COOLDOWN;
    public final ForgeConfigSpec.IntValue THUNDERBOLT_COOLDOWN;
    public final ForgeConfigSpec.BooleanValue UNICORN_HORN_CURES_EFFECTS;
    private final ForgeConfigSpec.DoubleValue WINGED_SANDALS_DURABILITY_CHANCE;
    private double wingedSandalsDurabilityChance;
    public final ForgeConfigSpec.IntValue WAND_OF_CIRCE_DURATION;
    public final ForgeConfigSpec.IntValue WAND_OF_CIRCE_COOLDOWN;

    private final ForgeConfigSpec.BooleanValue HELM_HIDES_ARMOR;
    private boolean helmHidesArmor;

    // enchantments
    public final ForgeConfigSpec.BooleanValue FIREFLASH_ENABLED;
    public final ForgeConfigSpec.BooleanValue FIREFLASH_DESTROYS_BLOCKS;
    private final ForgeConfigSpec.BooleanValue FLYING_ENABLED;
    private boolean isFlyingEnabled;
    public final ForgeConfigSpec.BooleanValue HUNTING_ENABLED;
    private final ForgeConfigSpec.BooleanValue MIRRORING_ENABLED;
    private boolean isMirroringEnabled;
    public final ForgeConfigSpec.BooleanValue SMASHING_NERF;
    private final ForgeConfigSpec.BooleanValue OVERSTEP_ENABLED;
    private boolean isOverstepEnabled;
    private final ForgeConfigSpec.BooleanValue POISONING_ENABLED;
    private boolean isPoisoningEnabled;
    public final ForgeConfigSpec.BooleanValue RAISING_ENABLED;
    public final ForgeConfigSpec.IntValue RAISING_SPARTI_LIFESPAN;
    private final ForgeConfigSpec.BooleanValue SILKSTEP_ENABLED;
    private boolean isSilkstepEnabled;

    // entity
    public final ForgeConfigSpec.DoubleValue ELPIS_SPAWN_CHANCE;
    public final ForgeConfigSpec.DoubleValue SHADE_SPAWN_CHANCE;
    public final ForgeConfigSpec.DoubleValue SATYR_SHAMAN_CHANCE;
    public final ForgeConfigSpec.BooleanValue SHADE_IMMUNE_TO_NONOWNER;
    private final ForgeConfigSpec.BooleanValue SHOW_ARACHNE_BOSS_BAR;
    private boolean showArachneBossBar;
    private final ForgeConfigSpec.BooleanValue SHOW_CIRCE_BOSS_BAR;
    private boolean showCirceBossBar;
    private final ForgeConfigSpec.BooleanValue SHOW_GIANT_BOAR_BOSS_BAR;
    private boolean showGiantBoarBossBar;
    private final ForgeConfigSpec.BooleanValue SHOW_HYDRA_BOSS_BAR;
    private boolean showHydraBossBar;
    private final ForgeConfigSpec.BooleanValue SHOW_MEDUSA_BOSS_BAR;
    private boolean showMedusaBossBar;
    private final ForgeConfigSpec.BooleanValue SHOW_NEMEAN_LION_BOSS_BAR;
    private boolean showNemeanLionBossBar;
    private final ForgeConfigSpec.BooleanValue SHOW_PYTHON_BOSS_BAR;
    private boolean showPythonBossBar;

    // mob effect
    private final ForgeConfigSpec.BooleanValue CURSE_OF_CIRCE_ENABLED;
    private boolean isCurseOfCirceEnabled;
    public final ForgeConfigSpec.IntValue CURSE_OF_CIRCE_DURATION;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> CURSE_OF_CIRCE_WHITELIST;
    private List<? extends String> curseOfCirceWhitelist;

    // palladium
    private final ForgeConfigSpec.BooleanValue PALLADIUM_ENABLED;
    private boolean palladiumEnabled;
    private final ForgeConfigSpec.IntValue PALLADIUM_REFRESH_INTERVAL;
    private int palladiumRefreshInterval;
    private final ForgeConfigSpec.IntValue PALLADIUM_CHUNK_RANGE;
    private int palladiumChunkRange;
    private final ForgeConfigSpec.IntValue PALLADIUM_Y_RANGE;
    private int palladiumYRange;

    private static final String WILDCARD = "*";

    // spawns
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_DIMENSION_WHITELIST;
    private List<? extends String> spawnDimensionWhitelist;
    private final ForgeConfigSpec.BooleanValue IS_SPAWN_DIMENSION_WHITELIST;
    private boolean isSpawnDimensionWhitelist;
    private final Map<String, BiomeListConfigSpec> SPAWN_CONFIG_SPECS = new HashMap<>();

    // features
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> FEATURE_DIMENSION_WHITELIST;
    private List<? extends String> featureDimensionWhitelist;
    private final ForgeConfigSpec.BooleanValue IS_FEATURE_DIMENSION_WHITELIST;
    private boolean isFeatureDimensionWhitelist;
    private final Map<String, BiomeListConfigSpec> FEATURE_CONFIG_SPECS = new HashMap<>();

    @SuppressWarnings("ConstantConditions")
    private static final String[] curseOfCirceWhitelistDefault = {
            ForgeRegistries.ENTITIES.getKey(EntityType.PLAYER).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.VILLAGER).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.ZOMBIE).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.ZOMBIE_VILLAGER).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.HUSK).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.VINDICATOR).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.WANDERING_TRADER).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.ILLUSIONER).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.PILLAGER).toString(),
            ForgeRegistries.ENTITIES.getKey(EntityType.WITCH).toString(),
            new ResourceLocation(GreekFantasy.MODID, "ara").toString(),
            new ResourceLocation(GreekFantasy.MODID, "dryad").toString(),
            new ResourceLocation(GreekFantasy.MODID, "naiad").toString(),
            new ResourceLocation(GreekFantasy.MODID, "satyr").toString()
    };

    public GFConfig(final ForgeConfigSpec.Builder builder) {

        builder.push("items");
        BAG_OF_WIND_DURATION = builder.defineInRange("bag_of_wind_duration", 400, 1, 24000);
        BAG_OF_WIND_COOLDOWN = builder.defineInRange("bag_of_wind_cooldown", 700, 0, 12000);
        HELM_HIDES_ARMOR = builder.define("helm_hides_armor", true);
        DRAGON_TOOTH_SPARTI_COUNT = builder.defineInRange("dragon_tooth_sparti_count", 1, 0, 8);
        DRAGON_TOOTH_SPARTI_LIFESPAN = builder.defineInRange("dragon_tooth_sparti_lifespan", 300, 1, 24000);
        THUNDERBOLT_COOLDOWN = builder.defineInRange("thunderbolt_cooldown", 100, 0, 24000);
        STAFF_OF_HEALING_COOLDOWN = builder.defineInRange("staff_of_healing_cooldown", 35, 0, 24000);
        UNICORN_HORN_CURES_EFFECTS = builder.define("unicorn_horn_cures_effects", true);
        WAND_OF_CIRCE_DURATION = builder.defineInRange("wand_of_circe_duration", 900, 1, 24000);
        WAND_OF_CIRCE_COOLDOWN = builder.defineInRange("wand_of_circe_cooldown", 50, 0, 24000);
        WINGED_SANDALS_DURABILITY_CHANCE = builder
                .comment("Percent chance of winged sandals losing durability each tick")
                .defineInRange("winged_sandals_durability_chance", 0.0F, 0.0F, 1.0);
        builder.pop();

        builder.push("enchantments");
        FIREFLASH_ENABLED = builder.define("fireflash_enabled", true);
        FIREFLASH_DESTROYS_BLOCKS = builder.define("fireflash_destroys_blocks", true);
        FLYING_ENABLED = builder.define("flying_enabled", true);
        HUNTING_ENABLED = builder.define("hunting_enabled", true);
        MIRRORING_ENABLED = builder.define("mirroring_enabled", true);
        SMASHING_NERF = builder
                .comment("When true, Smashing applies slowness instead of stunning")
                .define("smashing_nerf", false);
        OVERSTEP_ENABLED = builder.define("overstep_enabled", true);
        POISONING_ENABLED = builder.define("poisoning_enabled", true);
        RAISING_ENABLED = builder.define("raising_enabled", true);
        RAISING_SPARTI_LIFESPAN = builder.defineInRange("raising_sparti_lifespan", 120, 1, 24000);
        SILKSTEP_ENABLED = builder.define("silkstep_enabled", true);
        builder.pop();

        builder.push("mobs");
        ELPIS_SPAWN_CHANCE = builder
                .comment("Percent chance that opening a mysterious box spawns an Elpis")
                .defineInRange("elpis_spawn_chance", 60.0F, 0.0F, 100.0F);
        SHADE_SPAWN_CHANCE = builder.defineInRange("shade_spawn_chance", 100.0F, 0.0F, 100.0F);
        SATYR_SHAMAN_CHANCE = builder.defineInRange("satyr_shaman_chance", 24.0F, 0.0F, 100.0F);
        SHADE_IMMUNE_TO_NONOWNER = builder.define("shade_immune_to_nonowner", true);
        SHOW_ARACHNE_BOSS_BAR = builder.define("show_arachne_boss_bar", false);
        SHOW_CIRCE_BOSS_BAR = builder.define("show_circe_boss_bar", false);
        SHOW_GIANT_BOAR_BOSS_BAR = builder.define("show_giant_boar_boss_bar", true);
        SHOW_HYDRA_BOSS_BAR = builder.define("show_hydra_boss_bar", true);
        SHOW_MEDUSA_BOSS_BAR = builder.define("show_medusa_boss_bar", false);
        SHOW_NEMEAN_LION_BOSS_BAR = builder.define("show_nemean_lion_boss_bar", true);
        SHOW_PYTHON_BOSS_BAR = builder.define("show_python_boss_bar", true);
        builder.pop();

        builder.push("mob_effects");
        CURSE_OF_CIRCE_ENABLED = builder.define("curse_of_circe_enabled", true);
        CURSE_OF_CIRCE_DURATION = builder.defineInRange("curse_of_circe_duration", 900, 1, 24000);
        CURSE_OF_CIRCE_WHITELIST = builder.comment("Mobs that can be affected by the Curse of Circe.",
                        "Accepts entity id or mod id with wildcard.",
                        "Example: [\"minecraft:zombie\", \"othermod:" + WILDCARD + "\"]")
                .defineList("curse_of_circe_whitelist", List.of(curseOfCirceWhitelistDefault), o -> o instanceof String);
        builder.pop();

        builder.push("palladium");
        PALLADIUM_ENABLED = builder.comment("Whether the Palladium can prevent monster spawns")
                .define("palladium_enabled", true);
        PALLADIUM_REFRESH_INTERVAL = builder.comment("The number of server ticks between Palladium updates (increase to reduce lag)")
                .defineInRange("palladium_refresh_interval", 110, 2, 1000);
        PALLADIUM_CHUNK_RANGE = builder.comment("The radius (in chunks) of the area protected by Palladium blocks (0=same chunk only)")
                .defineInRange("palladium_chunk_range", 2, 0, 3);
        PALLADIUM_Y_RANGE = builder.comment("The vertical area (in blocks) protected by Palladium blocks")
                .defineInRange("palladium_y_range", 128, 0, 255);
        builder.pop();

        // helpers for spawn and feature configs
        final String[] forest = {BiomeDictionary.Type.FOREST.toString(), BiomeDictionary.Type.CONIFEROUS.toString(), BiomeDictionary.Type.JUNGLE.toString()};
        final String[] hostileBlacklist = {BiomeDictionary.Type.END.toString(), BiomeDictionary.Type.WATER.toString(), BiomeDictionary.Type.COLD.toString(),
                BiomeDictionary.Type.SNOWY.toString(), BiomeDictionary.Type.MUSHROOM.toString()};
        final String[] nonNetherHostileBlacklist = {BiomeDictionary.Type.NETHER.toString(), BiomeDictionary.Type.END.toString(), BiomeDictionary.Type.WATER.toString(),
                BiomeDictionary.Type.COLD.toString(), BiomeDictionary.Type.SNOWY.toString(), BiomeDictionary.Type.MUSHROOM.toString()};


        builder.comment("Mob spawn weights (higher number = more spawns)").push("mob_spawns");
        SPAWN_DIMENSION_WHITELIST = builder.comment("Dimensions in which mobs can spawn.",
                        "Accepts dimension id or mod id with wildcard.",
                        "Example: [\"minecraft:the_nether\", \"rftoolsdim:" + WILDCARD + "\"]")
                .define("spawn_dimensions", Lists.newArrayList("minecraft:" + WILDCARD));
        IS_SPAWN_DIMENSION_WHITELIST = builder.comment("true if the above list is a whitelist, false for blacklist")
                .define("is_whitelist", true);
        putSpawnConfigSpec(builder, "ara", 10, false, nonNetherHostileBlacklist);
        putSpawnConfigSpec(builder, "drakaina",40, false, hostileBlacklist);
        putSpawnConfigSpec(builder, "dryad", 24, true, forest);
        putSpawnConfigSpec(builder, "empusa",30, false, nonNetherHostileBlacklist);
        putSpawnConfigSpec(builder, "lampad", 24, true, Biomes.CRIMSON_FOREST.location().toString(), Biomes.WARPED_FOREST.location().toString());
        putSpawnConfigSpec(builder, "shade", 10, false);
        putSpawnConfigSpec(builder, "satyr", 22, true, forest);
        builder.pop();

        builder.comment("Feature generation chances (higher number = more features)").push("features");
        FEATURE_DIMENSION_WHITELIST = builder.comment("Dimensions in which mobs can spawn.",
                        "Accepts dimension id or mod id with wildcard.",
                        "Example: [\"minecraft:the_nether\", \"rftoolsdim:" + WILDCARD + "\"]")
                .define("spawn_dimensions", Lists.newArrayList("minecraft:" + WILDCARD));
        IS_FEATURE_DIMENSION_WHITELIST = builder.comment("true if the above list is a whitelist, false for blacklist")
                .define("is_whitelist", true);
        this.FEATURE_CONFIG_SPECS.clear();
        putFeatureConfigSpec(builder, "limestone_upper", 190, true, BiomeDictionary.Type.OVERWORLD.getName());
        putFeatureConfigSpec(builder, "limestone_lower", 1000, true, BiomeDictionary.Type.OVERWORLD.getName());
        putFeatureConfigSpec(builder, "marble_upper", 190, true, BiomeDictionary.Type.OVERWORLD.getName());
        putFeatureConfigSpec(builder, "marble_lower", 1000, true, BiomeDictionary.Type.OVERWORLD.getName());
        putFeatureConfigSpec(builder, "patch_reeds", 250, true, BiomeDictionary.Type.OVERWORLD.getName());
        putFeatureConfigSpec(builder, "patch_reeds_swamp", 900, true, BiomeDictionary.Type.SWAMP.getName());
        putFeatureConfigSpec(builder, "pomegranate_tree", 500, true, Biomes.WARPED_FOREST.location().toString(), Biomes.CRIMSON_FOREST.location().toString());
        builder.pop();
    }

    /**
     * Finalizes some values that might otherwise be called
     * fairly often and cause potential lag. Not all config
     * values are baked, since many of them are only called
     * once-per-load or once-per-entity
     **/
    public void bake() {
        // items
        helmHidesArmor = HELM_HIDES_ARMOR.get();
        wingedSandalsDurabilityChance = WINGED_SANDALS_DURABILITY_CHANCE.get();
        // enchantments
        isFlyingEnabled = FLYING_ENABLED.get();
        isMirroringEnabled = MIRRORING_ENABLED.get();
        isOverstepEnabled = OVERSTEP_ENABLED.get();
        isPoisoningEnabled = POISONING_ENABLED.get();
        isSilkstepEnabled = SILKSTEP_ENABLED.get();
        // mob
        showArachneBossBar = SHOW_ARACHNE_BOSS_BAR.get();
        showCirceBossBar = SHOW_CIRCE_BOSS_BAR.get();
        showGiantBoarBossBar = SHOW_GIANT_BOAR_BOSS_BAR.get();
        showHydraBossBar = SHOW_HYDRA_BOSS_BAR.get();
        showMedusaBossBar = SHOW_MEDUSA_BOSS_BAR.get();
        showNemeanLionBossBar = SHOW_NEMEAN_LION_BOSS_BAR.get();
        showPythonBossBar = SHOW_PYTHON_BOSS_BAR.get();
        // mob effects
        isCurseOfCirceEnabled = CURSE_OF_CIRCE_ENABLED.get();
        curseOfCirceWhitelist = ImmutableList.copyOf(CURSE_OF_CIRCE_WHITELIST.get());
        // palladium
        palladiumEnabled = PALLADIUM_ENABLED.get();
        palladiumRefreshInterval = PALLADIUM_REFRESH_INTERVAL.get();
        palladiumChunkRange = PALLADIUM_CHUNK_RANGE.get();
        palladiumYRange = PALLADIUM_Y_RANGE.get();
        // mob spawns
        spawnDimensionWhitelist = SPAWN_DIMENSION_WHITELIST.get();
        isSpawnDimensionWhitelist = IS_SPAWN_DIMENSION_WHITELIST.get();
        SPAWN_CONFIG_SPECS.values().forEach(c -> c.bake());
        // feature spawns
        featureDimensionWhitelist = FEATURE_DIMENSION_WHITELIST.get();
        isFeatureDimensionWhitelist = IS_FEATURE_DIMENSION_WHITELIST.get();
        FEATURE_CONFIG_SPECS.values().forEach(c -> c.bake());
    }

    // items

    public boolean helmHidesArmor() {
        return helmHidesArmor;
    }

    public double getWingedSandalsDurabilityChance() {
        return wingedSandalsDurabilityChance;
    }

    // enchantments

    public boolean isFlyingEnabled() {
        return isFlyingEnabled;
    }

    public boolean isPoisoningEnabled() {
        return isPoisoningEnabled;
    }

    public boolean isMirroringEnabled() {
        return isMirroringEnabled;
    }

    public boolean isOverstepEnabled() {
        return isOverstepEnabled;
    }

    public boolean isSilkstepEnabled() {
        return isSilkstepEnabled;
    }

    // mob

    public boolean showArachneBossBar() {
        return showArachneBossBar;
    }

    public boolean showCirceBossBar() {
        return showCirceBossBar;
    }

    public boolean showGiantBoarBossBar() {
        return showGiantBoarBossBar;
    }

    public boolean showHydraBossBar() {
        return showHydraBossBar;
    }

    public boolean showMedusaBossBar() {
        return showMedusaBossBar;
    }

    public boolean showNemeanLionBossBar() {
        return showNemeanLionBossBar;
    }

    public boolean showPythonBossBar() {
        return showPythonBossBar;
    }


    // mob effects

    public boolean isCurseOfCirceEnabled() {
        return isCurseOfCirceEnabled;
    }

    public boolean isCurseOfCirceApplicable(final LivingEntity entity) {
        ResourceLocation type = ForgeRegistries.ENTITIES.getKey(entity.getType());
        return curseOfCirceWhitelist.contains(type.toString())
                || curseOfCirceWhitelist.contains(type.getNamespace() + ":" + WILDCARD);
    }

    // palladium

    public boolean isPalladiumEnabled() {
        return palladiumEnabled;
    }

    public int getPalladiumRefreshInterval() {
        return palladiumRefreshInterval;
    }

    public int getPalladiumChunkRange() {
        return palladiumChunkRange;
    }

    public int getPalladiumYRange() {
        return palladiumYRange;
    }

    public BiomeListConfigSpec getSpawnConfigSpec(final String name) {
        return SPAWN_CONFIG_SPECS.get(name);
    }

    public BiomeListConfigSpec getFeatureConfigSpec(final String name) {
        return FEATURE_CONFIG_SPECS.get(name);
    }

    private void putSpawnConfigSpec(final ForgeConfigSpec.Builder builder, final String name, final int weight,
                                    final boolean isWhitelist, final String... biomes) {
        SPAWN_CONFIG_SPECS.put(name, new BiomeListConfigSpec(builder, name, weight, isWhitelist, biomes));
    }

    private void putFeatureConfigSpec(final ForgeConfigSpec.Builder builder, final String name, final int weight,
                                    final boolean isWhitelist, final String... biomes) {
        FEATURE_CONFIG_SPECS.put(name, new BiomeListConfigSpec(builder, name, weight, isWhitelist, biomes));
    }

    /**
     * @param level the level
     * @return true if features can be placed in the given dimension
     **/
    public boolean featureMatchesDimension(final Level level) {
        return matchesBiomeListConfigSpec(featureDimensionWhitelist, isFeatureDimensionWhitelist, level.dimension().location());
    }

    /**
     * @param level the level
     * @return true if mobs can spawn in the given dimension
     **/
    public boolean spawnMatchesDimension(final ServerLevel level) {
        return matchesBiomeListConfigSpec(spawnDimensionWhitelist, isSpawnDimensionWhitelist, level.dimension().location());
    }

    private static boolean matchesBiomeListConfigSpec(final List<? extends String> list, final boolean isWhitelist, final ResourceLocation dimensionId) {
        // check dimension id or mod id
        return isWhitelist == (list.contains(dimensionId.toString()) || list.contains(dimensionId.getNamespace() + ":" + WILDCARD));
    }
}
