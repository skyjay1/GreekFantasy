package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.ShadeEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;

public class ShadeModel<T extends ShadeEntity> extends BipedModel<T> {

  public ShadeModel(float modelSize) {
    super(modelSize, 0.0F, 64, 32);
    this.leftLeg.visible = false;
    this.rightLeg.visible = false;
  }
  
  @Override
  public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, final float r, final float g, final float b, final float a) {
    matrixStackIn.pushPose();
    super.renderToBuffer(matrixStackIn, bufferIn, 15728880, packedOverlayIn, r, g, b, 0.6F);
    matrixStackIn.popPose();
  }
  
}
