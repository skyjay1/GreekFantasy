package greekfantasy.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.DataResult;

import greekfantasy.GreekFantasy;
import greekfantasy.favor.FavorEffect;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class SFavorEffectPacket {

  protected ResourceLocation favorEffectName;
  protected FavorEffect favorEffect;

  public SFavorEffectPacket(final ResourceLocation songNameIn, final FavorEffect songIn) {
    this.favorEffectName = songNameIn;
    this.favorEffect = songIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   */
  public static SFavorEffectPacket fromBytes(final PacketBuffer buf) {
    final ResourceLocation sName = buf.readResourceLocation();
    final CompoundNBT sNBT = buf.readCompoundTag();
    final Optional<FavorEffect> sEffect = GreekFantasy.PROXY.FAVOR_EFFECTS.readObject(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read FavorEffect from NBT for packet\n" + error));
    return new SFavorEffectPacket(sName, sEffect.orElse(FavorEffect.EMPTY));
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final SFavorEffectPacket msg, final PacketBuffer buf) {
    DataResult<INBT> nbtResult = GreekFantasy.PROXY.FAVOR_EFFECTS.writeObject(msg.favorEffect);
    INBT tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write FavorEffect to NBT for packet\n" + error)).get();
    buf.writeResourceLocation(msg.favorEffectName);
    buf.writeCompoundTag((CompoundNBT)tag);
  }

  public static void handlePacket(final SFavorEffectPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        GreekFantasy.PROXY.FAVOR_EFFECTS.put(message.favorEffectName, message.favorEffect);
      });
    }
    context.setPacketHandled(true);
  }
}
