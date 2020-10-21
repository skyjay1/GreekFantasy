package greekfantasy.client.render;

import greekfantasy.client.render.model.CyprianCentaurModel;
import greekfantasy.entity.CyprianEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class CyprianCentaurRenderer<T extends CyprianEntity> extends CentaurRenderer<T> {
  
  public CyprianCentaurRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CyprianCentaurModel<T>(0.0F));
  }
}
