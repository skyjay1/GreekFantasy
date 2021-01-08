package greekfantasy.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.block.VaseBlock;
import greekfantasy.tileentity.VaseTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class VaseTileEntityRenderer extends TileEntityRenderer<VaseTileEntity> {

  public VaseTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(VaseTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {
    final ItemStack itemstack = tileEntityIn.getStackInSlot(0);
    if (!itemstack.isEmpty()) {
      final float scale = 0.315F;
      final float rotation = tileEntityIn.getBlockState().get(VaseBlock.HORIZONTAL_FACING).getHorizontalAngle();
      matrixStackIn.push();
      // transforms
      matrixStackIn.translate(0.5D, 0.70D, 0.5D);
      matrixStackIn.rotate(Vector3f.YN.rotationDegrees(rotation));
      matrixStackIn.scale(scale, scale, scale);
      // render the item stack
      Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, packedLightIn,
          OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
      // finish rendering
      matrixStackIn.pop();
    }
  }
}
