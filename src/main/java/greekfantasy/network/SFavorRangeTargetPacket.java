package greekfantasy.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.DataResult;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor_effect.FavorConfiguration;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class SFavorRangeTargetPacket {

  protected FavorConfiguration favorRange;

  public SFavorRangeTargetPacket(final FavorConfiguration favorRangeIn) {
    favorRange = favorRangeIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   */
  public static SFavorRangeTargetPacket fromBytes(final PacketBuffer buf) {
    final CompoundNBT sNBT = buf.readCompoundTag();
    final Optional<FavorConfiguration> sEffect = GreekFantasy.PROXY.FAVOR_CONFIGURATION.readObject(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read FavorConfiguration from NBT for packet\n" + error));
    return new SFavorRangeTargetPacket(sEffect.orElse(FavorConfiguration.EMPTY));
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final SFavorRangeTargetPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = GreekFantasy.PROXY.FAVOR_CONFIGURATION.writeObject(msg.favorRange);
    INBT tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write FavorConfiguration to NBT for packet\n" + error)).get();
    buf.writeCompoundTag((CompoundNBT)tag);
  }

  public static void handlePacket(final SFavorRangeTargetPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        GreekFantasy.PROXY.FAVOR_CONFIGURATION.put(FavorConfiguration.NAME, message.favorRange);
        // DEBUG
        GreekFantasy.LOGGER.debug(message.favorRange.toString());
      });
    }
    context.setPacketHandled(true);
  }
}
