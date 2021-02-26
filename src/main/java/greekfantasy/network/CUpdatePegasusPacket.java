package greekfantasy.network;

import java.util.function.Supplier;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.PegasusEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class CUpdatePegasusPacket {
  
  protected int entityId;
  protected int data;
  
  public CUpdatePegasusPacket() { }

  public CUpdatePegasusPacket(final int entityIdIn, final int dataIn) {
    this.entityId = entityIdIn;
    this.data = dataIn;
  }
  
  /**
   * Reads the raw packet data from the data stream.
   */
  public static CUpdatePegasusPacket fromBytes(final PacketBuffer buf) {
    final int e = buf.readInt();
    final int d = buf.readInt();
    return new CUpdatePegasusPacket(e, d);
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final CUpdatePegasusPacket msg, final PacketBuffer buf) {
    buf.writeInt(msg.entityId);
    buf.writeInt(msg.data);
  }

  public static void handlePacket(final CUpdatePegasusPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
      context.enqueueWork(() -> {
        final ServerPlayerEntity player = context.getSender();
        final Entity riding = player.getRidingEntity();
        if(riding instanceof PegasusEntity && ((PegasusEntity)riding).canJump() && riding.getEntityId() == message.entityId) {
          ((PegasusEntity)riding).handlePegasusUpdate(message.data);
        }
      });
    }
    context.setPacketHandled(true);
  }
}
