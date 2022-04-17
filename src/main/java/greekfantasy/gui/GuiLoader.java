package greekfantasy.gui;

import greekfantasy.client.gui.SongScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class GuiLoader {

    private GuiLoader() {
    }

    public static void openSongGui(final PlayerEntity playerIn, final int itemSlot, final ItemStack itemstack) {
        // only load client-side, of course
        if (!playerIn.getCommandSenderWorld().isClientSide()) {
            return;
        }
        // open the gui
        Minecraft.getInstance().setScreen(new SongScreen(itemSlot, itemstack));
    }
}
