package greekfantasy.proxy;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.AraRenderer;
import greekfantasy.client.render.CentaurRenderer;
import greekfantasy.client.render.CerastesRenderer;
import greekfantasy.client.render.CyclopesRenderer;
import greekfantasy.client.render.CyprianCentaurRenderer;
import greekfantasy.client.render.EmpusaRenderer;
import greekfantasy.client.render.GiganteRenderer;
import greekfantasy.client.render.GorgonRenderer;
import greekfantasy.client.render.HarpyRenderer;
import greekfantasy.client.render.MinotaurRenderer;
import greekfantasy.client.render.NymphRenderer;
import greekfantasy.client.render.OrthusRenderer;
import greekfantasy.client.render.SatyrRenderer;
import greekfantasy.client.render.ShadeRenderer;
import greekfantasy.client.render.SirenRenderer;
import greekfantasy.client.render.UnicornRenderer;
import greekfantasy.client.render.tileentity.StatueTileEntityRenderer;
import greekfantasy.events.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends Proxy {
  
  public void registerEventHandlers() { 
    super.registerEventHandlers();
    FMLJavaModLoadingContext.get().getModEventBus().register(ClientEventHandler.class);
    MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
  }
    
  @Override
  public void registerEntityRenders() {
    GreekFantasy.LOGGER.info("registerEntityRenders");
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ARA_ENTITY, AraRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CENTAUR_ENTITY, CentaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CYPRIAN_CENTAUR_ENTITY, CyprianCentaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CERASTES_ENTITY, CerastesRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CYCLOPES_ENTITY, CyclopesRenderer::new);    
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EMPUSA_ENTITY, EmpusaRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GIGANTE_ENTITY, GiganteRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GORGON_ENTITY, GorgonRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HARPY_ENTITY, HarpyRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MINOTAUR_ENTITY, MinotaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.NYMPH_ENTITY, NymphRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ORTHUS_ENTITY, OrthusRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SATYR_ENTITY, SatyrRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SHADE_ENTITY, ShadeRenderer::new);  
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SIREN_ENTITY, SirenRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.UNICORN_ENTITY, UnicornRenderer::new);
  }
  
  @Override
  public void registerTileEntityRenders() {
    GreekFantasy.LOGGER.info("registerTileEntityRenders");
    ClientRegistry.bindTileEntityRenderer(GFRegistry.STATUE_TE, StatueTileEntityRenderer::new);
  }
  
}
