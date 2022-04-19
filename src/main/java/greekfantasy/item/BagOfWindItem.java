package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BagOfWindItem extends Item {

    public BagOfWindItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // prevent the item from being used up all the way
        if (stack.getMaxDamage() - stack.getDamageValue() <= 1) {
            return ActionResult.fail(stack);
        }
        // give player potion effect
        player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, GreekFantasy.CONFIG.getBagOfWindDuration(), 1));
        player.addEffect(new EffectInstance(Effects.DOLPHINS_GRACE, GreekFantasy.CONFIG.getBagOfWindDuration(), 0));
        player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.getBagOfWindCooldown());
        if (!player.isCreative()) {
            stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        }
        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return toRepair.getItem() == this && toRepair.getDamageValue() < toRepair.getMaxDamage() && repair.getItem() == GFRegistry.MAGIC_FEATHER;
    }
}
