package greekfantasy.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import java.util.UUID;

public class IvorySwordItem extends SwordItem {

    public static final UUID ATTACK_KNOCKBACK_MODIFIER = UUID.fromString("dd208092-978f-4e55-b9b8-7e65c7c760e6");
    public static final double ATTACK_KNOCKBACK_AMOUNT = 1.25D;
    protected final Multimap<Attribute, AttributeModifier> attributeModifierMap;

    public IvorySwordItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties properties) {
        super(tier, attackDamageIn, attackSpeedIn, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamageIn + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeedIn, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_MODIFIER, "Weapon modifier", ATTACK_KNOCKBACK_AMOUNT, AttributeModifier.Operation.ADDITION));
        this.attributeModifierMap = builder.build();
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise the damage
     * on the stack.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        // apply knockback to the entity that was hit
        if (attacker instanceof Player player && !player.getCooldowns().isOnCooldown(this)) {
            // determine knockback amount
            float knockback = 0.0F;
            for (final AttributeModifier modifier : this.getAttributeModifiers(EquipmentSlot.MAINHAND, stack).get(Attributes.ATTACK_KNOCKBACK)) {
                knockback += modifier.getAmount();
            }
            target.knockback(knockback * 0.75F, Mth.sin(attacker.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(attacker.getYRot() * ((float) Math.PI / 180F)));
        }
        return true;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlot equipmentSlot, final ItemStack stack) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.attributeModifierMap : super.getAttributeModifiers(equipmentSlot, stack);
    }
}
