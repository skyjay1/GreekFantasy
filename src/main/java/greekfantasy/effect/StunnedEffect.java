package greekfantasy.effect;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class StunnedEffect extends Effect {
  public StunnedEffect(final String uuid) {
    super(EffectType.HARMFUL, 0xC0C0C0);
    this.addAttributesModifier(Attributes.MOVEMENT_SPEED, uuid, -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
  }
}
