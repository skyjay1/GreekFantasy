package greekfantasy.network;

import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
import greekfantasy.util.Song;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Called when datapacks are (re)loaded.
 * Sent from the server to the client with a map of
 * ResourceLocation IDs and Songs
 **/
public class SSongPacket {

    protected static final Codec<Map<ResourceLocation, Song>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Song.CODEC);

    protected Map<ResourceLocation, Song> data;

    /**
     * @param data the data map
     **/
    public SSongPacket(final Map<ResourceLocation, Song> data) {
        this.data = data;
        if (FMLEnvironment.dist != Dist.CLIENT) {
            // update server-side map
            GreekFantasy.SONG_MAP.clear();
            GreekFantasy.SONG_MAP.putAll(data);
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a SSongPacket based on the PacketBuffer
     */
    public static SSongPacket fromBytes(final FriendlyByteBuf buf) {
        final Map<ResourceLocation, Song> data = buf.readWithCodec(CODEC);
        return new SSongPacket(data);
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the SSongPacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final SSongPacket msg, final FriendlyByteBuf buf) {
        buf.writeWithCodec(CODEC, msg.data);
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
                GreekFantasy.SONG_MAP.clear();
                GreekFantasy.SONG_MAP.putAll(message.data);
            });
        }
        context.setPacketHandled(true);
    }
}
