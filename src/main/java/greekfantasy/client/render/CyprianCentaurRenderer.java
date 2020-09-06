package greekfantasy.client.render;

import greekfantasy.client.model.CyprianCentaurModel;
import greekfantasy.entity.CyprianCentaurEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class CyprianCentaurRenderer<T extends CyprianCentaurEntity> extends CentaurRenderer<T> {
  
  public CyprianCentaurRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CyprianCentaurModel<T>(0.0F));
  }
}
