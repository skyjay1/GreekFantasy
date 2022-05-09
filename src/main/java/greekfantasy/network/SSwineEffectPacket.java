package greekfantasy.network;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Sent from the server to the client to ensure that an entity
 * with the Swine potion effect is correctly rendered as a pig.
 **/
public class SSwineEffectPacket {

    protected int entity;
    protected int duration;

    public SSwineEffectPacket() {
    }

    /**
     * @param entityIn   the entity ID of the entity to affect
     * @param durationIn the length of the potion effect in ticks
     **/
    public SSwineEffectPacket(final int entityIn, final int durationIn) {
        entity = entityIn;
        duration = durationIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a SSwineEffectPacket based on the PacketBuffer
     */
    public static SSwineEffectPacket fromBytes(final PacketBuffer buf) {
        final int uuid = buf.readInt();
        final int duration = buf.readInt();
        return new SSwineEffectPacket(uuid, duration);
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the SSwineEffectPacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final SSwineEffectPacket msg, final PacketBuffer buf) {
        buf.writeInt(msg.entity);
        buf.writeInt(msg.duration);
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message         the SSwineEffectPacket
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final SSwineEffectPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                Entity e = mc.level.getEntity(message.entity);
                if (e instanceof LivingEntity) {
                    ((LivingEntity) e).addEffect(new EffectInstance(GFRegistry.MobEffectReg.PIG_EFFECT, message.duration));
                }
            });
        }
        context.setPacketHandled(true);
    }
}
