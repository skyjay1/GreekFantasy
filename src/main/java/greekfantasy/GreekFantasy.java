package greekfantasy;

import greekfantasy.network.SCurseOfCircePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static boolean isRpgGodsLoaded;

    public GreekFantasy() {
        // register config
        GreekFantasy.LOGGER.debug("registerConfig");
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::intermodEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::loadConfig);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::reloadConfig);

        // register messages
        GreekFantasy.LOGGER.debug("registerNetwork");
        int messageId = 0;
        //CHANNEL.registerMessage(messageId++, CUpdateInstrumentPacket.class, CUpdateInstrumentPacket::toBytes, CUpdateInstrumentPacket::fromBytes, CUpdateInstrumentPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        //CHANNEL.registerMessage(messageId++, SPanfluteSongPacket.class, SPanfluteSongPacket::toBytes, SPanfluteSongPacket::fromBytes, SPanfluteSongPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, SCurseOfCircePacket.class, SCurseOfCircePacket::toBytes, SCurseOfCircePacket::fromBytes, SCurseOfCircePacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //CHANNEL.registerMessage(messageId++, CUseEnchantmentPacket.class, CUseEnchantmentPacket::toBytes, CUseEnchantmentPacket::fromBytes, CUseEnchantmentPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));

    }

    public static void setup(final FMLCommonSetupEvent event) {

    }

    public static void intermodEnqueue(final InterModEnqueueEvent event) {
        isRpgGodsLoaded = ModList.get().isLoaded("rpggods");
    }

    public static void loadConfig(final ModConfigEvent.Loading event) {
        CONFIG.bake();
    }

    public static void reloadConfig(final ModConfigEvent.Reloading event) {
        CONFIG.bake();
    }

    public static boolean isRGLoaded() {
        return isRpgGodsLoaded;
    }
}
