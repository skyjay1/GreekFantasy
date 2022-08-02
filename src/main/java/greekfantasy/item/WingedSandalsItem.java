package greekfantasy.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class WingedSandalsItem extends ArmorItem {

    public static final UUID SPEED_MODIFIER = UUID.fromString("58b7ff54-706b-4b0b-80f7-0dce04a673e4");
    public static final int BROKEN = 2;

    private static final String TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/winged_layer_2.png").toString();

    protected Multimap<Attribute, AttributeModifier> attributeModifiers;

    public WingedSandalsItem(final ArmorMaterial armorMaterial, Properties builderIn) {
        super(armorMaterial, EquipmentSlot.FEET, builderIn);
        final double speedBonus = 1.5F;
        final double stepHeightBonus = 0.62F;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getDefaultAttributeModifiers(EquipmentSlot.FEET));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MODIFIER, "Armor speed modifier", speedBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
        this.attributeModifiers = builder.build();
    }

    @Override
    public void inventoryTick(final ItemStack stack, final Level level, final Entity entity,
                              final int itemSlot, final boolean isSelected) {
        // add Overstep enchantment if not present
        if (GreekFantasy.CONFIG.isOverstepEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.OVERSTEP.get(), stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.OVERSTEP.get(), 1);
        }
        // add mob effects
        if (entity instanceof LivingEntity livingEntity && itemSlot == EquipmentSlot.FEET.getIndex() && stack.getMaxDamage() - stack.getDamageValue() > BROKEN) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.JUMP, 30, 4, false, false, false));
            livingEntity.fallDistance = 0;
            // damage the item
            if (level.getRandom().nextFloat() < GreekFantasy.CONFIG.getWingedSandalsDurabilityChance()) {
                stack.hurtAndBreak(1, livingEntity, e -> e.broadcastBreakEvent(EquipmentSlot.FEET));
            }
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        // add the item to the group with enchantment already applied
        if (this.allowdedIn(group)) {
            final ItemStack stack = new ItemStack(this);
            if (GreekFantasy.CONFIG.isOverstepEnabled()) {
                stack.enchant(GFRegistry.EnchantmentReg.OVERSTEP.get(), 1);
            }
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        // add Overstep enchantment if not present
        if (GreekFantasy.CONFIG.isOverstepEnabled() && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.OVERSTEP.get(), stack) < 1) {
            stack.enchant(GFRegistry.EnchantmentReg.OVERSTEP.get(), 1);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GreekFantasy.CONFIG.isOverstepEnabled() ? stack.getEnchantmentTags().size() > 1 : super.isFoil(stack);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final EquipmentSlot equipmentSlot, final ItemStack stack) {
        // ensure equipment slot matches and item is not too damaged
        if (equipmentSlot == EquipmentSlot.FEET && (stack.getMaxDamage() - stack.getDamageValue() > BROKEN)) {
            return attributeModifiers;
        }
        return super.getAttributeModifiers(equipmentSlot, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        // add broken tooltip
        if (stack.getMaxDamage() - stack.getDamageValue() <= BROKEN) {
            tooltip.add(new TranslatableComponent("item.tooltip.broken").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        // add jump boost tooltip
        tooltip.add(new TranslatableComponent(MobEffects.JUMP.getDescriptionId()).withStyle(ChatFormatting.AQUA)
                .append(" ").append(new TranslatableComponent("enchantment.level.5").withStyle(ChatFormatting.AQUA)));

    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TEXTURE;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            private greekfantasy.client.armor.WingedSandalsModel model;

            @Nullable
            @Override
            public net.minecraft.client.model.HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, net.minecraft.client.model.HumanoidModel<?> _default) {
                if (null == model) {
                    model = new greekfantasy.client.armor.WingedSandalsModel(
                            net.minecraft.client.Minecraft.getInstance().getEntityModels()
                                    .bakeLayer(greekfantasy.client.armor.WingedSandalsModel.WINGED_SANDALS_MODEL_RESOURCE));
                }
                model.setupWingsAnim(entityLiving);
                return model;
            }
        });
    }
}
