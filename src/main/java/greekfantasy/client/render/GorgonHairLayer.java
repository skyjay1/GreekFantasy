package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.client.model.GorgonModel;
import greekfantasy.entity.GorgonEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class GorgonHairLayer<T extends GorgonEntity> extends LayerRenderer<T, GorgonModel<T>> {
  
  public GorgonHairLayer(IEntityRenderer<T, GorgonModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible()) {
      // get packed light and a vertex builder bound to the correct texture
      int packedOverlay = LivingRenderer.getPackedOverlay(entity, 0.0F);
      IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(this.getEntityTexture(entity)));
            
      // render snake hair
      matrixStackIn.push();
//      float yaw = MathHelper.lerp(partialTick, entity.prevRotationYaw, entity.rotationYaw) - MathHelper.lerp(partialTick, entity.prevRenderYawOffset, entity.renderYawOffset);
//      float pitch = MathHelper.lerp(partialTick, entity.prevRotationPitch, entity.rotationPitch);
//      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(yaw));
//      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(pitch));
//      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-pitch));
//      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-yaw));
      this.getEntityModel().renderSnakeHair(matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, entity.ticksExisted + partialTick);
      matrixStackIn.pop();
    }
  }
}