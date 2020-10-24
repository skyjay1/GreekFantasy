package greekfantasy.client.render.model.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.client.render.OrthusRenderer;
import greekfantasy.client.render.model.OrthusModel;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer.IWallModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class OrthusHeadModel extends Model implements IHasHead, IWallModel {
  
  protected ModelRenderer orthusHead;
    
  public OrthusHeadModel() {
    this(0.0F, 0.0F);
  }

  public OrthusHeadModel(final float modelSize, final float yOffset) {
    super(RenderType::getEntityCutoutNoCull);
    this.textureWidth = 64;
    this.textureHeight = 32;
    
    orthusHead = OrthusModel.getHeadModel(this, -5.55F, -3.0F, 0.0F, 0.0F);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    getModelHead().render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
  }
 
  @Override
  public ModelRenderer getModelHead() {
    return this.orthusHead;
  }

  @Override
  public void setWallRotations(boolean onWall) {
    if(onWall) {
      orthusHead.rotationPointY = -5.0F;
      orthusHead.rotationPointZ = 0.2F;
    } else {
      orthusHead.rotationPointY = -3.0F;
      orthusHead.rotationPointZ = -3.0F;
    }
  }
  
  @Override
  public float getScale() {
    return OrthusRenderer.SCALE;
  }
  
  public void setGuiRotations(final float yaw, final float pitch) {
    this.orthusHead.rotateAngleX = pitch * 0.017453292F;
    this.orthusHead.rotateAngleY = yaw * 0.017453292F;
  }
}
