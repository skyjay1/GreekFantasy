package greekfantasy;

import net.minecraftforge.common.ForgeConfigSpec;

public class GFConfig {
  
  // item configs
  public final ForgeConfigSpec.DoubleValue SANDALS_SPEED_BONUS;
  
  // effect configs
  private final ForgeConfigSpec.BooleanValue STUN_PREVENTS_JUMP;
  private final ForgeConfigSpec.BooleanValue STUN_PREVENTS_USE;
  private final ForgeConfigSpec.BooleanValue OVERSTEP_ENABLED;
  private boolean stunPreventsJump;
  private boolean stunPreventsUse;
  private boolean overstepEnabled;
  
  // special attack configs
  public final ForgeConfigSpec.BooleanValue EMPUSA_ATTACK;
  public final ForgeConfigSpec.BooleanValue GORGON_ATTACK;
  public final ForgeConfigSpec.BooleanValue MINOTAUR_ATTACK;
  public final ForgeConfigSpec.BooleanValue ORTHUS_ATTACK;
  public final ForgeConfigSpec.BooleanValue SHADE_ATTACK;
  
  // spawn rate configs
  public final ForgeConfigSpec.IntValue ARA_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue CENTAUR_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue CERASTES_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue CERBERUS_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue CYCLOPES_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue CYPRIAN_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue DRYAD_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue EMPUSA_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue GIGANTE_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue GORGON_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue HARPY_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue MINOTAUR_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue NAIAD_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue ORTHUS_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue SATYR_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue SHADE_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue SIREN_SPAWN_WEIGHT;
  public final ForgeConfigSpec.IntValue UNICORN_SPAWN_WEIGHT;
  // special spawn configs
  public final ForgeConfigSpec.BooleanValue SHADE_SPAWN_ON_DEATH;
  public final ForgeConfigSpec.IntValue SATYR_SHAMAN_CHANCE;
  
  // other special entity abilities
  public final ForgeConfigSpec.BooleanValue GIGANTE_RESISTANCE;
  public final ForgeConfigSpec.BooleanValue SHADE_PLAYER_ONLY;
  private final ForgeConfigSpec.BooleanValue DRYAD_ANGRY_ON_HARVEST;
  private final ForgeConfigSpec.IntValue DRYAD_ANGRY_RANGE;
  private boolean dryadAngryOnHarvest;
  private int dryadAngryRange;
  
  // feature configs
  public final ForgeConfigSpec.IntValue HARPY_NEST_SPREAD;
  
