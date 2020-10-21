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
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MobHeadTileEntityRenderer extends TileEntityRenderer<MobHeadTileEntity> {
      
  protected IWallModel giganteHeadModel;
  protected IWallModel cerberusHeadModel;
  protected IWallModel orthusHeadModel;
  
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
    final float rotation = tileEntityIn.getBlockState().get(StatueBlock.HORIZONTAL_FACING).getHorizontalAngle();
    final ResourceLocation texture = head.getTexture();
    // determine which model to use
    final IWallModel model = getModel(head);
    final float scale = model.getScale();
    matrixStackIn.push();
    // prepare to render model
    
    matrixStackIn.translate(0.5D, 0D, 0.5D);
    matrixStackIn.scale(scale, scale, scale);
    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation));
    matrixStackIn.translate(.5D / scale, 0D, .5D / scale);
    IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(texture));
    // render the correct model
    
    model.setWallRotations(tileEntityIn.onWall());    
    model.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
    matrixStackIn.pop();
  }
 
  protected IWallModel getModel(final MobHeadTileEntity.HeadType head) {
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
    void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
  }
}
