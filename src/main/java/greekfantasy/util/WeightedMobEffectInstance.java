package greekfantasy.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import greekfantasy.item.OliveSalveItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public class WeightedMobEffectInstance extends WeightedEntry.IntrusiveBase {

    public static final WeightedMobEffectInstance EMPTY = new WeightedMobEffectInstance(MobEffects.REGENERATION, 0, 0, Weight.of(1));

    public static final String WEIGHT = "weight";

    public static final Codec<WeightedMobEffectInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("mob_effect").forGetter(WeightedMobEffectInstance::getMobEffect),
            Codec.INT.optionalFieldOf("duration", 1).forGetter(WeightedMobEffectInstance::getDuration),
            Codec.INT.optionalFieldOf("amplifier", 1).forGetter(WeightedMobEffectInstance::getAmplifier),
            Weight.CODEC.optionalFieldOf("weight", Weight.of(1)).forGetter(WeightedMobEffectInstance::getWeight)
    ).apply(instance, WeightedMobEffectInstance::new));

    private final MobEffectInstance effectInstance;

    public WeightedMobEffectInstance(final MobEffect effect, int duration, int amplifier, Weight weight) {
        this(new MobEffectInstance(effect, duration, amplifier), weight);
    }

    public WeightedMobEffectInstance(final MobEffectInstance effect, Weight weight) {
        super(weight);
        this.effectInstance = new MobEffectInstance(effect);
    }

    public static WeightedMobEffectInstance fromTag(final CompoundTag tag) {
        int weight = tag.getInt(WEIGHT);
        MobEffectInstance effectInstance = MobEffectInstance.load(tag);
        return new WeightedMobEffectInstance(effectInstance, Weight.of(weight));
    }

    public MobEffect getMobEffect() {
        return effectInstance.getEffect();
    }

    public int getDuration() {
        return effectInstance.getDuration();
    }

    public int getAmplifier() {
        return effectInstance.getAmplifier();
    }

    public MobEffectInstance createMobEffectInstance() {
        return new MobEffectInstance(effectInstance);
    }

    public CompoundTag asTag() {
        CompoundTag effectTag = new CompoundTag();
        effectTag.putByte(OliveSalveItem.KEY_ID, (byte) MobEffect.getId(getMobEffect()));
        effectTag.putByte("Amplifier", (byte) getAmplifier());
        effectTag.putInt("Duration", getDuration());
        effectTag.putInt(WEIGHT, getWeight().asInt());
        return effectTag;
    }

    /**
     * Selects a weighted item from a collection
     * @param collection a collection of weighted entries
     * @param random the random instance
     * @param <T>  a weighted entry type
     * @return a weighted entry from the list, or null if it fails
     */
    @Nullable
    public static <T extends WeightedEntry> T sample(final Collection<T> collection, final RandomSource random) {
        // do not evaluate empty collections
        if(collection.size() == 0) {
            return null;
        }
        // return first element of single-element collections
        if(collection.size() == 1) {
            return collection.iterator().next();
        }
        // add all the weights
        int sum = 0;
        for(T instance : collection) {
            sum += instance.getWeight().asInt();
        }
        // choose a random number from 1 to {sum}
        int target = random.nextIntBetweenInclusive(1, sum);
        int w;
        // iterate through collection until weight is less than or equal to the selected item
        for(T instance : collection) {
            w = instance.getWeight().asInt();
            if(target <= w) {
                return instance;
            } else {
                target -= w;
            }
        }
        return null;
    }
}
