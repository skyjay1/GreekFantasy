package greekfantasy.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import greekfantasy.GreekFantasy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.List;

public class ThyrsusItem extends TieredItem implements Vanishable {

    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ThyrsusItem(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, properties);
        this.attackDamage = attackDamage + tier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity owner) {
        itemStack.hurtAndBreak(1, owner, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        // detect held items
        ItemStack mainhandItem = player.getItemInHand(hand);
        InteractionHand offhand = (hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        ItemStack offhandItem = player.getItemInHand(offhand);
        boolean success = false;
        // fill empty bucket with milk
        if (offhandItem.is(Items.BUCKET) && offhandItem.getItem() instanceof BucketItem bucketItem && bucketItem.getFluid().isSame(Fluids.EMPTY)) {
            offhandItem.shrink(1);
            player.getInventory().add(new ItemStack(Items.MILK_BUCKET));
            success = true;
        }
        // fill empty bottle with water
        if (offhandItem.is(Items.GLASS_BOTTLE)) {
            offhandItem.shrink(1);
            player.getInventory().add(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
            success = true;
        }
        // cooldown and item damage
        if (success) {
            player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.THYRSUS_COOLDOWN.get());
            if (!player.isCreative()) {
                mainhandItem.hurtAndBreak(GreekFantasy.CONFIG.THYRSUS_DURABILITY_ON_USE.get(), player, (entity) -> entity.broadcastBreakEvent(hand));
            }
        }

        return InteractionResultHolder.sidedSuccess(mainhandItem, level.isClientSide());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? defaultModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
}
