package greekfantasy.proxy;

import greekfantasy.GreekFantasy;
import greekfantasy.events.CommonForgeEventHandler;
import greekfantasy.favor.Deity;
import greekfantasy.favor.FavorEffect;
import greekfantasy.util.GenericJsonReloadListener;
import greekfantasy.util.PanfluteSong;
import net.minecraftforge.common.MinecraftForge;

public class Proxy {
  
  public final GenericJsonReloadListener<PanfluteSong> PANFLUTE_SONGS = new GenericJsonReloadListener<>("songs", PanfluteSong.class, PanfluteSong.CODEC, GreekFantasy::onReloadSongs);
  public final GenericJsonReloadListener<FavorEffect> FAVOR_EFFECTS = new GenericJsonReloadListener<>("favor_effect", FavorEffect.class, FavorEffect.CODEC, GreekFantasy::onReloadFavorEffects);
  public final GenericJsonReloadListener<Deity> DEITY = new GenericJsonReloadListener<>("deity", Deity.class, Deity.CODEC, GreekFantasy::onReloadDeity);
  
  public void registerReloadListeners() { }

  public void registerEntityRenders() { }
  
  public void registerTileEntityRenders() { }
  
  public void registerContainerRenders() { }
  
  public void registerRenderLayers() { }
    
  public void registerEventHandlers() {
    GreekFantasy.LOGGER.debug("registerEventHandlers");
    MinecraftForge.EVENT_BUS.register(CommonForgeEventHandler.class);
  }

}
