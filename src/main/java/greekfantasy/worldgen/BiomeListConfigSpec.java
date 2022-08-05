package greekfantasy.worldgen;

import com.google.common.collect.Lists;
import greekfantasy.GFConfig;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeListConfigSpec {
    protected final String name;
    protected final ForgeConfigSpec.IntValue WEIGHT;
    protected int weight;
    protected final ForgeConfigSpec.BooleanValue IS_WHITELIST;
    protected boolean isWhitelist;
    protected final ConfigValue<List<? extends String>> LIST_SPEC;
    protected List<? extends String > list;

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
        this.WEIGHT = builder.worldRestart().defineInRange("weight", weight, 0, 1000);
        this.IS_WHITELIST = builder.worldRestart().define("is_whitelist", isWhitelist);
        this.LIST_SPEC = builder.worldRestart().defineList("biomes", Lists.newArrayList(biomes), o -> o instanceof String);
        builder.pop();
    }

    public void bake() {
        this.weight = WEIGHT.get();
        this.isWhitelist = IS_WHITELIST.get();
        this.list = LIST_SPEC.get();
    }

    public int weight() {
        return this.weight;
    }

    public boolean getIsWhitelist() {
        return this.isWhitelist;
    }

    public List<? extends String> list() {
        return this.list;
    }

    public boolean canSpawnInBiome(final ResourceKey<Biome> biomeKey) {
        // spawns when whitelist=true and hasBiome=true OR whitelist=false and hasBiome=false
        return getIsWhitelist() == hasBiome(biomeKey);
    }

    public boolean hasBiome(final ResourceKey<Biome> biome) {
        final String wild = biome.location().getNamespace() + ":" + GFConfig.WILDCARD;
        // check each string in the whitelist
        for (final String whitelistName : list()) {
            // if the whitelistName contains wildcard, check if mod id matches
            if(whitelistName.equals(wild)) {
                return true;
            }
            // if the whitelistName is a biome registry name, compare against the given biome
            if (whitelistName.contains(":") && !whitelistName.startsWith("#") && biome.location().toString().equals(whitelistName)) {
                return true;
            }
            // if the whitelistName is a biome tag, check if the given biome contains that type
            if (whitelistName.contains(":") && whitelistName.startsWith("#")) {
                ResourceLocation biomeTag = ResourceLocation.tryParse(whitelistName.substring(1));
                if(biomeTag != null) {
                    TagKey<Biome> biomeTagKey = ForgeRegistries.BIOMES.tags().createTagKey(biomeTag);
                    Holder<Biome> biomeHolder = Holder.direct(ForgeRegistries.BIOMES.getValue(biome.location()));
                    return biomeHolder.is(biomeTagKey);
                }
                return true;
            }
        }
        // the above tests failed, so the biome is not in this list
        return false;
    }
}
