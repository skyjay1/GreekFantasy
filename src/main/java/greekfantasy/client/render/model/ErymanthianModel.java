package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.ErymanthianEntity;
import net.minecraft.client.renderer.entity.model.BoarModel;

public class ErymanthianModel<T extends ErymanthianEntity> extends BoarModel<T> {
  
  private float colorAlpha = 1.0F;


  public ErymanthianModel() {
    super();    
  }
  
  public float getColorAlpha() { return colorAlpha; }

  public void setColorAlpha(float alpha) { this.colorAlpha = alpha; }
  
  @Override
  public void render(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, final float redIn,
      final float greenIn, final float blueIn, final float alphaIn) {
    // render with custom alpha value
    super.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, colorAlpha);
  }
}
