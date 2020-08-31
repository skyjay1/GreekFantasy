package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.MinotaurModel;
import greekfantasy.entity.MinotaurEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class MinotaurRenderer<T extends MinotaurEntity> extends BipedRenderer<T, MinotaurModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/minotaur.png");

  public MinotaurRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new MinotaurModel<T>(0.0F), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
