package greekfantasy.enchantment;


import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.awt.*;
import java.util.function.Predicate;

public class DeityEnchantment extends Enchantment {

    protected final Predicate<ItemStack> applicable;
    protected final int maxLevel;

    public DeityEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot slotIn,
                            int maxLevel, Predicate<ItemStack> applicablePredicate) {
        super(rarity, category, new EquipmentSlot[]{slotIn});
        this.applicable = applicablePredicate;
        this.maxLevel = Math.max(1, maxLevel);
    }


    @Override
    public int getMinCost(int level) {
        return 999;
    }

    @Override
    public int getMaxCost(int level) {
        return 999;
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return !stack.isEmpty() && applicable.test(stack);
    }
}