  public GFConfig(final ForgeConfigSpec.Builder builder) {
    // items
    builder.push("items");
    SANDALS_SPEED_BONUS = builder.comment("Winged Sandals speed bonus (1.0 = +100%)").worldRestart()
        .defineInRange("sandals_speed_bonus", 1.0D, 0.0D, 2.0D);
    builder.pop();
    // mob attacks
    builder.push("effects");
    STUN_PREVENTS_JUMP = builder.comment("When stunned, players are prevented from jumping")
        .define("stun_prevents_jump", true);
    STUN_PREVENTS_USE = builder.comment("When stunned, players are prevented from using items")
        .define("stun_prevents_use", true);
    OVERSTEP_ENABLED = builder.comment("Whether 'overstep' effect can modify player step height")
        .define("enable_overstep", true);
    builder.pop();
    // mob abilities
    builder.push("mob_abilities");
    EMPUSA_ATTACK = builder.comment("Whether the Empusa can drain health")
        .define("empusa_attack", true);
    GORGON_ATTACK = builder.comment("Whether the Gorgon can stun players by staring")
        .define("gorgon_attack", true);
    MINOTAUR_ATTACK = builder.comment("Whether the Minotaur can charge and stun players")
        .define("minotaur_attack", true);
    ORTHUS_ATTACK = builder.comment("Whether the Orthus can breath fire")
        .define("orthus_attack", true);
    SHADE_ATTACK = builder.comment("Whether the Shade can steal player XP")
        .define("shade_attack", true);
    DRYAD_ANGRY_ON_HARVEST = builder.comment("Whether harvesting log blocks angers nearby dryads")
        .define("dryad_angry_on_harvest", true);
    DRYAD_ANGRY_RANGE = builder.comment("The distance at which dryads become angry when players harvest logs")
        .defineInRange("dryad_angry_range", 4, 0, 32);
    GIGANTE_RESISTANCE = builder.comment("Whether the Gigante has damage resistance")
        .define("gigante_resistance", true);
    SHADE_PLAYER_ONLY = builder.comment("Whether shades that spawn when a player dies can only be killed by that player")
        .define("shade_player_only", true);
    builder.pop();
    // mob spawns
    builder.push("mob_spawn_weights");
    ARA_SPAWN_WEIGHT = builder.worldRestart().defineInRange("ara_spawn_weight", 20, 0, 100);
    CERBERUS_SPAWN_WEIGHT = builder.worldRestart().defineInRange("cerberus_spawn_weight", 10, 0, 100);
    CENTAUR_SPAWN_WEIGHT = builder.worldRestart().defineInRange("centaur_spawn_weight", 15, 0, 100);
    CERASTES_SPAWN_WEIGHT = builder.worldRestart().defineInRange("cerastes_spawn_weight", 30, 0, 100);
    CYCLOPES_SPAWN_WEIGHT = builder.worldRestart().defineInRange("cyclopes_spawn_weight", 20, 0, 100);
    CYPRIAN_SPAWN_WEIGHT = builder.worldRestart().defineInRange("cyprian_spawn_weight", 10, 0, 100);
    DRYAD_SPAWN_WEIGHT = builder.worldRestart().defineInRange("dryad_spawn_weight", 24, 0, 100);
    EMPUSA_SPAWN_WEIGHT = builder.worldRestart().defineInRange("empusa_spawn_weight", 80, 0, 100);
    GIGANTE_SPAWN_WEIGHT = builder.worldRestart().defineInRange("gigante_spawn_weight", 20, 0, 100);
    GORGON_SPAWN_WEIGHT = builder.worldRestart().defineInRange("gorgon_spawn_weight", 16, 0, 100);
    HARPY_SPAWN_WEIGHT = builder.worldRestart().defineInRange("harpy_spawn_weight", 8, 0, 100);
    MINOTAUR_SPAWN_WEIGHT = builder.worldRestart().defineInRange("minotaur_spawn_weight", 60, 0, 100);
    NAIAD_SPAWN_WEIGHT = builder.worldRestart().defineInRange("naiad_spawn_weight", 10, 0, 100);
    ORTHUS_SPAWN_WEIGHT = builder.worldRestart().defineInRange("orthus_spawn_weight", 20, 0, 100);
    SATYR_SPAWN_WEIGHT = builder.worldRestart().defineInRange("satyr_spawn_weight", 22, 0, 100);
    SHADE_SPAWN_WEIGHT = builder.worldRestart().defineInRange("shade_spawn_weight", 10, 0, 100);
    SIREN_SPAWN_WEIGHT = builder.worldRestart().defineInRange("siren_spawn_weight", 10, 0, 100);
    UNICORN_SPAWN_WEIGHT = builder.worldRestart().defineInRange("unicorn_spawn_weight", 11, 0, 100);
    builder.pop();
    // mob spawn specials
    builder.push("mob_spawn_specials"); 
    SHADE_SPAWN_ON_DEATH = builder.comment("Whether a shade can spawn when players die")
        .define("shade_spawn_on_death", true);
    SATYR_SHAMAN_CHANCE = builder.comment("Percent chance that a satyr will be a shaman")
        .defineInRange("satyr_shaman_chance", 20, 0, 100);
    builder.pop();
    // feature configs
    builder.comment("structure and feature spread (higher number = less common)");
    builder.push("features");
    HARPY_NEST_SPREAD = builder.worldRestart().defineInRange("harpy_nest_spread", 90, 1, 1000);
    builder.pop();
  }
  
  /**
   * Finalizes some values that might otherwise be called
   * fairly often and cause potential lag. Not all config
   * values are baked, since many of them are only called
   * once-per-load or once-per-entity
   **/
  public void bake() {
    stunPreventsJump = STUN_PREVENTS_JUMP.get();
    stunPreventsUse = STUN_PREVENTS_USE.get();
    overstepEnabled = OVERSTEP_ENABLED.get();
    dryadAngryOnHarvest = DRYAD_ANGRY_ON_HARVEST.get();
    dryadAngryRange = DRYAD_ANGRY_RANGE.get();
  }
  
  public boolean doesStunPreventJump() { return stunPreventsJump; }
  public boolean doesStunPreventUse() { return stunPreventsUse; }
  public boolean isOverstepEnabled() { return overstepEnabled; }
  public boolean isDryadAngryOnHarvest() { return dryadAngryOnHarvest; }
  public int getDryadAngryRange() { return dryadAngryRange; }
}
