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
    this.texWidth = 64;
    this.texHeight = 64;
    
    // human parts
    this.body = new ModelRenderer(this, 16, 16);
    this.body.setPos(0.0F, 0.0F, -2.5F);
    this.body.addBox(-4.0F, -9.0F, -10.0F, 8.0F, 12.0F, 4.0F, modelSize);

    this.head = makeHeadModel(modelSize);
    
    this.hat = new ModelRenderer(this, 32, 0);
    this.hat.setPos(0.0F, -9.0F, -10.5F);
    this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.5F);

    this.leftArm = new ModelRenderer(this, 32, 48);
    this.leftArm.setPos(4.0F, -8.0F, -10.5F);
    this.leftArm.addBox(0.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    this.leftArm.mirror = true;

    this.rightArm = new ModelRenderer(this, 40, 16);
    this.rightArm.setPos(-4.0F, -8.0F, -10.5F);
    this.rightArm.addBox(-4.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    
    // hide biped legs
    this.leftLeg.visible = false;
    this.rightLeg.visible = false;
    
    // horse parts
    this.horseBody = new ModelRenderer(this, 0, 32);
    this.horseBody.addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, 0.05F);
    this.horseBody.setPos(0.0F, 0.0F, 5.0F);

    this.leftBackLeg = new ModelRenderer(this, 48, 21);
    this.leftBackLeg.addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSize);
    this.leftBackLeg.setPos(4.0F, 14.0F, 7.0F);
    this.leftBackLeg.mirror = true;
    
    this.rightBackLeg = new ModelRenderer(this, 48, 21);
    this.rightBackLeg.addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, modelSize);
    this.rightBackLeg.setPos(-4.0F, 14.0F, 7.0F);
    
    this.leftFrontLeg = new ModelRenderer(this, 48, 21);
    this.leftFrontLeg.addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSize);
    this.leftFrontLeg.setPos(4.0F, 6.0F, -12.0F);
    this.leftFrontLeg.mirror = true;
    
    this.rightFrontLeg = new ModelRenderer(this, 48, 21);
    this.rightFrontLeg.addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, modelSize);
    this.rightFrontLeg.setPos(-4.0F, 6.0F, -12.0F);
    
    this.tail = new ModelRenderer(this, 42, 36);
    this.tail.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, modelSize);
    this.tail.setPos(0.0F, -5.0F, 2.0F);
    this.tail.xRot = 0.5235988F;
    this.horseBody.addChild(this.tail);
    
    // quiver parts
    quiver = new ModelRenderer(this);
    quiver.setPos(0.0F, -4.0F, -6.5F);
    quiver.zRot = 0.6109F;
    quiver.texOffs(48, 0).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 10.0F, 4.0F, modelSize);

    arrows = new ModelRenderer(this);
    arrows.setPos(0.0F, 0.0F, 0.0F);
    quiver.addChild(arrows);

    arrow1 = new ModelRenderer(this);
    arrow1.setPos(0.0F, -7.0F, 0.0F);
    arrow1.texOffs(28, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, modelSize);
    arrows.addChild(arrow1);

    arrow2 = new ModelRenderer(this);
    arrow2.setPos(0.0F, -7.0F, 0.0F);
    arrow2.yRot = -1.5708F;
    arrow2.texOffs(28, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, modelSize);
    arrows.addChild(arrow2);
  }
  
  protected ModelRenderer makeHeadModel(final float modelSize) {
    final ModelRenderer head = new ModelRenderer(this, 0, 0);
    head.setPos(0.0F, -9.0F, -10.5F);
    head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    return head;
  }

  @Override
  protected Iterable<ModelRenderer> bodyParts() { return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.hat); }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
    // set arm poses
    final ItemStack item = entity.getItemInHand(Hand.MAIN_HAND);
    if (item.getItem() instanceof net.minecraft.item.BowItem && entity.isAggressive()) {
       if (entity.getMainArm() == HandSide.RIGHT) {
          this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       } else {
          this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       }
    } else {
      this.rightArmPose = this.leftArmPose = BipedModel.ArmPose.EMPTY;
    }
    // super method
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch);
    // horse rotation angles
    this.horseBody.y = 11.0F;
    float rearingTime = entity.getRearingAmount(ageInTicks - (float)Math.floor(ageInTicks));
    float rearingTimeLeft = 1.0F - rearingTime;
    this.body.setPos(0.0F, -7.0F * rearingTime, 5.0F * rearingTime - 2.5F * rearingTimeLeft);
    this.head.setPos(0.0F, -16.0F * rearingTime - 9.0F * rearingTimeLeft, -3.0F * rearingTime - 10.5F * rearingTimeLeft);
    this.hat.setPos(head.x, head.y, head.z);
    this.leftArm.setPos(4.0F, -15.0F * rearingTime - 8.0F * rearingTimeLeft, -3.0F * rearingTime - 10.5F * rearingTimeLeft);
    this.rightArm.setPos(-4.0F, leftArm.y, leftArm.z);
    this.quiver.setPos(0.0F, -11.0F * rearingTime - 4.0F * rearingTimeLeft, 1.5F * rearingTime - 6.5F * rearingTimeLeft);    
  }
  
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    float ticks = entity.tickCount + partialTick;   
    float swimmingOffset = entity.isInWater() ? 0.2F : 1.0F;
    float limbSwingCos = MathHelper.cos(swimmingOffset * limbSwing * 0.6662F + 3.1415927F);
    float limbSwingAmountCos = limbSwingCos * 0.8F * limbSwingAmount;
    // body rotations
    float rearingTime = entity.getRearingAmount(partialTick);
    float rearingTimeLeft = 1.0F - rearingTime;
    this.horseBody.xRot = 0;
    this.horseBody.xRot = rearingTime * -0.7853982F + rearingTimeLeft * this.horseBody.xRot;
    // front leg rotations
    float rearingAmount = 0.2617994F * rearingTime;
    float rearingCos = MathHelper.cos(ticks * 0.6F + 3.1415927F);
    this.leftFrontLeg.y = 2.0F * rearingTime + 14.0F * rearingTimeLeft;
    this.leftFrontLeg.z = -6.0F * rearingTime - 10.0F * rearingTimeLeft;
    this.rightFrontLeg.y = this.leftFrontLeg.y;
    this.rightFrontLeg.z = this.leftFrontLeg.z;
    // back leg rotations
    float frontAngleCos = (-1.0471976F + rearingCos) * rearingTime + limbSwingAmountCos * rearingTimeLeft;
    float frontAngleSin = (-1.0471976F - rearingCos) * rearingTime - limbSwingAmountCos * rearingTimeLeft;
    this.leftBackLeg.xRot = rearingAmount - limbSwingCos * 0.5F * limbSwingAmount * rearingTimeLeft;
    this.rightBackLeg.xRot = rearingAmount + limbSwingCos * 0.5F * limbSwingAmount * rearingTimeLeft;
    this.leftFrontLeg.xRot = frontAngleCos;
    this.rightFrontLeg.xRot = frontAngleSin;
    // tail rotations
    boolean tailSwinging = (entity.tailCounter != 0);
    this.tail.xRot = 0.5235988F + limbSwingAmount * 0.75F;
    this.tail.y = -5.0F + limbSwingAmount;
    this.tail.z = 2.0F + limbSwingAmount * 2.0F;
    this.tail.yRot = tailSwinging ? MathHelper.cos(ticks * 0.7F) : 0.0F;
    // child rotation points
    boolean child = entity.isBaby();
    this.horseBody.y = child ? 10.8F : 0.0F;
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
    this.body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
  
  public ModelRenderer getActualHead() { return head; }
}
