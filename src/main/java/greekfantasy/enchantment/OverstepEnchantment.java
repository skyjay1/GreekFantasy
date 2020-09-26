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
  public int getMinEnchantability(int level) { return 15; }
  @Override
  public int getMaxEnchantability(int level) { return 30; }
  @Override
  public boolean isTreasureEnchantment() { return GreekFantasy.CONFIG.isOverstepEnabled(); }
  @Override
  public boolean canVillagerTrade() { return false; }
  @Override
  public boolean canGenerateInLoot() { return GreekFantasy.CONFIG.isOverstepEnabled(); }
  @Override
  public int getMaxLevel() { return 1; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return GreekFantasy.CONFIG.isOverstepEnabled() && stack.canApplyAtEnchantingTable(this); }
  @Override
  public boolean isAllowedOnBooks() { return GreekFantasy.CONFIG.isOverstepEnabled(); }
}
