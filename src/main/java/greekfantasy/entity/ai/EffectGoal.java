package greekfantasy.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class EffectGoal<T extends LivingEntity> extends Goal {

    protected final T entity;
    protected final Supplier<Effect> effect;
    protected final int minLength;
    protected final int maxLength;
    protected final int minAmplifier;
    protected final int maxAmplifier;
    protected final Predicate<T> shouldApply;

    public EffectGoal(final T entityIn, final Supplier<Effect> supplierIn, final int minLen,
                      final int maxLen, final int minAmp, final int maxAmp, final Predicate<T> applyWhen) {
        entity = entityIn;
        effect = supplierIn;
        minLength = Math.min(minLen, maxLen);
        maxLength = Math.max(minLen, maxLen);
        minAmplifier = Math.min(minAmp, maxAmp);
        maxAmplifier = Math.max(minAmp, maxAmp);
        shouldApply = applyWhen;
    }

    @Override
    public boolean canUse() {
        return shouldApply.test(entity);
    }

    @Override
    public void start() {
        final int len = minLength != maxLength ? minLength + entity.getRandom().nextInt(maxLength - minLength + 1) : minLength;
        final int amp = minAmplifier != maxAmplifier ? minAmplifier + entity.getRandom().nextInt(maxAmplifier - minAmplifier + 1) : minAmplifier;
        entity.addEffect(new EffectInstance(effect.get(), len, amp));
    }

    /**
     * Creates a predicate that uses a random number check to determine
     * whether to apply.
     *
     * @param <E>    the entity
     * @param chance a 1 in x chance to pass
     * @return a predicate that passes approximately every {chance} ticks
     */
    public static <E extends LivingEntity> Predicate<E> randomPredicate(final int chance) {
        final int c = Math.max(1, chance);
        return e -> e.getRandom().nextInt(c) == 0;
    }

    /**
     * Creates a predicate that checks if the entity does not have the given effect.
     * Useful for having an effect constantly be renewed.
     *
     * @param <E>    the entity
     * @param effect the effect
     * @return a predicate that passes when the entity does not have the specified effect.
     */
    public static <E extends LivingEntity> Predicate<E> hasNoEffectPredicate(final Effect effect) {
        return e -> e.getEffect(effect) == null;
    }
}
