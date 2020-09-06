package greekfantasy.proxy;

import greekfantasy.client.render.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends Proxy {
    
  @Override
  public void registerEntityRenders() {
    RenderingRegistry.registerEntityRenderingHandler(NYMPH_ENTITY, NymphRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(SATYR_ENTITY, SatyrRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(CENTAUR_ENTITY, CentaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(CYPRIAN_CENTAUR_ENTITY, CyprianCentaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(MINOTAUR_ENTITY, MinotaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(SIREN_ENTITY, SirenRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GORGON_ENTITY, GorgonRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(SHADE_ENTITY, ShadeRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(HARPY_ENTITY, HarpyRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(EMPUSA_ENTITY, EmpusaRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(ARA_ENTITY, AraRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(CERASTES_ENTITY, CerastesRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GIGANTE_ENTITY, GiganteRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(CYCLOPES_ENTITY, CyclopesRenderer::new);
  }
  
}
