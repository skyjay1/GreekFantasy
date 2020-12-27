package greekfantasy.client.render.tileentity;

import greekfantasy.tileentity.AltarTileEntity;
import greekfantasy.tileentity.StatueTileEntity;
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
}
