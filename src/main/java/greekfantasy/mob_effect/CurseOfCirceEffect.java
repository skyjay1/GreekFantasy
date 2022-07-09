package greekfantasy.mob_effect;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class CurseOfCirceEffect extends MobEffect {

    protected static final UUID UUID_HEALTH = UUID.fromString("5b73458d-f6f6-465d-8738-6d851e494c53");
    protected static final UUID UUID_ATTACK = UUID.fromString("bc86214e-f422-4b59-912a-73e03090ff30");
    protected static final UUID UUID_KNOCKBACK = UUID.fromString("06be9c8d-d233-4db2-bde8-02375cd96c11");

    public CurseOfCirceEffect() {
        super(MobEffectCategory.HARMFUL, 0xF19E98);
        this.addAttributeModifier(Attributes.MAX_HEALTH, UUID_HEALTH.toString(), -10.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID_ATTACK.toString(), -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, UUID_KNOCKBACK.toString(), -0.8D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
        super.removeAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
        entityLivingBaseIn.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F,
                0.9F + entityLivingBaseIn.getRandom().nextFloat() * 0.2F);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
        super.addAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
        entityLivingBaseIn.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F,
                0.9F + entityLivingBaseIn.getRandom().nextFloat() * 0.2F);
        if (entityLivingBaseIn instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
    }
}
