package greekfantasy.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.UUID;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class KnifeItem extends SwordItem {

    protected static final UUID BASE_ATTACK_RANGE_UUID = UUID.fromString("be98d72c-b546-49b1-b808-58ba80bb7f87");
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final float attackSpeed;
    private final float attackRange;

    /**
     * @param iItemTier        the item tier, used for durability, etc.
     * @param baseAttackDamage the attack damage modifier
     * @param attackSpeed      the attack speed modifier
     * @param attackRange      the attack range modifier
     * @param properties       the item properties
     */
    public KnifeItem(Tier iItemTier, int baseAttackDamage, float attackSpeed, float attackRange, Properties properties) {
        super(iItemTier, baseAttackDamage, attackSpeed, properties);
        this.attackSpeed = attackSpeed;
        this.attackRange = attackRange;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        // lazy-load modifier map (because forge attributes are not available sooner)
        if (null == this.defaultModifiers) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.getDamage(), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(BASE_ATTACK_RANGE_UUID, "Weapon modifier", attackRange, AttributeModifier.Operation.ADDITION));
            this.defaultModifiers = builder.build();
        }
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }
}
