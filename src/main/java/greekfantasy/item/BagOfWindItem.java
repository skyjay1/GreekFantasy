package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BagOfWindItem extends Item {

    public BagOfWindItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // give player potion effect
        final int duration = GreekFantasy.CONFIG.BAG_OF_WIND_DURATION.get();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, duration, 0));
        player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.BAG_OF_WIND_COOLDOWN.get());
        if (!player.isCreative()) {
            stack.hurtAndBreak(GreekFantasy.CONFIG.BAG_OF_WIND_DURABILITY_ON_USE.get(), player, (entity) -> entity.broadcastBreakEvent(hand));
        }
        // play sound
        player.playSound(SoundEvents.ELYTRA_FLYING, 0.11F, 1.0F);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return toRepair.getItem() == this && toRepair.getDamageValue() < toRepair.getMaxDamage() && repair.is(Items.FEATHER);
    }
}
