package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.client.model.EmpusaModel;
import greekfantasy.entity.EmpusaEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class EmpusaHairLayer<T extends EmpusaEntity> extends LayerRenderer<T, EmpusaModel<T>> {
  
  public EmpusaHairLayer(IEntityRenderer<T, EmpusaModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible()) {
      // prepare to render fire on top of head
      final float scale = 0.5F;
      matrixStackIn.push();
      this.getEntityModel().getHeadModel().translateRotate(matrixStackIn);
      matrixStackIn.translate(0.5D * scale, -0.5, -0.5D * scale);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      matrixStackIn.scale(scale, -scale, -scale);
      // render fire here
      Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.FIRE.getDefaultState(), 
          matrixStackIn, bufferIn, 15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
      // finish rendering
      matrixStackIn.pop();
    }
  }
}