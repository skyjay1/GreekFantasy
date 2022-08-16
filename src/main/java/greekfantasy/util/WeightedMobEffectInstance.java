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
import net.minecraftforge.registries.ForgeRegistries;

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
}
