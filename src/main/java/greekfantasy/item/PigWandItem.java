package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.PigSpellEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PigWandItem extends Item {

    public PigWandItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.getPigWandCooldown());
        // spawn a healing spell entity
        if (!world.isClientSide()) {
            PigSpellEntity healingSpell = PigSpellEntity.create(world, player);
            world.addFreshEntity(healingSpell);
        }
        player.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 0.5F, 1.0F);

        // damage the item stack
        if (!player.isCreative()) {
            stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
        }

        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == GFRegistry.BOAR_EAR;
    }
}
