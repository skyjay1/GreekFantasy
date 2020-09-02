package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.ShadeModel;
import greekfantasy.entity.ShadeEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class ShadeRenderer<T extends ShadeEntity> extends BipedRenderer<T, ShadeModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/shade.png");

  public ShadeRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new ShadeModel<T>(0.0F), 0.0F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
