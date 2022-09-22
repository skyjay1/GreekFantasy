package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class OverstepEnchantment extends Enchantment {

    public OverstepEnchantment(final Enchantment.Rarity rarity) {
        super(rarity, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinCost(int level) {
        return 10;
    }

    @Override
    public int getMaxCost(int level) {
        return 20;
    }

    @Override
    public boolean isTreasureOnly() {
        return GreekFantasy.CONFIG.isOverstepEnabled();
    }

    @Override
    public boolean isTradeable() {
        return GreekFantasy.CONFIG.OVERSTEP_TRADEABLE.get();
    }

    @Override
    public boolean isDiscoverable() {
        return GreekFantasy.CONFIG.isOverstepEnabled();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return GreekFantasy.CONFIG.isOverstepEnabled() && stack.canApplyAtEnchantingTable(this);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return GreekFantasy.CONFIG.isOverstepEnabled();
    }
}
