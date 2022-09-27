package greekfantasy;

import greekfantasy.capability.IFriendlyGuardian;
import greekfantasy.network.CPlayNotePacket;
import greekfantasy.network.SCurseOfCircePacket;
import greekfantasy.network.SQuestPacket;
import greekfantasy.network.SSongPacket;
import greekfantasy.util.CodecJsonDataManager;
import greekfantasy.util.Quest;
import greekfantasy.util.Song;
import greekfantasy.worldgen.maze.WeightedTemplateList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod(GreekFantasy.MODID)
public class GreekFantasy {

    public static final String MODID = "greekfantasy";

    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
    public static GFConfig CONFIG = new GFConfig(CONFIG_BUILDER);
    private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();

    private static final String PROTOCOL_VERSION = "3";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static final Logger LOGGER = LogManager.getFormatterLogger(GreekFantasy.MODID);

    public static final Capability<IFriendlyGuardian> FRIENDLY_GUARDIAN_CAP = CapabilityManager.get(new CapabilityToken<>(){});

    private static boolean isRpgGodsLoaded;

    private static final CodecJsonDataManager<Song> SONG_JSON_MANAGER = new CodecJsonDataManager<>("songs", Song.CODEC);
    public static final Map<ResourceLocation, Song> SONG_MAP = new HashMap<>();

    private static final CodecJsonDataManager<Quest> QUEST_JSON_MANAGER = new CodecJsonDataManager<>("quests", Quest.CODEC);
    public static final Map<ResourceLocation, Quest> QUEST_MAP = new HashMap<>();

    private static final CodecJsonDataManager<WeightedTemplateList> MAZE_PIECE_JSON_MANAGER = new CodecJsonDataManager<>("worldgen/maze_piece", WeightedTemplateList.CODEC);

    public GreekFantasy() {
        // register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
        // registry listeners
        GFRegistry.register();
        // event listeners
        MinecraftForge.EVENT_BUS.register(GFEvents.ForgeHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFEvents.ModHandler.class);
        // client-only event listeners
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(greekfantasy.client.GFClientEvents.ForgeHandler.class);
            FMLJavaModLoadingContext.get().getModEventBus().register(greekfantasy.client.GFClientEvents.ModHandler.class);
        });
        // other listeners
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::loadConfig);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::reloadConfig);
        MinecraftForge.EVENT_BUS.addListener(GreekFantasy::addReloadListeners);

        // register messages
        int messageId = 0;
        CHANNEL.registerMessage(messageId++, SSongPacket.class, SSongPacket::toBytes, SSongPacket::fromBytes, SSongPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, SQuestPacket.class, SQuestPacket::toBytes, SQuestPacket::fromBytes, SQuestPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, SCurseOfCircePacket.class, SCurseOfCircePacket::toBytes, SCurseOfCircePacket::fromBytes, SCurseOfCircePacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, CPlayNotePacket.class, CPlayNotePacket::toBytes, CPlayNotePacket::fromBytes, CPlayNotePacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // data managers
        QUEST_JSON_MANAGER.subscribeAsSyncable(CHANNEL, SQuestPacket::new);
        SONG_JSON_MANAGER.subscribeAsSyncable(CHANNEL, SSongPacket::new);
    }

    public static void setup(final FMLCommonSetupEvent event) {
        isRpgGodsLoaded = ModList.get().isLoaded("rpggods");
    }

    public static void loadConfig(final ModConfigEvent.Loading event) {
        CONFIG.bake();
    }

    public static void reloadConfig(final ModConfigEvent.Reloading event) {
        CONFIG.bake();
    }

    public static void addReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(QUEST_JSON_MANAGER);
        event.addListener(SONG_JSON_MANAGER);
        event.addListener(MAZE_PIECE_JSON_MANAGER);
    }

    public static WeightedTemplateList getMazePiece(final ResourceLocation id) {
        return MAZE_PIECE_JSON_MANAGER.getData().getOrDefault(id, WeightedTemplateList.EMPTY);
    }

    public static boolean isRGLoaded() {
        return isRpgGodsLoaded;
    }
}
