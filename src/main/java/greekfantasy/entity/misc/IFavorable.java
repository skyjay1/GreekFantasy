package greekfantasy.entity.misc;

import java.util.Map;

import greekfantasy.util.FavorRange;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface IFavorable {
  
  // Commonly used keys defined here for convenience
  public static final String CAN_ATTACK = "CanAttack";
  public static final String CAN_TRADE = "CanTrade";
  
  /** @return the FavorRange map **/
  Map<String, FavorRange> getFavorRangeMap();
  
  /**
   * @param key the String key
   * @return the FavorRange stored at the given key, or EMPTY if none is found
   */
  default FavorRange getFavorRange(final String key) {
    return getFavorRangeMap().getOrDefault(key, FavorRange.EMPTY);
  }
  
  /**
   * Adds a FavorRange object at the given key
   * @param key the String key
   * @param favorRange the FavorRange instance
   */
  default void addFavorRange(final String key, final FavorRange favorRange) {
    getFavorRangeMap().put(key, favorRange);
  }
  
  /**
   * Tests whether the given player is in the favor range
   * stored with the given key. Calling this client-side
   * will always result in FALSE
   * @param player the player
   * @param key the favor range key
   * @return true if the player is within the given favor range
   */
  default boolean test(final PlayerEntity player, final String key) {
    return getFavorRange(key).isInFavorRange(player);
  }
  
  /**
   * Tests whether the given entity is a player in the favor range
   * stored with the given key. Calling this client-side
   * will always result in FALSE
   * @param entity the entity
   * @param key the favor range key
   * @return true if the entity is a player and within the given favor range
   * @see #test(PlayerEntity, String)
   */
  default boolean test(final LivingEntity entity, final String key) {
    return entity instanceof PlayerEntity && test((PlayerEntity)entity, key);
  }
  
  /**
   * Tests whether the given entity is a player in the favor range
   * stored in the {@link #CAN_ATTACK} key. Calling this client-side
   * will always result in FALSE
   * @param entity the entity
   * @return true if the entity is a player and has a key for {@link #CAN_ATTACK} and is in range for the favor range
   */
  default boolean targetFavorAttackable(final LivingEntity entity) {
    return entity instanceof PlayerEntity && getFavorRangeMap().containsKey(CAN_ATTACK) && test((PlayerEntity)entity, CAN_ATTACK);
  }
}
