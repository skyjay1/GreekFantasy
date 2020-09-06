package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.GiganteModel;
import greekfantasy.entity.GiganteEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class GiganteRenderer<T extends GiganteEntity> extends BipedRenderer<T, GiganteModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/gigante.png");

  public GiganteRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new GiganteModel<T>(0.0F), 0.0F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
