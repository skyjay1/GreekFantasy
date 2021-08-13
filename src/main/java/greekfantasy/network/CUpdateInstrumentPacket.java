package greekfantasy.network;

import java.util.function.Supplier;

import greekfantasy.item.InstrumentItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Created when an InstrumentItem is used and the song is updated.
 * The packet sends the player, item slot, and song name to the server
 * to update the NBT data.
 **/
public class CUpdateInstrumentPacket {

  protected int slot;
  protected ResourceLocation songName;
  
  public CUpdateInstrumentPacket() { }

  /**
   * @param slotIn the ID of the inventory slot of the instrument ItemStack
   * @param songNameIn the name of the song to apply to the ItemStack
   **/
  public CUpdateInstrumentPacket(final int slotIn, final ResourceLocation songNameIn) {
    this.slot = slotIn;
    this.songName = songNameIn;
  }

  public int getSlot() {
    return this.slot;
  }
  
  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a CUpdateInstrumentPacket based on the PacketBuffer
   */
  public static CUpdateInstrumentPacket fromBytes(final PacketBuffer buf) {
    final int msgSlot = buf.readInt();
    final ResourceLocation msgSongName = buf.readResourceLocation();
    return new CUpdateInstrumentPacket(msgSlot, msgSongName);
  }

  /**
   * Writes the raw packet data to the data stream.
   * @param msg the CUpdateInstrumentPacket
   * @param buf the PacketBuffer
   */
  public static void toBytes(final CUpdateInstrumentPacket msg, final PacketBuffer buf) {
    buf.writeInt(msg.getSlot());
    buf.writeResourceLocation(msg.songName);
  }

  /**
   * Handles the packet when it is received.
   * @param message the CUpdateInstrumentPacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final CUpdateInstrumentPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
      context.enqueueWork(() -> {
        final ServerPlayerEntity player = context.getSender();
        // make sure they are holding an instrument in this slot
        if(message.getSlot() >= 0 && message.getSlot() < player.inventory.getSizeInventory()) {
          final ItemStack stack = player.inventory.getStackInSlot(message.getSlot());
          if(stack.getItem() instanceof InstrumentItem) {
            // update the deity stored in the instrument NBT
            InstrumentItem.writeSong(stack, message.songName);
            player.inventory.setInventorySlotContents(message.getSlot(), stack);
          }
        }
      });
    }
    context.setPacketHandled(true);
  }
}
