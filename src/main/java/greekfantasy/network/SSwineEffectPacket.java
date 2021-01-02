package greekfantasy.network;

import java.util.function.Supplier;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSwineEffectPacket {

  protected int entity;
  protected int effect;
  
  public SSwineEffectPacket() { }

  public SSwineEffectPacket(final int entityIn, final int effectIn) {
    entity = entityIn;
    effect = effectIn;
  }

  /**
   * Reads the raw packet data from the data stream.
   */
  public static SSwineEffectPacket fromBytes(final PacketBuffer buf) {
    final int uuid = buf.readInt();
    final int duration = buf.readInt();
    return new SSwineEffectPacket(uuid, duration);
  }

  /**
   * Writes the raw packet data to the data stream.
   */
  public static void toBytes(final SSwineEffectPacket msg, final PacketBuffer buf) {
    buf.writeInt(msg.entity);
    buf.writeInt(msg.effect);
  }

  public static void handlePacket(final SSwineEffectPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        Entity e = mc.world.getEntityByID(message.entity);
        if(e instanceof LivingEntity) {
          ((LivingEntity) e).addPotionEffect(new EffectInstance(GFRegistry.SWINE_EFFECT, message.effect));
        }
      });
    }
    context.setPacketHandled(true);
  }
}
