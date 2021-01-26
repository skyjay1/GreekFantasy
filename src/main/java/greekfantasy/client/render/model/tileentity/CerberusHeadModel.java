package greekfantasy.client.render.model.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.client.render.CerberusRenderer;
import greekfantasy.client.render.model.CerberusModel;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer.IWallModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CerberusHeadModel extends Model implements IHasHead, IWallModel {
  
  private final ModelRenderer cerberusHead;
  private final ModelRenderer cerberusMouth;
    
  public CerberusHeadModel() {
    this(0.0F, 0.0F);
  }

  public CerberusHeadModel(final float modelSize, final float yOffset) {
    super(RenderType::getEntityCutoutNoCull);
    this.textureWidth = 64;
    this.textureHeight = 64;
    
    cerberusHead = new ModelRenderer(this);
    cerberusMouth = new ModelRenderer(this);
    CerberusModel.initCerberusHead(this, cerberusHead, cerberusMouth, -4.5F, -4.0F, 0.0F);
    cerberusMouth.rotateAngleX = 0.19F;
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    //matrixStackIn.translate(0.04125D, 0, 0);
    cerberusHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
  }
 
  @Override
  public ModelRenderer getModelHead() {
    return this.cerberusHead;
  }

  @Override
  public void setWallRotations(boolean onWall) {
    if(onWall) {
      cerberusHead.rotationPointY = -4.5F;
      cerberusMouth.rotateAngleX = 0.19F;
    } else {
      cerberusHead.rotationPointY = -4.0F;
      cerberusMouth.rotateAngleX = 0.0F;
    }
  }
  
  @Override
  public float getScale() {
    return CerberusRenderer.SCALE;// + 0.1125F;
  }
}
