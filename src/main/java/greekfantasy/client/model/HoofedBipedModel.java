package greekfantasy.client.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class HoofedBipedModel<T extends LivingEntity> extends BipedModel<T> {
  
  protected final ModelRenderer tail;
  protected final ModelRenderer tail2;
  
  protected final ModelRenderer leftLegUpper;
  protected final ModelRenderer leftLegLower;
  protected final ModelRenderer leftHoof;
  
  protected final ModelRenderer rightLegUpper;
  protected final ModelRenderer rightLegLower;
  protected final ModelRenderer rightHoof;

  public HoofedBipedModel(final float modelSize, final boolean hasTail, final boolean showHeadwear) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;
    
    // head, body
    
    this.bipedHead = new ModelRenderer(this);
    this.bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
    
    this.bipedHeadwear = new ModelRenderer(this, 32, 0);
    this.bipedHeadwear.showModel = showHeadwear;
    if(showHeadwear) {
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
    }
    
    this.bipedBody = new ModelRenderer(this, 16, 16);
    this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSize);
    this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
    
    // arms
    
    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
    this.bipedLeftArm.mirror = true;
    
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);

    // legs
    
    this.bipedLeftLeg = new ModelRenderer(this);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
    this.bipedLeftLeg.mirror = true;

    leftLegUpper = new ModelRenderer(this, 16, 36);
    leftLegUpper.setRotationPoint(0.0F, 0.0F, 0.0F);
    leftLegUpper.rotateAngleX = -0.2618F;
    leftLegUpper.addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    leftLegUpper.mirror = true;
    this.bipedLeftLeg.addChild(leftLegUpper);

    leftLegLower = new ModelRenderer(this, 16, 46);
    leftLegLower.setRotationPoint(0.0F, 6.0F, -2.0F);
    leftLegLower.addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    leftLegLower.mirror = true;
    leftLegUpper.addChild(leftLegLower);

    leftHoof = new ModelRenderer(this, 16, 56);
    leftHoof.setRotationPoint(0.0F, 6.0F, 4.0F);
    leftHoof.addBox(-1.9F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    leftHoof.mirror = true;
    leftLegLower.addChild(leftHoof);

    this.bipedRightLeg = new ModelRenderer(this);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
    
    rightLegUpper = new ModelRenderer(this, 0, 16);
    rightLegUpper.setRotationPoint(0.0F, 0.0F, 0.0F);
    rightLegUpper.rotateAngleX = -0.2618F;
    rightLegUpper.addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    this.bipedRightLeg.addChild(rightLegUpper);

    rightLegLower = new ModelRenderer(this, 0, 26);
    rightLegLower.setRotationPoint(0.0F, 6.0F, -2.0F);
    rightLegLower.addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    rightLegUpper.addChild(rightLegLower);

    rightHoof = new ModelRenderer(this, 0, 36);
    rightHoof.setRotationPoint(0.0F, 6.0F, 4.0F);
    rightHoof.addBox(-2.1F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    rightLegLower.addChild(rightHoof);
    
    // tail
    
    tail = new ModelRenderer(this, 0, 51);
    tail2 = new ModelRenderer(this);
    tail.showModel = hasTail;
    tail2.showModel = hasTail;
    if(hasTail) { 
      tail.setRotationPoint(0.0F, 11.0F, 2.0F);
      tail.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      this.bipedBody.addChild(tail);
  
      tail2.setRotationPoint(0.0F, 5.0F, -1.0F);
      tail2.setTextureOffset(4, 51).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
      tail2.setTextureOffset(0, 58).addBox(-1.0F, 2.5F, -0.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);
      tail.addChild(tail2);
    }
  }
  
  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    final float limbSwingSin = MathHelper.sin(limbSwing) * limbSwingAmount;
    final float limbSwingCos = MathHelper.cos(limbSwing) * limbSwingAmount;
    // legs
    float rightLegSwing = 0.58F * limbSwingSin;
    float leftLegSwing = 0.58F * limbSwingCos;
    rightLegLower.rotateAngleX = 0.7854F + rightLegSwing;
    rightHoof.rotateAngleX = -0.5236F - rightLegSwing;
    leftLegLower.rotateAngleX = 0.7854F + leftLegSwing;
    leftHoof.rotateAngleX = -0.5236F - leftLegSwing;
    // tail
    if(tail.showModel) {
      float idleSwing = 0.1F * MathHelper.cos((entityIn.ticksExisted + partialTick) * 0.08F);
      float tailSwing = 0.42F * limbSwingCos;
      tail.rotateAngleX = 0.6854F + tailSwing;
      tail2.rotateAngleX = 0.3491F + tailSwing * 0.6F;
      tail.rotateAngleZ = idleSwing;
      tail2.rotateAngleZ = idleSwing * 0.85F;
    }
  }
}
