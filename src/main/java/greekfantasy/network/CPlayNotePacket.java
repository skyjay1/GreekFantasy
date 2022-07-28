package greekfantasy.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * Sent from the server to the client to ensure that all nearby players
 * see a note and hear the sound
 **/
public class CPlayNotePacket {

    protected int entity;
    protected int note;
    protected float volume;
    protected SoundEvent sound;

    public CPlayNotePacket() {
    }

    /**
     * @param entity the entity ID of the entity to play the sound
     * @param note   the integer value of the note
     * @param sound  the sound
     * @param volume the sound volume
     **/
    public CPlayNotePacket(final int entity, final int note, final SoundEvent sound, final float volume) {
        this.entity = entity;
        this.note = note;
        this.sound = sound;
        this.volume = volume;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a CPlayNotePacket based on the PacketBuffer
     */
    public static CPlayNotePacket fromBytes(final FriendlyByteBuf buf) {
        final int uuid = buf.readInt();
        final int note = buf.readInt();
        final SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
        final float volume = buf.readFloat();
        return new CPlayNotePacket(uuid, note, sound, volume);
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the CPlayNotePacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final CPlayNotePacket msg, final FriendlyByteBuf buf) {
        buf.writeInt(msg.entity);
        buf.writeInt(msg.note);
        buf.writeResourceLocation(ForgeRegistries.SOUND_EVENTS.getKey(msg.sound));
        buf.writeFloat(msg.volume);
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message         the CPlayNotePacket
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final CPlayNotePacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
                // locate the server-side entity and play the sound at its location
                ServerPlayer player = context.getSender();
                ServerLevel level = player.getLevel();
                Entity entity = level.getEntity(message.entity);
                if(null == entity) {
                    entity = player;
                }
                int note = Mth.clamp(message.note, 0, 25);
                final float volume = Mth.clamp(message.volume, 0.0F, 3.0F);
                final float pitch = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);
                double noteData = ((float) Math.pow(2.0D, (double) (note) / 24.0D));
                // send particles
                level.sendParticles(player, ParticleTypes.NOTE, true,
                        entity.getX(), entity.getEyeY(), entity.getZ(),
                        0, 1.0F, 0, 0, noteData);
                // play sound
                level.playSound(null, entity, message.sound, SoundSource.PLAYERS, volume, pitch);
            });
        }
        context.setPacketHandled(true);
    }
}
