package greekfantasy.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.tileentity.AltarTileEntity;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class AltarTileEntityRenderer extends StatueTileEntityRenderer {

  public AltarTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  protected ResourceLocation getOverlayTexture(final StatueTileEntity te) {
    return ((AltarTileEntity)te).getDeity().getTexture();
  }
  
  @Override
  public void render(StatueTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {
    super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
