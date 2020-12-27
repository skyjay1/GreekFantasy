package greekfantasy.favor;

import java.util.Random;

import greekfantasy.favor.FavorEffects.IFavorEffect;

/**
 * This class holds an IFavorEffect and a weight for that effect
 */
public class WeightedFavorEffect {
  public static final WeightedFavorEffect EMPTY = new WeightedFavorEffect(FavorEffects.NONE, 0);
  public final IFavorEffect favorEffect;
  public final int favorWeight;
  
  public WeightedFavorEffect(final IFavorEffect effect, final int weight) {
    favorEffect = effect;
    favorWeight = weight;
  }
  
  /**
   * @param totalWeight the total weight of effects in the pool
   * @param rand a Random instance
   * @return true if this weighted favor effect should be chosen
   */
  public boolean choose(final int totalWeight, final Random rand) {
    return rand.nextInt(Math.max(1, totalWeight)) <= favorWeight;
  }
}
