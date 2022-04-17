package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.GiantBoarEntity;
import net.minecraft.client.renderer.entity.model.BoarModel;

public class GiantBoarModel<T extends GiantBoarEntity> extends BoarModel<T> {
  
  private float colorAlpha = 1.0F;


  public GiantBoarModel() {
    super();    
  }
  
  public float getColorAlpha() { return colorAlpha; }

  public void setColorAlpha(float alpha) { this.colorAlpha = alpha; }
  
  @Override
  public void renderToBuffer(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, final float redIn,
      final float greenIn, final float blueIn, final float alphaIn) {
    // render with custom alpha value
    super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, colorAlpha);
  }
}
