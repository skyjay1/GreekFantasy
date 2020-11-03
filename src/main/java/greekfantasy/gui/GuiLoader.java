package greekfantasy.gui;

import greekfantasy.block.StatueBlock;
import greekfantasy.client.gui.PanfluteScreen;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.StatuePose;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public final class GuiLoader {

  private GuiLoader() { }
  
  public static void openStatueGui(final BlockState state, final BlockPos tePos, final StatueTileEntity teStatue, final ServerPlayerEntity playerIn) {
    // get info to send to the GUI constructor and byte buffer
    final StatuePose currentPose = teStatue.getStatuePose();
    final boolean isFemale = teStatue.isStatueFemale();
    final String name = teStatue.getTextureName();
    final Direction facing = state.get(StatueBlock.HORIZONTAL_FACING);
    // open the container GUI
    NetworkHooks.openGui(playerIn, 
      new SimpleNamedContainerProvider((id, inventory, player) -> 
          new StatueContainer(id, inventory, teStatue, currentPose, isFemale, name, tePos, facing), 
          StringTextComponent.EMPTY), 
          buf -> {
            buf.writeBoolean(isFemale);
            buf.writeBlockPos(tePos);
            buf.writeCompoundTag(currentPose.serializeNBT());
            buf.writeString(name);
            buf.writeByte(facing.getHorizontalIndex());
          }
      );
  }
  
  public static void openPanfluteGui(final PlayerEntity playerIn, final int itemSlot, final ItemStack itemstack) {
    // only load client-side, of course
    if (!playerIn.getEntityWorld().isRemote()) {
      return;
    }
    // open the gui
    Minecraft.getInstance().displayGuiScreen(new PanfluteScreen(itemSlot, itemstack));
  }
}
