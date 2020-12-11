package greekfantasy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import greekfantasy.util.BiomeHelper;
import greekfantasy.util.BiomeWhitelistConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;

public class GFConfig {
  
  // item configs
  public final ForgeConfigSpec.DoubleValue SANDALS_SPEED_BONUS;
  public final ForgeConfigSpec.BooleanValue NERF_AMBROSIA;
  public final ForgeConfigSpec.BooleanValue UNICORN_HORN_CURES_EFFECTS;
  public final ForgeConfigSpec.IntValue UNICORN_HORN_DURABILITY;
  public final ForgeConfigSpec.IntValue HEALING_ROD_DURABILITY;
  public final ForgeConfigSpec.IntValue THUNDERBOLT_DURABILITY;
  public final ForgeConfigSpec.IntValue BAG_OF_WIND_DURABILITY;
  private final ForgeConfigSpec.BooleanValue HELM_HIDES_ARMOR;
  private final ForgeConfigSpec.BooleanValue DRAGON_TOOTH_SPAWNS_SPARTI;
  private final ForgeConfigSpec.BooleanValue SWORD_OF_HUNT_BYPASSES_ARMOR;
  private final ForgeConfigSpec.IntValue HEALING_ROD_COOLDOWN;
  private final ForgeConfigSpec.BooleanValue THUNDERBOLT_STORMS_ONLY;
  private final ForgeConfigSpec.IntValue THUNDERBOLT_COOLDOWN;
  private final ForgeConfigSpec.BooleanValue CONCH_ENABLED;
  private final ForgeConfigSpec.IntValue CONCH_COOLDOWN;
  private final ForgeConfigSpec.IntValue BAG_OF_WIND_COOLDOWN;
  public final ForgeConfigSpec.IntValue BAG_OF_WIND_DURATION;
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

  // effect configs
  private final ForgeConfigSpec.BooleanValue STUN_PREVENTS_JUMP;
  private final ForgeConfigSpec.BooleanValue STUN_PREVENTS_USE;
  private final ForgeConfigSpec.BooleanValue OVERSTEP_ENABLED;
  private final ForgeConfigSpec.BooleanValue SMASHING_ENABLED;
  private final ForgeConfigSpec.BooleanValue HUNTING_ENABLED;
  private final ForgeConfigSpec.BooleanValue MIRROR_ENABLED;
  private boolean stunPreventsJump;
  private boolean stunPreventsUse;
  private boolean overstepEnabled;
  private boolean smashingEnabled;
  private boolean huntingEnabled;
  private boolean mirrorEnabled;
  
  // potion configs
  private final ForgeConfigSpec.BooleanValue MIRROR_POTION;
  private boolean mirrorPotion;
  
  // special attack configs
  public final ForgeConfigSpec.BooleanValue EMPUSA_ATTACK;
  public final ForgeConfigSpec.BooleanValue GORGON_ATTACK;
  public final ForgeConfigSpec.BooleanValue MINOTAUR_ATTACK;
  public final ForgeConfigSpec.BooleanValue SHADE_ATTACK;
  public final ForgeConfigSpec.BooleanValue SATYR_ATTACK;
  public final ForgeConfigSpec.BooleanValue SIREN_ATTACK;
  
  // other special entity abilities
  public final ForgeConfigSpec.BooleanValue GIGANTE_RESISTANCE;
  public final ForgeConfigSpec.BooleanValue GERYON_RESISTANCE;
  public final ForgeConfigSpec.BooleanValue SHADE_PLAYER_ONLY;
  private final ForgeConfigSpec.BooleanValue DRYAD_ANGRY_ON_HARVEST;
  private final ForgeConfigSpec.IntValue DRYAD_ANGRY_RANGE;
  private final ForgeConfigSpec.IntValue SPARTI_LIFESPAN;
  private final ForgeConfigSpec.BooleanValue NERF_STUNNING;
  private final ForgeConfigSpec.BooleanValue NERF_PARALYSIS;
  private boolean dryadAngryOnHarvest;
  private int dryadAngryRange;
  private int spartiLifespan;
  private boolean nerfStunning;
  private boolean nerfParalysis;
  
