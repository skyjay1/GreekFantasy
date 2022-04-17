package greekfantasy;

import greekfantasy.network.CUpdateInstrumentPacket;
import greekfantasy.network.CUseEnchantmentPacket;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.network.SSimpleParticlesPacket;
import greekfantasy.network.SSwineEffectPacket;
import greekfantasy.proxy.ClientProxy;
import greekfantasy.proxy.Proxy;
import greekfantasy.proxy.ServerProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(GreekFantasy.MODID)
public class GreekFantasy {

    public static final String MODID = "greekfantasy";

    public static final Proxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
    public static GFConfig CONFIG = new GFConfig(CONFIG_BUILDER);
    private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();

    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static final Logger LOGGER = LogManager.getFormatterLogger(GreekFantasy.MODID);

    public GreekFantasy() {
        // register mod event listeners
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFWorldGen.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::loadConfig);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::reloadConfig);
        // register config
        GreekFantasy.LOGGER.debug("registerConfig");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
        // register side-specific or common event handlers
        PROXY.registerEventHandlers();
        // register reload listeners (not currently used)
        PROXY.registerReloadListeners();
        // register messages
        GreekFantasy.LOGGER.debug("registerNetwork");
        int messageId = 0;
        CHANNEL.registerMessage(messageId++, CUpdateInstrumentPacket.class, CUpdateInstrumentPacket::toBytes, CUpdateInstrumentPacket::fromBytes, CUpdateInstrumentPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(messageId++, SPanfluteSongPacket.class, SPanfluteSongPacket::toBytes, SPanfluteSongPacket::fromBytes, SPanfluteSongPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, SSwineEffectPacket.class, SSwineEffectPacket::toBytes, SSwineEffectPacket::fromBytes, SSwineEffectPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, SSimpleParticlesPacket.class, SSimpleParticlesPacket::toBytes, SSimpleParticlesPacket::fromBytes, SSimpleParticlesPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, CUseEnchantmentPacket.class, CUseEnchantmentPacket::toBytes, CUseEnchantmentPacket::fromBytes, CUseEnchantmentPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void setup(final FMLCommonSetupEvent event) {
        // register capability
        event.enqueueWork(() -> GFWorldGen.registerConfiguredFeatures());
        event.enqueueWork(() -> GFWorldGen.finishBiomeSetup());
        event.enqueueWork(() -> GFRegistry.finishBrewingRecipes());
    }

    public static void loadConfig(final ModConfig.Loading event) {
        CONFIG.bake();
    }

    public static void reloadConfig(final ModConfig.Reloading event) {
        CONFIG.bake();
    }
}
