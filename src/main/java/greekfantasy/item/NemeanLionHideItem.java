package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NemeanLionHideItem extends ArmorItem {

    private static final String TEXTURE_1 = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/nemean_layer_1.png").toString();

    public NemeanLionHideItem(final ArmorMaterial armorMaterial, final EquipmentSlot slot, Properties builderIn) {
        super(armorMaterial, slot, builderIn);
    }

    public static boolean isImmune(final LivingEntity entity, final Projectile projectile, final double dotProduct) {
        // determine if projectile is facing the same direction as player
        return dotProduct > 0.70D;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TEXTURE_1;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private greekfantasy.client.armor.NemeanArmorModel model;

            @Nullable
            @Override
            public net.minecraft.client.model.HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, net.minecraft.client.model.HumanoidModel<?> _default) {
                //if (null == model) {
                    model = new greekfantasy.client.armor.NemeanArmorModel(
                            net.minecraft.client.Minecraft.getInstance().getEntityModels()
                                    .bakeLayer(greekfantasy.client.armor.NemeanArmorModel.NEMEAN_ARMOR_MODEL_RESOURCE));
                //}
                model.copyRotations(_default);
                return model;
            }
        });
    }
}