  // spawn configs
  public final Map<String, BiomeWhitelistConfig> MOB_SPAWNS = new HashMap<>();
  // special spawn configs
  private final ForgeConfigSpec.BooleanValue SHADE_SPAWN_ON_DEATH;
  private final ForgeConfigSpec.IntValue SATYR_SHAMAN_CHANCE;
  private final ForgeConfigSpec.IntValue ELPIS_SPAWN_CHANCE;
  public final ForgeConfigSpec.IntValue NUM_SPARTI_SPAWNED;
  private boolean shadeSpawnOnDeath;
  private int satyrShamanChance;
  private int elpisSpawnChance;
  private int numSpartiSpawned;
  
  // feature configs
  public final ForgeConfigSpec.IntValue OLIVE_FOREST_BIOME_WEIGHT;
  public final Map<String, BiomeWhitelistConfig> FEATURES = new HashMap<>();
  
  // other
  public final ForgeConfigSpec.BooleanValue STATUES_HOLD_ITEMS;
  private final ForgeConfigSpec.IntValue PALLADIUM_REFRESH_INTERVAL;
  private final ForgeConfigSpec.IntValue PALLADIUM_CHUNK_RANGE;
  private final ForgeConfigSpec.IntValue PALLADIUM_Y_RANGE;
  private int palladiumRefreshInterval;
  private int palladiumChunkRange;
  private int palladiumYRange;
  
