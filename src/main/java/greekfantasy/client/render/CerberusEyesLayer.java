package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.CerberusModel;
import greekfantasy.entity.CerberusEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;

public class CerberusEyesLayer<T extends CerberusEntity> extends AbstractEyesLayer<T, CerberusModel<T>> {
  
  private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerberus/cerberus_eyes.png"));

  public CerberusEyesLayer(IEntityRenderer<T, CerberusModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public RenderType getRenderType() {
    return RENDER_TYPE;
  }

}
