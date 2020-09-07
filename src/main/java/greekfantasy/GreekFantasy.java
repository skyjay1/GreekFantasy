package greekfantasy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import greekfantasy.config.GFConfig;
import greekfantasy.proxy.ClientProxy;
import greekfantasy.proxy.Proxy;
import greekfantasy.proxy.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(GreekFantasy.MODID)
@Mod.EventBusSubscriber(modid = GreekFantasy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GreekFantasy {
  
  public static final String MODID = "greekfantasy";

  public static final Proxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
  private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();
  public static GFConfig CONFIG = null;
  
  public static final Logger LOGGER = LogManager.getFormatterLogger(GreekFantasy.MODID);

  public GreekFantasy() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    GreekFantasy.LOGGER.info("registerConfig");
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC);
    GFConfig.loadConfig(CONFIG_SPEC, FMLPaths.CONFIGDIR.get().resolve(MODID + "-server.toml"));

  }

  private void setup(final FMLCommonSetupEvent event) {

  }
  
  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    GreekFantasy.LOGGER.info("registerEntities");
    GreekFantasy.PROXY.registerEntities(event);
    GreekFantasy.PROXY.registerEntityRenders();
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    GreekFantasy.LOGGER.info("registerItems");
    GreekFantasy.PROXY.registerItems(event);
  }

  @SubscribeEvent
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    GreekFantasy.LOGGER.info("registerBlocks");
    GreekFantasy.PROXY.registerBlocks(event);
  }
}
