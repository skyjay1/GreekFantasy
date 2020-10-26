package greekfantasy.client.gui;

import greekfantasy.GreekFantasy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class GuiLoader {

  private GuiLoader() { }
  
  public static void openPanfluteGui(final PlayerEntity playerIn, final int itemSlot, final ItemStack itemstack) {
    // only load client-side, of course
    if (!playerIn.getEntityWorld().isRemote()) {
      return;
    }
    // open the gui
    Minecraft.getInstance().displayGuiScreen(new PanfluteScreen(itemSlot, itemstack));
  }
}
