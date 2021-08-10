package greekfantasy.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

/**
 * Indicates the the player should not use portals while in the Nether.
 * The actual code for this is executed in the player tick
 * @see greekfantasy.deity.favor.FavorManager#onPlayerTick(PlayerEntity, IFavor)
 */
public class PrisonerEffect extends Effect {

  public PrisonerEffect() {
    super(EffectType.NEUTRAL, 0x9e1739);
  }
}
