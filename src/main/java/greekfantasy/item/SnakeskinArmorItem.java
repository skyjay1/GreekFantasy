package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SnakeskinArmorItem extends ArmorItem {

    private static final String TEXTURE_1 = GreekFantasy.MODID + ":textures/models/armor/snakeskin_layer_1.png";
    private static final String TEXTURE_2 = GreekFantasy.MODID + ":textures/models/armor/snakeskin_layer_2.png";


    public SnakeskinArmorItem(final ArmorMaterial armorMaterial, final EquipmentSlot slot, Properties builderIn) {
        super(armorMaterial, slot, builderIn);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        // add the item to the group with enchantment already applied
        if (this.allowdedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            if (GreekFantasy.CONFIG.isPoisonEnabled()) {
                // TODO poison enchantment
                //stack.enchant(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, 1);
            }
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        // add Poison enchantment if not present
        /*if (GreekFantasy.CONFIG.isPoisonEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, 1);
        }*/
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GreekFantasy.CONFIG.isPoisonEnabled() ? stack.getEnchantmentTags().size() > 1 : super.isFoil(stack);
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void inventoryTick(final ItemStack stack, final Level worldIn, final Entity entityIn,
                              final int itemSlot, final boolean isSelected) {
        // add Poison enchantment if not present
        // TODO
        /*if (GreekFantasy.CONFIG.isPoisonEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, 1);
        }*/
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return (slot == EquipmentSlot.LEGS) ? TEXTURE_2 : TEXTURE_1;
    }
}
