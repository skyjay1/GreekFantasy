package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.client.model.AraModel;
import greekfantasy.entity.AraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AraFlintLayer<T extends AraEntity> extends LayerRenderer<T, AraModel<T>> {
  
  public AraFlintLayer(IEntityRenderer<T, AraModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible()) {
      // prepare to render flint inside body opening
      matrixStackIn.push();
      
      this.getEntityModel().getBodyModel().translateRotate(matrixStackIn);
      matrixStackIn.translate(0.0D, 20.0D, 0.0D);
      
      ItemStack stack = new ItemStack(Items.FLINT);
      Minecraft.getInstance().getItemRenderer().renderItem(entity, stack, ItemCameraTransforms.TransformType.HEAD, false, 
          matrixStackIn, bufferIn, entity.world, packedLightIn, LivingRenderer.getPackedOverlay(entity, 0.0F));
//    final float scale = 0.5F;
//      this.getEntityModel().getHeadModel().translateRotate(matrixStackIn);
//      matrixStackIn.translate(0.5D * scale, -0.5, -0.5D * scale);
//      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
//      matrixStackIn.scale(scale, -scale, -scale);
//      // render fire here
//      Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.FIRE.getDefaultState(), 
//          matrixStackIn, bufferIn, 15728640, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
      // finish rendering
      matrixStackIn.pop();
    }
  }
}