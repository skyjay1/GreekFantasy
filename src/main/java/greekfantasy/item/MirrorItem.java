package greekfantasy.item;

import greekfantasy.GreekFantasy;
import greekfantasy.gui.DeityContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class MirrorItem extends Item {

  public MirrorItem(final Item.Properties properties) {
    super(properties);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity playerIn, final Hand handIn) {
    final ItemStack item = playerIn.getHeldItem(handIn);
    if(!world.isRemote() && playerIn instanceof ServerPlayerEntity) {
      if(playerIn.isSneaking()) {
        // print capability values on sneak-click
        playerIn.getCapability(GreekFantasy.FAVOR).ifPresent(f -> {
          if(f.isEnabled()) {
            GreekFantasy.PROXY.getDeityCollection(true).forEach(d -> f.getFavor(d).sendStatusMessage(playerIn, d));
          }
        });
        playerIn.getCooldownTracker().setCooldown(item.getItem(), 20);
      } else {
        // open capability gui on regular click
        playerIn.getCapability(GreekFantasy.FAVOR).ifPresent(f -> {
          if(f.isEnabled()) {
            NetworkHooks.openGui((ServerPlayerEntity)playerIn, 
              new SimpleNamedContainerProvider((id, inventory, player) -> 
              new DeityContainer(id, inventory, f), 
              StringTextComponent.EMPTY), 
              buf -> buf.writeCompoundTag(f.serializeNBT()));
          }
        });
      }
    }
    return ActionResult.resultSuccess(item);
  }
}
