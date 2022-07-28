package greekfantasy.network;

import com.mojang.serialization.DataResult;
import greekfantasy.GreekFantasy;
import greekfantasy.util.Song;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a single ResourceLocation ID
 * and the corresponding Song as it was read from JSON.
 **/
public class SSongPacket {

    protected ResourceLocation songName;
    protected Song song;

    public SSongPacket() {
    }

    /**
     * @param songNameIn the ResourceLocation ID of the song
     * @param songIn     the Song
     **/
    public SSongPacket(final ResourceLocation songNameIn, final Song songIn) {
        this.songName = songNameIn;
        this.song = songIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a SSongPacket based on the PacketBuffer
     */
    public static SSongPacket fromBytes(final FriendlyByteBuf buf) {
        final ResourceLocation sName = buf.readResourceLocation();
        final CompoundTag sNBT = buf.readNbt();
        final Optional<Song> sSong = GreekFantasy.SONGS.readObject(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read deity from NBT for packet\n" + error));
        return new SSongPacket(sName, sSong.orElse(Song.EMPTY));
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the SSongPacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final SSongPacket msg, final FriendlyByteBuf buf) {
        DataResult<Tag> nbtResult = GreekFantasy.SONGS.writeObject(msg.song);
        Tag tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write deity to NBT for packet\n" + error)).get();
        buf.writeResourceLocation(msg.songName);
        buf.writeNbt((CompoundTag) tag);
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message         the SSongPacket
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final SSongPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                GreekFantasy.SONGS.put(message.songName, message.song);
            });
        }
        context.setPacketHandled(true);
    }
}
