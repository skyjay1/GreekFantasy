package greekfantasy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import greekfantasy.proxy.ClientProxy;
import greekfantasy.proxy.Proxy;
import greekfantasy.proxy.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GreekFantasy.MODID)
@Mod.EventBusSubscriber(modid = GreekFantasy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GreekFantasy {
  
  public static final String MODID = "greekfantasy";

  public static final Proxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

  public static final Logger LOGGER = LogManager.getFormatterLogger(GreekFantasy.MODID);

  public GreekFantasy() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
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
