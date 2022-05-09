package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SnakeskinArmorItem extends ArmorItem {
    protected static final IArmorMaterial MATERIAL = new SnakeskinArmorMaterial();

    private static final String TEXTURE_1 = GreekFantasy.MODID + ":textures/models/armor/snakeskin_layer_1.png";
    private static final String TEXTURE_2 = GreekFantasy.MODID + ":textures/models/armor/snakeskin_layer_2.png";


    public SnakeskinArmorItem(final EquipmentSlotType slot, Properties builderIn) {
        super(MATERIAL, slot, builderIn);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        // add the item to the group with enchantment already applied
        if (this.allowdedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            if (GreekFantasy.CONFIG.isPoisonEnabled()) {
                stack.enchant(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, 1);
            }
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        // add Poison enchantment if not present
        if (GreekFantasy.CONFIG.isPoisonEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, 1);
        }
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
    public void inventoryTick(final ItemStack stack, final World worldIn, final Entity entityIn,
                              final int itemSlot, final boolean isSelected) {
        // add Poison enchantment if not present
        if (GreekFantasy.CONFIG.isPoisonEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.POISON_ENCHANTMENT, 1);
        }
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that
     * should be use for the currently equipped item. This will only be called on
     * instances of ItemArmor.
     * <p>
     * Returning null from this function will use the default value.
     *
     * @param stack  ItemStack for the equipped armor
     * @param entity The entity wearing the armor
     * @param slot   The slot the armor is in
     * @param type   The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return (slot == EquipmentSlotType.LEGS) ? TEXTURE_2 : TEXTURE_1;
    }

    public static class SnakeskinArmorMaterial implements IArmorMaterial {
        private static final String NAME = "snakeskin";

        @Override
        public int getDefenseForSlot(EquipmentSlotType slot) {
            return ArmorMaterial.CHAIN.getDefenseForSlot(slot);
        }

        @Override
        public int getDurabilityForSlot(EquipmentSlotType slot) {
            return ArmorMaterial.IRON.getDurabilityForSlot(slot);
        }

        @Override
        public int getEnchantmentValue() {
            return ArmorMaterial.IRON.getEnchantmentValue();
        }

        @Override
        public float getKnockbackResistance() {
            return ArmorMaterial.LEATHER.getKnockbackResistance();
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(GFRegistry.ItemReg.TOUGH_SNAKESKIN);
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_TURTLE;
        }

        @Override
        public float getToughness() {
            return ArmorMaterial.IRON.getToughness();
        }
    }
}
