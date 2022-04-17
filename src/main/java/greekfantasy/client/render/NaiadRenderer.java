package greekfantasy.client.render;

import greekfantasy.client.render.model.NaiadModel;
import greekfantasy.entity.NaiadEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class NaiadRenderer<T extends NaiadEntity> extends BipedRenderer<T, NaiadModel<T>> {

  public NaiadRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new NaiadModel<T>(0.0F), 0.25F);
  }

  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless
   * you call Render.bindEntityTexture.
   */
  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return entity.getVariant().getTexture();
  }
}
