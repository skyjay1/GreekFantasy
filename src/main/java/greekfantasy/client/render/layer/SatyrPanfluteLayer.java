package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GFRegistry;
import greekfantasy.client.render.model.SatyrModel;
import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;

public class SatyrPanfluteLayer<T extends SatyrEntity> extends LayerRenderer<T, SatyrModel<T>> {
  
  public SatyrPanfluteLayer(IEntityRenderer<T, SatyrModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible() && entity.holdingPanfluteTime > 0) {
      matrixStackIn.pushPose();
      // transforms
      this.getParentModel().translateToHand(HandSide.RIGHT, matrixStackIn);
      matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      matrixStackIn.translate((double) (1.0F / 16.0F), 0.125D, -0.625D);
      // render the item stack
      Minecraft.getInstance().getItemRenderer().renderStatic(entity, new ItemStack(GFRegistry.PANFLUTE), ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, 
          false, matrixStackIn, bufferIn, entity.getCommandSenderWorld(), packedLightIn, OverlayTexture.NO_OVERLAY);
      // finish rendering
      matrixStackIn.popPose();
    }
  }
}