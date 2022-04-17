package greekfantasy.network;

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
 * Created when the player closes a StatueContainer GUI.
 * The packet sends the BlockPos, StatuePose, and other settings
 * to the server to update the StatueTileEntity NBT data. 
 **/
public class CUpdateStatuePosePacket {

  protected static final int NAME_LEN = 16;
  protected BlockPos blockPos = BlockPos.ZERO;
  protected StatuePose statuePose = StatuePoses.NONE;
  protected boolean statueFemale = false;
  protected String textureName = "";

  public CUpdateStatuePosePacket() { }

  /**
   * @param blockPosIn the BlockPos of the statue TileEntity (lower half)
   * @param statuePoseIn the StatuePose settings
   * @param statueFemaleIn true if the statue uses the female model
   * @param textureNameIn the String name of the texture to use
   **/
  public CUpdateStatuePosePacket(final BlockPos blockPosIn, final StatuePose statuePoseIn, 
      final boolean statueFemaleIn, final String textureNameIn) {
    this.blockPos = blockPosIn;
    this.statuePose = statuePoseIn;
    this.statueFemale = statueFemaleIn;
    this.textureName = textureNameIn;
  }
  
  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a CUpdateStatuePosePacket based on the PacketBuffer
   */
    public static CUpdateStatuePosePacket fromBytes(final PacketBuffer buf) {
    final BlockPos blockPos = buf.readBlockPos();
    final CompoundNBT nbt = buf.readNbt();
    final boolean female = buf.readBoolean();
    final String textureName = buf.readUtf(NAME_LEN);
    return new CUpdateStatuePosePacket(blockPos, new StatuePose(nbt), female, textureName);
  }

  /**
   * Writes the raw packet data to the data stream.
   * @param msg the CUpdateStatuePosePacket
   * @param buf the PacketBuffer
   */
    public static void toBytes(final CUpdateStatuePosePacket msg, final PacketBuffer buf) {
    buf.writeBlockPos(msg.blockPos);
    buf.writeNbt(msg.statuePose.serializeNBT());
    buf.writeBoolean(msg.statueFemale);
    String name = msg.textureName;
    if(name.length() > NAME_LEN) {
      name = name.substring(0, NAME_LEN);
    }
    buf.writeUtf(name, NAME_LEN);
  }

  /**
   * Handles the packet when it is received.
   * @param message the CUpdateStatuePosePacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final CUpdateStatuePosePacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
      context.enqueueWork(() -> {
        final ServerPlayerEntity player = context.getSender();
        // make sure the player is in range of the given position
        if (message.blockPos.distSqr(player.blockPosition()) < 100.0D) {
          final TileEntity tileentity = context.getSender().getCommandSenderWorld().getBlockEntity(message.blockPos);
          if (tileentity instanceof StatueTileEntity) {
            // update the tile entity using info from this packet
            final StatueTileEntity statueTileEntity = (StatueTileEntity) tileentity;
            statueTileEntity.setStatuePose(message.statuePose);
            statueTileEntity.setStatueFemale(message.statueFemale, true);
            statueTileEntity.setTextureName(message.textureName, true);
          }
        }
      });
    }
    context.setPacketHandled(true);
  }
}
