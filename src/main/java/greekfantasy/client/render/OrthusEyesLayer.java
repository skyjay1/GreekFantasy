package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.OrthusModel;
import greekfantasy.entity.OrthusEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;

public class OrthusEyesLayer<T extends OrthusEntity> extends AbstractEyesLayer<T, OrthusModel<T>> {
  
  private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(GreekFantasy.MODID, "textures/entity/orthus/orthus_eyes.png"));

  public OrthusEyesLayer(IEntityRenderer<T, OrthusModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public RenderType getRenderType() {
    return RENDER_TYPE;
  }

}
