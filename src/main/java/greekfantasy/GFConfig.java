package greekfantasy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class GFConfig {

    // items
    public final ForgeConfigSpec.IntValue BAG_OF_WIND_DURATION;
    public final ForgeConfigSpec.IntValue BAG_OF_WIND_COOLDOWN;
    public final ForgeConfigSpec.IntValue DRAGON_TOOTH_SPARTI_COUNT;
    public final ForgeConfigSpec.IntValue DRAGON_TOOTH_SPARTI_LIFESPAN;
    public final ForgeConfigSpec.BooleanValue UNICORN_HORN_CURES_EFFECTS;

    private final ForgeConfigSpec.BooleanValue HELM_HIDES_ARMOR;
    private boolean helmHidesArmor;

    // enchantments
    private final ForgeConfigSpec.BooleanValue IS_HUNTING_ENABLED;
    private boolean isHuntingEnabled;
    private final ForgeConfigSpec.BooleanValue IS_MIRRORING_ENABLED;
    private boolean isMirroringEnabled;
    private final ForgeConfigSpec.BooleanValue IS_OVERSTEP_ENABLED;
    private boolean isOverstepEnabled;
    private final ForgeConfigSpec.BooleanValue IS_POISONING_ENABLED;
    private boolean isPoisoningEnabled;
    private final ForgeConfigSpec.BooleanValue NERF_SMASHING;
    private boolean nerfSmashing;

    // entity
    public final ForgeConfigSpec.DoubleValue ELPIS_SPAWN_CHANCE;

    // palladium
    private final ForgeConfigSpec.BooleanValue PALLADIUM_ENABLED;
    private boolean palladiumEnabled;
    private final ForgeConfigSpec.IntValue PALLADIUM_REFRESH_INTERVAL;
    private int palladiumRefreshInterval;
    private final ForgeConfigSpec.IntValue PALLADIUM_CHUNK_RANGE;
    private int palladiumChunkRange;
    private final ForgeConfigSpec.IntValue PALLADIUM_Y_RANGE;
    private int palladiumYRange;

    public GFConfig(final ForgeConfigSpec.Builder builder) {

        builder.push("items");
        BAG_OF_WIND_DURATION = builder.defineInRange("bag_of_wind_duration", 400, 1, 24000);
        BAG_OF_WIND_COOLDOWN = builder.defineInRange("bag_of_wind_cooldown", 700, 0, 12000);
        HELM_HIDES_ARMOR = builder.define("helm_hides_armor", true);
        DRAGON_TOOTH_SPARTI_COUNT = builder.defineInRange("dragon_tooth_sparti_count", 1, 0, 8);
        DRAGON_TOOTH_SPARTI_LIFESPAN = builder.defineInRange("dragon_tooth_sparti_lifespan", 300, 1, 24000);
        UNICORN_HORN_CURES_EFFECTS = builder.define("unicorn_horn_cures_effects", true);
        builder.pop();

        builder.push("enchantments");
        IS_HUNTING_ENABLED = builder.define("hunting_enabled", true);
        IS_MIRRORING_ENABLED = builder.define("mirroring_enabled", true);
        IS_OVERSTEP_ENABLED = builder.define("overstep_enabled", true);
        IS_POISONING_ENABLED = builder.define("poisoning_enabled", true);
        NERF_SMASHING = builder
                .comment("When true, Smashing applies slowness instead of stunning")
                .define("nerf_smashing", false);
        builder.pop();

        builder.push("entity");
        ELPIS_SPAWN_CHANCE = builder
                .comment("Percent chance that opening a mysterious box spawns an Elpis")
                .defineInRange("elpis_spawn_chance", 0.6F, 0.0F, 1.0F);
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
        isHuntingEnabled = IS_HUNTING_ENABLED.get();
        isMirroringEnabled = IS_MIRRORING_ENABLED.get();
        isOverstepEnabled = IS_OVERSTEP_ENABLED.get();
        isPoisoningEnabled = IS_POISONING_ENABLED.get();
        nerfSmashing = NERF_SMASHING.get();
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

    public boolean isHuntingEnabled() {
        return isHuntingEnabled;
    }

    public boolean isMirroringEnabled() {
        return isMirroringEnabled;
    }

    public boolean isOverstepEnabled() {
        return isOverstepEnabled;
    }

    public boolean nerfSmashing() {
        return nerfSmashing;
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
