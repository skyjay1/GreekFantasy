package greekfantasy.util;

import java.util.List;
import java.util.Set;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class BiomeWhitelistConfig {
  protected final String name;
  protected final ForgeConfigSpec.IntValue spawnChance;
  protected final ForgeConfigSpec.BooleanValue whitelist;
  protected final ConfigValue<List<? extends String>> spawnBiomes;
  
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
    spawnBiomes = builder.worldRestart().defineList("biome_types", biomes, o -> o instanceof String);
    builder.pop();
  }
    
  public int chance() { return spawnChance.get(); }
  
  public boolean isWhitelist() { return whitelist.get(); }
  
  public List<? extends String> biomeTypes() { return spawnBiomes.get(); }
  
  public boolean canSpawnInBiome(final RegistryKey<Biome> biomeKey) {
    // spawns when whitelist=true and hasBiome=true OR whitelist=false and hasBiome=false
    return isWhitelist() == hasBiome(biomeKey);
  }
  
  public boolean hasBiome(final RegistryKey<Biome> biome) { 
//    if(biomeKeys.isEmpty()) {
//      populateBiomeList();
//    }
    // check each entry to see if it matches one of the types
    final List<? extends String> spawnBiomeList = spawnBiomes.get();
    final Set<Type> types = BiomeDictionary.getTypes(biome);
    for(final BiomeDictionary.Type t : types) {
      if(spawnBiomeList.contains(t.getName())) {
        return true;
      }
    }
    return false;
  }
//  
//  public void populateBiomeList() {
//    biomeKeys.clear();
//    for(final String s : biomeStrings()) {
//      final ResourceLocation r = new ResourceLocation(s);
//      if(ForgeRegistries.BIOMES.containsKey(r)) {
//        biomeKeys.add(s);
//      } else {
//        GreekFantasy.LOGGER.error("Could not parse biome type '" + s + "' from config for '" + name + "'");
//      }
//    }
//  }
}
