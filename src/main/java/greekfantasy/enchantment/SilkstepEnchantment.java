package greekfantasy.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class SilkstepEnchantment extends Enchantment {
  
  public SilkstepEnchantment(Rarity rarityIn) {
    super(rarityIn, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[] { EquipmentSlotType.FEET });
  }
  
  @Override 
  public int getMinEnchantability(int level) { return 5; }
  @Override
  public int getMaxEnchantability(int level) { return 5; }
  @Override
  public boolean isTreasureEnchantment() { return false; }
  @Override
  public boolean canVillagerTrade() { return false; }
  @Override
  public boolean canGenerateInLoot() { return true; }
  @Override
  public int getMaxLevel() { return 1; }
  @Override
  public boolean isAllowedOnBooks() { return true; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return super.canApplyAtEnchantingTable(stack); }
}
