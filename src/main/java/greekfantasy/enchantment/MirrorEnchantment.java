package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class MirrorEnchantment extends Enchantment {

  public MirrorEnchantment(Rarity rarityIn) {
    super(rarityIn, EnchantmentType.WEAPON, new EquipmentSlotType[] { EquipmentSlotType.OFFHAND });
  }
  
  private boolean isApplicableItem(final ItemStack item) {
    return item.getItem() instanceof ShieldItem;
  }
  
  @Override 
  public int getMinEnchantability(int level) { return 10; }
  @Override
  public int getMaxEnchantability(int level) { return 20; }
  @Override
  public boolean isTreasureEnchantment() { return GreekFantasy.CONFIG.isMirrorEnabled(); }
  @Override
  public boolean canVillagerTrade() { return GreekFantasy.CONFIG.isMirrorEnabled(); }
  @Override
  public boolean canGenerateInLoot() { return GreekFantasy.CONFIG.isMirrorEnabled(); }
  @Override
  public int getMaxLevel() { return 1; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { 
    return GreekFantasy.CONFIG.isMirrorEnabled() && isApplicableItem(stack) && super.canApplyAtEnchantingTable(stack); 
  }
}
