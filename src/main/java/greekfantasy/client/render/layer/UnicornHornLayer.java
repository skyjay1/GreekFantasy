package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.UnicornModel;
import greekfantasy.entity.UnicornEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class UnicornHornLayer<T extends UnicornEntity> extends LayerRenderer<T, UnicornModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/unicorn/horn.png");
  
  public UnicornHornLayer(IEntityRenderer<T, UnicornModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible() && !entity.isBaby()) {
      // get packed light and a vertex builder bound to the correct texture
      int packedOverlay = LivingRenderer.getOverlayCoords(entity, 0.0F);
      IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
            
      // render horn
      matrixStackIn.pushPose();
      this.getParentModel().renderHorn(entity, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, limbSwing, limbSwingAmount);
      matrixStackIn.popPose();
    }
  }
}