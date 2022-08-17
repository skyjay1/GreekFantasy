package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

public class WeightedTemplate extends WeightedEntry.IntrusiveBase {

    public static final Codec<WeightedTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("location").forGetter(WeightedTemplate::getLocation),
            Weight.CODEC.optionalFieldOf("weight", Weight.of(1)).forGetter(WeightedTemplate::getWeight)
    ).apply(instance, WeightedTemplate::new));

    private final ResourceLocation location;

    public WeightedTemplate(ResourceLocation location, Weight weight) {
        super(weight);
        this.location = location;
    }

    /**
     * @return the ResourceLocation ID of the structure template NBT
     */
    public ResourceLocation getLocation() {
        return location;
    }
}
