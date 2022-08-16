package greekfantasy.item;

import greekfantasy.entity.misc.BronzeFeather;
import greekfantasy.entity.misc.DragonTooth;
import greekfantasy.entity.misc.ThrowingAxe;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ThrowingAxeItem extends AxeItem {

    private static final int OPTIMAL_USE_DURATION = 25;

    public ThrowingAxeItem(Tier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
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

        if (!world.isClientSide()) {
            // determine inaccuracy
            int useDuration = getUseDuration(stack) - duration;
            float inaccuracy = 1.0F + 12.0F * (1.0F - Mth.clamp((float)(useDuration) / (float)OPTIMAL_USE_DURATION, 0.0F, 1.0F));
            ThrowingAxe throwingAxe = new ThrowingAxe(world, player, stack, !player.isCreative());
            throwingAxe.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.25F, inaccuracy);
            world.addFreshEntity(throwingAxe);
        }

        // shrink the item stack
        if(!player.isCreative()) {
            player.setItemInHand(player.getUsedItemHand(), ItemStack.EMPTY);
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
}
