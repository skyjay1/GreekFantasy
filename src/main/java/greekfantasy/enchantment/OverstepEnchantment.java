package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class OverstepEnchantment extends Enchantment {

  public OverstepEnchantment(final Enchantment.Rarity rarity) {
    super(rarity, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[] { EquipmentSlotType.FEET });
  }
  
  @Override 
  public int getMinCost(int level) { return 10; }
  @Override
  public int getMaxCost(int level) { return 20; }
  @Override
  public boolean isTreasureOnly() { return GreekFantasy.CONFIG.isOverstepEnabled(); }
  @Override
  public boolean isTradeable() { return false; }
  @Override
  public boolean isDiscoverable() { return GreekFantasy.CONFIG.isOverstepEnabled(); }
  @Override
  public int getMaxLevel() { return 1; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return GreekFantasy.CONFIG.isOverstepEnabled() && stack.canApplyAtEnchantingTable(this); }
  @Override
  public boolean isAllowedOnBooks() { return GreekFantasy.CONFIG.isOverstepEnabled(); }
}
