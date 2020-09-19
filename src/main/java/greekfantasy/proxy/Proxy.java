package greekfantasy.proxy;

import greekfantasy.events.CommonForgeEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class Proxy {

  public void registerEntityRenders() { }
  
  public void registerTileEntityRenders() { }
  
  public void registerContainerRenders() { }
    
  public void registerEventHandlers() { 
    MinecraftForge.EVENT_BUS.register(CommonForgeEventHandler.class);
  }

}
