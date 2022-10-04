package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.CurseOfCirce;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WandOfCirceItem extends Item {

    public WandOfCirceItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level world, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.WAND_OF_CIRCE_COOLDOWN.get());
        // spawn a healing spell entity
        if (!world.isClientSide()) {
            CurseOfCirce curseOfCirce = CurseOfCirce.create(world, player);
            world.addFreshEntity(curseOfCirce);
        }
        player.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 0.5F, 1.0F);

        // damage the item stack
        if (!player.isCreative()) {
            stack.hurtAndBreak(GreekFantasy.CONFIG.WAND_OF_CIRCE_DURABILITY_ON_USE.get(), player, (entity) -> entity.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return toRepair.getItem() == this && toRepair.getDamageValue() < toRepair.getMaxDamage() && repair.is(GFRegistry.ItemReg.BOAR_EAR.get());
    }
}
