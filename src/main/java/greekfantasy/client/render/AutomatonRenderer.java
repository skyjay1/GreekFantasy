package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.AutomatonModel;
import greekfantasy.entity.AutomatonEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class AutomatonRenderer<T extends AutomatonEntity> extends BipedRenderer<T, AutomatonModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/automaton.png");
  private static final float SCALE = 2.0F;
  
  public AutomatonRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new AutomatonModel<T>(0.0F), 1.0F);
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
