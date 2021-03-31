package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.BronzeBullModel;
import greekfantasy.entity.BronzeBullEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class BronzeBullRenderer<T extends BronzeBullEntity> extends MobRenderer<T, BronzeBullModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/bronze_bull.png");
  public static final float SCALE = 1.5F;
  
  public BronzeBullRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new BronzeBullModel<>(), 1.0F);
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float f) {
    matrix.scale(SCALE, SCALE, SCALE);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return BronzeBullRenderer.TEXTURE;
  }
}
