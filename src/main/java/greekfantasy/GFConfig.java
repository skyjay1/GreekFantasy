package greekfantasy;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GFConfig {

    // items
    public final ForgeConfigSpec.IntValue BAG_OF_WIND_DURATION;
    public final ForgeConfigSpec.IntValue BAG_OF_WIND_COOLDOWN;
    public final ForgeConfigSpec.IntValue DRAGON_TOOTH_SPARTI_COUNT;
    public final ForgeConfigSpec.IntValue DRAGON_TOOTH_SPARTI_LIFESPAN;
    public final ForgeConfigSpec.IntValue STAFF_OF_HEALING_COOLDOWN;
    public final ForgeConfigSpec.IntValue THUNDERBOLT_COOLDOWN;
    public final ForgeConfigSpec.BooleanValue UNICORN_HORN_CURES_EFFECTS;
    public final ForgeConfigSpec.IntValue WAND_OF_CIRCE_DURATION;
    public final ForgeConfigSpec.IntValue WAND_OF_CIRCE_COOLDOWN;

    private final ForgeConfigSpec.BooleanValue HELM_HIDES_ARMOR;
    private boolean helmHidesArmor;

    // enchantments
    public final ForgeConfigSpec.BooleanValue FIREFLASH_ENABLED;
    public final ForgeConfigSpec.BooleanValue FIREFLASH_DESTROYS_BLOCKS;
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

    // entity
    public final ForgeConfigSpec.DoubleValue ELPIS_SPAWN_CHANCE;

    // mob effect
    private final ForgeConfigSpec.BooleanValue CURSE_OF_CIRCE_ENABLED;
    private boolean isCurseOfCirceEnabled;
    public final ForgeConfigSpec.IntValue CURSE_OF_CIRCE_DURATION;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> CURSE_OF_CIRCE_WHITELIST;
    private List<ResourceLocation> curseOfCirceWhitelist;

    // palladium
    private final ForgeConfigSpec.BooleanValue PALLADIUM_ENABLED;
    private boolean palladiumEnabled;
    private final ForgeConfigSpec.IntValue PALLADIUM_REFRESH_INTERVAL;
    private int palladiumRefreshInterval;
    private final ForgeConfigSpec.IntValue PALLADIUM_CHUNK_RANGE;
    private int palladiumChunkRange;
    private final ForgeConfigSpec.IntValue PALLADIUM_Y_RANGE;
    private int palladiumYRange;

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
            ForgeRegistries.ENTITIES.getKey(EntityType.WITCH).toString()
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
        builder.pop();

        builder.push("enchantments");
        FIREFLASH_ENABLED = builder.define("fireflash_enabled", true);
        FIREFLASH_DESTROYS_BLOCKS = builder.define("fireflash_destroys_blocks", true);
        HUNTING_ENABLED = builder.define("hunting_enabled", true);
        MIRRORING_ENABLED = builder.define("mirroring_enabled", true);
        SMASHING_NERF = builder
                .comment("When true, Smashing applies slowness instead of stunning")
                .define("smashing_nerf", false);
        OVERSTEP_ENABLED = builder.define("overstep_enabled", true);
        POISONING_ENABLED = builder.define("poisoning_enabled", true);
        RAISING_ENABLED = builder.define("raising_enabled", true);
        RAISING_SPARTI_LIFESPAN = builder.defineInRange("raising_sparti_lifespan", 120, 1, 24000);
        builder.pop();

        builder.push("entity");
        ELPIS_SPAWN_CHANCE = builder
                .comment("Percent chance that opening a mysterious box spawns an Elpis")
                .defineInRange("elpis_spawn_chance", 0.6F, 0.0F, 1.0F);
        builder.pop();

        builder.push("mob_effects");
        CURSE_OF_CIRCE_ENABLED = builder.define("curse_of_circe_enabled", true);
        CURSE_OF_CIRCE_DURATION = builder.defineInRange("curse_of_circe_duration", 900, 1, 24000);
        CURSE_OF_CIRCE_WHITELIST = builder.defineList("curse_of_circe_whitelist", List.of(curseOfCirceWhitelistDefault), o -> o instanceof String);
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
        // enchantments
        isMirroringEnabled = MIRRORING_ENABLED.get();
        isOverstepEnabled = OVERSTEP_ENABLED.get();
        isPoisoningEnabled = POISONING_ENABLED.get();
        // mob effects
        isCurseOfCirceEnabled = CURSE_OF_CIRCE_ENABLED.get();
        curseOfCirceWhitelist = createEntityWhitelist(CURSE_OF_CIRCE_WHITELIST.get());
        // palladium
        palladiumEnabled = PALLADIUM_ENABLED.get();
        palladiumRefreshInterval = PALLADIUM_REFRESH_INTERVAL.get();
        palladiumChunkRange = PALLADIUM_CHUNK_RANGE.get();
        palladiumYRange = PALLADIUM_Y_RANGE.get();
    }

    // items

    public boolean helmHidesArmor() {
        return helmHidesArmor;
    }

    // enchantments

    public boolean isPoisoningEnabled() {
        return isPoisoningEnabled;
    }

    public boolean isMirroringEnabled() {
        return isMirroringEnabled;
    }

    public boolean isOverstepEnabled() {
        return isOverstepEnabled;
    }

    // mob effects

    public boolean isCurseOfCirceEnabled() {
        return isCurseOfCirceEnabled;
    }

    public boolean isCurseOfCirceApplicable(final LivingEntity entity) {
        return curseOfCirceWhitelist.contains(entity.getType().getRegistryName());
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

    private static List<ResourceLocation> createEntityWhitelist(final Collection<? extends String> stringList) {
        ImmutableList.Builder<ResourceLocation> builder = new ImmutableList.Builder<>();
        for(final String s : stringList) {
            ResourceLocation id = ResourceLocation.tryParse(s);
            if(id != null && ForgeRegistries.ENTITIES.containsKey(id)) {
                builder.add(id);
            }
        }
        return builder.build();
    }



    /*public boolean canPigApply(final String entityName) {
        return IS_PIG_ENTITY_WHITELIST.get() == PIG_ENTITY_WHITELIST.get().contains(entityName);
    }

    private static List<String> entitiesAsList(final EntityType<?>... types) {
        final List<String> list = new ArrayList<>();
        for (final EntityType<?> t : types) {
            list.add(t.getRegistryName().toString());
        }
        return list;
    }*/
}
