package greekfantasy.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.entity.SpartiEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public class SpartiModel<T extends SpartiEntity> extends BipedModel<T> {

  public SpartiModel(float modelSize) {
    super(modelSize);
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
    
    this.bipedLeftArm = new ModelRenderer(this, 40, 16);
    this.bipedLeftArm.mirror = true;
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
    
    this.bipedRightLeg = new ModelRenderer(this, 0, 16);
    this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
    
    this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
    this.bipedLeftLeg.mirror = true;
    this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
  }

  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
  }

  /**
   * Sets this entity's model rotation angles
   */
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
  }

  @Override
  public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
    float f = sideIn == HandSide.RIGHT ? 1.0F : -1.0F;
    ModelRenderer modelrenderer = this.getArmForSide(sideIn);
    modelrenderer.rotationPointX += f;
    modelrenderer.translateRotate(matrixStackIn);
    modelrenderer.rotationPointX -= f;
  }

}
