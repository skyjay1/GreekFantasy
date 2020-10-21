package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.CerastesModel;
import greekfantasy.entity.CerastesEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CerastesRenderer<T extends CerastesEntity> extends MobRenderer<T, CerastesModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerastes.png");

  public CerastesRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CerastesModel<T>(0.0F), 0.0F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
