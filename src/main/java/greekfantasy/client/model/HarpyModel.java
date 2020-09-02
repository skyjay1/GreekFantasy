package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.HarpyEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class HarpyModel<T extends HarpyEntity> extends BipedModel<T> {
  
  private final ModelRenderer chest;
  private final ModelRenderer tail;
  private final ModelRenderer leftWing1;
  private final ModelRenderer leftWing2;
  private final ModelRenderer leftWing3;
  private final ModelRenderer rightWing1;
  private final ModelRenderer rightWing2;
  private final ModelRenderer rightWing3;

  public HarpyModel(final float modelSize) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;

    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    bipedHead.setTextureOffset(24, 0).addBox(-4.0F, 0.0F, 4.0F, 8.0F, 5.0F, 0.0F, modelSize);
    
    bipedHeadwear = new ModelRenderer(this, 32, 0);
    bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
    bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.5F);

    bipedBody = new ModelRenderer(this);
    bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
    bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSize);

    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 1.0F, -2.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(19, 20).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);
    
    tail = new ModelRenderer(this);
    tail.setRotationPoint(0.0F, 12.0F, 2.0F);
    tail.rotateAngleX = 0.3491F;
    tail.setTextureOffset(48, 57).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 7.0F, 0.0F, 0.0F, false);
    
    this.bipedRightLeg = new ModelRenderer(this);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
    this.bipedRightLeg.setTextureOffset(0, 17).addBox(-2.1F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, 0.0F, false);
    
    this.bipedLeftLeg = new ModelRenderer(this);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
    this.bipedLeftLeg.setTextureOffset(0, 33).addBox(-0.9F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, 0.0F, false);

    this.bipedRightLeg.addChild(makeFoot(false));
    this.bipedLeftLeg.addChild(makeFoot(true));
   
    leftWing1 = new ModelRenderer(this);
    leftWing1.setRotationPoint(4.0F, 7.0F, 0.0F);
    setRotationAngle(leftWing1, 0.2618F, 0.0F, -0.3491F);
    leftWing1.setTextureOffset(0, 48).addBox(0.0F, -6.0F, 0.0F, 6.0F, 10.0F, 1.0F, 0.0F, true);

    leftWing2 = new ModelRenderer(this);
    leftWing2.setRotationPoint(6.0F, -6.0F, 1.0F);
    leftWing1.addChild(leftWing2);
    leftWing2.setTextureOffset(15, 48).addBox(0.0F, 0.0F, -1.05F, 8.0F, 14.0F, 1.0F, 0.0F, true);

    leftWing3 = new ModelRenderer(this);
    leftWing3.setRotationPoint(8.0F, 0.0F, 0.0F);
    leftWing2.addChild(leftWing3);
    leftWing3.setTextureOffset(34, 48).addBox(0.0F, 0.0F, -1.1F, 6.0F, 10.0F, 1.0F, 0.0F, true);

    rightWing1 = new ModelRenderer(this);
    rightWing1.setRotationPoint(-4.0F, 7.0F, 0.0F);
    setRotationAngle(rightWing1, 0.2618F, 0.0F, 0.3491F);
    rightWing1.setTextureOffset(0, 48).addBox(-6.0F, -6.0F, 0.0F, 6.0F, 10.0F, 1.0F, 0.0F, false);

    rightWing2 = new ModelRenderer(this);
    rightWing2.setRotationPoint(-6.0F, -6.0F, 1.0F);
    rightWing1.addChild(rightWing2);
    rightWing2.setTextureOffset(15, 48).addBox(-8.0F, 0.0F, -1.05F, 8.0F, 14.0F, 1.0F, 0.0F, false);

    rightWing3 = new ModelRenderer(this);
    rightWing3.setRotationPoint(-8.0F, 0.0F, 0.0F);
    rightWing2.addChild(rightWing3);
    rightWing3.setTextureOffset(34, 48).addBox(-6.0F, 0.0F, -1.1F, 6.0F, 10.0F, 1.0F, 0.0F, false);
    
    // hide biped arms
    this.bipedRightArm.showModel = false;
    this.bipedLeftArm.showModel = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.chest, this.bipedRightLeg, this.bipedLeftLeg, this.rightWing1, this.leftWing1, this.bipedHeadwear); }

  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    float ticks = entity.ticksExisted + partialTick;
    final float cosTicks = MathHelper.cos(ticks * 0.5F);
    final float sinTicks = MathHelper.cos(ticks * 0.5F + (float) Math.PI);
    final float downSwing = 0.5F;
    final float lerpFactor = 0.24F;
    final float idleSwing = 0.035F * MathHelper.cos(ticks * 0.08F);
    // set up angles for standing (not flying) pose and lerp from that to the cosine values
    boolean flying = entity.isFlying();
    
    final float angleLZ = flying ? -0.3491F : 0.0F;
    final float angleL1 = flying ? (cosTicks + downSwing) * 0.25F : 1.0472F;
    final float angleL2 = flying ? (cosTicks + downSwing) * 0.32F : 1.1345F;
    final float angleL3 = flying ? (cosTicks + downSwing) * 0.32F : 0.7854F;
    
    final float angleRZ = -angleLZ;
    final float angleR1 = flying ? (sinTicks - downSwing) * 0.25F : -1.0472F;
    final float angleR2 = flying ? (sinTicks - downSwing) * 0.32F : -1.2217F;
    final float angleR3 = flying ? (sinTicks - downSwing) * 0.32F : -1.3963F;
    
    // left wing
    this.leftWing1.rotateAngleX = 0.2618F + idleSwing;
    this.leftWing1.rotateAngleZ = MathHelper.lerp(lerpFactor, this.leftWing1.rotateAngleZ, angleLZ);
    this.leftWing1.rotateAngleY = MathHelper.lerp(lerpFactor, this.leftWing1.rotateAngleY, angleL1);
    this.leftWing2.rotateAngleY = MathHelper.lerp(lerpFactor, this.leftWing2.rotateAngleY, angleL2);
    this.leftWing3.rotateAngleY = MathHelper.lerp(lerpFactor, this.leftWing3.rotateAngleY, angleL3);
    // right wing
    this.rightWing1.rotateAngleX = this.leftWing1.rotateAngleX;
    this.rightWing1.rotateAngleZ = MathHelper.lerp(lerpFactor, this.rightWing1.rotateAngleZ, angleRZ);
    this.rightWing1.rotateAngleY = MathHelper.lerp(lerpFactor, this.rightWing1.rotateAngleY, angleR1);
    this.rightWing2.rotateAngleY = MathHelper.lerp(lerpFactor, this.rightWing2.rotateAngleY, angleR2);
    this.rightWing3.rotateAngleY = MathHelper.lerp(lerpFactor, this.rightWing3.rotateAngleY, angleR3);
  }
  
  private ModelRenderer makeFoot(final boolean isLeft) {
    final float offsetX = isLeft ? 1.0F : 0.0F;
    
    final ModelRenderer foot = new ModelRenderer(this);
    foot.setRotationPoint(0.0F, 0.0F, 0.0F);

    final ModelRenderer frontToe1 = new ModelRenderer(this);
    frontToe1.setRotationPoint(offsetX - 1.0F, 10.0F, -2.0F);
    foot.addChild(frontToe1);
    setRotationAngle(frontToe1, 0.3491F, 0.3491F, 0.0F);
    frontToe1.setTextureOffset(13, 38).addBox(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

    final ModelRenderer frontToe2 = new ModelRenderer(this);
    frontToe2.setRotationPoint(offsetX, 10.0F, -2.0F);
    foot.addChild(frontToe2);
    setRotationAngle(frontToe2, 0.3491F, -0.3491F, 0.0F);
    frontToe2.setTextureOffset(13, 38).addBox(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

    final ModelRenderer backToe = new ModelRenderer(this);
    backToe.setRotationPoint(offsetX, 10.0F, 1.0F);
    foot.addChild(backToe);
    backToe.rotateAngleX = -0.5236F;
    backToe.setTextureOffset(13, 33).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
    
    return foot;
  }
  
  public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
}