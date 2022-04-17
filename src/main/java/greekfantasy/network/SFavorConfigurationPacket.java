package greekfantasy.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.DataResult;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.FavorConfiguration;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with the FavorConfiguration 
 * as it was read from JSON.
 **/
public class SFavorConfigurationPacket {

  protected FavorConfiguration favorConfig;

  /**
   * @param favorRangeIn the FavorConfiguration as it was read from JSON
   **/
  public SFavorConfigurationPacket(final FavorConfiguration favorRangeIn) {
    favorConfig = favorRangeIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a SFavorConfigurationPacket based on the PacketBuffer
   */
  public static SFavorConfigurationPacket fromBytes(final PacketBuffer buf) {
    final CompoundNBT sNBT = buf.readNbt();
    final Optional<FavorConfiguration> sEffect = GreekFantasy.PROXY.FAVOR_CONFIGURATION.readObject(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read FavorConfiguration from NBT for packet\n" + error));
    return new SFavorConfigurationPacket(sEffect.orElse(FavorConfiguration.EMPTY));
  }

  /**
   * Writes the raw packet data to the data stream.
   * @param msg the SFavorConfigurationPacket
   * @param buf the PacketBuffer
   */
  public static void toBytes(final SFavorConfigurationPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = GreekFantasy.PROXY.FAVOR_CONFIGURATION.writeObject(msg.favorConfig);
    INBT tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write FavorConfiguration to NBT for packet\n" + error)).get();
    buf.writeNbt((CompoundNBT)tag);
  }

  /**
   * Handles the packet when it is received.
   * @param message the SFavorConfigurationPacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final SFavorConfigurationPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        GreekFantasy.PROXY.FAVOR_CONFIGURATION.put(FavorConfiguration.NAME, message.favorConfig);
      });
    }
    context.setPacketHandled(true);
  }
}
