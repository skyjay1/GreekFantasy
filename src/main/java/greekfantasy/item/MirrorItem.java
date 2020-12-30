package greekfantasy.item;

import greekfantasy.GreekFantasy;
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
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand handIn) {
    final ItemStack item = player.getHeldItem(handIn);
    if(!world.isRemote()) {
      player.getCapability(GreekFantasy.FAVOR).ifPresent(f -> GreekFantasy.PROXY.DEITY.getValues()
          .forEach(oDeity -> oDeity.ifPresent(d -> f.getFavor(d).sendStatusMessage(player, d))));
      player.getCooldownTracker().setCooldown(item.getItem(), 20);
    }
    return ActionResult.resultSuccess(item);
  }
}
