package greekfantasy.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

/**
 * This effect does nothing except indicate to the Gorgon that the player cannot be petrified.
 */
public class MirrorEffect extends Effect {

    public MirrorEffect() {
        super(EffectType.BENEFICIAL, 0xD0D0D0);
    }
}
