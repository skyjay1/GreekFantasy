package greekfantasy.effect;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvents;

public class SwineEffect extends Effect {
  
  protected static final UUID UUID_HEALTH = UUID.fromString("5b73458d-f6f6-465d-8738-6d851e494c53");
  protected static final UUID UUID_ATTACK = UUID.fromString("bc86214e-f422-4b59-912a-73e03090ff30");
  protected static final UUID UUID_KNOCKBACK = UUID.fromString("06be9c8d-d233-4db2-bde8-02375cd96c11");

  public SwineEffect() {
    super(EffectType.HARMFUL, 0xF19E98);
    this.addAttributesModifier(Attributes.MAX_HEALTH, UUID_HEALTH.toString(), -10.0D, AttributeModifier.Operation.ADDITION);
    this.addAttributesModifier(Attributes.ATTACK_DAMAGE, UUID_ATTACK.toString(), -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    this.addAttributesModifier(Attributes.ATTACK_KNOCKBACK, UUID_KNOCKBACK.toString(), -0.8D, AttributeModifier.Operation.MULTIPLY_TOTAL);
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
    if(entityLivingBaseIn instanceof MobEntity) {
      ((MobEntity)entityLivingBaseIn).enablePersistence();
    }
  }
}