package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.HealingSpell;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StaffOfHealingItem extends Item {

    public StaffOfHealingItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level world, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // prevent the item from being used up all the way
        if (stack.getMaxDamage() - stack.getDamageValue() <= 1) {
            return InteractionResultHolder.fail(stack);
        }
        player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.STAFF_OF_HEALING_COOLDOWN.get());
        // spawn a healing spell entity
        if (!world.isClientSide()) {
            HealingSpell healingSpell = HealingSpell.create(world, player);
            world.addFreshEntity(healingSpell);
        }

        // damage the item stack
        if (!player.isCreative()) {
            stack.hurtAndBreak(GreekFantasy.CONFIG.STAFF_OF_HEALING_DURABILITY_ON_USE.get(), player, (entity) -> entity.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return toRepair.getItem() == this && toRepair.getDamageValue() < toRepair.getMaxDamage() && repair.is(GFRegistry.ItemReg.SNAKESKIN.get());
    }
}
