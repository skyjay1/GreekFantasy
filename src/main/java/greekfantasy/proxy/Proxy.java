package greekfantasy.proxy;

import greekfantasy.events.CommonEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class Proxy {

  public void registerEntityRenders() { }
  
  public void registerEventHandlers() { 
    MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
  }
}
