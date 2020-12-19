package greekfantasy.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;

public class IvorySwordItem extends SwordItem {

  public static final UUID ATTACK_KNOCKBACK_MODIFIER = UUID.fromString("dd208092-978f-4e55-b9b8-7e65c7c760e6");
  public static final double ATTACK_KNOCKBACK_AMOUNT = 1.45D;
  protected final Multimap<Attribute, AttributeModifier> attributeModifierMap;

  public IvorySwordItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Item.Properties properties) {
    super(tier, attackDamageIn, attackSpeedIn, properties);
    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamageIn + tier.getAttackDamage(), AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeedIn, AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_MODIFIER, "Weapon modifier", ATTACK_KNOCKBACK_AMOUNT, AttributeModifier.Operation.ADDITION));
    this.attributeModifierMap = builder.build();
  }

  /**
   * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
   */
  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlotType equipmentSlot, final ItemStack stack) {
    return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifierMap : super.getAttributeModifiers(equipmentSlot, stack);
  }
  
  /**
   * Return whether this item is repairable in an anvil.
   */
  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
     return repair.getItem() == Items.BONE;
  }

  /**
   * Return the enchantability factor of the item, most of the time is based on material.
   */
  @Override
  public int getItemEnchantability() {
     return ItemTier.STONE.getEnchantability();
  }
}
