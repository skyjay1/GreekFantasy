package greekfantasy.mob_effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class StunnedEffect extends MobEffect {

    protected static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("ef99fb38-38d1-4703-9607-fabea29c0e6e");
    protected static final UUID SWIM_SPEED_MODIFIER = UUID.fromString("117c5495-662a-470c-a3b4-1c7989c9220b");

    public StunnedEffect() {
        super(MobEffectCategory.HARMFUL, 0xC0C0C0);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER.toString(), -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_MODIFIER.toString(), -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
