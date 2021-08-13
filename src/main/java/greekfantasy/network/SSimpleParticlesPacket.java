package greekfantasy.network;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Sent from the server to the client when a server-side action occurs,
 * especially those that use or modify Favor. When received client-side, 
 * spawns up to {@link Byte#MAX_VALUE} particles that are either 
 * {@link ParticleTypes#HAPPY_VILLAGER} or {@link ParticleTypes#ANGRY_VILLAGER}
 */
public class SSimpleParticlesPacket {
  protected boolean isHappy;
  protected BlockPos pos = BlockPos.ZERO;
  protected byte count = 0;
  
  public SSimpleParticlesPacket() { }

  /**
   * @param isHappyIn true to use HAPPY particles, false to use ANGRY particles
   * @param posIn the BlockPos at which the particles will be centered
   * @param countIn the number of particles to spawn (max is 127)
   */
  public SSimpleParticlesPacket(final boolean isHappyIn, final BlockPos posIn, final int countIn) {
    isHappy = isHappyIn;
    pos = posIn;
    count = (byte)Math.max(countIn, Byte.MAX_VALUE);
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the PacketBuffer
   * @return a new instance of a SSimpleParticlesPacket based on the PacketBuffer
   */
  public static SSimpleParticlesPacket fromBytes(final PacketBuffer buf) {
    final boolean happy = buf.readBoolean();
    final BlockPos pos = buf.readBlockPos();
    final int count = buf.readByte();
    return new SSimpleParticlesPacket(happy, pos, count);
  }

  /**
   * Writes the raw packet data to the data stream.
   * @param msg the SSimpleParticlesPacket
   * @param buf the PacketBuffer
   */
  public static void toBytes(final SSimpleParticlesPacket msg, final PacketBuffer buf) {
    buf.writeBoolean(msg.isHappy);
    buf.writeBlockPos(msg.pos);
    buf.writeByte(msg.count);
  }

  /**
   * Handles the packet when it is received.
   * @param message the SSimpleParticlesPacket
   * @param contextSupplier the NetworkEvent.Context supplier
   */
  public static void handlePacket(final SSimpleParticlesPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        IParticleData particle = message.isHappy ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.ANGRY_VILLAGER;
        final Random rand = mc.player.getRNG();
        for (int i = 0; i < message.count; ++i) {
          double x2 = message.pos.getX() + rand.nextDouble();
          double y2 = message.pos.getY() + rand.nextDouble();
          double z2 = message.pos.getZ() + rand.nextDouble();
          mc.world.addParticle(particle, x2, y2, z2, 0, 0, 0);
        }
      });
    }
    context.setPacketHandled(true);
  }
}
