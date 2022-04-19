package greekfantasy.util;

import com.google.common.collect.Lists;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeWhitelistConfig {
    protected final String name;
    protected final ForgeConfigSpec.IntValue spawnChance;
    protected final ForgeConfigSpec.BooleanValue whitelist;
    protected final ConfigValue<List<? extends String>> spawnBiomes;

    /**
     * @param builder     the config spec builder
     * @param nameIn      the name of the config
     * @param weight      the default weight
     * @param isWhitelist when false, treats the biome list as a blacklist instead
     * @param biomes      the biomes to include in the default list
     **/
    public BiomeWhitelistConfig(final ForgeConfigSpec.Builder builder, final String nameIn, final int weight,
                                final boolean isWhitelist, final String... biomes) {
        name = nameIn;
        builder.push(nameIn);
        spawnChance = builder.worldRestart().defineInRange("chance", weight, 0, 1000);
        whitelist = builder.worldRestart().define("whitelist", isWhitelist);
        spawnBiomes = builder.worldRestart().defineList("biome_types", Lists.newArrayList(biomes), o -> o instanceof String);
        builder.pop();
    }

    public int chance() {
        return spawnChance.get();
    }

    public boolean isWhitelist() {
        return whitelist.get();
    }

    public List<? extends String> biomeTypes() {
        return spawnBiomes.get();
    }

    public boolean canSpawnInBiome(final RegistryKey<Biome> biomeKey) {
        // spawns when whitelist=true and hasBiome=true OR whitelist=false and hasBiome=false
        return isWhitelist() == hasBiome(biomeKey);
    }

    public boolean hasBiome(final RegistryKey<Biome> biome) {
        final Set<String> types = BiomeDictionary.getTypes(biome).stream().map(t -> t.getName()).collect(Collectors.toSet());
        // check each string in the whitelist
        for (final String whitelistName : spawnBiomes.get()) {
            // if the whitelistName is a biome registry name, compare against the given biome
            if (!whitelistName.isEmpty() && whitelistName.contains(":") && biome.location().toString().equals(whitelistName)) {
                return true;
            }
            // if the whitelistName is a biome type, check if the given biome contains that type
            if (types.contains(whitelistName)) {
                return true;
            }
        }
        // the above tests failed, so the biome is not in this list
        return false;
    }
}
