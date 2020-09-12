package greekfantasy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import greekfantasy.config.GFConfig;
import greekfantasy.proxy.ClientProxy;
import greekfantasy.proxy.Proxy;
import greekfantasy.proxy.ServerProxy;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(GreekFantasy.MODID)
public class GreekFantasy {

  public static final String MODID = "greekfantasy";

  public static final Proxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
  public static GFConfig CONFIG = new GFConfig(CONFIG_BUILDER);
  private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();

  public static final Logger LOGGER = LogManager.getFormatterLogger(GreekFantasy.MODID);

  public GreekFantasy() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().register(GFRegistry.class);
    GreekFantasy.LOGGER.info("registerConfig");
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC);
    GFConfig.loadConfig(CONFIG_SPEC, FMLPaths.CONFIGDIR.get().resolve(MODID + "-server.toml"));
    PROXY.registerEventHandlers();
  }
  
  public void setup(final FMLCommonSetupEvent event) {
    GFRegistry.setupFeatures();
    GFRegistry.setupStructures();
  }
}
