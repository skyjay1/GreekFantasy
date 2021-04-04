package greekfantasy.deity.favor_effect;

import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor.IFavor;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Used in the FavorConfiguration to pair an IDeity and SpecialFavorEffect
 */
public class ConfiguredSpecialFavorEffect {
  
  private final IDeity deity;
  private final SpecialFavorEffect effect;
  
  public ConfiguredSpecialFavorEffect(IDeity deity, SpecialFavorEffect effect) {
    super();
    this.deity = deity;
    this.effect = effect;
  }
  
  public IDeity getDeity() { return deity; }
  
  public SpecialFavorEffect getEffect() { return effect; }

  /**
   * @param player the player
   * @param favor the player's favor
   * @return true if this special effect can apply to the given player
   */
  public boolean canApply(final PlayerEntity player, final IFavor favor) {
    return effect.canApply(player, deity, favor);
  }
  
  /**
   * @param player the player
   * @param favor the player's favor 
   * @return true if the player's favor matches this favor range
   * @see SpecialFavorEffect#isInFavorRange(PlayerEntity, IFavor, IDeity)
   */
  public boolean isInFavorRange(final PlayerEntity player, final IFavor favor) {
    return effect.isInFavorRange(player, deity, favor);
  }
  
  @Override
  public String toString() {
    return "CSFE deity[" + deity.getName().toString() + "] " + effect.toString();
  }
  
}
