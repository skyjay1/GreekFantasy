package greekfantasy.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Indicates the the player should not use portals while in the Nether.
 * The actual code for this is executed in the player tick
 */
public class PrisonerOfHadesEffect extends MobEffect {

    public PrisonerOfHadesEffect() {
        super(MobEffectCategory.NEUTRAL, 0x9e1739);
    }
}
