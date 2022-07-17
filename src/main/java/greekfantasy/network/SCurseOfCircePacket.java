package greekfantasy.network;

import greekfantasy.GFRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Sent from the server to the client to ensure that an entity
 * with the Curse of Circe potion effect is correctly rendered as a pig.
 **/
public class SCurseOfCircePacket {

    protected int entity;
    protected int duration;

    public SCurseOfCircePacket() { }

    /**
     * @param entity   the entity ID of the entity to affect
     * @param duration the length of the potion effect in ticks (-1 to remove)
     **/
    private SCurseOfCircePacket(final int entity, final int duration) {
        this.entity = entity;
        this.duration = duration;
    }

    public static SCurseOfCircePacket addEffect(final int entity, final int duration) {
        return new SCurseOfCircePacket(entity, duration);
    }

    public static SCurseOfCircePacket removeEffect(final int entity) {
        return new SCurseOfCircePacket(entity, -1);
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a SCurseOfCircePacket based on the PacketBuffer
     */
    public static SCurseOfCircePacket fromBytes(final FriendlyByteBuf buf) {
        final int uuid = buf.readInt();
        final int duration = buf.readInt();
        return new SCurseOfCircePacket(uuid, duration);
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the SCurseOfCircePacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final SCurseOfCircePacket msg, final FriendlyByteBuf buf) {
        buf.writeInt(msg.entity);
        buf.writeInt(msg.duration);
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message         the SCurseOfCircePacket
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final SCurseOfCircePacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                // locate the client-side entity and ensure it receives the mob effect information
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                Entity entity = mc.level.getEntity(message.entity);
                // add or remove effect based on duration
                if (entity instanceof LivingEntity livingEntity) {
                    if(message.duration > 0) {
                        // add effect, play sound, and set persistence
                        livingEntity.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get(), message.duration));
                        livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F,
                                0.9F + livingEntity.getRandom().nextFloat() * 0.2F);
                        if (livingEntity instanceof Mob mob) {
                            mob.setPersistenceRequired();
                        }
                    } else {
                        // remove effect and play sound
                        livingEntity.removeEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get());
                        livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F,
                                0.9F + livingEntity.getRandom().nextFloat() * 0.2F);
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}
