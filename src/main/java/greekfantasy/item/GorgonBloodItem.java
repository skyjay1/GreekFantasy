package greekfantasy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;

public class GorgonBloodItem extends Item {

    public static final FoodProperties GORGON_BLOOD_GOOD = new FoodProperties.Builder()
            .nutrition(1).saturationMod(0.1F).alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 180), 1.0F)
            .build();

    public static final FoodProperties GORGON_BLOOD_BAD = new FoodProperties.Builder()
            .nutrition(1).saturationMod(0.1F).alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 180), 1.0F)
            .build();

    public GorgonBloodItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    @Override
    public FoodProperties getFoodProperties() {
        return Math.random() < 0.5D ? GORGON_BLOOD_BAD : GORGON_BLOOD_GOOD;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return getDrinkingSound();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack item, Level level, LivingEntity entity) {
        ItemStack container = item.getCraftingRemainingItem();
        ItemStack result = super.finishUsingItem(item, level, entity);
        // replace with container item
        if (result.isEmpty()) {
            return container;
        }
        if (entity instanceof Player player && !player.isCreative()) {
            player.getInventory().add(container);
        }

        entity.gameEvent(GameEvent.DRINK);
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(getDescriptionId() + ".tooltip.heal").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable(getDescriptionId() + ".tooltip.poison").withStyle(ChatFormatting.RED));
    }
}