  public GFConfig(final ForgeConfigSpec.Builder builder) {
    // items
    builder.push("items");
    SANDALS_SPEED_BONUS = builder.comment("Winged Sandals speed bonus (1.0 = +100%)").worldRestart()
        .defineInRange("sandals_speed_bonus", 1.0D, 0.0D, 2.0D);
    NERF_AMBROSIA = builder.comment("When true, ambrosia gives effects of Golden Apple instead of Enchanted Golden Apple")
        .define("nerf_ambrosia", false);
    UNICORN_HORN_CURES_EFFECTS = builder.comment("Whether using the unicorn horn can cure potion effects")
        .define("unicorn_horn_cures_effects", true);
    UNICORN_HORN_DURABILITY = builder.defineInRange("unicorn_horn_durability", 44, 1, 4000);
    HELM_HIDES_ARMOR = builder.comment("Whether the helm of darkness hides armor")
        .define("helm_hides_armor", true);
    DRAGON_TOOTH_SPAWNS_SPARTI = builder.comment("Whether throwing a dragon tooth can spawn Sparti")
        .define("dragon_tooth_spawns_sparti", true);
    SWORD_OF_HUNT_BYPASSES_ARMOR = builder.comment("Whether the Sword of the Hunt deals absolute damage to animals")
        .define("sword_of_hunt_bypasses_armor", true);
    CONCH_ENABLED = builder.comment("Whether the Conch can place/remove water")
        .define("conch_enabled", true);
    CONCH_COOLDOWN = builder.comment("Cooldown time after using the conch")
        .defineInRange("conch_cooldown", 10, 0, 100);
    HEALING_ROD_COOLDOWN = builder.defineInRange("healing_rod_cooldown", 35, 0, 100);
    HEALING_ROD_DURABILITY = builder.defineInRange("healing_rod_durability", 384, 1, 4000);
    THUNDERBOLT_STORMS_ONLY = builder.comment("Whether the Thunderbolt can only be used during storms")
        .define("thunderbolt_storms_only", true);
    THUNDERBOLT_COOLDOWN = builder.defineInRange("thunderbolt_cooldown", 50, 0, 100);
    THUNDERBOLT_DURABILITY = builder.defineInRange("thunderbolt_durability", 168, 1, 4000);
    BAG_OF_WIND_DURATION = builder.defineInRange("bag_of_wind_duration", 400, 1, 24000);
    BAG_OF_WIND_COOLDOWN = builder.defineInRange("bag_of_wind_cooldown", 700, 0, 100);
    BAG_OF_WIND_DURABILITY = builder.defineInRange("bag_of_wind_durability", 24, 1, 4000);
    builder.pop();
    // mob attacks
    builder.push("effects");
    STUN_PREVENTS_JUMP = builder.comment("When stunned, players are prevented from jumping")
        .define("stun_prevents_jump", true);
    STUN_PREVENTS_USE = builder.comment("When stunned, players are prevented from using items")
        .define("stun_prevents_use", true);
    OVERSTEP_ENABLED = builder.comment("Whether 'overstep' enchantment can modify player step height")
        .define("enable_overstep", true);
    SMASHING_ENABLED = builder.comment("Whether 'smashing' enchantment can be used")
        .define("enable_smashing", true);
    HUNTING_ENABLED = builder.comment("Whether 'hunting' enchantment can be used")
        .define("enable_hunting", true);
    MIRROR_ENABLED = builder.comment("Whether 'mirror' enchantment can be used")
        .define("enable_mirror", true);
    builder.pop();
    // potion effects
    builder.push("potions");
    MIRROR_POTION = builder.comment("Whether the Potion of Mirroring can prevent paralysis")
        .define("enable_mirror_potion", true);
    builder.pop();
    // mob abilities
    builder.push("mob_abilities");
    EMPUSA_ATTACK = builder.comment("Whether the Empusa can drain health")
        .define("empusa_attack", true);
    GORGON_ATTACK = builder.comment("Whether the Gorgon can stun players by staring")
        .define("gorgon_attack", true);
    MINOTAUR_ATTACK = builder.comment("Whether the Minotaur can charge and stun players")
        .define("minotaur_attack", true);
    SATYR_ATTACK = builder.comment("Whether the Satyr Shaman can summon wolves")
        .define("satyr_attack", true);
    SHADE_ATTACK = builder.comment("Whether the Shade can steal player XP")
        .define("shade_attack", true);
    SIREN_ATTACK = builder.comment("Whether the Siren can charm players")
        .define("siren_attack", true);
    DRYAD_ANGRY_ON_HARVEST = builder.comment("Whether harvesting log blocks angers nearby dryads")
        .define("dryad_angry_on_harvest", true);
    DRYAD_ANGRY_RANGE = builder.comment("The distance at which dryads become angry when players harvest logs")
        .defineInRange("dryad_angry_range", 4, 0, 32);
    GIGANTE_RESISTANCE = builder.comment("Whether the Gigante has damage resistance")
        .define("gigante_resistance", true);
    GERYON_RESISTANCE = builder.comment("Whether the Geryon has damage resistance")
        .define("geryon_resistance", true);
    SHADE_PLAYER_ONLY = builder.comment("Whether shades that spawn when a player dies can only be killed by that player")
        .define("shade_player_only", true);
    SPARTI_LIFESPAN = builder.comment("Number of seconds until the Sparti begins taking damage")
        .defineInRange("sparti_lifespan", 300, 1, 8000);
    NERF_STUNNING = builder.comment("When true, replaces stunning with Slowness I and Weakness I")
        .define("nerf_stunning", false);
    NERF_PARALYSIS = builder.comment("When true, replaces paralysis with Slowness II and Weakness II")
        .define("nerf_paralysis", false);
    builder.pop();
    // mob spawn specials
    builder.push("mob_spawn_specials"); 
    SHADE_SPAWN_ON_DEATH = builder.comment("Whether a shade can spawn when players die")
        .define("shade_spawn_on_death", true);
    SATYR_SHAMAN_CHANCE = builder.comment("Percent chance that a satyr will be a shaman")
        .defineInRange("satyr_shaman_chance", 24, 0, 100);
    ELPIS_SPAWN_CHANCE = builder.comment("Percent chance that opening a mysterious box spawns an Elpis")
        .defineInRange("elpis_spawn_chance", 60, 0, 100);
    NUM_SPARTI_SPAWNED = builder.comment("Number of Sparti spawned by using a dragon tooth")
        .defineInRange("num_sparti_spawned", 1, 1, 8);
    builder.pop();
    // other
    builder.push("other");
    STATUES_HOLD_ITEMS = builder.comment("Whether statues can hold items (kinda buggy when disabled)")
        .define("statues_hold_items", true);
    PALLADIUM_REFRESH_INTERVAL = builder.comment("The number of server ticks between Palladium updates (increase to reduce lag)")
        .defineInRange("palladium_refresh_interval", 110, 2, 1000);
    PALLADIUM_CHUNK_RANGE = builder.comment("The radius (in chunks) of the area protected by Palladium blocks (0=disabled)")
        .defineInRange("palladium_chunk_range", 1, 0, 2);
    PALLADIUM_Y_RANGE = builder.comment("The vertical area protected by Palladium blocks, if enabled")
        .defineInRange("palladium_y_range", 128, 0, 255);
    builder.pop();
    // mob spawns
    builder.push("mob_spawns");
    final List<String> nether = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.NETHER);
    final List<String> ocean = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.OCEAN);
    final List<String> forest = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.FOREST, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.JUNGLE);
    final List<String> taiga = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.CONIFEROUS);
    final List<String> mountains = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.MOUNTAIN);
    final List<String> sandy = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
    final List<String> plains = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.PLAINS);
    final List<String> netherEndOceanIcy = BiomeHelper.getBiomeTypes(BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END, BiomeDictionary.Type.WATER, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY);
    MOB_SPAWNS.put("ara", new BiomeWhitelistConfig(builder, "ara_spawn", 10, false, netherEndOceanIcy));
    MOB_SPAWNS.put("centaur", new BiomeWhitelistConfig(builder, "centaur_spawn", 15, true, plains));
    MOB_SPAWNS.put("cerastes", new BiomeWhitelistConfig(builder, "cerastes_spawn", 30, true, sandy));
    MOB_SPAWNS.put("charybdis", new BiomeWhitelistConfig(builder, "charybdis_spawn", 4, true, ocean));
    MOB_SPAWNS.put("cyclopes", new BiomeWhitelistConfig(builder, "cyclopes_spawn", 20, true, mountains));
    MOB_SPAWNS.put("cyprian", new BiomeWhitelistConfig(builder, "cyprian_spawn", 10, true, BiomeHelper.concat(plains, taiga)));
    MOB_SPAWNS.put("dryad", new BiomeWhitelistConfig(builder, "dryad_spawn", 24, true, forest));
    MOB_SPAWNS.put("empusa", new BiomeWhitelistConfig(builder, "empusa_spawn", 75, false, netherEndOceanIcy));
    MOB_SPAWNS.put("gigante", new BiomeWhitelistConfig(builder, "gigante_spawn", 10, true, mountains));
    MOB_SPAWNS.put("gorgon", new BiomeWhitelistConfig(builder, "gorgon_spawn", 16, false, netherEndOceanIcy));
    MOB_SPAWNS.put("harpy", new BiomeWhitelistConfig(builder, "harpy_spawn", 10, true, sandy));
    MOB_SPAWNS.put("mad_cow", new BiomeWhitelistConfig(builder, "mad_cow_spawn", 2, false, netherEndOceanIcy));
    MOB_SPAWNS.put("minotaur", new BiomeWhitelistConfig(builder, "minotaur_spawn", 60, false, netherEndOceanIcy));
    MOB_SPAWNS.put("naiad", new BiomeWhitelistConfig(builder, "naiad_spawn", 10, true, BiomeHelper.getBiomeTypes(BiomeDictionary.Type.WATER)));
    MOB_SPAWNS.put("orthus", new BiomeWhitelistConfig(builder, "orthus_spawn", 20, true, nether));
    MOB_SPAWNS.put("satyr", new BiomeWhitelistConfig(builder, "satyr_spawn", 22, true, forest));
    MOB_SPAWNS.put("shade", new BiomeWhitelistConfig(builder, "shade_spawn", 10, false, new ArrayList<>()));
    MOB_SPAWNS.put("siren", new BiomeWhitelistConfig(builder, "siren_spawn", 10, true, ocean));
    MOB_SPAWNS.put("unicorn", new BiomeWhitelistConfig(builder, "unicorn_spawn", 11, true, plains));
    builder.pop();
    // feature configs
    builder.push("features");
    OLIVE_FOREST_BIOME_WEIGHT = builder.defineInRange("olive_forest_weight", 10, 0, 1000);
    FEATURES.put("harpy_nest", new BiomeWhitelistConfig(builder, "harpy_nest", 17, false, netherEndOceanIcy));
    FEATURES.put("small_shrine", new BiomeWhitelistConfig(builder, "small_shrine", 23, false, netherEndOceanIcy));
    FEATURES.put("small_nether_shrine", new BiomeWhitelistConfig(builder, "small_nether_shrine", 20, true, nether));
    FEATURES.put("ara_camp", new BiomeWhitelistConfig(builder, "ara_camp", 14, false, netherEndOceanIcy));
    FEATURES.put("satyr_camp", new BiomeWhitelistConfig(builder, "satyr_camp", 25, false, BiomeHelper.concat(netherEndOceanIcy, sandy)));
    FEATURES.put("python_pit", new BiomeWhitelistConfig(builder, "python_pit", 6, true, BiomeHelper.getBiomeTypes(BiomeDictionary.Type.JUNGLE)));
    FEATURES.put("reeds", new BiomeWhitelistConfig(builder, "reeds", 250, false, netherEndOceanIcy));
    FEATURES.put("reeds_swamp", new BiomeWhitelistConfig(builder, "reeds_swamp", 990, true, BiomeHelper.getBiomeTypes(BiomeDictionary.Type.SWAMP)));
    FEATURES.put("olive_tree_single", new BiomeWhitelistConfig(builder, "olive_tree_single", 28, true, BiomeHelper.getBiomeTypes(BiomeDictionary.Type.FOREST)));
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
    stunPreventsJump = STUN_PREVENTS_JUMP.get();
    stunPreventsUse = STUN_PREVENTS_USE.get();
    overstepEnabled = OVERSTEP_ENABLED.get();
    smashingEnabled = SMASHING_ENABLED.get();
    huntingEnabled = HUNTING_ENABLED.get();
    mirrorEnabled = MIRROR_ENABLED.get();
    mirrorPotion = MIRROR_POTION.get();
    shadeSpawnOnDeath = SHADE_SPAWN_ON_DEATH.get();
    satyrShamanChance = SATYR_SHAMAN_CHANCE.get();
    elpisSpawnChance = ELPIS_SPAWN_CHANCE.get();
    numSpartiSpawned = NUM_SPARTI_SPAWNED.get();
    dryadAngryOnHarvest = DRYAD_ANGRY_ON_HARVEST.get();
    dryadAngryRange = DRYAD_ANGRY_RANGE.get();
    spartiLifespan = SPARTI_LIFESPAN.get();
    nerfStunning = NERF_STUNNING.get();
    nerfParalysis = NERF_PARALYSIS.get();
    palladiumRefreshInterval = PALLADIUM_REFRESH_INTERVAL.get();
    palladiumChunkRange = PALLADIUM_CHUNK_RANGE.get();
    palladiumYRange = PALLADIUM_Y_RANGE.get();
  }
  
  public boolean doesHelmHideArmor() { return helmHidesArmor; }
  public boolean doesDragonToothSpawnSparti() { return dragonToothSpawnsSparti; }
  public boolean doesSwordOfHuntBypassArmor() { return swordOfHuntBypassesArmor; }
  public int getHealingRodCooldown() { return healingRodCooldown; }
  public boolean isThunderboltStormsOnly() { return thunderboltStormsOnly; }
  public int getThunderboltCooldown() { return thunderboltCooldown; }
  public boolean isConchEnabled() { return conchEnabled; }
  public int getConchCooldown() { return conchCooldown; }
  public int getBagOfWindDuration() { return bagOfWindDuration; }
  public int getBagOfWindCooldown() { return bagOfWindCooldown; }
  public boolean doesStunPreventJump() { return stunPreventsJump; }
  public boolean doesStunPreventUse() { return stunPreventsUse; }
  public boolean isOverstepEnabled() { return overstepEnabled; }
  public boolean isSmashingEnabled() { return smashingEnabled; }
  public boolean isHuntingEnabled() { return huntingEnabled; }
  public boolean isMirrorEnabled() { return mirrorEnabled; }
  public boolean isMirrorPotionEnabled() { return mirrorPotion; }
  public boolean doesShadeSpawnOnDeath() { return shadeSpawnOnDeath; }
  public int getSatyrShamanChance() { return satyrShamanChance; }
  public int getElpisSpawnChance() { return elpisSpawnChance; }
  public int getNumSpartiSpawned() { return numSpartiSpawned; }
  public boolean isDryadAngryOnHarvest() { return dryadAngryOnHarvest; }
  public int getDryadAngryRange() { return dryadAngryRange; }
  public int getSpartiLifespan() { return spartiLifespan; }
  public boolean isStunningNerf() { return nerfStunning; }
  public boolean isParalysisNerf() { return nerfParalysis; }
  public int getPalladiumRefreshInterval() { return palladiumRefreshInterval; }
  public int getPalladiumChunkRange() { return palladiumChunkRange; }
  public int getPalladiumYRange() { return palladiumYRange; }
}
