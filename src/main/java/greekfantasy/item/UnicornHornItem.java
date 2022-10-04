package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class UnicornHornItem extends Item {

    private final int useDuration = 50;
    private final int useCooldown = 240;

    public UnicornHornItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return useDuration;
    }

    @Override
    public ItemStack finishUsingItem(final ItemStack stack, final Level worldIn, final LivingEntity entityLiving) {
        if (entityLiving instanceof Player player) {
            player.getCooldowns().addCooldown(this, useCooldown);
        }
        // remove negative potion effects
        if (GreekFantasy.CONFIG.UNICORN_HORN_CURES_EFFECTS.get()) {
            // create set of active negative effects
            Set<MobEffect> negativeEffects = new HashSet<>();
            for(MobEffectInstance effectInstance : entityLiving.getActiveEffects()) {
                if(effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    negativeEffects.add(effectInstance.getEffect());
                }
            }
            // remove each of the effects
            for(MobEffect negativeEffect : negativeEffects) {
                entityLiving.removeEffect(negativeEffect);
            }
        }
        // give brief regen effect
        entityLiving.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0));
        // damage item
        stack.hurtAndBreak(GreekFantasy.CONFIG.UNICORN_HORN_DURABILITY_ON_USE.get(), entityLiving, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack stack) {
        return UseAnim.BOW;
    }

}
