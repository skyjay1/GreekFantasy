package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.CretanMinotaurEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class CretanMinotaurRenderer<T extends CretanMinotaurEntity> extends MinotaurRenderer<T> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cretan_minotaur.png");
  public static final float SCALE = 1.75F;
  
  public CretanMinotaurRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn);
  }
  
  @Override
  protected void scale(final T entity, MatrixStack matrix, float f) {
    matrix.scale(SCALE, SCALE, SCALE);
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return CretanMinotaurRenderer.TEXTURE;
  }
}
