package greekfantasy.item;

import greekfantasy.entity.misc.DragonToothHook;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class DragonToothRodItem extends FishingRodItem {

    protected static final Enchantment FISHING_LUCK = Enchantments.FISHING_LUCK;
    protected static final int FISHING_LUCK_LEVEL = 3;

    public DragonToothRodItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (player.fishing != null) {
            if (!level.isClientSide) {
                int i = player.fishing.retrieve(itemstack);
                itemstack.hurtAndBreak(i, player, (p_41288_) -> {
                    p_41288_.broadcastBreakEvent(interactionHand);
                });
            }

            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            level.gameEvent(player, GameEvent.FISHING_ROD_REEL_IN, player);
        } else {
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!level.isClientSide) {
                int k = EnchantmentHelper.getFishingSpeedBonus(itemstack);
                int j = EnchantmentHelper.getFishingLuckBonus(itemstack);
                level.addFreshEntity(new DragonToothHook(player, level, j, k));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            level.gameEvent(player, GameEvent.FISHING_ROD_CAST, player);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void inventoryTick(final ItemStack stack, final Level level, final Entity entityIn,
                              final int itemSlot, final boolean isSelected) {
        if(EnchantmentHelper.getItemEnchantmentLevel(FISHING_LUCK, stack) < FISHING_LUCK_LEVEL) {
            stack.enchant(FISHING_LUCK, FISHING_LUCK_LEVEL);
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        // add the item to the group with enchantment already applied
        if (this.allowdedIn(group)) {
            ItemStack stack = new ItemStack(this);
            stack.enchant(FISHING_LUCK, FISHING_LUCK_LEVEL);
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player playerIn) {
        if(EnchantmentHelper.getItemEnchantmentLevel(FISHING_LUCK, stack) < FISHING_LUCK_LEVEL) {
            stack.enchant(FISHING_LUCK, FISHING_LUCK_LEVEL);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getEnchantmentTags().size() > 1;
    }
}
