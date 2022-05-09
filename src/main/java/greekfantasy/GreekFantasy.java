package greekfantasy;

import greekfantasy.event.CommonEventHandler;
import greekfantasy.network.CUpdateInstrumentPacket;
import greekfantasy.network.CUseEnchantmentPacket;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.network.SSwineEffectPacket;
import greekfantasy.util.GenericJsonReloadListener;
import greekfantasy.util.Song;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(GreekFantasy.MODID)
public class GreekFantasy {

    public static final String MODID = "greekfantasy";

    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
    public static GFConfig CONFIG = new GFConfig(CONFIG_BUILDER);
    private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();

    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static final Logger LOGGER = LogManager.getFormatterLogger(GreekFantasy.MODID);

    private static boolean rgLoaded;

    // TODO use this instead of proxy
    public static final GenericJsonReloadListener<Song> PANFLUTE_SONGS = new GenericJsonReloadListener<>("songs", Song.class, Song.CODEC,
            l -> l.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SPanfluteSongPacket(e.getKey(), e.getValue().get()))));


    public GreekFantasy() {
        // registry event listeners
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.BlockReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.BlockEntityReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.EnchantmentReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.EntityReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.ItemReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.MenuReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.MobEffectReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.ParticleReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.PotionReg.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.RecipeReg.class);
        // world event listeners
        FMLJavaModLoadingContext.get().getModEventBus().register(GFWorldGen.class);
        // forge event listeners
        MinecraftForge.EVENT_BUS.register(CommonEventHandler.class);
        // client-only listeners
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().register(greekfantasy.event.ClientEventHandler.ModEvents.class);
            MinecraftForge.EVENT_BUS.register(greekfantasy.event.ClientEventHandler.ForgeEvents.class);
        });
        // other listeners
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::intermodEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::loadConfig);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::reloadConfig);
        // register config
        GreekFantasy.LOGGER.debug("registerConfig");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
        // register messages
        GreekFantasy.LOGGER.debug("registerNetwork");
        int messageId = 0;
        CHANNEL.registerMessage(messageId++, CUpdateInstrumentPacket.class, CUpdateInstrumentPacket::toBytes, CUpdateInstrumentPacket::fromBytes, CUpdateInstrumentPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(messageId++, SPanfluteSongPacket.class, SPanfluteSongPacket::toBytes, SPanfluteSongPacket::fromBytes, SPanfluteSongPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, SSwineEffectPacket.class, SSwineEffectPacket::toBytes, SSwineEffectPacket::fromBytes, SSwineEffectPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(messageId++, CUseEnchantmentPacket.class, CUseEnchantmentPacket::toBytes, CUseEnchantmentPacket::fromBytes, CUseEnchantmentPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void setup(final FMLCommonSetupEvent event) {
        // register capability
        event.enqueueWork(() -> GFWorldGen.registerConfiguredFeatures());
        event.enqueueWork(() -> GFWorldGen.finishBiomeSetup());
    }

    public static void intermodEnqueue(final InterModEnqueueEvent event) {
        rgLoaded = ModList.get().isLoaded("rpggods");
    }

    public static void loadConfig(final ModConfig.Loading event) {
        CONFIG.bake();
    }

    public static void reloadConfig(final ModConfig.Reloading event) {
        CONFIG.bake();
    }

    public static boolean isRGLoaded() {
        return rgLoaded;
    }
}
