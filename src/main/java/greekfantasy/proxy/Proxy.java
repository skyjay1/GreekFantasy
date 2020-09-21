package greekfantasy.proxy;

import greekfantasy.GreekFantasy;
import greekfantasy.events.CommonForgeEventHandler;
import greekfantasy.util.PanfluteSongs;
import net.minecraftforge.common.MinecraftForge;

public class Proxy {
  
  public final PanfluteSongs PANFLUTE_SONGS = new PanfluteSongs("songs");
  
  public void registerReloadListeners() { }

  public void registerEntityRenders() { }
  
  public void registerTileEntityRenders() { }
  
  public void registerContainerRenders() { }
    
  public void registerEventHandlers() {
    GreekFantasy.LOGGER.info("registerEventHandlers");
    MinecraftForge.EVENT_BUS.register(CommonForgeEventHandler.class);
  }

}
