package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.OrthusModel;
import greekfantasy.entity.OrthusEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class OrthusRenderer<T extends OrthusEntity> extends MobRenderer<T, OrthusModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/orthus.png");

  public OrthusRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new OrthusModel<T>(1.0F), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
