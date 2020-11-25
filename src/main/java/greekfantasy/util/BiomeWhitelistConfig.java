package greekfantasy.util;

import java.util.ArrayList;
import java.util.List;

import greekfantasy.GreekFantasy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeWhitelistConfig {
  protected final String name;
  protected final ForgeConfigSpec.IntValue spawnChance;
  protected final ForgeConfigSpec.BooleanValue whitelist;
  protected final ConfigValue<List<? extends String>> spawnBiomes;
  protected final List<String> biomeKeys = new ArrayList<>();
  
  /**
   * @param builder the config spec builder
   * @param nameIn the name of the config
   * @param weight the default weight
   * @param isWhitelist when false, treats the biome list as a blacklist instead
   * @param biomes the biomes to include in the default list
   **/
  public BiomeWhitelistConfig(final ForgeConfigSpec.Builder builder, final String nameIn, final int weight, 
      final boolean isWhitelist, final List<String> biomes) {
    name = nameIn;
    builder.push(nameIn);
    spawnChance = builder.worldRestart().defineInRange("chance", weight, 0, 1000);
    whitelist = builder.worldRestart().define("whitelist", isWhitelist);
    spawnBiomes = builder.worldRestart().defineList("biomes", biomes, o -> o instanceof String);
    builder.pop();
  }
    
  public int chance() { return spawnChance.get(); }
  
  public boolean isWhitelist() { return whitelist.get(); }
  
  public List<? extends String> biomeStrings() { return spawnBiomes.get(); }
  
  public boolean canSpawnInBiome(final String biomeKey) {
    // spawns when whitelist=true and hasBiome=true OR whitelist=false and hasBiome=false
    return isWhitelist() == hasBiome(biomeKey);
  }
  
  public boolean hasBiome(final String biomeKey) { 
    if(biomeKeys.isEmpty()) {
      populateBiomeList();
    }
    return biomeKeys.contains(biomeKey);
  }
  
  public void populateBiomeList() {
    biomeKeys.clear();
    for(final String s : biomeStrings()) {
      final ResourceLocation r = new ResourceLocation(s);
      if(ForgeRegistries.BIOMES.containsKey(r)) {
        biomeKeys.add(s);
      } else {
        GreekFantasy.LOGGER.error("Could not parse biome '" + s + "' from config for '" + name + "'");
      }
    }
  }
}
