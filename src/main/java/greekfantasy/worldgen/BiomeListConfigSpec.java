package greekfantasy.worldgen;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeListConfigSpec {
    protected final String name;
    protected final ForgeConfigSpec.IntValue weight;
    protected final ForgeConfigSpec.BooleanValue isWhitelist;
    protected final ConfigValue<List<? extends String>> listSpec;

    /**
     * @param builder     the config spec builder
     * @param nameIn      the name of the config
     * @param weight      the default weight
     * @param isWhitelist when false, treats the biome list as a blacklist instead
     * @param biomes      the biomes to include in the default list
     **/
    public BiomeListConfigSpec(final ForgeConfigSpec.Builder builder, final String nameIn, final int weight,
                               final boolean isWhitelist, final String... biomes) {
        name = nameIn;
        builder.push(nameIn);
        this.weight = builder.worldRestart().defineInRange("weight", weight, 0, 1000);
        this.isWhitelist = builder.worldRestart().define("is_whitelist", isWhitelist);
        this.listSpec = builder.worldRestart().defineList("biomes", Lists.newArrayList(biomes), o -> o instanceof String);
        builder.pop();
    }

    public int weight() {
        return weight.get();
    }

    public boolean getIsWhitelist() {
        return isWhitelist.get();
    }

    public List<? extends String> biomeTypes() {
        return listSpec.get();
    }

    public boolean canSpawnInBiome(final ResourceKey<Biome> biomeKey) {
        // spawns when whitelist=true and hasBiome=true OR whitelist=false and hasBiome=false
        return getIsWhitelist() == hasBiome(biomeKey);
    }

    public boolean hasBiome(final ResourceKey<Biome> biome) {
        final Set<String> types = BiomeDictionary.getTypes(biome).stream().map(t -> t.getName()).collect(Collectors.toSet());
        // check each string in the whitelist
        for (final String whitelistName : listSpec.get()) {
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
