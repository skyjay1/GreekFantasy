package greekfantasy.effect;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvents;

public class SwineEffect extends Effect {
  
  protected static final UUID UUID_SWINE = UUID.fromString("5b73458d-f6f6-465d-8738-6d851e494c53");

  public SwineEffect() {
    super(EffectType.HARMFUL, 0xF926FF);
    this.addAttributesModifier(Attributes.MAX_HEALTH, UUID_SWINE.toString(), -10.0D, AttributeModifier.Operation.ADDITION);
  }
  
  public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
    super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    entityLivingBaseIn.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F,
        0.9F + entityLivingBaseIn.getRNG().nextFloat() * 0.2F);
  }

  public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
    super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    entityLivingBaseIn.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F,
        0.9F + entityLivingBaseIn.getRNG().nextFloat() * 0.2F);
  }
}