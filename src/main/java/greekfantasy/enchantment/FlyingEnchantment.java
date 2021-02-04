package greekfantasy.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class FlyingEnchantment extends Enchantment {

  public FlyingEnchantment(Rarity rarityIn) {
    super(rarityIn, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[] { EquipmentSlotType.FEET });
  }
  
  @Override
  public ITextComponent getDisplayName(int level) {
    return ((IFormattableTextComponent)super.getDisplayName(level)).mergeStyle(TextFormatting.GOLD);
  }
  
  @Override 
  public int getMinEnchantability(int level) { return 999; }
  @Override
  public int getMaxEnchantability(int level) { return 999; }
  @Override
  public boolean isTreasureEnchantment() { return false; }
  @Override
  public boolean canVillagerTrade() { return false; }
  @Override
  public boolean canGenerateInLoot() { return false; }
  @Override
  public int getMaxLevel() { return 1; }
  @Override
  public boolean isAllowedOnBooks() { return false; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }
}
