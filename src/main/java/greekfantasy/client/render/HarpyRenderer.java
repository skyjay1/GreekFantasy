package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.HarpyModel;
import greekfantasy.entity.HarpyEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class HarpyRenderer<T extends HarpyEntity> extends BipedRenderer<T, HarpyModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/harpy.png");

  public HarpyRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new HarpyModel<T>(0.0F), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
