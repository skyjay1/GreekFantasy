package greekfantasy.events;

import greekfantasy.GreekFantasy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {
  
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    GreekFantasy.PROXY.registerEntityRenders();
    GreekFantasy.PROXY.registerTileEntityRenders();
  }
  
}
