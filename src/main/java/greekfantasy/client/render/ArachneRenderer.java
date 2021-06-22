package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.ArachneEyesLayer;
import greekfantasy.client.render.model.ArachneModel;
import greekfantasy.entity.ArachneEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class ArachneRenderer<T extends ArachneEntity> extends BipedRenderer<T, ArachneModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/arachne/arachne.png");

  public ArachneRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new ArachneModel<T>(0.0F), 0.8F);
    // this.addLayer(new ArachneEyesLayer<>(this)); // this was not working correctly...
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
