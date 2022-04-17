package greekfantasy.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.block.VaseBlock;
import greekfantasy.client.render.model.tileentity.IHasName;
import greekfantasy.tileentity.VaseTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class VaseTileEntityRenderer extends TileEntityRenderer<VaseTileEntity> implements IHasName<VaseTileEntity> {

  public VaseTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(VaseTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {
    final ItemStack itemstack = tileEntityIn.getItem(0);
    if (!itemstack.isEmpty()) {
      final float scale = 0.315F;
      final float rotation = tileEntityIn.getBlockState().getValue(VaseBlock.FACING).toYRot();
      matrixStackIn.pushPose();
      // render nameplate
      if(canRenderName(tileEntityIn)) {
        renderName(tileEntityIn, matrixStackIn, bufferIn, packedLightIn);
      }
      // transforms
      matrixStackIn.translate(0.5D, 0.70D, 0.5D);
      matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(rotation));
      matrixStackIn.scale(scale, scale, scale);
      // render the item stack
      Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.FIXED, packedLightIn,
          OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
      // finish rendering
      matrixStackIn.popPose();
    }
  }
  
  
  @Override
  public boolean canRenderName(final VaseTileEntity entityIn) {
    final ItemStack item = entityIn.getItem(0);
    return !item.isEmpty() && item.hasCustomHoverName() && IHasName.isWithinDistanceToRenderName(entityIn, 6.0D);
  }

  @Override
  public void renderName(VaseTileEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    matrixStackIn.pushPose();
    IHasName.renderNameplate(entityIn, entityIn.getItem(0).getHoverName(), 1.15F, matrixStackIn, bufferIn, packedLightIn);
    matrixStackIn.popPose();
  }
}
