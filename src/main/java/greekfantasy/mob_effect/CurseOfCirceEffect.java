package greekfantasy.mob_effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class CurseOfCirceEffect extends MobEffect {

    protected static final UUID UUID_HEALTH = UUID.fromString("5b73458d-f6f6-465d-8738-6d851e494c53");
    protected static final UUID UUID_ATTACK = UUID.fromString("bc86214e-f422-4b59-912a-73e03090ff30");
    protected static final UUID UUID_KNOCKBACK = UUID.fromString("06be9c8d-d233-4db2-bde8-02375cd96c11");
    protected static final UUID UUID_ATTACK_RANGE = UUID.fromString("a8d14f21-4db7-4a6a-9a92-a7b39b1f1070");
    protected static final UUID UUID_REACH_DISTANCE = UUID.fromString("c85e7079-e9f1-40e8-970e-bf327c23251a");
    protected static final UUID SWIM_SPEED_MODIFIER = UUID.fromString("082e253f-1347-4c50-b30f-f846192dc2e1");

    public static final double HEALTH_MODIFIER = -10.0D;

    public CurseOfCirceEffect() {
        super(MobEffectCategory.HARMFUL, 0xF19E98);
        this.addAttributeModifier(Attributes.MAX_HEALTH, UUID_HEALTH.toString(), HEALTH_MODIFIER, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID_ATTACK.toString(), -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, UUID_KNOCKBACK.toString(), -0.8D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(ForgeMod.ATTACK_RANGE.get(), UUID_ATTACK_RANGE.toString(), -1.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(ForgeMod.REACH_DISTANCE.get(), UUID_REACH_DISTANCE.toString(), -1.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_MODIFIER.toString(), -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
