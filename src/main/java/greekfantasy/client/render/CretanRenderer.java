package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.CretanEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class CretanRenderer<T extends CretanEntity> extends MinotaurRenderer<T> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cretan.png");
  public static final float SCALE = 1.75F;
  
  public CretanRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn);
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float f) {
    matrix.scale(SCALE, SCALE, SCALE);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return CretanRenderer.TEXTURE;
  }
}
