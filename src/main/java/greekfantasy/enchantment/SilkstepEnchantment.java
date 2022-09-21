package greekfantasy.enchantment;


import greekfantasy.GreekFantasy;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SilkstepEnchantment extends Enchantment {

    public SilkstepEnchantment(Rarity rarityIn) {
        super(rarityIn, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinCost(int level) {
        return 5;
    }

    @Override
    public int getMaxCost(int level) {
        return 5;
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return GreekFantasy.CONFIG.SILKSTEP_TRADEABLE.get();
    }

    @Override
    public boolean isDiscoverable() {
        return GreekFantasy.CONFIG.isSilkstepEnabled();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack);
    }
}
