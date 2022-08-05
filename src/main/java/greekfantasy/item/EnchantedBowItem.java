package greekfantasy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class EnchantedBowItem extends BowItem {

    public EnchantedBowItem(Properties builder) {
        super(builder);
    }

    /**
     * Copy of BowItem#releaseUsing with hooks for custom behavior
     */
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            boolean doNotConsume = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack ammo = player.getProjectile(stack);

            int useDuration = this.getUseDuration(stack) - timeLeft;
            useDuration = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, level, player, useDuration, !ammo.isEmpty() || doNotConsume);
            if (useDuration < 0) {
                return;
            }

            if (!ammo.isEmpty() || doNotConsume) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }

                float power = getPowerForTime(useDuration);
                if (!(power < 0.1D)) {
                    boolean isInfinite = player.getAbilities().instabuild
                            || (ammo.getItem() instanceof ArrowItem arrowItem && arrowItem.isInfinite(ammo, stack, player));
                    if (!level.isClientSide()) {
                        // shoot first arrow, then shoot remaining arrows (if any) with adjusted stats
                        shootArrow(level, stack, ammo, power, 1.0F, isInfinite, player);
                        for (int i = 1, arrowCount = getArrowCount(stack); i < arrowCount; i++) {
                            shootArrow(level, stack, ammo, power, 8.0F, true, player);
                        }
                        // attempt to damage item
                        stack.hurtAndBreak(1, player, (e) -> {
                            e.broadcastBreakEvent(player.getUsedItemHand());
                        });

                    }
                    // play sound
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
                    // shrink item stack if applicable
                    if (!isInfinite && !player.getAbilities().instabuild) {
                        ammo.shrink(1);
                        if (ammo.isEmpty()) {
                            player.getInventory().removeItem(ammo);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    /**
     * Creates and shoots an arrow
     * @param level the level
     * @param itemStack the bow
     * @param ammo the ammo
     * @param power the power factor
     * @param inaccuracy the innacuracy factor
     * @param creativePickup true if the arrow has creative-only pickup status
     * @param player the player that is shooting the arrow
     * @return the arrow after it has been added to the world
     */
    protected AbstractArrow shootArrow(final Level level, final ItemStack itemStack, final ItemStack ammo,
                                       final float power, final float inaccuracy, final boolean creativePickup,
                                       final Player player) {
        // create arrow entity
        ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
        AbstractArrow arrow = arrowitem.createArrow(level, ammo, player);
        arrow = customArrow(arrow);
        // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
        float velocity = power * 3.0F * getArrowVelocityMultiplier();
        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
        // set crit
        if (power >= 1.0F) {
            arrow.setCritArrow(true);
        }
        // apply power enchantment
        int powerEnchant = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
        if (powerEnchant > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerEnchant * 0.5D + 0.5D);
        }
        // apply punch enchantment
        int punchEnchant = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStack);
        if (punchEnchant > 0) {
            arrow.setKnockback(punchEnchant);
        }
        // apply flame enchantment
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemStack) > 0) {
            arrow.setSecondsOnFire(100);
        }
        // set pickup status
        if (creativePickup || player.getAbilities().instabuild && (ammo.is(Items.SPECTRAL_ARROW) || ammo.is(Items.TIPPED_ARROW))) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        // actually add the arrow entity
        level.addFreshEntity(arrow);
        return arrow;
    }

    protected abstract void checkAndApplyBaseEnchantments(final ItemStack stack);

    protected abstract int getBaseEnchantmentCount();

    protected float getArrowVelocityMultiplier() {
        return 1.0F;
    }

    protected int getArrowCount(final ItemStack stack) {
        return 1;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        // add the item to the group with enchantment already applied
        if (this.allowedIn(group)) {
            ItemStack stack = new ItemStack(this);
            checkAndApplyBaseEnchantments(stack);
            items.add(stack);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player playerIn) {
        checkAndApplyBaseEnchantments(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getEnchantmentTags().size() > getBaseEnchantmentCount();
    }

    @Override
    public void inventoryTick(final ItemStack stack, final Level level, final Entity entityIn,
                              final int itemSlot, final boolean isSelected) {
        checkAndApplyBaseEnchantments(stack);
    }

    public static class AvernalBowItem extends EnchantedBowItem {
        public AvernalBowItem(Properties builder) {
            super(builder);
        }

        @Override
        protected void checkAndApplyBaseEnchantments(final ItemStack stack) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) < 1) {
                stack.enchant(Enchantments.FLAMING_ARROWS, 1);
            }
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) < 1) {
                stack.enchant(Enchantments.VANISHING_CURSE, 1);
            }
        }

        @Override
        protected int getBaseEnchantmentCount() {
            return 2;
        }

        @Override
        protected float getArrowVelocityMultiplier() {
            return 1.0F;
        }
    }

    public static class ArtemisBowItem extends EnchantedBowItem {
        public ArtemisBowItem(Properties builder) {
            super(builder);
        }

        @Override
        protected void checkAndApplyBaseEnchantments(final ItemStack stack) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack) < 5) {
                stack.enchant(Enchantments.POWER_ARROWS, 5);
            }
        }

        @Override
        protected int getBaseEnchantmentCount() {
            return 1;
        }

        @Override
        protected float getArrowVelocityMultiplier() {
            return 1.25F;
        }

        @Override
        protected int getArrowCount(final ItemStack stack) {
            return 3;
        }

        @Override
        public AbstractArrow customArrow(AbstractArrow arrow) {
            arrow.setBaseDamage(arrow.getBaseDamage() * 1.25D);
            return arrow;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
            // add multishot tooltip
            list.add(Component.translatable(Enchantments.MULTISHOT.getDescriptionId()).withStyle(ChatFormatting.AQUA)
                    .append(" ").append(Component.translatable("enchantment.level.2").withStyle(ChatFormatting.AQUA)));
        }
    }

    public static class ApolloBowItem extends EnchantedBowItem {
        public ApolloBowItem(Properties builder) {
            super(builder);
        }

        @Override
        protected void checkAndApplyBaseEnchantments(final ItemStack stack) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) < 1) {
                stack.enchant(Enchantments.FLAMING_ARROWS, 1);
            }
        }

        @Override
        protected int getBaseEnchantmentCount() {
            return 1;
        }

        @Override
        protected float getArrowVelocityMultiplier() {
            return 1.5F;
        }

        @Override
        protected int getArrowCount(final ItemStack stack) {
            return 2;
        }

        @Override
        public AbstractArrow customArrow(AbstractArrow arrow) {
            arrow.setBaseDamage(arrow.getBaseDamage() * 1.75D);
            return arrow;
        }

        @Override
        public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
            // add multishot tooltip
            list.add(Component.translatable(Enchantments.MULTISHOT.getDescriptionId()).withStyle(ChatFormatting.AQUA)
                    .append(" ").append(Component.translatable("enchantment.level.1").withStyle(ChatFormatting.AQUA)));
        }
    }
}
