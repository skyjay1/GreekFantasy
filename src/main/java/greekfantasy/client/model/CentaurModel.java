package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.CentaurEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CentaurModel<T extends CentaurEntity> extends BipedModel<T> {

  // horse parts
  private final ModelRenderer horseBody;
  private final ModelRenderer leftBackLeg;
  private final ModelRenderer rightBackLeg;
  private final ModelRenderer leftFrontLeg;
  private final ModelRenderer rightFrontLeg;
  private final ModelRenderer tail;
  
  // quiver parts
  private final ModelRenderer quiver;
  private final ModelRenderer arrows;
  private final ModelRenderer arrow1;
  private final ModelRenderer arrow2;
  
  public CentaurModel(float modelSize) {
    super(modelSize);
    this.textureWidth = 64;
    this.textureHeight = 64;
    
    // human parts
    this.bipedBody = new ModelRenderer(this, 16, 16);
    this.bipedBody.setRotationPoint(0.0F, 0.0F, -2.5F);
    this.bipedBody.addBox(-4.0F, -9.0F, -10.0F, 8.0F, 12.0F, 4.0F, modelSize);

    this.bipedHead = makeHeadModel(modelSize);
    
    this.bipedHeadwear = new ModelRenderer(this, 32, 0);
    this.bipedHeadwear.setRotationPoint(0.0F, -9.0F, -10.5F);
    this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.5F);

    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.setRotationPoint(4.0F, -8.0F, -10.5F);
    this.bipedLeftArm.addBox(0.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftArm.mirror = true;

    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.setRotationPoint(-4.0F, -8.0F, -10.5F);
    this.bipedRightArm.addBox(-4.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    
    // hide biped legs
    this.bipedLeftLeg.showModel = false;
    this.bipedRightLeg.showModel = false;
    
    // horse parts
    this.horseBody = new ModelRenderer(this, 0, 32);
    this.horseBody.addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, 0.05F);
    this.horseBody.setRotationPoint(0.0F, 0.0F, 5.0F);

    this.leftBackLeg = new ModelRenderer(this, 48, 21);
    this.leftBackLeg.addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSize);
    this.leftBackLeg.setRotationPoint(4.0F, 14.0F, 7.0F);
    this.leftBackLeg.mirror = true;
    
    this.rightBackLeg = new ModelRenderer(this, 48, 21);
    this.rightBackLeg.addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSize);
    this.rightBackLeg.setRotationPoint(-4.0F, 14.0F, 7.0F);
    
    this.leftFrontLeg = new ModelRenderer(this, 48, 21);
    this.leftFrontLeg.addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSize);
    this.leftFrontLeg.setRotationPoint(4.0F, 6.0F, -12.0F);
    this.leftFrontLeg.mirror = true;
    
    this.rightFrontLeg = new ModelRenderer(this, 48, 21);
    this.rightFrontLeg.addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSize);
    this.rightFrontLeg.setRotationPoint(-4.0F, 6.0F, -12.0F);
    
    this.tail = new ModelRenderer(this, 42, 36);
    this.tail.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, modelSize);
    this.tail.setRotationPoint(0.0F, -5.0F, 2.0F);
    this.tail.rotateAngleX = 0.5235988F;
    this.horseBody.addChild(this.tail);
    
    // quiver parts
    quiver = new ModelRenderer(this);
    quiver.setRotationPoint(0.0F, -4.0F, -6.5F);
    quiver.rotateAngleZ = 0.6109F;
    quiver.setTextureOffset(48, 0).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 10.0F, 4.0F, modelSize);

    arrows = new ModelRenderer(this);
    arrows.setRotationPoint(0.0F, 0.0F, 0.0F);
    quiver.addChild(arrows);

    arrow1 = new ModelRenderer(this);
    arrow1.setRotationPoint(0.0F, -7.0F, 0.0F);
    arrow1.setTextureOffset(28, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, modelSize);
    arrows.addChild(arrow1);

    arrow2 = new ModelRenderer(this);
    arrow2.setRotationPoint(0.0F, -7.0F, 0.0F);
    arrow2.rotateAngleY = -1.5708F;
    arrow2.setTextureOffset(28, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, modelSize);
    arrows.addChild(arrow2);
  }
  
  protected ModelRenderer makeHeadModel(final float modelSize) {
    final ModelRenderer head = new ModelRenderer(this, 0, 0);
    head.setRotationPoint(0.0F, -9.0F, -10.5F);
    head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    return head;
  }

  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.bipedLeftArm, this.bipedRightArm, this.bipedHeadwear); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float p_225597_4_, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, p_225597_4_, rotationYaw, rotationPitch);
    this.horseBody.rotationPointY = 11.0F;
    if(entity.isRearing()) {
      // TODO find a way to lerp this, maybe in #setLivingAnimations
      this.bipedBody.setRotationPoint(0.0F, -7.0F, 5.0F);
      this.bipedRightArm.setRotationPoint(-4.0F, -15.0F, -3.0F);
      this.bipedLeftArm.setRotationPoint(4.0F, -15.0F, -3.0F);
      this.bipedHead.setRotationPoint(0.0F, -16.0F, -3.0F);
      this.bipedHeadwear.setRotationPoint(0.0F, -16.0F, -3.0F);
      this.quiver.setRotationPoint(0.0F, -11.0F, -13.0F);
    } else {
      this.bipedBody.setRotationPoint(0.0F, 0.0F, -2.5F);
      this.bipedRightArm.setRotationPoint(-4.0F, -8.0F, -10.5F);
      this.bipedLeftArm.setRotationPoint(4.0F, -8.0F, -10.5F);
      this.bipedHead.setRotationPoint(0.0F, -9.0F, -10.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, -9.0F, -10.5F);
      this.quiver.setRotationPoint(0.0F, -4.0F, -6.5F);
    }
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);

    float rearingTime = entity.getRearingAmount(partialTick);
    float rearingTimeLeft = 1.0F - rearingTime;
    boolean tailSwinging = (entity.tailCounter != 0);
    float ticks = entity.ticksExisted + partialTick;
     
    this.horseBody.rotateAngleX = 0.0F;
   
    float lvt_16_1_ = entity.isInWater() ? 0.2F : 1.0F;
    float lvt_17_1_ = MathHelper.cos(lvt_16_1_ * limbSwing * 0.6662F + 3.1415927F);
    float lvt_18_1_ = lvt_17_1_ * 0.8F * limbSwingAmount;

    this.horseBody.rotateAngleX = rearingTime * -0.7853982F + rearingTimeLeft * this.horseBody.rotateAngleX;
    
    float rearingAmount = 0.2617994F * rearingTime;
    float lvt_21_1_ = MathHelper.cos(ticks * 0.6F + 3.1415927F);
    
    this.leftFrontLeg.rotationPointY = 2.0F * rearingTime + 14.0F * rearingTimeLeft;
    this.leftFrontLeg.rotationPointZ = -6.0F * rearingTime - 10.0F * rearingTimeLeft;
    this.rightFrontLeg.rotationPointY = this.leftFrontLeg.rotationPointY;
    this.rightFrontLeg.rotationPointZ = this.leftFrontLeg.rotationPointZ;
    // TODO something like the above but for bipedal body section
    // 
    
    float lvt_22_1_ = (-1.0471976F + lvt_21_1_) * rearingTime + lvt_18_1_ * rearingTimeLeft;
    float lvt_23_1_ = (-1.0471976F - lvt_21_1_) * rearingTime - lvt_18_1_ * rearingTimeLeft;
    
    this.leftBackLeg.rotateAngleX = rearingAmount - lvt_17_1_ * 0.5F * limbSwingAmount * rearingTimeLeft;
    this.rightBackLeg.rotateAngleX = rearingAmount + lvt_17_1_ * 0.5F * limbSwingAmount * rearingTimeLeft;
    this.leftFrontLeg.rotateAngleX = lvt_22_1_;
    this.rightFrontLeg.rotateAngleX = lvt_23_1_;
    
    this.tail.rotateAngleX = 0.5235988F + limbSwingAmount * 0.75F;
    this.tail.rotationPointY = -5.0F + limbSwingAmount;
    this.tail.rotationPointZ = 2.0F + limbSwingAmount * 2.0F;
    
    if (tailSwinging) {
      this.tail.rotateAngleY = MathHelper.cos(ticks * 0.7F);
    } else {
      this.tail.rotateAngleY = 0.0F;
    } 
 
    boolean child = entity.isChild();
    
    this.horseBody.rotationPointY = child ? 10.8F : 0.0F;
  }
  
  public void renderHorseBody(T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    this.horseBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    this.leftBackLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    this.rightBackLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    this.leftFrontLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    this.rightFrontLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
  
  public void renderQuiver(T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    this.quiver.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    this.bipedBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
