package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.HydraModel;
import greekfantasy.entity.HydraEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class HydraRenderer<T extends HydraEntity> extends MobRenderer<T, HydraModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra_body.png");
  private static final float SCALE = 1.35F;
  
  
  public HydraRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new HydraModel<T>(), 0.5F);
  }
  
  @Override
  protected void scale(final T entity, MatrixStack matrix, float f) {
    matrix.scale(SCALE, SCALE, SCALE);
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
}
