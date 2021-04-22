package greekfantasy.client.render.layer;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.ArachneModel;
import greekfantasy.entity.ArachneEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;

public class ArachneEyesLayer<T extends ArachneEntity> extends AbstractEyesLayer<T, ArachneModel<T>> {
  
  private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(GreekFantasy.MODID, "textures/entity/arachne/arachne_eyes.png"));

  public ArachneEyesLayer(IEntityRenderer<T, ArachneModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public RenderType getRenderType() {
//    return RenderType.getEntityCutout(new ResourceLocation(GreekFantasy.MODID, "textures/entity/arachne/arachne_eyes.png"));
    return RENDER_TYPE;
  }

}
