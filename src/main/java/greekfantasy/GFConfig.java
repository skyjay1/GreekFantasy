package greekfantasy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import greekfantasy.util.BiomeWhitelistConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class GFConfig {
  
  // item configs
  public final ForgeConfigSpec.DoubleValue SANDALS_SPEED_BONUS;
  public final ForgeConfigSpec.BooleanValue NERF_AMBROSIA;
  public final ForgeConfigSpec.BooleanValue UNICORN_HORN_CURES_EFFECTS;
  public final ForgeConfigSpec.IntValue UNICORN_HORN_DURABILITY;
  public final ForgeConfigSpec.IntValue HEALING_ROD_DURABILITY;
  public final ForgeConfigSpec.IntValue THUNDERBOLT_DURABILITY;
  public final ForgeConfigSpec.IntValue BAG_OF_WIND_DURABILITY;
  public final ForgeConfigSpec.IntValue SWINE_WAND_DURABILITY;
  private final ForgeConfigSpec.BooleanValue HELM_HIDES_ARMOR;
  private final ForgeConfigSpec.BooleanValue DRAGON_TOOTH_SPAWNS_SPARTI;
  private final ForgeConfigSpec.BooleanValue SWORD_OF_HUNT_BYPASSES_ARMOR;
  private final ForgeConfigSpec.IntValue HEALING_ROD_COOLDOWN;
  private final ForgeConfigSpec.BooleanValue THUNDERBOLT_STORMS_ONLY;
  private final ForgeConfigSpec.IntValue THUNDERBOLT_COOLDOWN;
  private final ForgeConfigSpec.BooleanValue CONCH_ENABLED;
  private final ForgeConfigSpec.IntValue CONCH_COOLDOWN;
  private final ForgeConfigSpec.IntValue BAG_OF_WIND_COOLDOWN;
  private final ForgeConfigSpec.IntValue BAG_OF_WIND_DURATION;
  private final ForgeConfigSpec.IntValue SWINE_WAND_COOLDOWN;
  private final ForgeConfigSpec.IntValue SWINE_WAND_DURATION;
  private final ForgeConfigSpec.BooleanValue WINGED_SANDALS_DEPLETE;
  private boolean helmHidesArmor;
  private boolean dragonToothSpawnsSparti;
  private boolean swordOfHuntBypassesArmor;
  private int healingRodCooldown;
  private boolean thunderboltStormsOnly;
  private int thunderboltCooldown;
  private boolean conchEnabled;
  private int conchCooldown;
  private int bagOfWindCooldown;
  private int bagOfWindDuration;
  private int swineWandCooldown;
  private int swineWandDuration;
  private boolean wingedSandalsDeplete;

  // effect configs
  private final ForgeConfigSpec.BooleanValue STUN_PREVENTS_JUMP;
  private final ForgeConfigSpec.BooleanValue STUN_PREVENTS_USE;
  private final ForgeConfigSpec.BooleanValue OVERSTEP_ENABLED;
  private final ForgeConfigSpec.BooleanValue SMASHING_ENABLED;
  private final ForgeConfigSpec.BooleanValue HUNTING_ENABLED;
  private final ForgeConfigSpec.BooleanValue POISON_ENABLED;
  private final ForgeConfigSpec.BooleanValue MIRROR_ENABLED;
  private final ForgeConfigSpec.BooleanValue SWINE_ENABLED;
  private final ForgeConfigSpec.BooleanValue SWINE_DROPS_ARMOR;
  private final ForgeConfigSpec.BooleanValue SWINE_PREVENTS_TARGET;
  private final ForgeConfigSpec.BooleanValue IS_SWINE_ENTITY_WHITELIST;
  private final ForgeConfigSpec.ConfigValue<List<? extends String>> SWINE_ENTITY_WHITELIST;
  private boolean stunPreventsJump;
  private boolean stunPreventsUse;
  private boolean overstepEnabled;
  private boolean smashingEnabled;
  private boolean huntingEnabled;
  private boolean poisonEnabled;
  private boolean mirrorEnabled;
  private boolean swineEnabled;
  private boolean swineDropsArmor;
  private boolean swinePreventsTarget;
  
  // potion configs
  private final ForgeConfigSpec.BooleanValue MIRROR_POTION;
  private final ForgeConfigSpec.BooleanValue SWINE_POTION;
  private boolean mirrorPotion;
  private boolean swinePotion;
  
  // special attack configs
  public final ForgeConfigSpec.BooleanValue DRAKAINA_ATTACK;
  public final ForgeConfigSpec.BooleanValue EMPUSA_ATTACK;
  public final ForgeConfigSpec.BooleanValue FURY_ATTACK;
  public final ForgeConfigSpec.BooleanValue GORGON_ATTACK;
  public final ForgeConfigSpec.BooleanValue MINOTAUR_ATTACK;
  public final ForgeConfigSpec.BooleanValue ORTHUS_ATTACK;
  public final ForgeConfigSpec.BooleanValue SHADE_ATTACK;
  public final ForgeConfigSpec.BooleanValue SATYR_ATTACK;
  public final ForgeConfigSpec.BooleanValue SIREN_ATTACK;
  public final ForgeConfigSpec.BooleanValue GIGANTE_RESISTANCE;
  public final ForgeConfigSpec.BooleanValue GERYON_RESISTANCE;
  public final ForgeConfigSpec.BooleanValue SATYR_LIGHTS_CAMPFIRES;
  private final ForgeConfigSpec.ConfigValue<? extends String> SATYR_SONG;
  public final ForgeConfigSpec.BooleanValue SHADE_PLAYER_ONLY;
  private final ForgeConfigSpec.BooleanValue WHIRL_INVULNERABLE;
  private final ForgeConfigSpec.BooleanValue DRYAD_ANGRY_ON_HARVEST;
  private final ForgeConfigSpec.IntValue DRYAD_ANGRY_RANGE;
  private final ForgeConfigSpec.IntValue SPARTI_LIFESPAN;
  private final ForgeConfigSpec.BooleanValue NERF_STUNNING;
  private final ForgeConfigSpec.BooleanValue NERF_PARALYSIS;
  private boolean whirlInvulnerable;
  private boolean dryadAngryOnHarvest;
  private int dryadAngryRange;
  private int spartiLifespan;
  private ResourceLocation satyrSong;
  private boolean nerfStunning;
  private boolean nerfParalysis;
  
  // spawn configs
  public final Map<String, BiomeWhitelistConfig> MOB_SPAWNS = new HashMap<>();
  // special spawn configs
  private final ForgeConfigSpec.BooleanValue SHADE_SPAWN_ON_DEATH;
  private final ForgeConfigSpec.IntValue SATYR_SHAMAN_CHANCE;
  private final ForgeConfigSpec.DoubleValue GORGON_MEDUSA_CHANCE;
  private final ForgeConfigSpec.IntValue LIGHTNING_MEDUSA_CHANCE;
  private final ForgeConfigSpec.IntValue ELPIS_SPAWN_CHANCE;
  public final ForgeConfigSpec.IntValue NUM_SPARTI_SPAWNED;
  private final ForgeConfigSpec.BooleanValue GIANT_BOAR_NON_NETHER;
  private boolean shadeSpawnOnDeath;
  private int satyrShamanChance;
  private double gorgonMedusaChance;
  private int lightningMedusaChance;
  private int elpisSpawnChance;
  private int numSpartiSpawned;
  private boolean giantBoarNonNether;
  
  // feature configs
  public final ForgeConfigSpec.IntValue OLIVE_FOREST_BIOME_WEIGHT;
  public final Map<String, BiomeWhitelistConfig> FEATURES = new HashMap<>();
  public final ForgeConfigSpec.BooleanValue IS_SPAWNS_WHITELIST;
  public final ConfigValue<List<? extends String>> SPAWNS_DIMENSION_WHITELIST;
//public final ForgeConfigSpec.BooleanValue IS_FEATURES_WHITELIST;
//public final ConfigValue<List<? extends String>> FEATURES_DIMENSION_WHITELIST;
  
  // palladium
  private final ForgeConfigSpec.BooleanValue PALLADIUM_ENABLED;
  private final ForgeConfigSpec.IntValue PALLADIUM_REFRESH_INTERVAL;
  private final ForgeConfigSpec.IntValue PALLADIUM_CHUNK_RANGE;
  private final ForgeConfigSpec.IntValue PALLADIUM_Y_RANGE;
  private boolean palladiumEnabled;
  private int palladiumRefreshInterval;
  private int palladiumChunkRange;
  private int palladiumYRange;
  
  // favor
  private final ForgeConfigSpec.BooleanValue FAVOR_DECREASES;
  private final ForgeConfigSpec.LongValue FAVOR_DECREASE_INTERVAL;
  private boolean favorDecreases;
  private long favorDecreaseInterval;
  
  public GFConfig(final ForgeConfigSpec.Builder builder) {
    // items
    builder.push("items");
    SANDALS_SPEED_BONUS = builder.comment("Winged Sandals speed bonus (1.0 = +100%)").worldRestart()
        .defineInRange("sandals_speed_bonus", 1.5D, 0.0D, 2.0D);
    NERF_AMBROSIA = builder.comment("When true, ambrosia gives effects of Golden Apple instead of Enchanted Golden Apple")
        .define("nerf_ambrosia", false);
    UNICORN_HORN_CURES_EFFECTS = builder.comment("Whether using the unicorn horn can cure potion effects")
        .define("unicorn_horn_cures_effects", true);
    UNICORN_HORN_DURABILITY = builder.defineInRange("unicorn_horn_durability", 44, 1, 4000);
    HELM_HIDES_ARMOR = builder.comment("Whether the helm of darkness hides armor")
        .define("helm_hides_armor", true);
    DRAGON_TOOTH_SPAWNS_SPARTI = builder.comment("Whether throwing a hydra tooth can spawn Sparti")
        .define("dragon_tooth_spawns_sparti", true);
    SWORD_OF_HUNT_BYPASSES_ARMOR = builder.comment("Whether the Sword of the Hunt deals absolute damage to animals")
        .define("sword_of_hunt_bypasses_armor", true);
    CONCH_ENABLED = builder.comment("Whether the Conch can place/remove water")
        .define("conch_enabled", true);
    CONCH_COOLDOWN = builder.defineInRange("conch_cooldown", 10, 0, 100);
    WINGED_SANDALS_DEPLETE = builder.comment("Whether the Winged Sandals slowly lose durability")
        .define("winged_sandals_deplete", false);
    HEALING_ROD_COOLDOWN = builder.defineInRange("healing_rod_cooldown", 35, 0, 100);
    HEALING_ROD_DURABILITY = builder.defineInRange("healing_rod_durability", 384, 1, 4000);
    THUNDERBOLT_STORMS_ONLY = builder.comment("Whether the Thunderbolt can only be used during storms")
        .define("thunderbolt_storms_only", false);
    THUNDERBOLT_COOLDOWN = builder.defineInRange("thunderbolt_cooldown", 50, 0, 100);
    THUNDERBOLT_DURABILITY = builder.defineInRange("thunderbolt_durability", 168, 1, 4000);
    BAG_OF_WIND_DURATION = builder.defineInRange("bag_of_wind_duration", 400, 1, 24000);
    BAG_OF_WIND_COOLDOWN = builder.defineInRange("bag_of_wind_cooldown", 700, 0, 100);
    BAG_OF_WIND_DURABILITY = builder.defineInRange("bag_of_wind_durability", 24, 1, 4000);
    SWINE_WAND_DURATION = builder.defineInRange("swine_wand_duration", 9600, 1, 24000);
    SWINE_WAND_COOLDOWN = builder.defineInRange("swine_wand_cooldown", 50, 0, 100);
    SWINE_WAND_DURABILITY = builder.defineInRange("swine_wand_durability", 104, 1, 4000);
    builder.pop();
    // mob attacks
    builder.push("effects");
    STUN_PREVENTS_JUMP = builder.comment("When stunned, players are prevented from jumping")
        .define("stun_prevents_jump", true);
    STUN_PREVENTS_USE = builder.comment("When stunned, players are prevented from using items")
        .define("stun_prevents_use", true);
    OVERSTEP_ENABLED = builder.define("enable_overstep_enchantment", true);
    SMASHING_ENABLED = builder.define("enable_smashing_enchantment", true);
    HUNTING_ENABLED = builder.define("enable_hunting_enchantment", true);
    POISON_ENABLED = builder.define("enable_poison_enchantment", true);
    MIRROR_ENABLED = builder.define("enable_mirror_enchantment", true);
    SWINE_ENABLED = builder.define("enable_swine_effect", true);
    SWINE_DROPS_ARMOR = builder.comment("Whether players under the swine effect drop their armor")
        .define("swine_drops_armor", true);
    SWINE_PREVENTS_TARGET = builder.comment("Whether some monsters ignore players under the swine effect")
        .define("swine_prevents_target", true);
    IS_SWINE_ENTITY_WHITELIST = builder.define("is_swine_entity_whitelist", true);
    final List<String> swineWhitelist = entitiesAsList(EntityType.PLAYER, EntityType.VILLAGER, EntityType.ZOMBIE,
        EntityType.ZOMBIE_VILLAGER, EntityType.HUSK, EntityType.VINDICATOR, EntityType.WANDERING_TRADER,
        EntityType.ILLUSIONER, EntityType.PILLAGER);
    Collections.addAll(swineWhitelist, GreekFantasy.MODID + ":ara", GreekFantasy.MODID + ":dryad", 
        GreekFantasy.MODID + ":naiad", GreekFantasy.MODID + ":satyr");
    SWINE_ENTITY_WHITELIST = builder.defineList("swine_entity_whitelist", swineWhitelist, o -> o instanceof String);
    NERF_STUNNING = builder.comment("When true, replaces stunning with Slowness I and Weakness I")
        .define("nerf_stunning", false);
    NERF_PARALYSIS = builder.comment("When true, replaces paralysis with Slowness II and Weakness II")
        .define("nerf_paralysis", false);
    builder.pop();
    // potion effects
    builder.push("potions");
    MIRROR_POTION = builder.comment("Whether the Potion of Mirroring can prevent paralysis")
        .define("enable_mirror_potion", true);
    SWINE_POTION = builder.comment("Whether the Potion of Swine can turn players into pigs")
        .define("enable_swine_potion", true);
    builder.pop();
    // mob abilities
    builder.push("mob_abilities");
    DRAKAINA_ATTACK = builder.comment("Whether the Drakaina can use special attacks based on variant")
        .define("drakaina_attack", true);
    EMPUSA_ATTACK = builder.comment("Whether the Empusa can drain health")
        .define("empusa_attack", true);
    FURY_ATTACK = builder.comment("Whether the Fury can throw curses")
        .define("fury_attack", true);
    GORGON_ATTACK = builder.comment("Whether the Gorgon can stun players by staring")
        .define("gorgon_attack", true);
    MINOTAUR_ATTACK = builder.comment("Whether the Minotaur can charge and stun players")
        .define("minotaur_attack", true);
    ORTHUS_ATTACK = builder.comment("Whether the Orthus can shoot fire")
        .define("orthus_attack", true);
    SATYR_ATTACK = builder.comment("Whether the Satyr Shaman can summon wolves")
        .define("satyr_attack", true);
    SHADE_ATTACK = builder.comment("Whether the Shade can steal player XP")
        .define("shade_attack", true);
    SIREN_ATTACK = builder.comment("Whether the Siren can charm players")
        .define("siren_attack", true);
    WHIRL_INVULNERABLE = builder.comment("Whether the Whirl is invulnerable to damage")
        .define("whirl_invulnerable", false);
    DRYAD_ANGRY_ON_HARVEST = builder.comment("Whether harvesting log blocks angers nearby dryads")
        .define("dryad_angry_on_harvest", true);
    DRYAD_ANGRY_RANGE = builder.comment("The distance at which dryads become angry when players harvest logs")
        .defineInRange("dryad_angry_range", 4, 0, 32);
    GIGANTE_RESISTANCE = builder.comment("Whether the Gigante has damage resistance")
        .define("gigante_resistance", true);
    GERYON_RESISTANCE = builder.comment("Whether the Geryon has damage resistance")
        .define("geryon_resistance", true);
    SATYR_LIGHTS_CAMPFIRES = builder.comment("Whether the Satyr can light unlit campfires")
        .define("satyr_lights_campfires", true);
    SATYR_SONG = builder.comment("The song played by the Satyr while dancing")
        .define("satyr_song", GreekFantasy.MODID + ":greensleeves");
    SHADE_PLAYER_ONLY = builder.comment("Whether shades that spawn when a player dies can only be killed by that player")
        .define("shade_player_only", true);
    SPARTI_LIFESPAN = builder.comment("Number of seconds until the Sparti begins taking damage")
        .defineInRange("sparti_lifespan", 300, 1, 8000);
    builder.pop();
    // mob spawn specials
    builder.push("mob_spawn_specials"); 
    SHADE_SPAWN_ON_DEATH = builder.comment("Whether a shade can spawn when players die")
        .define("shade_spawn_on_death", true);
    SATYR_SHAMAN_CHANCE = builder.comment("Percent chance that a satyr will be a shaman")
        .defineInRange("satyr_shaman_chance", 24, 0, 100);
    GORGON_MEDUSA_CHANCE = builder.comment("Percent chance that a gorgon will be a medusa")
        .defineInRange("gorgon_medusa_chance", 0.8D, 0.0D, 100.0D);
    LIGHTNING_MEDUSA_CHANCE = builder.comment("Percent chance that lightning striking a gorgon will turn it into a medusa")
        .defineInRange("lightning_medusa_chance", 95, 0, 100);
    ELPIS_SPAWN_CHANCE = builder.comment("Percent chance that opening a mysterious box spawns an Elpis")
        .defineInRange("elpis_spawn_chance", 60, 0, 100);
    NUM_SPARTI_SPAWNED = builder.comment("Number of Sparti spawned by using a hydra tooth")
        .defineInRange("num_sparti_spawned", 1, 1, 8);
    GIANT_BOAR_NON_NETHER = builder.comment("Whether a hoglin must be outside of the nether to be turned to a Giant Boar")
        .define("giant_boar_non_nether", true);
    builder.pop();
    // palladium
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
    // favor
    builder.push("favor");
    FAVOR_DECREASES = builder.comment("Whether favor tends toward zero over time")
        .define("favor_decreases", true);
    FAVOR_DECREASE_INTERVAL = builder.comment("Number of ticks between decreasing favor, if enabled")
        .defineInRange("favor_decrease_interval", 4000L, 1L, 96000L);
    builder.pop();
    // mob spawns
    builder.push("mob_spawns");
    final List<String> dimensions = new ArrayList<>();
    dimensions.add(Dimension.OVERWORLD.getLocation().toString());
    dimensions.add(Dimension.THE_NETHER.getLocation().toString());
    IS_SPAWNS_WHITELIST = builder.worldRestart().define("whitelist_dimensions", true);
    SPAWNS_DIMENSION_WHITELIST = builder.worldRestart().define("dimensions", dimensions);
    final List<String> overworld = biomesAsList(BiomeDictionary.Type.OVERWORLD);
    final List<String> nether = biomesAsList(BiomeDictionary.Type.NETHER);
    final List<String> ocean = biomesAsList(BiomeDictionary.Type.OCEAN);
    final List<String> forest = biomesAsList(BiomeDictionary.Type.FOREST, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.JUNGLE);
    final List<String> taiga = biomesAsList(BiomeDictionary.Type.CONIFEROUS);
    final List<String> mountains = biomesAsList(BiomeDictionary.Type.MOUNTAIN);
    final List<String> sandy = biomesAsList(BiomeDictionary.Type.MESA, BiomeDictionary.Type.SANDY);
    final List<String> plains = biomesAsList(BiomeDictionary.Type.PLAINS);
    final List<String> hostileBlacklist = biomesAsList(BiomeDictionary.Type.END, BiomeDictionary.Type.WATER, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.MUSHROOM);
    final List<String> nonNetherHostileBlacklist = biomesAsList(BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END, BiomeDictionary.Type.WATER, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.MUSHROOM);
    MOB_SPAWNS.put("ara", new BiomeWhitelistConfig(builder, "ara_spawn", 10, false, nonNetherHostileBlacklist));
    MOB_SPAWNS.put("centaur", new BiomeWhitelistConfig(builder, "centaur_spawn", 15, true, plains));
    MOB_SPAWNS.put("cerastes", new BiomeWhitelistConfig(builder, "cerastes_spawn", 30, true, sandy));
    MOB_SPAWNS.put("cyclopes", new BiomeWhitelistConfig(builder, "cyclopes_spawn", 20, true, mountains));
    MOB_SPAWNS.put("cyprian", new BiomeWhitelistConfig(builder, "cyprian_spawn", 15, true, concat(plains, taiga)));
    MOB_SPAWNS.put("drakaina", new BiomeWhitelistConfig(builder, "drakaina_spawn", 60, false, hostileBlacklist));
    MOB_SPAWNS.put("dryad", new BiomeWhitelistConfig(builder, "dryad_spawn", 24, true, forest));
    MOB_SPAWNS.put("empusa", new BiomeWhitelistConfig(builder, "empusa_spawn", 30, false, nonNetherHostileBlacklist));
    MOB_SPAWNS.put("fury", new BiomeWhitelistConfig(builder, "fury_spawn", 9, true, nether));
    MOB_SPAWNS.put("gigante", new BiomeWhitelistConfig(builder, "gigante_spawn", 10, true, mountains));
    MOB_SPAWNS.put("gorgon", new BiomeWhitelistConfig(builder, "gorgon_spawn", 30, false, nonNetherHostileBlacklist));
    MOB_SPAWNS.put("harpy", new BiomeWhitelistConfig(builder, "harpy_spawn", 24, true, sandy));
    MOB_SPAWNS.put("mad_cow", new BiomeWhitelistConfig(builder, "mad_cow_spawn", 2, false, nonNetherHostileBlacklist));
    MOB_SPAWNS.put("minotaur", new BiomeWhitelistConfig(builder, "minotaur_spawn", 60, false, nonNetherHostileBlacklist));
    MOB_SPAWNS.put("naiad", new BiomeWhitelistConfig(builder, "naiad_spawn", 12, true, biomesAsList(BiomeDictionary.Type.WATER)));
    MOB_SPAWNS.put("orthus", new BiomeWhitelistConfig(builder, "orthus_spawn", 20, true, nether));
    MOB_SPAWNS.put("satyr", new BiomeWhitelistConfig(builder, "satyr_spawn", 22, true, forest));
    MOB_SPAWNS.put("shade", new BiomeWhitelistConfig(builder, "shade_spawn", 10, false, new ArrayList<>()));
    MOB_SPAWNS.put("siren", new BiomeWhitelistConfig(builder, "siren_spawn", 10, true, ocean));
    MOB_SPAWNS.put("unicorn", new BiomeWhitelistConfig(builder, "unicorn_spawn", 11, true, plains));
    MOB_SPAWNS.put("whirl", new BiomeWhitelistConfig(builder, "whirl_spawn", 6, true, ocean));
    builder.pop();
    // feature configs
    builder.push("features");
//    IS_FEATURES_WHITELIST = builder.worldRestart().define("whitelist_dimensions", true);
//    FEATURES_DIMENSION_WHITELIST = builder.worldRestart().define("dimensions", dimensions);
    OLIVE_FOREST_BIOME_WEIGHT = builder.defineInRange("olive_forest_weight", 9, 0, 1000);
    FEATURES.put("limestone", new BiomeWhitelistConfig(builder, "limestone", 1000, true, overworld));
    FEATURES.put("marble", new BiomeWhitelistConfig(builder, "marble", 1000, true, overworld));
    FEATURES.put("harpy_nest", new BiomeWhitelistConfig(builder, "harpy_nest", 12, false, nonNetherHostileBlacklist));
    FEATURES.put("small_shrine", new BiomeWhitelistConfig(builder, "small_shrine", 17, false, nonNetherHostileBlacklist));
    FEATURES.put("small_nether_shrine", new BiomeWhitelistConfig(builder, "small_nether_shrine", 16, true, nether));
    FEATURES.put("ara_camp", new BiomeWhitelistConfig(builder, "ara_camp", 10, false, nonNetherHostileBlacklist));
    FEATURES.put("satyr_camp", new BiomeWhitelistConfig(builder, "satyr_camp", 15, false, concat(nonNetherHostileBlacklist, sandy)));
    FEATURES.put("python_pit", new BiomeWhitelistConfig(builder, "python_pit", 6, true, biomesAsList(BiomeDictionary.Type.JUNGLE)));
    FEATURES.put("reeds", new BiomeWhitelistConfig(builder, "reeds", 250, false, nonNetherHostileBlacklist));
    FEATURES.put("reeds_swamp", new BiomeWhitelistConfig(builder, "reeds_swamp", 900, true, biomesAsList(BiomeDictionary.Type.SWAMP)));
    FEATURES.put("olive_tree_single", new BiomeWhitelistConfig(builder, "olive_tree_single", 22, true, biomesAsList(BiomeDictionary.Type.FOREST)));
    builder.pop();
  }
  
  /**
   * Finalizes some values that might otherwise be called
   * fairly often and cause potential lag. Not all config
   * values are baked, since many of them are only called
   * once-per-load or once-per-entity
   **/
  public void bake() {
    helmHidesArmor = HELM_HIDES_ARMOR.get();
    dragonToothSpawnsSparti = DRAGON_TOOTH_SPAWNS_SPARTI.get();
    swordOfHuntBypassesArmor = SWORD_OF_HUNT_BYPASSES_ARMOR.get();
    healingRodCooldown = HEALING_ROD_COOLDOWN.get();
    thunderboltStormsOnly = THUNDERBOLT_STORMS_ONLY.get();
    thunderboltCooldown = THUNDERBOLT_COOLDOWN.get();
    conchEnabled = CONCH_ENABLED.get();
    conchCooldown = CONCH_COOLDOWN.get();
    bagOfWindDuration = BAG_OF_WIND_DURATION.get();
    bagOfWindCooldown = BAG_OF_WIND_COOLDOWN.get();
    swineWandDuration = SWINE_WAND_DURATION.get();
    swineWandCooldown = SWINE_WAND_COOLDOWN.get();
    wingedSandalsDeplete = WINGED_SANDALS_DEPLETE.get();
    stunPreventsJump = STUN_PREVENTS_JUMP.get();
    stunPreventsUse = STUN_PREVENTS_USE.get();
    overstepEnabled = OVERSTEP_ENABLED.get();
    smashingEnabled = SMASHING_ENABLED.get();
    huntingEnabled = HUNTING_ENABLED.get();
    poisonEnabled = POISON_ENABLED.get();
    mirrorEnabled = MIRROR_ENABLED.get();
    swineEnabled = SWINE_ENABLED.get();
    swineDropsArmor = SWINE_DROPS_ARMOR.get();
    swinePreventsTarget = SWINE_PREVENTS_TARGET.get();
    mirrorPotion = MIRROR_POTION.get();
    swinePotion = SWINE_POTION.get();
    // other mob spawn specials
    shadeSpawnOnDeath = SHADE_SPAWN_ON_DEATH.get();
    satyrShamanChance = SATYR_SHAMAN_CHANCE.get();
    gorgonMedusaChance = GORGON_MEDUSA_CHANCE.get();
    lightningMedusaChance = LIGHTNING_MEDUSA_CHANCE.get();
    elpisSpawnChance = ELPIS_SPAWN_CHANCE.get();
    numSpartiSpawned = NUM_SPARTI_SPAWNED.get();
    giantBoarNonNether = GIANT_BOAR_NON_NETHER.get();
    dryadAngryOnHarvest = DRYAD_ANGRY_ON_HARVEST.get();
    dryadAngryRange = DRYAD_ANGRY_RANGE.get();
    whirlInvulnerable = WHIRL_INVULNERABLE.get();
    spartiLifespan = SPARTI_LIFESPAN.get();
    satyrSong = new ResourceLocation(SATYR_SONG.get());
    nerfStunning = NERF_STUNNING.get();
    nerfParalysis = NERF_PARALYSIS.get();
    // palladium
    palladiumEnabled = PALLADIUM_ENABLED.get();
    palladiumRefreshInterval = PALLADIUM_REFRESH_INTERVAL.get();
    palladiumChunkRange = PALLADIUM_CHUNK_RANGE.get();
    palladiumYRange = PALLADIUM_Y_RANGE.get();
    // favor
    favorDecreases = FAVOR_DECREASES.get();
    favorDecreaseInterval = FAVOR_DECREASE_INTERVAL.get();
  }
  
  public boolean doesHelmHideArmor() { return helmHidesArmor; }
  public boolean doesDragonToothSpawnSparti() { return dragonToothSpawnsSparti; }
  public boolean doesSwordOfHuntBypassArmor() { return swordOfHuntBypassesArmor; }
  public boolean doesWingedSandalsDeplete() { return wingedSandalsDeplete; }
  public int getHealingRodCooldown() { return healingRodCooldown; }
  public boolean isThunderboltStormsOnly() { return thunderboltStormsOnly; }
  public int getThunderboltCooldown() { return thunderboltCooldown; }
  public boolean isConchEnabled() { return conchEnabled; }
  public int getConchCooldown() { return conchCooldown; }
  public int getBagOfWindDuration() { return bagOfWindDuration; }
  public int getBagOfWindCooldown() { return bagOfWindCooldown; }
  public int getSwineWandDuration() { return swineWandDuration; }
  public int getSwineWandCooldown() { return swineWandCooldown; }
  public boolean doesStunPreventJump() { return stunPreventsJump; }
  public boolean doesStunPreventUse() { return stunPreventsUse; }
  public boolean isOverstepEnabled() { return overstepEnabled; }
  public boolean isSmashingEnabled() { return smashingEnabled; }
  public boolean isHuntingEnabled() { return huntingEnabled; }
  public boolean isPoisonEnabled() { return poisonEnabled; }
  public boolean isMirrorEnabled() { return mirrorEnabled; }
  public boolean isSwineEnabled() { return swineEnabled; }
  public boolean doesSwineDropArmor() { return swineDropsArmor; }
  public boolean doesSwinePreventTarget() { return swinePreventsTarget; }
  public boolean isMirrorPotionEnabled() { return mirrorPotion; }
  public boolean isSwinePotionEnabled() { return swinePotion; }
  public boolean doesShadeSpawnOnDeath() { return shadeSpawnOnDeath; }
  public int getSatyrShamanChance() { return satyrShamanChance; }
  public ResourceLocation getSatyrSong() { return satyrSong; }
  public double getGorgonMedusaChance() { return gorgonMedusaChance; }
  public int getLightningMedusaChance() { return lightningMedusaChance; }
  public int getElpisSpawnChance() { return elpisSpawnChance; }
  public int getNumSpartiSpawned() { return numSpartiSpawned; }
  public boolean getGiantBoarNonNether() { return giantBoarNonNether; }
  public boolean isWhirlInvulnerable() { return whirlInvulnerable; }
  public boolean isDryadAngryOnHarvest() { return dryadAngryOnHarvest; }
  public int getDryadAngryRange() { return dryadAngryRange; }
  public int getSpartiLifespan() { return spartiLifespan; }
  public boolean isStunningNerf() { return nerfStunning; }
  public boolean isParalysisNerf() { return nerfParalysis; }
  public boolean isPalladiumEnabled() { return palladiumEnabled; }
  public int getPalladiumRefreshInterval() { return palladiumRefreshInterval; }
  public int getPalladiumChunkRange() { return palladiumChunkRange; }
  public int getPalladiumYRange() { return palladiumYRange; }
  public boolean doesFavorDecrease() { return favorDecreases; }
  public long getFavorDecreaseInterval() { return favorDecreaseInterval; }
  public boolean canSwineApply(final String entityName) {
    return IS_SWINE_ENTITY_WHITELIST.get() == SWINE_ENTITY_WHITELIST.get().contains(entityName);
  }
  
  private static List<String> concat(final List<String> list1, final List<String> list2) {
    final List<String> list = new ArrayList<>(list1);
    list.addAll(list2);
    return list;
  }
  
  private static List<String> entitiesAsList(final EntityType<?>... types) {
    final List<String> list = new ArrayList<>();
    for(final EntityType<?> t : types) {
      list.add(t.getRegistryName().toString());
    }
    return list;
  }
  
  private static List<String> biomesAsList(final BiomeDictionary.Type... types) {
    final List<String> list = new ArrayList<>();
    for(final BiomeDictionary.Type t : types) {
      list.add(t.getName());
    }
    return list;
  }
}
