package greekfantasy.enchantment;

import greekfantasy.GFRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class FireflashEnchantment extends Enchantment {

  public FireflashEnchantment(Rarity rarityIn) {
    super(rarityIn, EnchantmentType.WEAPON, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
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
  public int getMaxLevel() { return 3; }
  @Override
  public boolean isAllowedOnBooks() { return false; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return stack.getItem() == GFRegistry.THUNDERBOLT; }
}
