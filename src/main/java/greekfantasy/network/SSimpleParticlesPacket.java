package greekfantasy.network;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSimpleParticlesPacket {
  protected boolean isHappy;
  protected BlockPos pos = BlockPos.ZERO;
  protected int count = 0;
  
  public SSimpleParticlesPacket() { }

  public SSimpleParticlesPacket(final boolean isHappyIn, final BlockPos posIn, final int countIn) {
    isHappy = isHappyIn;
    pos = posIn;
    count = countIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   */
  public static SSimpleParticlesPacket fromBytes(final PacketBuffer buf) {
    final boolean happy = buf.readBoolean();
    final BlockPos pos = buf.readBlockPos();
    final int count = buf.readVarInt();
    return new SSimpleParticlesPacket(happy, pos, count);
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final SSimpleParticlesPacket msg, final PacketBuffer buf) {
    buf.writeBoolean(msg.isHappy);
    buf.writeBlockPos(msg.pos);
    buf.writeVarInt(msg.count);
  }

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
