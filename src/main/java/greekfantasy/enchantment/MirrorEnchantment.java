package greekfantasy.enchantment;

import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class MirrorEnchantment extends Enchantment {

    public MirrorEnchantment(Rarity rarityIn) {
        super(rarityIn, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.OFFHAND});
    }

    private boolean isApplicableItem(final ItemStack item) {
        return item.getItem() instanceof ShieldItem;
    }

    @Override
    public ITextComponent getFullname(int level) {
        return ((IFormattableTextComponent) super.getFullname(level)).withStyle(TextFormatting.WHITE);
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
        return GreekFantasy.CONFIG.isMirrorEnabled();
    }

    @Override
    public boolean isTradeable() {
        return GreekFantasy.CONFIG.isMirrorEnabled();
    }

    @Override
    public boolean isDiscoverable() {
        return GreekFantasy.CONFIG.isMirrorEnabled();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return GreekFantasy.CONFIG.isMirrorEnabled();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return GreekFantasy.CONFIG.isMirrorEnabled() && isApplicableItem(stack) && super.canApplyAtEnchantingTable(stack);
    }
}
