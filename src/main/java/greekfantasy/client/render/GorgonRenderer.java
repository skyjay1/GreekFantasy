package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.GorgonModel;
import greekfantasy.entity.GorgonEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class GorgonRenderer<T extends GorgonEntity> extends BipedRenderer<T, GorgonModel<T>> {
  
  public static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/gorgon.png");

  public GorgonRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new GorgonModel<T>(0.0F), 0.5F);
    this.addLayer(new GorgonHairLayer<>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
