package greekfantasy.item;


import greekfantasy.entity.misc.Discus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiscusItem extends Item {

    private static final float OPTIMAL_USE_DURATION = 25;

    public DiscusItem(final Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(final ItemStack stack, final Level level, final LivingEntity entity, final int duration) {
        if(entity instanceof Player player) {
            int useDuration = getUseDuration(stack) - duration;
            if (useDuration < OPTIMAL_USE_DURATION / 2) {
                return;
            }

            if (!level.isClientSide()) {
                throwDiscus(level, player, stack, useDuration);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    protected void throwDiscus(final Level level, final Player thrower, final ItemStack stack, int usedTicks) {
        Discus discus = Discus.create(level, thrower);
        float speed = 1.7F - 0.05F * Math.min(Math.abs(usedTicks - OPTIMAL_USE_DURATION), OPTIMAL_USE_DURATION * 3 / 4);
        discus.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, speed, 3.5F);
        level.addFreshEntity(discus);
        // remove from item stack
        if (!thrower.isCreative()) {
            stack.shrink(1);
        }
        // play sound
        thrower.playSound(SoundEvents.EGG_THROW, 0.6F, 0.6F + thrower.getRandom().nextFloat() * 0.2F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
}
