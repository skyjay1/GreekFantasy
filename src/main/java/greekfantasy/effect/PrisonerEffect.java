package greekfantasy.effect;

import greekfantasy.GreekFantasy;
import greekfantasy.integration.RGCompat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.world.World;

/**
 * Indicates the the player should not use portals while in the Nether.
 * The actual code for this is executed in the player tick
 */
public class PrisonerEffect extends Effect {

    public PrisonerEffect() {
        super(EffectType.NEUTRAL, 0x9e1739);
    }

    public void applyEffectTick(LivingEntity entity, int duration) {
        // remove when not in nether
        if(entity.level.dimension() != World.NETHER) {
            entity.removeEffect(this);
        }
        // remove when RPG Gods installed and high favor with Hades
        if(GreekFantasy.isRGLoaded() && entity instanceof PlayerEntity
                && RGCompat.getInstance().canRemovePrisonerEffect((PlayerEntity) entity)) {
            entity.removeEffect(this);
        }
    }
}
