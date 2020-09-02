package greekfantasy.proxy;

import greekfantasy.client.render.CentaurRenderer;
import greekfantasy.client.render.GorgonRenderer;
import greekfantasy.client.render.HarpyRenderer;
import greekfantasy.client.render.MinotaurRenderer;
import greekfantasy.client.render.NymphRenderer;
import greekfantasy.client.render.SatyrRenderer;
import greekfantasy.client.render.ShadeRenderer;
import greekfantasy.client.render.SirenRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends Proxy {
    
  @Override
  public void registerEntityRenders() {
//    final IRenderFactory<NymphEntity> NYMPH_RENDERER = NymphRenderer::new;
//    for(final Entry<Variant, EntityType<? extends NymphEntity>> t : nymphEntityMap.entrySet()) {
//      RenderingRegistry.registerEntityRenderingHandler(t.getValue(), NYMPH_RENDERER);
//    }
    RenderingRegistry.registerEntityRenderingHandler(NYMPH_ENTITY, NymphRenderer::new);
    
    RenderingRegistry.registerEntityRenderingHandler(SATYR_ENTITY, SatyrRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(CENTAUR_ENTITY, CentaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(MINOTAUR_ENTITY, MinotaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(SIREN_ENTITY, SirenRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GORGON_ENTITY, GorgonRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(SHADE_ENTITY, ShadeRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(HARPY_ENTITY, HarpyRenderer::new);
  }
  
}
