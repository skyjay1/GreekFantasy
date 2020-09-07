package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.OrthusEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class OrthusModel<T extends OrthusEntity> extends AgeableModel<T> {
  

  private final ModelRenderer head;
  private final ModelRenderer headChild1;
  private final ModelRenderer headChild2;

  private final ModelRenderer body;
  private final ModelRenderer legBackRight;
  private final ModelRenderer legBackLeft;
  private final ModelRenderer legFrontRight; 
  private final ModelRenderer legFrontLeft; 
  private final ModelRenderer tail; 
  private final ModelRenderer tailChild;
  private final ModelRenderer mane;


  public OrthusModel(final float modelSize) {
    super();
    textureWidth = 64;
    textureHeight = 32;
    
    this.head = new ModelRenderer(this, 0, 0);
    this.head.setRotationPoint(-1.0F, 13.5F + modelSize, -7.0F);
    
    this.headChild1 = new ModelRenderer(this, 0, 0);
    this.headChild1.setRotationPoint(0.0F, 0.0F, 0.0F); // added
    this.headChild1.rotateAngleY = -0.48F; // added
    this.headChild1.addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
    this.headChild1.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    this.headChild1.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    this.headChild1.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);
    this.head.addChild(this.headChild1);
    
    this.headChild2 = new ModelRenderer(this, 0, 0);
    this.headChild2.setRotationPoint(0.0F, 0.0F, 0.0F); // added
    this.headChild2.rotateAngleY = 0.48F; // added
    this.headChild2.addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
    this.headChild2.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    this.headChild2.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    this.headChild2.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);
    this.head.addChild(this.headChild2);
    
    

    this.body = new ModelRenderer(this, 18, 14);
    this.body.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, 0.0F);
    this.body.setRotationPoint(0.0F, 14.0F + modelSize, 2.0F);
    
    this.mane = new ModelRenderer(this, 21, 0);
    this.mane.addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, 0.0F);
    this.mane.setRotationPoint(-1.0F, 14.0F + modelSize, 2.0F);
    
    this.legBackRight = new ModelRenderer(this, 0, 18);
    this.legBackRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
    this.legBackRight.setRotationPoint(-2.5F, 16.0F + modelSize, 7.0F);
    
    this.legBackLeft = new ModelRenderer(this, 0, 18);
    this.legBackLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
    this.legBackLeft.setRotationPoint(0.5F, 16.0F + modelSize, 7.0F);
    
    this.legFrontRight = new ModelRenderer(this, 0, 18);
    this.legFrontRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
    this.legFrontRight.setRotationPoint(-2.5F, 16.0F + modelSize, -4.0F);
    
    this.legFrontLeft = new ModelRenderer(this, 0, 18);
    this.legFrontLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
    this.legFrontLeft.setRotationPoint(0.5F, 16.0F + modelSize, -4.0F);
    
    this.tail = new ModelRenderer(this, 9, 18);
    this.tail.setRotationPoint(-1.0F, 12.0F + modelSize, 8.0F);

    this.tailChild = new ModelRenderer(this, 9, 18);
    this.tailChild.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
    this.tail.addChild(this.tailChild);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.tail, this.mane); }

  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return ImmutableList.of(this.head); }

  public void setLivingAnimations(final T entity, float limbSwing, float limbSwingAmount, float p_212843_4_) {
//    if (entity.isAggressive()) {
//      this.tail.rotateAngleY = 0.0F;
//    } else {
      this.tail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
//    } 
    
//    if (entity.isSitting()) {
//      this.mane.setRotationPoint(-1.0F, 16.0F, -3.0F);
//      this.mane.rotateAngleX = 1.2566371F;
//      this.mane.rotateAngleY = 0.0F;
//      
//      this.body.setRotationPoint(0.0F, 18.0F, 0.0F);
//      this.body.rotateAngleX = 0.7853982F;
//      
//      this.tail.setRotationPoint(-1.0F, 21.0F, 6.0F);
//      
//      this.legBackRight.setRotationPoint(-2.5F, 22.7F, 2.0F);
//      this.legBackRight.rotateAngleX = 4.712389F;
//      this.legBackLeft.setRotationPoint(0.5F, 22.7F, 2.0F);
//      this.legBackLeft.rotateAngleX = 4.712389F;
//      
//      this.legFrontRight.rotateAngleX = 5.811947F;
//      this.legFrontRight.setRotationPoint(-2.49F, 17.0F, -4.0F);
//      this.legFrontLeft.rotateAngleX = 5.811947F;
//      this.legFrontLeft.setRotationPoint(0.51F, 17.0F, -4.0F);
//    } else {
//      this.body.setRotationPoint(0.0F, 14.0F, 2.0F);
//      this.body.rotateAngleX = 1.5707964F;
      
//      this.mane.setRotationPoint(-1.0F, 14.0F, -3.0F);
//      this.mane.rotateAngleX = this.body.rotateAngleX;
      
//      this.tail.setRotationPoint(-1.0F, 12.0F, 8.0F);
      
//      this.legBackRight.setRotationPoint(-2.5F, 16.0F, 7.0F);
//      this.legBackLeft.setRotationPoint(0.5F, 16.0F, 7.0F);
//      this.legFrontRight.setRotationPoint(-2.5F, 16.0F, -4.0F);
//      this.legFrontLeft.setRotationPoint(0.5F, 16.0F, -4.0F);
      
      this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
      this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
      this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
//    } 
    
    //this.headChild.rotateAngleZ = p_212843_1_.getInterestedAngle(p_212843_4_) + p_212843_1_.getShakeAngle(p_212843_4_, 0.0F);
    
    //this.mane.rotateAngleZ = p_212843_1_.getShakeAngle(p_212843_4_, -0.08F);
    //this.body.rotateAngleZ = p_212843_1_.getShakeAngle(p_212843_4_, -0.16F);
    //this.tailChild.rotateAngleZ = p_212843_1_.getShakeAngle(p_212843_4_, -0.2F);
  }

  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, 
      float rotationYaw, float rotationPitch) {
    this.head.rotateAngleX = rotationPitch * 0.017453292F;
    this.head.rotateAngleY = rotationYaw * 0.017453292F;
    
    this.tail.rotateAngleX = limbSwingAmount;
  } 
}
