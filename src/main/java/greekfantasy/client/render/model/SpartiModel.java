package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.entity.SpartiEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public class SpartiModel<T extends SpartiEntity> extends BipedModel<T> {

  public SpartiModel(float modelSize) {
    super(modelSize);
    this.rightArm = new ModelRenderer(this, 40, 16);
    this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
    
    this.leftArm = new ModelRenderer(this, 40, 16);
    this.leftArm.mirror = true;
    this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.leftArm.setPos(5.0F, 2.0F, 0.0F);
    
    this.rightLeg = new ModelRenderer(this, 0, 16);
    this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
    
    this.leftLeg = new ModelRenderer(this, 0, 16);
    this.leftLeg.mirror = true;
    this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
    this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
  }

  @Override
  public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
  }

  /**
   * Sets this entity's model rotation angles
   */
  public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
  }

  @Override
  public void translateToHand(HandSide sideIn, MatrixStack matrixStackIn) {
    float f = sideIn == HandSide.RIGHT ? 1.0F : -1.0F;
    ModelRenderer modelrenderer = this.getArm(sideIn);
    modelrenderer.x += f;
    modelrenderer.translateAndRotate(matrixStackIn);
    modelrenderer.x -= f;
  }

}
