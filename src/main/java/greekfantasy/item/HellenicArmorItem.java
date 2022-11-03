package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HellenicArmorItem extends ArmorItem {

    private static final String TEXTURE_1 = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/hellenic_layer_1.png").toString();
    private static final String TEXTURE_2 = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/hellenic_layer_2.png").toString();
    private static final String TEXTURE_3 = new ResourceLocation(GreekFantasy.MODID, "textures/models/armor/hellenic_layer_3.png").toString();

    private static final TagKey<Item> ACHILLES_ITEM = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "achilles_heel"));

    public HellenicArmorItem(final ArmorMaterial armorMaterial, EquipmentSlot slot, Properties properties) {
        super(armorMaterial, slot, properties);
    }

    /**
     * @param entity an entity
     * @return the number of achilles heel items worn by the entity
     */
    public static int getAchillesCount(final LivingEntity entity) {
        int armorCount = 0;
        for(ItemStack itemStack : entity.getArmorSlots()) {
            if(itemStack.is(ACHILLES_ITEM)) {
                armorCount++;
            }
        }
        return armorCount;
    }

    /**
     * @param entity the entity that is wearing this armor
     * @param projectile the projectile that hit this entity
     * @param dotProduct the dot product of the facing directions between the entity and projectile
     * @param armorCount the number of Achilles Heel armor pieces the entity is wearing
     * @return true if the projectile hit is ignored
     */
    public static boolean isImmune(final LivingEntity entity, final Projectile projectile,
                                   final double dotProduct, final int armorCount) {
        // checks if the entity and projectile are facing opposite direction and random check against armor count
        return dotProduct < -0.70D && armorCount > 0 && (entity.getRandom().nextInt(4) + 1) < armorCount;
    }

    public static boolean damageArmor(final LivingEntity entity, final int amount) {
        for(EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            ItemStack itemStack = entity.getItemBySlot(slot);
            if(itemStack.is(ACHILLES_ITEM)) {
                itemStack.hurtAndBreak(amount, entity, e -> e.broadcastBreakEvent(slot));
                return true;
            }
        }
        return false;
    }

    /**
     * @param entity the entity that is wearing this armor
     * @param projectile the projectile that hit this entity
     * @param dotProduct the dot product of the facing directions between the entity and projectile
     * @param armorCount the number of Achilles Heel armor pieces the entity is wearing
     * @return true if the projectile hit is critical
     */
    public static boolean isCritical(final LivingEntity entity, final AbstractArrow projectile,
                                     final double dotProduct, final int armorCount) {
        // checks if the entity and projectile are facing same direction and hit lower half of entity
        return dotProduct > 0.64D && armorCount > 0 && projectile.getY() < (entity.getY() + entity.getBbHeight() * 0.5D);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("item.greekfantasy.hellenic_armor.tooltip.resist").withStyle(ChatFormatting.GOLD));
        list.add(Component.translatable("item.greekfantasy.hellenic_armor.tooltip.weak").withStyle(ChatFormatting.RED));
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        switch (slot) {
            case HEAD:
                return TEXTURE_3;
            case LEGS:
                return TEXTURE_2;
            default:
                return TEXTURE_1;
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private greekfantasy.client.armor.HellenicArmorModel model;
            @Nullable
            @Override
            public net.minecraft.client.model.HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, net.minecraft.client.model.HumanoidModel<?> _default) {
                // lazy-load model
                if(null == model) {
                    model = new greekfantasy.client.armor.HellenicArmorModel(
                            net.minecraft.client.Minecraft.getInstance().getEntityModels()
                                    .bakeLayer(greekfantasy.client.armor.HellenicArmorModel.HELLENIC_ARMOR_MODEL_RESOURCE));
                }
                // use custom model for head
                if(slot == EquipmentSlot.HEAD) {
                    return model;
                }
                // use default model for other slots
                return _default;
            }
        });
    }
}
