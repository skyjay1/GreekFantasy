package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.EmpusaModel;
import greekfantasy.entity.EmpusaEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class EmpusaRenderer<T extends EmpusaEntity> extends BipedRenderer<T, EmpusaModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/empusa.png");

  public EmpusaRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new EmpusaModel<T>(0.0F), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
