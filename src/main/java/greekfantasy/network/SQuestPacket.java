package greekfantasy.network;

import com.mojang.serialization.DataResult;
import greekfantasy.GreekFantasy;
import greekfantasy.util.Quest;
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
public class SQuestPacket {

    protected ResourceLocation questId;
    protected Quest quest;

    public SQuestPacket() {
    }

    /**
     * @param questId the ResourceLocation ID of the quest
     * @param quest     the Quest
     **/
    public SQuestPacket(final ResourceLocation questId, final Quest quest) {
        this.questId = questId;
        this.quest = quest;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a SQuestPacket based on the PacketBuffer
     */
    public static SQuestPacket fromBytes(final FriendlyByteBuf buf) {
        final ResourceLocation sName = buf.readResourceLocation();
        final CompoundTag sNBT = buf.readNbt();
        final Optional<Quest> sQuest = GreekFantasy.QUESTS.readObject(sNBT).resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to read quest from NBT for packet\n" + error));
        return new SQuestPacket(sName, sQuest.orElse(Quest.EMPTY));
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the SQuestPacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final SQuestPacket msg, final FriendlyByteBuf buf) {
        DataResult<Tag> nbtResult = GreekFantasy.QUESTS.writeObject(msg.quest);
        Tag tag = nbtResult.resultOrPartial(error -> GreekFantasy.LOGGER.error("Failed to write quest to NBT for packet\n" + error)).get();
        buf.writeResourceLocation(msg.questId);
        buf.writeNbt((CompoundTag) tag);
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
                GreekFantasy.QUESTS.put(message.questId, message.quest);
            });
        }
        context.setPacketHandled(true);
    }
}
