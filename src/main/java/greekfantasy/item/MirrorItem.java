package greekfantasy.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MirrorItem extends Item {

    public MirrorItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(final World world, final PlayerEntity playerIn, final Hand handIn) {
        final ItemStack item = playerIn.getItemInHand(handIn);
        return ActionResult.success(item);
    }
}
