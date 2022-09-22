package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ToolActions;

public class MirroringEnchantment extends Enchantment {

    public MirroringEnchantment(Rarity rarityIn) {
        super(rarityIn, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND});
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
        return GreekFantasy.CONFIG.isMirroringEnchantmentEnabled();
    }

    @Override
    public boolean isTradeable() {
        return GreekFantasy.CONFIG.MIRRORING_TRADEABLE.get();
    }

    @Override
    public boolean isDiscoverable() {
        return GreekFantasy.CONFIG.isMirroringEnchantmentEnabled();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return GreekFantasy.CONFIG.isMirroringEnchantmentEnabled();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return GreekFantasy.CONFIG.isMirroringEnchantmentEnabled()
                && stack.canPerformAction(ToolActions.SHIELD_BLOCK)
                && super.canApplyAtEnchantingTable(stack);
    }
}
