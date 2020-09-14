package greekfantasy.client.network;

import java.util.function.Supplier;

import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Sent from the client to the server to update a StatueTileEntity
 * when the player closes a StatueContainer GUI
 **/
public class CUpdateStatuePosePacket {

  private BlockPos blockPos = BlockPos.ZERO;
  private StatuePose statuePose = StatuePoses.NONE;

  public CUpdateStatuePosePacket() { }

  public CUpdateStatuePosePacket(final BlockPos blockPosIn, final StatuePose statuePoseIn) {
    this.blockPos = blockPosIn;
    this.statuePose = statuePoseIn;
  }

  public BlockPos getBlockPos() {
    return this.blockPos;
  }

  public StatuePose getStatuePose() {
    return this.statuePose;
  }
  
  /**
   * Reads the raw packet data from the data stream.
   */
  public static CUpdateStatuePosePacket fromBytes(final PacketBuffer buf) {
    final BlockPos blockPos = buf.readBlockPos();
    final CompoundNBT nbt = buf.readCompoundTag();
    return new CUpdateStatuePosePacket(blockPos, new StatuePose(nbt));
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final CUpdateStatuePosePacket msg, final PacketBuffer buf) {
    buf.writeBlockPos(msg.getBlockPos());
    buf.writeCompoundTag(msg.getStatuePose().serializeNBT());
  }

  public static void handlePacket(final CUpdateStatuePosePacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
      context.enqueueWork(() -> {
        final ServerPlayerEntity player = context.getSender();
        if (message.getBlockPos().distanceSq(player.getPosition()) < 100.0D) {
          final TileEntity tileentity = context.getSender().getEntityWorld().getTileEntity(message.getBlockPos());
          if (tileentity instanceof StatueTileEntity) {
            ((StatueTileEntity) tileentity).setStatuePose(message.getStatuePose());
          }
        }
      });
    }
    context.setPacketHandled(true);
  }
}
