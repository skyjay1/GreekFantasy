package greekfantasy.proxy;

import java.util.Map.Entry;

import greekfantasy.client.render.NymphRenderer;
import greekfantasy.entity.NymphEntity;
import greekfantasy.entity.NymphEntity.Variant;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends Proxy {
  
  public static final IRenderFactory<NymphEntity> NYMPH_RENDERER = NymphRenderer::new;
  
  @Override
  public void registerEntityRenders() {
    for(final Entry<Variant, EntityType<? extends NymphEntity>> t : nymphEntityMap.entrySet()) {
      RenderingRegistry.registerEntityRenderingHandler(t.getValue(), NYMPH_RENDERER);
    }
  }
  
}
