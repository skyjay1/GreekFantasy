package greekfantasy.client.render.model;

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
    super(modelSize, 0.0F, 64, 64);

    this.hat.visible = showHeadwear;

    // arms
    
    this.leftArm = new ModelRenderer(this, 32, 48);
    this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.leftArm.setPos(5.0F, 2.5F, 0.0F);
    this.leftArm.mirror = true;
    
    this.rightArm = new ModelRenderer(this, 40, 16);
    this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.rightArm.setPos(-5.0F, 2.5F, 0.0F);

    // legs
    
    this.leftLeg = new ModelRenderer(this);
    this.leftLeg.setPos(2.0F, 12.0F, 2.0F);
    this.leftLeg.mirror = true;

    leftLegUpper = new ModelRenderer(this, 16, 36);
    leftLegUpper.setPos(0.0F, 0.0F, 0.0F);
    leftLegUpper.xRot = -0.2618F;
    leftLegUpper.addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    leftLegUpper.mirror = true;
    this.leftLeg.addChild(leftLegUpper);

    leftLegLower = new ModelRenderer(this, 16, 46);
    leftLegLower.setPos(0.0F, 6.0F, -2.0F);
    leftLegLower.addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    leftLegLower.mirror = true;
    leftLegUpper.addChild(leftLegLower);

    leftHoof = new ModelRenderer(this, 16, 56);
    leftHoof.setPos(0.0F, 6.0F, 4.0F);
    leftHoof.addBox(-1.9F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    leftHoof.mirror = true;
    leftLegLower.addChild(leftHoof);

    this.rightLeg = new ModelRenderer(this);
    this.rightLeg.setPos(-2.0F, 12.0F, 2.0F);
    
    rightLegUpper = new ModelRenderer(this, 0, 16);
    rightLegUpper.setPos(0.0F, 0.0F, 0.0F);
    rightLegUpper.xRot = -0.2618F;
    rightLegUpper.addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    this.rightLeg.addChild(rightLegUpper);

    rightLegLower = new ModelRenderer(this, 0, 26);
    rightLegLower.setPos(0.0F, 6.0F, -2.0F);
    rightLegLower.addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, modelSize);
    rightLegUpper.addChild(rightLegLower);

    rightHoof = new ModelRenderer(this, 0, 36);
    rightHoof.setPos(0.0F, 6.0F, 4.0F);
    rightHoof.addBox(-2.1F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, modelSize);
    rightLegLower.addChild(rightHoof);
    
    // tail
    
    tail = new ModelRenderer(this, 0, 51);
    tail2 = new ModelRenderer(this);
    tail.visible = hasTail;
    tail2.visible = hasTail;
    if(hasTail) { 
      tail.setPos(0.0F, 11.0F, 2.0F);
      tail.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, modelSize);
      this.body.addChild(tail);
  
      tail2.setPos(0.0F, 5.0F, -1.0F);
      tail2.texOffs(4, 51).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, modelSize);
      tail2.texOffs(0, 58).addBox(-1.0F, 2.5F, -0.5F, 2.0F, 4.0F, 2.0F, modelSize);
      tail.addChild(tail2);
    }
  }
  
  @Override
  public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
    final float ticks = entityIn.tickCount + partialTick;
    float limbSwingSin = MathHelper.cos(limbSwing + (float)Math.PI) * limbSwingAmount;
    float limbSwingCos = MathHelper.cos(limbSwing) * limbSwingAmount;
    float rightLegSwing = 0.38F * limbSwingSin;
    float leftLegSwing = 0.38F * limbSwingCos;
    // legs
    rightLegLower.xRot = 0.7854F + rightLegSwing;
    rightHoof.xRot = -0.5236F - rightLegSwing;
    leftLegLower.xRot = 0.7854F + leftLegSwing;
    leftHoof.xRot = -0.5236F - leftLegSwing;
    
    // tail
    if(tail.visible) {
      float idleSwing = 0.1F * MathHelper.cos(ticks * 0.08F);
      float tailSwing = 0.42F * limbSwingCos;
      tail.xRot = 0.6854F + tailSwing;
      tail2.xRot = 0.3491F + tailSwing * 0.6F;
      tail.zRot = idleSwing;
      tail2.zRot = idleSwing * 0.85F;
    }
  }
}
