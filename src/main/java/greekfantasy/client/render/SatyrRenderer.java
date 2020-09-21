package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.SatyrModel;
import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SatyrRenderer<T extends SatyrEntity> extends BipedRenderer<T, SatyrModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr.png");

  public SatyrRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new SatyrModel<T>(0.0F), 0.5F);
    this.addLayer(new SatyrPanfluteLayer<>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
