package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.CentaurEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class CentaurModel<T extends CentaurEntity & IRangedAttackMob> extends BipedModel<T> {

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
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
    // set arm poses
    final ItemStack item = entity.getHeldItem(Hand.MAIN_HAND);
    if (item.getItem() instanceof net.minecraft.item.BowItem && entity.isAggressive()) {
       if (entity.getPrimaryHand() == HandSide.RIGHT) {
          this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       } else {
          this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       }
    } else {
      this.rightArmPose = this.leftArmPose = BipedModel.ArmPose.EMPTY;
    }
    // super method
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch);
    // horse rotation angles
    this.horseBody.rotationPointY = 11.0F;
    float rearingTime = entity.getRearingAmount(ageInTicks - (float)Math.floor(ageInTicks));
    float rearingTimeLeft = 1.0F - rearingTime;
    this.bipedBody.setRotationPoint(0.0F, -7.0F * rearingTime, 5.0F * rearingTime - 2.5F * rearingTimeLeft);
    this.bipedHead.setRotationPoint(0.0F, -16.0F * rearingTime - 9.0F * rearingTimeLeft, -3.0F * rearingTime - 10.5F * rearingTimeLeft);
    this.bipedHeadwear.setRotationPoint(bipedHead.rotationPointX, bipedHead.rotationPointY, bipedHead.rotationPointZ);
    this.bipedLeftArm.setRotationPoint(4.0F, -15.0F * rearingTime - 8.0F * rearingTimeLeft, -3.0F * rearingTime - 10.5F * rearingTimeLeft);
    this.bipedRightArm.setRotationPoint(-4.0F, bipedLeftArm.rotationPointY, bipedLeftArm.rotationPointZ);
    this.quiver.setRotationPoint(0.0F, -11.0F * rearingTime - 4.0F * rearingTimeLeft, 1.5F * rearingTime - 6.5F * rearingTimeLeft);    
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    float ticks = entity.ticksExisted + partialTick;   
    float swimmingOffset = entity.isInWater() ? 0.2F : 1.0F;
    float limbSwingCos = MathHelper.cos(swimmingOffset * limbSwing * 0.6662F + 3.1415927F);
    float limbSwingAmountCos = limbSwingCos * 0.8F * limbSwingAmount;
    // body rotations
    float rearingTime = entity.getRearingAmount(partialTick);
    float rearingTimeLeft = 1.0F - rearingTime;
    this.horseBody.rotateAngleX = 0;
    this.horseBody.rotateAngleX = rearingTime * -0.7853982F + rearingTimeLeft * this.horseBody.rotateAngleX;
    // front leg rotations
    float rearingAmount = 0.2617994F * rearingTime;
    float rearingCos = MathHelper.cos(ticks * 0.6F + 3.1415927F);
    this.leftFrontLeg.rotationPointY = 2.0F * rearingTime + 14.0F * rearingTimeLeft;
    this.leftFrontLeg.rotationPointZ = -6.0F * rearingTime - 10.0F * rearingTimeLeft;
    this.rightFrontLeg.rotationPointY = this.leftFrontLeg.rotationPointY;
    this.rightFrontLeg.rotationPointZ = this.leftFrontLeg.rotationPointZ;
    // back leg rotations
    float frontAngleCos = (-1.0471976F + rearingCos) * rearingTime + limbSwingAmountCos * rearingTimeLeft;
    float frontAngleSin = (-1.0471976F - rearingCos) * rearingTime - limbSwingAmountCos * rearingTimeLeft;
    this.leftBackLeg.rotateAngleX = rearingAmount - limbSwingCos * 0.5F * limbSwingAmount * rearingTimeLeft;
    this.rightBackLeg.rotateAngleX = rearingAmount + limbSwingCos * 0.5F * limbSwingAmount * rearingTimeLeft;
    this.leftFrontLeg.rotateAngleX = frontAngleCos;
    this.rightFrontLeg.rotateAngleX = frontAngleSin;
    // tail rotations
    boolean tailSwinging = (entity.tailCounter != 0);
    this.tail.rotateAngleX = 0.5235988F + limbSwingAmount * 0.75F;
    this.tail.rotationPointY = -5.0F + limbSwingAmount;
    this.tail.rotationPointZ = 2.0F + limbSwingAmount * 2.0F;
    this.tail.rotateAngleY = tailSwinging ? MathHelper.cos(ticks * 0.7F) : 0.0F;
    // child rotation points
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
  
  public ModelRenderer getActualHead() { return bipedHead; }
}
