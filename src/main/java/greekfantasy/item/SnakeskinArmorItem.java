package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
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

    private static final String TEXTURE_1 = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/snakeskin_layer_1.png").toString();
    private static final String TEXTURE_2 = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/snakeskin_layer_2.png").toString();


    public SnakeskinArmorItem(final ArmorMaterial armorMaterial, final EquipmentSlot slot, Properties builderIn) {
        super(armorMaterial, slot, builderIn);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        // add the item to the group with enchantment already applied
        if (this.allowedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            if (GreekFantasy.CONFIG.isPoisoningEnabled()) {
                stack.enchant(GFRegistry.EnchantmentReg.POISONING.get(), 1);
            }
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        // add Poison enchantment if not present
        if (GreekFantasy.CONFIG.isPoisoningEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.POISONING.get(), stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.POISONING.get(), 1);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GreekFantasy.CONFIG.isPoisoningEnabled() ? stack.getEnchantmentTags().size() > 1 : super.isFoil(stack);
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void inventoryTick(final ItemStack stack, final Level worldIn, final Entity entityIn,
                              final int itemSlot, final boolean isSelected) {
        // add Poison enchantment if not present
        if (GreekFantasy.CONFIG.isPoisoningEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.POISONING.get(), stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.POISONING.get(), 1);
        }
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return (slot == EquipmentSlot.LEGS) ? TEXTURE_2 : TEXTURE_1;
    }
}
