package greekfantasy.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.DataResult;

import greekfantasy.GreekFantasy;
import greekfantasy.util.Song;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a single ResourceLocation ID
 * and the corresponding Song as it was read from JSON.
 **/
public class SPanfluteSongPacket {

  protected ResourceLocation songName;
  protected Song song;

  public SPanfluteSongPacket() { }

  /**
   * @param songNameIn the ResourceLocation ID of the song
   * @param songIn the Song
   **/
  public SPanfluteSongPacket(final ResourceLocation songNameIn, final Song songIn) {
    this.songName = songNameIn;
    this.song = songIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a SPanfluteSongPacket based on the PacketBuffer
   */
  public static SPanfluteSongPacket fromBytes(final PacketBuffer buf) {
    final ResourceLocation sName = buf.readResourceLocation();
    final CompoundNBT sNBT = buf.readNbt();
    final Optional<Song> sSong = GreekFantasy.PROXY.PANFLUTE_SONGS.readObject(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read deity from NBT for packet\n" + error));
    return new SPanfluteSongPacket(sName, sSong.orElse(Song.EMPTY));
  }

  /**
   * Writes the raw packet data to the data stream.
   * @param msg the SPanfluteSongPacket
   * @param buf the PacketBuffer
   */
  public static void toBytes(final SPanfluteSongPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = GreekFantasy.PROXY.PANFLUTE_SONGS.writeObject(msg.song);
    INBT tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write deity to NBT for packet\n" + error)).get();
    buf.writeResourceLocation(msg.songName);
    buf.writeNbt((CompoundNBT)tag);
  }

  /**
   * Handles the packet when it is received.
   * @param message the SPanfluteSongPacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final SPanfluteSongPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        GreekFantasy.PROXY.PANFLUTE_SONGS.put(message.songName, message.song);
      });
    }
    context.setPacketHandled(true);
  }
}
