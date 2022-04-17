package greekfantasy.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.DataResult;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.Deity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a single ResourceLocation ID
 * and the corresponding Deity as it was read from JSON.
 **/
public class SDeityPacket {

  protected ResourceLocation deityName;
  protected Deity deity;

  /**
   * @param deityNameIn the ResourceLocation ID of the Deity
   * @param deityIn the Deity
   **/
  public SDeityPacket(final ResourceLocation deityNameIn, final Deity deityIn) {
    this.deityName = deityNameIn;
    this.deity = deityIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a SDeityPacket based on the PacketBuffer
   */
  public static SDeityPacket fromBytes(final PacketBuffer buf) {
    final ResourceLocation sName = buf.readResourceLocation();
    final CompoundNBT sNBT = buf.readNbt();
    final Optional<Deity> sEffect = GreekFantasy.PROXY.DEITY.readObject(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read IDeity from NBT for packet\n" + error));
    return new SDeityPacket(sName, sEffect.orElse(Deity.EMPTY));
  }
  
  /**
   * Writes the raw packet data to the data stream.
   * @param msg the SDeityPacket
   * @param buf the PacketBuffer
   */
  public static void toBytes(final SDeityPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = GreekFantasy.PROXY.DEITY.writeObject(msg.deity);
    INBT tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write IDeity to NBT for packet\n" + error)).get();
    buf.writeResourceLocation(msg.deityName);
    buf.writeNbt((CompoundNBT)tag);
  }

  /**
   * Handles the packet when it is received.
   * @param message the SDeityPacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final SDeityPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        GreekFantasy.PROXY.DEITY.put(message.deityName, message.deity);
      });
    }
    context.setPacketHandled(true);
  }
}
