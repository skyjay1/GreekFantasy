package greekfantasy;

import java.util.Map.Entry;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import greekfantasy.favor.Deity;
import greekfantasy.favor.Favor;
import greekfantasy.favor.IFavor;
import greekfantasy.network.CUpdatePanflutePacket;
import greekfantasy.network.CUpdateStatuePosePacket;
import greekfantasy.network.SDeityPacket;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.proxy.ClientProxy;
import greekfantasy.proxy.Proxy;
import greekfantasy.proxy.ServerProxy;
import greekfantasy.util.GenericJsonReloadListener;
import greekfantasy.util.PanfluteSong;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(GreekFantasy.MODID)
public class GreekFantasy {

  public static final String MODID = "greekfantasy";

  public static final Proxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
  public static GFConfig CONFIG = new GFConfig(CONFIG_BUILDER);
  private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();
  
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public static final Logger LOGGER = LogManager.getFormatterLogger(GreekFantasy.MODID);
  
  @CapabilityInject(IFavor.class)
  public static final Capability<IFavor> FAVOR = null;

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
    CHANNEL.registerMessage(messageId++, CUpdateStatuePosePacket.class, CUpdateStatuePosePacket::toBytes, CUpdateStatuePosePacket::fromBytes, CUpdateStatuePosePacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    CHANNEL.registerMessage(messageId++, CUpdatePanflutePacket.class, CUpdatePanflutePacket::toBytes, CUpdatePanflutePacket::fromBytes, CUpdatePanflutePacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    CHANNEL.registerMessage(messageId++, SPanfluteSongPacket.class, SPanfluteSongPacket::toBytes, SPanfluteSongPacket::fromBytes, SPanfluteSongPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    CHANNEL.registerMessage(messageId++, SDeityPacket.class, SDeityPacket::toBytes, SDeityPacket::fromBytes, SDeityPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
  }

  public static void setup(final FMLCommonSetupEvent event) {
    // register capability
    CapabilityManager.INSTANCE.register(IFavor.class, new Favor.Storage(), Favor::new);
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
  
  public static void onReloadSongs(final GenericJsonReloadListener<PanfluteSong> listener) {
    for(final Entry<ResourceLocation, Optional<PanfluteSong>> e : listener.getEntries()) {
      GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SPanfluteSongPacket(e.getKey(), e.getValue().get()));
    }
  }
  
  public static void onReloadDeity(final GenericJsonReloadListener<Deity> listener) {
    for(final Entry<ResourceLocation, Optional<Deity>> e : listener.getEntries()) {
      GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SDeityPacket(e.getKey(), e.getValue().get()));
    }
  }
}
