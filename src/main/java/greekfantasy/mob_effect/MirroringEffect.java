package greekfantasy.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * This effect does nothing except indicate to the Gorgon that the player cannot be petrified.
 */
public class MirroringEffect extends MobEffect {

    public MirroringEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xD0D0D0);
    }
}
