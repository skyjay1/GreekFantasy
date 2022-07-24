package greekfantasy.mob_effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class SlowSwimEffect extends MobEffect {

    protected static final UUID SWIM_SPEED_MODIFIER = UUID.fromString("db06b6a3-4fab-41ea-9b05-eaa6b43aa47f");

    public SlowSwimEffect() {
        super(MobEffectCategory.HARMFUL, 0x4c423f);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_MODIFIER.toString(), -0.15000000596046448D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
