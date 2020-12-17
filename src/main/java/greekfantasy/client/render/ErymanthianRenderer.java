package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.HoglinRenderer;
import net.minecraft.entity.monster.HoglinEntity;

public class ErymanthianRenderer<T extends HoglinEntity> extends HoglinRenderer {
  
  public static final float SCALE = 1.9F;
  
  public ErymanthianRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn);
//    this.addLayer(new MadCowEyesLayer<>(this));
  }
  
  @Override
  protected void preRenderCallback(final HoglinEntity entity, MatrixStack matrix, float ageInTicks) {
    matrix.scale(SCALE, SCALE, SCALE);
  }
}
