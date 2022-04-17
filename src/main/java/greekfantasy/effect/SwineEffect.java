package greekfantasy.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvents;

import java.util.UUID;

/**
 * Indicates the the entity should appear as a pig.
 * The actual code for this is in the player tick and player render events.
 * This class handles modification of health and attack damage/knockback.
 */
public class SwineEffect extends Effect {

    protected static final UUID UUID_HEALTH = UUID.fromString("5b73458d-f6f6-465d-8738-6d851e494c53");
    protected static final UUID UUID_ATTACK = UUID.fromString("bc86214e-f422-4b59-912a-73e03090ff30");
    protected static final UUID UUID_KNOCKBACK = UUID.fromString("06be9c8d-d233-4db2-bde8-02375cd96c11");

    public SwineEffect() {
        super(EffectType.HARMFUL, 0xF19E98);
        this.addAttributeModifier(Attributes.MAX_HEALTH, UUID_HEALTH.toString(), -10.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID_ATTACK.toString(), -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, UUID_KNOCKBACK.toString(), -0.8D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public void removeAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        super.removeAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
        entityLivingBaseIn.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F,
                0.9F + entityLivingBaseIn.getRandom().nextFloat() * 0.2F);
    }

    public void addAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        super.addAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
        entityLivingBaseIn.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F,
                0.9F + entityLivingBaseIn.getRandom().nextFloat() * 0.2F);
        if (entityLivingBaseIn instanceof MobEntity) {
            ((MobEntity) entityLivingBaseIn).setPersistenceRequired();
        }
    }
}