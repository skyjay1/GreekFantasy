package greekfantasy.item;

import greekfantasy.entity.misc.DiscusEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class DiscusItem extends Item {

    private static final float OPTIMAL_USE_DURATION = 25;

    public DiscusItem(final Properties properties) {
        super(properties);
    }

    @Override
    public UseAction getUseAnimation(final ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(final ItemStack stack, final World world, final LivingEntity entity, final int duration) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entity;

        int useDuration = getUseDuration(stack) - duration;
        if (useDuration < OPTIMAL_USE_DURATION / 2) {
            return;
        }

        if (!world.isClientSide()) {
            throwDiscus(world, player, stack, useDuration);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
    }

    protected void throwDiscus(final World world, final PlayerEntity thrower, final ItemStack stack, int usedTicks) {
        DiscusEntity discus = DiscusEntity.create(world, thrower);
        float speed = 1.7F - 0.05F * Math.min(Math.abs(usedTicks - OPTIMAL_USE_DURATION), OPTIMAL_USE_DURATION * 3 / 4);
        discus.shootFromRotation(thrower, thrower.xRot, thrower.yRot, 0.0F, speed, 3.5F);
        world.addFreshEntity(discus);
        // remove from item stack
        if (!thrower.isCreative()) {
            stack.shrink(1);
        }
        // play sound
        thrower.playSound(SoundEvents.EGG_THROW, 0.6F, 0.6F + thrower.getRandom().nextFloat() * 0.2F);
    }

    @Override
    public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return ActionResult.consume(stack);
    }
}
