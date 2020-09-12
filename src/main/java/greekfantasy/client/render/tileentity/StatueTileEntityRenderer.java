package greekfantasy.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock;
import greekfantasy.client.model.tileentity.StatueModel;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class StatueTileEntityRenderer extends TileEntityRenderer<StatueTileEntity> {
  
  protected static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/limestone.png");

  protected StatueModel<StatueTileEntity> model;
  
  public StatueTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
    this.model = new StatueModel<StatueTileEntity>();
  }

  @Override
  public void render(StatueTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {
    // render biped model
    final boolean upper = tileEntityIn.getBlockState().get(StatueBlock.HALF) == DoubleBlockHalf.UPPER;
    final float rotation = tileEntityIn.getBlockState().get(StatueBlock.HORIZONTAL_FACING).getHorizontalAngle();
    final double translateY = upper ? 0.95D : 1.95D;
    StatueTileEntity te = tileEntityIn;
    if(upper) {
      final TileEntity temp = tileEntityIn.getWorld().getTileEntity(tileEntityIn.getPos().down());
      if(temp instanceof StatueTileEntity) {
        te = (StatueTileEntity)temp;
      }
    }
    matrixStackIn.push();
    matrixStackIn.translate(0.5D, translateY, 0.5D);
    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation));
    IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
    this.model.setRotationAngles(te, partialTicks);
    this.model.render(matrixStackIn, vertexBuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F, upper);
    matrixStackIn.pop();
  }

}
