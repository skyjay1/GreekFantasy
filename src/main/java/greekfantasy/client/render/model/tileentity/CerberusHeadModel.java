package greekfantasy.client.render.model.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.client.render.CerberusRenderer;
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
    cerberusHead.setRotationPoint(-4.5F, -4.0F, 0.0F);
    cerberusHead.setTextureOffset(0, 0).addBox(-2.5F, -2.0F, -5.0F, 5.0F, 6.0F, 5.0F, modelSize);
    cerberusHead.setTextureOffset(21, 0).addBox(-1.5F, 1.0F, -9.0F, 3.0F, 2.0F, 4.0F, modelSize);
    cerberusHead.setTextureOffset(16, 0).addBox(-2.5F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, modelSize);
    cerberusHead.setTextureOffset(16, 0).addBox(0.5F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);

    cerberusMouth = new ModelRenderer(this);
    cerberusMouth.setRotationPoint(0.0F, 3.0F, -5.0F);
    cerberusMouth.setTextureOffset(21, 6).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, modelSize);
    cerberusHead.addChild(cerberusMouth);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    getModelHead().render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
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
    return CerberusRenderer.SCALE;
  }
}
