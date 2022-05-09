package greekfantasy.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;
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
import java.util.UUID;

public class WingedSandalsItem extends ArmorItem {
    public static final UUID SPEED_MODIFIER = UUID.fromString("58b7ff54-706b-4b0b-80f7-0dce04a673e4");
    protected static final IArmorMaterial MATERIAL = new WingedSandalsArmorMaterial();

    private static final String TEXTURE = GreekFantasy.MODID + ":textures/models/armor/winged_layer_2.png";
    @OnlyIn(Dist.CLIENT)
    private greekfantasy.client.render.model.armor.WingedSandalsModel MODEL;

    protected final Multimap<Attribute, AttributeModifier> flyingAttributeModifiers;
    protected final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public WingedSandalsItem(Properties builderIn) {
        super(MATERIAL, EquipmentSlotType.FEET, builderIn);
        final double speedBonus = GreekFantasy.CONFIG.SANDALS_SPEED_BONUS.get();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getDefaultAttributeModifiers(EquipmentSlotType.FEET));
        flyingAttributeModifiers = builder.build();
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MODIFIER, "Armor speed modifier", speedBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
        this.attributeModifiers = builder.build();
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        // add the item to the group with enchantment already applied
        if (this.allowdedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            if (GreekFantasy.CONFIG.isOverstepEnabled()) {
                stack.enchant(GFRegistry.EnchantmentReg.OVERSTEP_ENCHANTMENT, 1);
            }
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        // add Overstep enchantment if not present
        if (GreekFantasy.CONFIG.isOverstepEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.OVERSTEP_ENCHANTMENT, stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.OVERSTEP_ENCHANTMENT, 1);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GreekFantasy.CONFIG.isOverstepEnabled() ? stack.getEnchantmentTags().size() > 1 : super.isFoil(stack);
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void inventoryTick(final ItemStack stack, final World worldIn, final Entity entityIn,
                              final int itemSlot, final boolean isSelected) {
        // add Overstep enchantment if not present
        if (GreekFantasy.CONFIG.isOverstepEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.OVERSTEP_ENCHANTMENT, stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.OVERSTEP_ENCHANTMENT, 1);
        }
        // add Jump Boost effect
        if (itemSlot == EquipmentSlotType.FEET.getIndex() && stack.getMaxDamage() - stack.getDamageValue() > 10 && entityIn instanceof LivingEntity) {
            final LivingEntity entity = (LivingEntity) entityIn;
            entity.addEffect(new EffectInstance(Effects.JUMP, 20, 4, false, false, false));
            entity.fallDistance = 0;
            if (GreekFantasy.CONFIG.doesWingedSandalsDeplete() && entity.getRandom().nextInt(40) == 0
                    && (!(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity) entity).isCreative())) {
                stack.hurtAndBreak(1, entity, (e) -> e.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
            }
        }
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlotType equipmentSlot, final ItemStack stack) {
        if (equipmentSlot == EquipmentSlotType.FEET && (stack.getMaxDamage() - stack.getDamageValue() > 10)) {
            //return EnchantmentHelper.getEnchantmentLevel(GFRegistry.FLYING_ENCHANTMENT, stack) < 1
            //    ? this.attributeModifiers : this.flyingAttributeModifiers;
            return attributeModifiers;
        }
        return super.getAttributeModifiers(equipmentSlot, stack);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        // add broken tooltip
        if (stack.getMaxDamage() - stack.getDamageValue() <= 10) {
            tooltip.add(new TranslationTextComponent("item.tooltip.broken").withStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        } else {
            // add jump boost tooltip
            tooltip.add(new TranslationTextComponent("effect.minecraft.jump_boost").withStyle(TextFormatting.AQUA)
                    .append(" ").append(new TranslationTextComponent("enchantment.level.5").withStyle(TextFormatting.AQUA)));
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
        return TEXTURE;
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     *
     * @param entityLiving The entity wearing the armor
     * @param itemStack    The itemStack to render the model of
     * @param armorSlot    The slot the armor is in
     * @param _default     Original armor model. Will have attributes set.
     * @return A ModelBiped to render instead of the default
     */
    @OnlyIn(Dist.CLIENT)
    public <A extends net.minecraft.client.renderer.entity.model.BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        if (MODEL == null) {
            MODEL = new greekfantasy.client.render.model.armor.WingedSandalsModel(1.0F);
        }
        MODEL.young = _default.young;
        MODEL.crouching = _default.crouching;
        MODEL.riding = _default.riding;
        MODEL.setupAnim(entityLiving, entityLiving.animationPosition, entityLiving.animationSpeed, (float) entityLiving.tickCount, entityLiving.yHeadRot, entityLiving.xRot);
        // MODEL.rightArmPose = _default.rightArmPose;
        // MODEL.leftArmPose = _default.leftArmPose;
        return (A) MODEL;
    }


    public static class WingedSandalsArmorMaterial implements IArmorMaterial {
        private static final String NAME = "winged";

        @Override
        public int getDefenseForSlot(EquipmentSlotType arg0) {
            return 2;
        }

        @Override
        public int getDurabilityForSlot(EquipmentSlotType arg0) {
            return 195;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(GFRegistry.ItemReg.MAGIC_FEATHER);
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }
    }

}
