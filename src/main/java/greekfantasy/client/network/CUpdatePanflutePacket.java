package greekfantasy.client.network;

import java.util.function.Supplier;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.PanfluteItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class CUpdatePanflutePacket {

  private int slot;
  private String songName;
  
  public CUpdatePanflutePacket() { }

  public CUpdatePanflutePacket(final int slotIn, final String songNameIn) {
    this.slot = slotIn;
    this.songName = songNameIn;
  }

  public int getSlot() {
    return this.slot;
  }
  
  public String getSongName() {
    return songName;
  }
  
  /**
   * Reads the raw packet data from the data stream.
   */
  public static CUpdatePanflutePacket fromBytes(final PacketBuffer buf) {
    final int msgSlot = buf.readInt();
    final String msgSongName = buf.readString();
    return new CUpdatePanflutePacket(msgSlot, msgSongName);
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final CUpdatePanflutePacket msg, final PacketBuffer buf) {
    buf.writeInt(msg.getSlot());
    buf.writeString(msg.getSongName());
  }

  public static void handlePacket(final CUpdatePanflutePacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
      context.enqueueWork(() -> {
        final ServerPlayerEntity player = context.getSender();
        // make sure they are holding a panflute in this slot
        if(message.getSlot() >= 0 && message.getSlot() < player.inventory.getSizeInventory()) {
          final ItemStack stack = player.inventory.getStackInSlot(message.getSlot());
          if(stack.getItem() == GFRegistry.PANFLUTE) {
            // update the song stored in the panflute NBT
            stack.getOrCreateTag().putString(PanfluteItem.KEY_SONG, message.getSongName());
            player.inventory.setInventorySlotContents(message.getSlot(), stack);
          }
        }
      });
    }
    context.setPacketHandled(true);
  }
}
