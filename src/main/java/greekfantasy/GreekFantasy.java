package greekfantasy;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import greekfantasy.client.network.CUpdateStatuePosePacket;
import greekfantasy.proxy.ClientProxy;
import greekfantasy.proxy.Proxy;
import greekfantasy.proxy.ServerProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
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

  public GreekFantasy() {
    FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.class);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(GreekFantasy::setup);
    // register config
    GreekFantasy.LOGGER.info("registerConfig");
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC);
    //GFConfig.loadConfig(CONFIG_SPEC, FMLPaths.CONFIGDIR.get().resolve(MODID + "-server.toml"));
    // register side-specific or common event handlers
    PROXY.registerEventHandlers();
    // register messages
    GreekFantasy.LOGGER.info("registerNetwork");
    int messageId = 0;
    CHANNEL.registerMessage(messageId++, CUpdateStatuePosePacket.class, CUpdateStatuePosePacket::toBytes, CUpdateStatuePosePacket::fromBytes, CUpdateStatuePosePacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_SERVER));
  }

  public static void setup(final FMLCommonSetupEvent event) {
    
  }
}
