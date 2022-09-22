package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.List;

public class ThyrsusItem extends DiggerItem {

    public ThyrsusItem(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, tier, tier.getTag(), properties);
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
                mainhandItem.hurtAndBreak(10, player, (entity) -> entity.broadcastBreakEvent(hand));
            }
        }

        return InteractionResultHolder.sidedSuccess(mainhandItem, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
}
