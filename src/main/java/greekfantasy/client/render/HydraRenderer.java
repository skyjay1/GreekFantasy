package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.HydraModel;
import greekfantasy.entity.HydraEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class HydraRenderer<T extends HydraEntity> extends MobRenderer<T, HydraModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra.png");

  public HydraRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new HydraModel<T>(), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
