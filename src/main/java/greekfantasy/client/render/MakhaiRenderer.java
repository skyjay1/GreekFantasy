package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.MakhaiHeldItemLayer;
import greekfantasy.client.render.model.MakhaiModel;
import greekfantasy.entity.MakhaiEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class MakhaiRenderer<T extends MakhaiEntity> extends BipedRenderer<T, MakhaiModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/makhai.png");
  
  public MakhaiRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new MakhaiModel<T>(0.0F), 0.5F);
    addLayer(new MakhaiHeldItemLayer<>(this));
  }
  
  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
}
