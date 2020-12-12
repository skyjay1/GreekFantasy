package greekfantasy.effect;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class SwineEffect extends Effect {
  
  protected static final UUID UUID_SWINE = UUID.fromString("5b73458d-f6f6-465d-8738-6d851e494c53");

  public SwineEffect() {
    super(EffectType.HARMFUL, 0xF926FF);
    this.addAttributesModifier(Attributes.MAX_HEALTH, UUID_SWINE.toString(), -10.0D, AttributeModifier.Operation.ADDITION);
  }
}
