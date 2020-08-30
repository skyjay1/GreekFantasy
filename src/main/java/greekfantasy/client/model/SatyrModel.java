package greekfantasy.client.model;

import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class SatyrModel<T extends SatyrEntity> extends BipedModel<T> {
  
  private final ModelRenderer rightEar;
  private final ModelRenderer leftEar;
  private final ModelRenderer tail;
  private final ModelRenderer tail2;
  
  private final ModelRenderer leftLegUpper;
  private final ModelRenderer leftLegLower;
  private final ModelRenderer leftHoof;
  
  private final ModelRenderer rightLegUpper;
  private final ModelRenderer rightLegLower;
  private final ModelRenderer rightHoof;

  public SatyrModel(float modelSize) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;
    
    // head, horseBody
    
    this.bipedHead = new ModelRenderer(this, 0, 0);
    this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
    
    this.bipedHeadwear = new ModelRenderer(this, 32, 0);
    this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.5F);
    this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
    
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

    // ears
    
    rightEar = new ModelRenderer(this);
    rightEar.setRotationPoint(-3.0F, -4.0F, -1.0F);
    setRotationAngle(rightEar, -0.2618F, -0.3491F, 0.0F);
    rightEar.setTextureOffset(56, 16).addBox(-1.5F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, modelSize);
    this.bipedHead.addChild(rightEar);

    leftEar = new ModelRenderer(this);
    leftEar.setRotationPoint(4.0F, -4.0F, -1.0F);
    this.bipedHead.addChild(leftEar);
    setRotationAngle(leftEar, -0.2618F, 0.3491F, 0.0F);
    leftEar.setTextureOffset(56, 22).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, modelSize);

    // horns
    
    this.bipedHead.addChild(makeHornModel(modelSize, true));
    this.bipedHead.addChild(makeHornModel(modelSize, false));
    
    // tail
    
    tail = new ModelRenderer(this);
    tail.setRotationPoint(0.0F, 11.0F, 2.0F);
    this.bipedBody.addChild(tail);
    tail.rotateAngleX = 0.5236F;
    tail.setTextureOffset(0, 51).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

    tail2 = new ModelRenderer(this);
    tail2.setRotationPoint(0.0F, 5.0F, -1.0F);
    tail.addChild(tail2);
    tail2.rotateAngleX = 0.3491F;
    tail2.setTextureOffset(4, 51).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
    tail2.setTextureOffset(0, 58).addBox(-1.0F, 2.5F, -0.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);

    // legs
    
    this.bipedLeftLeg = new ModelRenderer(this);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
    this.bipedLeftLeg.mirror = true;

    leftLegUpper = new ModelRenderer(this);
    leftLegUpper.setRotationPoint(0.0F, 0.0F, 0.0F);
    leftLegUpper.rotateAngleX = -0.2618F;
    leftLegUpper.setTextureOffset(16, 36).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    leftLegUpper.mirror = true;
    this.bipedLeftLeg.addChild(leftLegUpper);

    leftLegLower = new ModelRenderer(this);
    leftLegLower.setRotationPoint(0.0F, 6.0F, -2.0F);
    leftLegLower.setTextureOffset(16, 46).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    leftLegLower.mirror = true;
    leftLegUpper.addChild(leftLegLower);

    leftHoof = new ModelRenderer(this);
    leftHoof.setRotationPoint(0.0F, 6.0F, 4.0F);
    leftHoof.setTextureOffset(16, 56).addBox(-1.9F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    leftHoof.mirror = true;
    leftLegLower.addChild(leftHoof);

    this.bipedRightLeg = new ModelRenderer(this);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
    
    rightLegUpper = new ModelRenderer(this);
    rightLegUpper.setRotationPoint(0.0F, 0.0F, 0.0F);
    rightLegUpper.rotateAngleX = -0.2618F;
    rightLegUpper.setTextureOffset(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    this.bipedRightLeg.addChild(rightLegUpper);

    rightLegLower = new ModelRenderer(this);
    rightLegLower.setRotationPoint(0.0F, 6.0F, -2.0F);
    rightLegLower.setTextureOffset(0, 26).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    rightLegUpper.addChild(rightLegLower);

    rightHoof = new ModelRenderer(this);
    rightHoof.setRotationPoint(0.0F, 6.0F, 4.0F);
    rightHoof.setTextureOffset(0, 36).addBox(-2.1F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    rightLegLower.addChild(rightHoof);
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
    float idleSwing = 0.1F * MathHelper.cos((entityIn.ticksExisted + partialTick) * 0.08F);
    float tailSwing = 0.42F * limbSwingCos;
    tail.rotateAngleX = 0.6854F + tailSwing;
    tail2.rotateAngleX = 0.3491F + tailSwing * 0.6F;
    tail.rotateAngleZ = idleSwing;
    tail2.rotateAngleZ = idleSwing * 0.85F;
  }
  
  private ModelRenderer makeHornModel(final float modelSize, final boolean isLeft) {
    final int textureX = isLeft ? 54 : 47;
    final float horn1X = isLeft ? 4.0F : -5.0F;
    final float horn2X = isLeft ? 8.25F : -1.25F;
    final float horn3X = isLeft ? 8.5F : -1.5F;
    
    final ModelRenderer leftHorn3 = new ModelRenderer(this);
    leftHorn3.setRotationPoint(0.0F, -3.0F, 0.0F);
    leftHorn3.rotateAngleX = -0.7854F;
    leftHorn3.setTextureOffset(textureX, 59).addBox(horn3X, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
    leftHorn3.mirror = isLeft;
    
    final ModelRenderer leftHorn2 = new ModelRenderer(this);
    leftHorn2.setRotationPoint(-4.0F, -4.0F, -1.0F);
    leftHorn2.rotateAngleX = -0.7854F;
    leftHorn2.setTextureOffset(textureX, 54).addBox(horn2X, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
    leftHorn2.addChild(leftHorn3);
    leftHorn2.mirror = isLeft;
    
    final ModelRenderer leftHorn = new ModelRenderer(this);
    leftHorn.setRotationPoint(0.0F, -6.0F, -1.0F);
    leftHorn.rotateAngleX = 0.8727F;
    leftHorn.setTextureOffset(textureX, 48).addBox(horn1X, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, modelSize);
    leftHorn.addChild(leftHorn2);
    leftHorn.mirror = isLeft;
    
    return leftHorn;
  }

  private static void setRotationAngle(final ModelRenderer modelRenderer, final float x, final float y, final float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
}
