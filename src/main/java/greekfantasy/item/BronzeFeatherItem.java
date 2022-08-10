package greekfantasy.item;

import greekfantasy.entity.misc.BronzeFeather;
import greekfantasy.entity.misc.DragonTooth;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
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

public class BronzeFeatherItem extends Item {

    private static final int OPTIMAL_USE_DURATION = 25;

    public BronzeFeatherItem(final Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(final ItemStack stack, final Level world, final LivingEntity entity, final int duration) {
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        player.getCooldowns().addCooldown(this, 10);

        if (!world.isClientSide()) {
            // determine inaccuracy
            int useDuration = getUseDuration(stack) - duration;
            float inaccuracy = 1.0F + 10.0F * (1.0F - Mth.clamp((float)(useDuration) / (float)OPTIMAL_USE_DURATION, 0.0F, 1.0F));
            BronzeFeather bronzeFeather = BronzeFeather.create(world, player);
            bronzeFeather.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.25F, inaccuracy);
            world.addFreshEntity(bronzeFeather);
        }

        // shrink the item stack
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        player.playSound(SoundEvents.TRIDENT_THROW, 1.2F, 1.2F + world.getRandom().nextFloat() * 0.2F);
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level world, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
}
