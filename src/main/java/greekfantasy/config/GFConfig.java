package greekfantasy.config;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class GFConfig {
  
  // effect configs
  public final ForgeConfigSpec.BooleanValue STUN_PREVENTS_JUMP;
  public final ForgeConfigSpec.BooleanValue STUN_PREVENTS_USE;
  
  // special attack configs
  public final ForgeConfigSpec.BooleanValue EMPUSA_ATTACK;
  public final ForgeConfigSpec.BooleanValue GORGON_ATTACK;
  public final ForgeConfigSpec.BooleanValue MINOTAUR_ATTACK;
  public final ForgeConfigSpec.BooleanValue ORTHUS_ATTACK;
  public final ForgeConfigSpec.BooleanValue SHADE_ATTACK;
  
  // spawn rate configs
  // TODO
//  public final ForgeConfigSpec.IntValue ARA_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue CENTAUR_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue CERASTES_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue CERBERUS_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue CYCLOPES_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue CYPRIAN_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue EMPUSA_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue GIGANTE_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue GORGON_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue HARPY_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue MINOTAUR_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue NYMPH_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue SATYR_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue SHADE_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue SIREN_SPAWN_RATE;
//  public final ForgeConfigSpec.IntValue UNICORN_SPAWN_RATE;
  public final ForgeConfigSpec.BooleanValue SHADE_SPAWN;
  
  public GFConfig(final ForgeConfigSpec.Builder builder) {
    // mob attacks
    builder.push("stun_effect");
    STUN_PREVENTS_JUMP = builder.comment("When stunned, players are prevented from jumping")
        .define("stun_prevents_jump", true);
    STUN_PREVENTS_USE = builder.comment("When stunned, players are prevented from using items")
        .define("stun_prevents_use", true);
    builder.pop();
    builder.push("mob_attacks");
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
    builder.pop();
    // mob spawns
    builder.push("mob_spawns");
    SHADE_SPAWN = builder.comment("Whether a shade can spawn when players die")
        .define("shade_spawn", true);
//    SHADE_SPAWN_RATE = builder.comment("Natural spawn rate of shades")
//        .defineInRange("shade_spawn_rate", 20, 0, 50);
    builder.pop();
  }
  
  /**
   * Loads a config file to initialize values early
   * @param spec the spec
   * @param path the exact path to the config
   */
  public static void loadConfig(final ForgeConfigSpec spec, final Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave()
        .writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }
}
