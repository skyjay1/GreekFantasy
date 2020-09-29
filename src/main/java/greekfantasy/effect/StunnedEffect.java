package greekfantasy.effect;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class StunnedEffect extends Effect {
  
  protected static final UUID UUID_STUN = UUID.fromString("ef99fb38-38d1-4703-9607-fabea29c0e6e");
  
  public StunnedEffect() {
    super(EffectType.HARMFUL, 0xC0C0C0);
    this.addAttributesModifier(Attributes.MOVEMENT_SPEED, UUID_STUN.toString(), -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
  }
}
