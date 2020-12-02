package greekfantasy.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.DataResult;

import greekfantasy.GreekFantasy;
import greekfantasy.util.PanfluteSong;
import greekfantasy.util.PanfluteSongs;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class SPanfluteSongPacket {

  protected ResourceLocation songName;
  protected PanfluteSong song;

  public SPanfluteSongPacket() { }

  public SPanfluteSongPacket(final ResourceLocation songNameIn, final PanfluteSong songIn) {
    this.songName = songNameIn;
    this.song = songIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   */
  public static SPanfluteSongPacket fromBytes(final PacketBuffer buf) {
    final ResourceLocation sName = buf.readResourceLocation();
    final CompoundNBT sNBT = buf.readCompoundTag();
    final Optional<PanfluteSong> sSong = PanfluteSongs.readSong(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read song from NBT for packet\n" + error));
    return new SPanfluteSongPacket(sName, sSong.orElse(PanfluteSong.EMPTY));
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final SPanfluteSongPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = PanfluteSongs.writeSong(msg.song);
    INBT tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write song to NBT for packet\n" + error)).get();
    buf.writeResourceLocation(msg.songName);
    buf.writeCompoundTag((CompoundNBT)tag);
  }

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
