package greekfantasy.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ScreenLoader {

    private ScreenLoader() {
    }

    public static void openQuestScreen(final Player playerIn, final int itemSlot, final ItemStack itemstack) {
        // only load client-side, of course
        if (!playerIn.level.isClientSide()) {
            return;
        }
        // open the gui
        Minecraft.getInstance().setScreen(new QuestScreen(itemSlot, itemstack));
    }

    public static void openInstrumentScreen(final Player playerIn, final int itemSlot, final ItemStack itemstack) {
        // only load client-side, of course
        if (!playerIn.level.isClientSide()) {
            return;
        }
        // open the gui
        Minecraft.getInstance().setScreen(new InstrumentScreen(itemSlot, itemstack));
    }
}
