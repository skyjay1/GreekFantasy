package greekfantasy.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.block.StatueBlock;
import greekfantasy.client.render.model.tileentity.CerberusHeadModel;
import greekfantasy.client.render.model.tileentity.GiganteHeadModel;
import greekfantasy.client.render.model.tileentity.OrthusHeadModel;
import greekfantasy.tileentity.MobHeadTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MobHeadTileEntityRenderer extends TileEntityRenderer<MobHeadTileEntity> {
      
  protected Model giganteHeadModel;
  protected Model cerberusHeadModel;
  protected Model orthusHeadModel;
  
  public MobHeadTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
    this.giganteHeadModel = new GiganteHeadModel();
    this.cerberusHeadModel = new CerberusHeadModel();
    this.orthusHeadModel = new OrthusHeadModel();
  }

  @Override
  public void render(MobHeadTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {    
    // determine texture, rotations, and style
    final MobHeadTileEntity.HeadType head = tileEntityIn.getHeadType();
    final float rotation = tileEntityIn.getBlockState().getValue(StatueBlock.FACING).toYRot();
    final ResourceLocation texture = head.getTexture();
    // determine which model to use
    final Model model = getModel(head);
    final float scale = ((IWallModel)model).getScale();
    matrixStackIn.pushPose();
    // prepare to render model
    
    matrixStackIn.translate(0.5D, 0D, 0.5D);
    matrixStackIn.scale(scale, scale, scale);
    matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rotation));
    matrixStackIn.translate(.5D / scale, 0D, .5D / scale);
    IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(texture));
    // render the correct model
    
    ((IWallModel)model).setWallRotations(tileEntityIn.onWall());    
    model.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
    matrixStackIn.popPose();
  }
 
  protected Model getModel(final MobHeadTileEntity.HeadType head) {
    switch(head) {
    case GIGANTE: return giganteHeadModel;
    case CERBERUS: return cerberusHeadModel;
    default:
    case ORTHUS: return orthusHeadModel;
    }
  }
  
  public interface IWallModel {
    float getScale();
    void setWallRotations(final boolean onWall);
  }
  
  public static class OrthusItemStackRenderer extends ItemStackTileEntityRenderer {
    final OrthusHeadModel orthusHeadModel = new OrthusHeadModel();
    @Override
    public void renderByItem(ItemStack item, TransformType transform, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
      matrixStack.pushPose();
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      IVertexBuilder vertexBuilder = buffer.getBuffer(orthusHeadModel.renderType(MobHeadTileEntity.HeadType.ORTHUS.getTexture()));
      orthusHeadModel.renderToBuffer(matrixStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
    }
  }
  
  public static class GiganteItemStackRenderer extends ItemStackTileEntityRenderer {
    final GiganteHeadModel giganteHeadModel = new GiganteHeadModel();
    @Override
    public void renderByItem(ItemStack item, TransformType transform, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
      matrixStack.pushPose();
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      IVertexBuilder vertexBuilder = buffer.getBuffer(giganteHeadModel.renderType(MobHeadTileEntity.HeadType.GIGANTE.getTexture()));
      giganteHeadModel.renderToBuffer(matrixStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
    }
  }
  
  public static class CerberusItemStackRenderer extends ItemStackTileEntityRenderer {
    final CerberusHeadModel cerberusHeadModel = new CerberusHeadModel();
    @Override
    public void renderByItem(ItemStack item, TransformType transform, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
      matrixStack.pushPose();
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      IVertexBuilder vertexBuilder = buffer.getBuffer(cerberusHeadModel.renderType(MobHeadTileEntity.HeadType.CERBERUS.getTexture()));
      cerberusHeadModel.renderToBuffer(matrixStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
    }
  }
}
