package greekfantasy.item;

import com.google.common.collect.Multimap;
import greekfantasy.GreekFantasy;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class NemeanLionHideItem extends ArmorItem {
    protected static final IArmorMaterial MATERIAL = new NemeanLionHideArmorMaterial();

    private static final String TEXTURE = GreekFantasy.MODID + ":textures/models/armor/nemean_lion_hide_layer_1.png";

    public static final float IMMUNITY_CHANCE = 0.56F;

    public NemeanLionHideItem(Properties builderIn) {
        super(MATERIAL, EquipmentSlotType.HEAD, builderIn);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlotType equipmentSlot, final ItemStack stack) {
        return super.getAttributeModifiers(equipmentSlot, stack);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("enchantment.minecraft.projectile_protection").withStyle(TextFormatting.GRAY));
    }

    public static float getProjectileImmunityChance() {
        return IMMUNITY_CHANCE;
    }

    public static class NemeanLionHideArmorMaterial implements IArmorMaterial {
        private static final String NAME = "nemean_lion_hide";

        @Override
        public int getDefenseForSlot(EquipmentSlotType arg0) {
            return 4;
        }

        @Override
        public int getDurabilityForSlot(EquipmentSlotType arg0) {
            return 985;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.5F;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public float getToughness() {
            return 2.0F;
        }
    }

}
