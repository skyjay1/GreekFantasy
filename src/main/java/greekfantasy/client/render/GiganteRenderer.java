package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.GiganteModel;
import greekfantasy.entity.GiganteEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class GiganteRenderer<T extends GiganteEntity> extends BipedRenderer<T, GiganteModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/gigante.png");
  public static final float SCALE = 1.9F;
  
  public GiganteRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new GiganteModel<T>(0.0F), 1.0F);
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
  
  @Override
  protected void scale(final T entity, MatrixStack matrix, float ageInTicks) {
    matrix.scale(SCALE, SCALE, SCALE);
  }
}
