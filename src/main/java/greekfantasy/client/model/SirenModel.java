package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.SirenEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SirenModel<T extends SirenEntity> extends BipedModel<T> {
  private final ModelRenderer chest;
  private final ModelRenderer upperTail;
  private final ModelRenderer midTail;
  private final ModelRenderer lowerTail1;
  private final ModelRenderer lowerTail2;

  public SirenModel(final float modelSize) {
    super(modelSize, 0.0F, 64, 64);

    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 1.0F, -2.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(0, 17).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);

    upperTail = new ModelRenderer(this);
    upperTail.setRotationPoint(0.0F, 10.0F, 0.0F);
    setRotationAngle(upperTail, -0.2618F, 0.0F, 0.0F);
    upperTail.setTextureOffset(0, 32).addBox(-3.999F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, modelSize);

    midTail = new ModelRenderer(this);
    midTail.setRotationPoint(0.0F, 6.0F, -2.0F);
    upperTail.addChild(midTail);
    setRotationAngle(midTail, 0.5236F, 0.0F, 0.0F);
    midTail.setTextureOffset(0, 46).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 6.0F, 4.0F, modelSize);

    lowerTail1 = new ModelRenderer(this);
    lowerTail1.setRotationPoint(0.0F, 6.0F, 0.0F);
    midTail.addChild(lowerTail1);
    setRotationAngle(lowerTail1, 0.2618F, 0.0F, 0.0F);
    lowerTail1.setTextureOffset(0, 23).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 4.0F, modelSize);

    lowerTail2 = new ModelRenderer(this);
    lowerTail2.setRotationPoint(0.0F, 5.0F, 2.0F);
    lowerTail1.addChild(lowerTail2);
    lowerTail2.setTextureOffset(0, 57).addBox(-5.0F, -2.0F, -0.5F, 10.0F, 6.0F, 1.0F, modelSize);

    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
    this.bipedLeftArm.mirror = true;
    
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
    
    // hide biped legs
    this.bipedLeftLeg.showModel = false;
    this.bipedRightLeg.showModel = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.chest /* TODO use layer */, this.bipedLeftArm, this.bipedRightArm, this.upperTail, this.bipedHeadwear); }

  public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
  
  public void renderChest(T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    this.chest.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}