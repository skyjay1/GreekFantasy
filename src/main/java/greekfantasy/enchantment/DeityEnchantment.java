package greekfantasy.enchantment;

import java.util.function.Predicate;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import net.minecraft.enchantment.Enchantment.Rarity;

public class DeityEnchantment extends Enchantment {
  
  protected final Predicate<ItemStack> applicable;
  protected final TextFormatting color;
  protected final int maxLevel;
  
  public DeityEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType slotIn, 
      TextFormatting colorIn, int maxLevelIn, Predicate<ItemStack> applicablePredicate) {
    super(rarityIn, typeIn, new EquipmentSlotType[] { slotIn });
    color = colorIn;
    applicable = applicablePredicate;
    maxLevel = Math.max(1, maxLevelIn);
  }
  
  @Override
  public ITextComponent getFullname(int level) {
    return ((IFormattableTextComponent)super.getFullname(level)).withStyle(color);
  }
  
  @Override 
  public int getMinCost(int level) { return 999; }
  @Override
  public int getMaxCost(int level) { return 999; }
  @Override
  public boolean isTreasureOnly() { return false; }
  @Override
  public boolean isTradeable() { return false; }
  @Override
  public boolean isDiscoverable() { return false; }
  @Override
  public int getMaxLevel() { return maxLevel; }
  @Override
  public boolean isAllowedOnBooks() { return false; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return !stack.isEmpty() && applicable.test(stack); }
}
