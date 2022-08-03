package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
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
        list.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return TEXTURE_1;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            private greekfantasy.client.armor.NemeanArmorModel model;

            @Nullable
            @Override
            public net.minecraft.client.model.HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, net.minecraft.client.model.HumanoidModel<?> _default) {
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
