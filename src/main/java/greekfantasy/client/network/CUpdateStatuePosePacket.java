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
  private boolean statueFemale = false;
  private String textureName = "";

  public CUpdateStatuePosePacket() { }

  public CUpdateStatuePosePacket(final BlockPos blockPosIn, final StatuePose statuePoseIn, 
      final boolean statueFemaleIn, final String textureNameIn) {
    this.blockPos = blockPosIn;
    this.statuePose = statuePoseIn;
    this.statueFemale = statueFemaleIn;
    this.textureName = textureNameIn;
  }

  public BlockPos getBlockPos() {
    return this.blockPos;
  }

  public StatuePose getStatuePose() {
    return this.statuePose;
  }
  
  public boolean isStatueFemale() {
    return this.statueFemale;
  }
  
  public String getTextureName() {
    return this.textureName;
  }
  
  /**
   * Reads the raw packet data from the data stream.
   */
  public static CUpdateStatuePosePacket fromBytes(final PacketBuffer buf) {
    final BlockPos blockPos = buf.readBlockPos();
    final CompoundNBT nbt = buf.readCompoundTag();
    final boolean female = buf.readBoolean();
    final String textureName = buf.readString();
    return new CUpdateStatuePosePacket(blockPos, new StatuePose(nbt), female, textureName);
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final CUpdateStatuePosePacket msg, final PacketBuffer buf) {
    buf.writeBlockPos(msg.getBlockPos());
    buf.writeCompoundTag(msg.getStatuePose().serializeNBT());
    buf.writeBoolean(msg.isStatueFemale());
    buf.writeString(msg.getTextureName());
  }

  public static void handlePacket(final CUpdateStatuePosePacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
      context.enqueueWork(() -> {
        final ServerPlayerEntity player = context.getSender();
        // make sure the player is in range of the given position
        if (message.getBlockPos().distanceSq(player.getPosition()) < 100.0D) {
          final TileEntity tileentity = context.getSender().getEntityWorld().getTileEntity(message.getBlockPos());
          if (tileentity instanceof StatueTileEntity) {
            // update the tile entity using info from this packet
            final StatueTileEntity statueTileEntity = (StatueTileEntity)tileentity;
            statueTileEntity.setStatuePose(message.getStatuePose());
            statueTileEntity.setStatueFemale(message.isStatueFemale(), true);
            statueTileEntity.setTextureName(message.getTextureName(), true);
          }
        }
      });
    }
    context.setPacketHandled(true);
  }
}
