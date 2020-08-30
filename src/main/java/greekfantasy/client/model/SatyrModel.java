package greekfantasy.client.model;

import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SatyrModel<T extends SatyrEntity> extends BipedModel<T> {
  
  private final ModelRenderer rightEar;
  private final ModelRenderer leftEar;
  private final ModelRenderer rightHorn;
  private final ModelRenderer leftHorn;
  
  private final ModelRenderer tail;
  
  private final ModelRenderer upperLeftLeg;
  private final ModelRenderer lowerLeftLeg;
  private final ModelRenderer leftHoof;
  
  private final ModelRenderer upperRightLeg;
  private final ModelRenderer lowerRightLeg;
  private final ModelRenderer rightHoof;

  public SatyrModel(float modelSize) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;
//
//    this.bipedHead = new ModelRenderer(this);
//    this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
//    this.bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    this.bipedHead.setTextureSize(textureWidth, textureHeight).setTextureOffset(0, 0);
    this.bipedHeadwear.showModel = false;

    rightEar = new ModelRenderer(this);
    rightEar.setRotationPoint(-3.0F, -4.0F, -1.0F);
    setRotationAngle(rightEar, -0.2618F, -0.2618F, 0.0F);
    rightEar.setTextureOffset(56, 16).addBox(-1.0F, -1.0F, 0.0F, 0.0F, 2.0F, 3.0F, modelSize);
    this.bipedHead.addChild(rightEar);

    leftEar = new ModelRenderer(this);
    leftEar.setRotationPoint(4.0F, -4.0F, -1.0F);
    setRotationAngle(leftEar, -0.2618F, 0.2618F, 0.0F);
    leftEar.setTextureOffset(56, 22).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 3.0F, modelSize);
    leftEar.mirror = true;
    this.bipedHead.addChild(leftEar);

    rightHorn = new ModelRenderer(this);
    rightHorn.setRotationPoint(-4.0F, -6.0F, 0.0F);
    setRotationAngle(rightHorn, 0.0F, 0.0F, -0.2618F);
    rightHorn.setTextureOffset(48, 48).addBox(0.0F, -8.0F, -4.0F, 0.0F, 8.0F, 8.0F, modelSize);
    this.bipedHead.addChild(rightHorn);

    leftHorn = new ModelRenderer(this);
    leftHorn.setRotationPoint(4.0F, -6.0F, 0.0F);
    setRotationAngle(leftHorn, 0.0F, 0.0F, 0.2618F);
    leftHorn.setTextureOffset(48, 40).addBox(0.0F, -8.0F, -4.0F, 0.0F, 8.0F, 8.0F, modelSize);
    leftHorn.mirror = true;
    this.bipedHead.addChild(leftHorn);

//    this.bipedBody = new ModelRenderer(this);
//    this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
//    this.bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSize);
    this.bipedBody.setTextureSize(textureWidth, textureHeight).setTextureOffset(16, 16);
    
    tail = new ModelRenderer(this);
    tail.setRotationPoint(0.0F, 0.0F, 0.0F); // -1.0F, -13.0F, 7.5F
    tail.setTextureOffset(0, 52).addBox(1.0F, 7.0F, 2.0F, 0.0F, 6.0F, 6.0F, modelSize);
    this.bipedBody.addChild(tail);

//    this.bipedLeftArm = new ModelRenderer(this);
//    this.bipedLeftArm.setRotationPoint(4.0F, 2.0F, 2.0F);
//    this.bipedLeftArm.setTextureOffset(32, 48).addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
//    this.bipedLeftArm.mirror = true;
    this.bipedLeftArm.setTextureSize(textureWidth, textureHeight).setTextureOffset(32, 48);
//    this.bipedRightArm = new ModelRenderer(this);
//    this.bipedRightArm.setRotationPoint(-4.0F, 2.0F, 2.0F);
//    this.bipedRightArm.setTextureOffset(40, 16).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
//
    this.bipedRightArm.setTextureSize(textureWidth, textureHeight).setTextureOffset(40, 16);
    
    this.bipedLeftLeg = new ModelRenderer(this);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
    this.bipedLeftLeg.mirror = true;

    upperLeftLeg = new ModelRenderer(this);
    upperLeftLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
    setRotationAngle(upperLeftLeg, -0.2618F, 0.0F, 0.0F);
    upperLeftLeg.setTextureOffset(16, 36).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    upperLeftLeg.mirror = true;
    this.bipedLeftLeg.addChild(upperLeftLeg);

    lowerLeftLeg = new ModelRenderer(this);
    lowerLeftLeg.setRotationPoint(0.0F, 6.0F, -2.0F);
    setRotationAngle(lowerLeftLeg, 0.7854F, 0.0F, 0.0F);
    lowerLeftLeg.setTextureOffset(16, 46).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    lowerLeftLeg.mirror = true;
    upperLeftLeg.addChild(lowerLeftLeg);

    leftHoof = new ModelRenderer(this);
    leftHoof.setRotationPoint(0.0F, 6.0F, 4.0F);
    setRotationAngle(leftHoof, -0.5236F, 0.0F, 0.0F);
    leftHoof.setTextureOffset(16, 56).addBox(-1.9F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    leftHoof.mirror = true;
    lowerLeftLeg.addChild(leftHoof);

    this.bipedRightLeg = new ModelRenderer(this);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
    
    upperRightLeg = new ModelRenderer(this);
    upperRightLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
    setRotationAngle(upperRightLeg, -0.2618F, 0.0F, 0.0F);
    upperRightLeg.setTextureOffset(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    this.bipedRightLeg.addChild(upperRightLeg);

    lowerRightLeg = new ModelRenderer(this);
    lowerRightLeg.setRotationPoint(0.0F, 6.0F, -2.0F);
    setRotationAngle(lowerRightLeg, 0.7854F, 0.0F, 0.0F);
    lowerRightLeg.setTextureOffset(0, 26).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    upperRightLeg.addChild(lowerRightLeg);

    rightHoof = new ModelRenderer(this);
    rightHoof.setRotationPoint(0.0F, 6.0F, 4.0F);
    setRotationAngle(rightHoof, -0.5236F, 0.0F, 0.0F);
    rightHoof.setTextureOffset(0, 36).addBox(-2.1F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    lowerRightLeg.addChild(rightHoof);
  }

  public void setRotationAngle(final ModelRenderer modelRenderer, final float x, final float y, final float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
}
