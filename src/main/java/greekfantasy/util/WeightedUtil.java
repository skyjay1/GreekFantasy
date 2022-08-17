package greekfantasy.util;

import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;

import javax.annotation.Nullable;
import java.util.Collection;

public final class WeightedUtil {

    /**
     * Selects a weighted item from a collection
     *
     * @param collection a collection of weighted entries, each with weight greater than or equal to one
     * @param random     the random instance
     * @param <T>        a weighted entry type
     * @return a weighted entry from the collection, or null if the collection was empty
     */
    @Nullable
    public static <T extends WeightedEntry> T sample(final Collection<T> collection, final RandomSource random) {
        // do not evaluate empty collections
        if (collection.size() == 0) {
            return null;
        }
        // return first element of single-element collections
        if (collection.size() == 1) {
            return collection.iterator().next();
        }
        // add all the weights
        int sum = 0;
        for (T instance : collection) {
            sum += instance.getWeight().asInt();
        }
        // choose a random number from 1 to {sum}
        int target = random.nextIntBetweenInclusive(1, sum);
        int w;
        // iterate through collection until weight is found that is greater than or equal to the random target
        for (T instance : collection) {
            w = instance.getWeight().asInt();
            if (target <= w) {
                return instance;
            } else {
                target -= w;
            }
        }
        return null;
    }
}
