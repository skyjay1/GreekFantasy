package greekfantasy.network;

import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
import greekfantasy.util.Quest;
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
 * ResourceLocation IDs and Quests
 **/
public class SQuestPacket {

    protected static final Codec<Map<ResourceLocation, Quest>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Quest.CODEC);

    protected Map<ResourceLocation, Quest> data;

    /**
     * @param data the data map
     **/
    public SQuestPacket(final Map<ResourceLocation, Quest> data) {
        this.data = data;
        if (FMLEnvironment.dist != Dist.CLIENT) {
            // update server-side map
            GreekFantasy.QUEST_MAP.clear();
            GreekFantasy.QUEST_MAP.putAll(data);
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a SQuestPacket based on the PacketBuffer
     */
    public static SQuestPacket fromBytes(final FriendlyByteBuf buf) {
        final Map<ResourceLocation, Quest> data = buf.readWithCodec(CODEC);
        return new SQuestPacket(data);
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the SQuestPacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final SQuestPacket msg, final FriendlyByteBuf buf) {
        buf.writeWithCodec(CODEC, msg.data);
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message         the SQuestPacket
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final SQuestPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                GreekFantasy.QUEST_MAP.clear();
                GreekFantasy.QUEST_MAP.putAll(message.data);
            });
        }
        context.setPacketHandled(true);
    }
}
