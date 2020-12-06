package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.PythonModel;
import greekfantasy.entity.PythonEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class PythonRenderer<T extends PythonEntity> extends MobRenderer<T, PythonModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/python.png");
  public static final float SCALE = 1.4F;

  public PythonRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new PythonModel<T>(0.0F), 1.0F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float f) {
    matrix.scale(SCALE, SCALE, SCALE);
  }
}
