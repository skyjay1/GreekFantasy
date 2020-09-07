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
    super(modelSize, 0.0F, 64, 64);
 
    bipedHead.setTextureOffset(24, 0).addBox(-4.0F, 0.0F, 4.0F, 8.0F, 5.0F, 0.0F, modelSize);

    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 1.0F, -2.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(19, 20).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);
    
    tail = new ModelRenderer(this);
    tail.setRotationPoint(0.0F, 12.0F, 2.0F);
    tail.rotateAngleX = 0.3491F;
    tail.setTextureOffset(48, 57).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 7.0F, 0.0F, modelSize);
    
    this.bipedLeftLeg = new ModelRenderer(this);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
    this.bipedLeftLeg.setTextureOffset(0, 33).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, modelSize);
    this.bipedLeftLeg.mirror = true;

    this.bipedRightLeg = new ModelRenderer(this);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
    this.bipedRightLeg.setTextureOffset(0, 17).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, modelSize);
    
    this.bipedLeftLeg.addChild(makeFoot(modelSize, true));
    this.bipedRightLeg.addChild(makeFoot(modelSize, false));
   
    leftWing1 = new ModelRenderer(this);
    leftWing1.setRotationPoint(4.0F, 1.0F, 0.0F);
    leftWing1.setTextureOffset(0, 48).addBox(0.0F, 0.0F, 0.0F, 6.0F, 10.0F, 1.0F, 0.0F, true);
    leftWing1.mirror = true;

    leftWing2 = new ModelRenderer(this);
    leftWing2.setRotationPoint(6.0F, 0.0F, 1.0F);
    leftWing1.addChild(leftWing2);
    leftWing2.setTextureOffset(15, 48).addBox(0.0F, 0.0F, -1.05F, 8.0F, 14.0F, 1.0F, 0.0F, true);
    leftWing2.mirror = true;

    leftWing3 = new ModelRenderer(this);
    leftWing3.setRotationPoint(8.0F, 0.0F, 0.0F);
    leftWing2.addChild(leftWing3);
    leftWing3.setTextureOffset(34, 48).addBox(0.0F, 0.0F, -1.1F, 6.0F, 10.0F, 1.0F, 0.0F, true);
    leftWing3.mirror = true;

    rightWing1 = new ModelRenderer(this);
    rightWing1.setRotationPoint(-4.0F, 1.0F, 0.0F);
    rightWing1.setTextureOffset(0, 48).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 10.0F, 1.0F, modelSize);

    rightWing2 = new ModelRenderer(this);
    rightWing2.setRotationPoint(-6.0F, 0.0F, 1.0F);
    rightWing1.addChild(rightWing2);
    rightWing2.setTextureOffset(15, 48).addBox(-8.0F, 0.0F, -1.05F, 8.0F, 14.0F, 1.0F, modelSize);

    rightWing3 = new ModelRenderer(this);
    rightWing3.setRotationPoint(-8.0F, 0.0F, 0.0F);
    rightWing2.addChild(rightWing3);
    rightWing3.setTextureOffset(34, 48).addBox(-6.0F, 0.0F, -1.1F, 6.0F, 10.0F, 1.0F, modelSize);
    
    // hide biped arms
    this.bipedRightArm.showModel = false;
    this.bipedLeftArm.showModel = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.chest, this.tail, this.bipedRightLeg, this.bipedLeftLeg, this.rightWing1, this.leftWing1, this.bipedHeadwear); }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

    final float flyingTime = entity.flyingTime;
    final float flyingTimeLeft = 1.0F - flyingTime;
    // animate legs (only while flying)
    this.bipedLeftLeg.rotateAngleX *= (flyingTimeLeft * 0.6F);
    this.bipedLeftLeg.rotateAngleX += (-0.35F * flyingTime);  
    this.bipedRightLeg.rotateAngleX *= (flyingTimeLeft * 0.6F);
    this.bipedRightLeg.rotateAngleX += (-0.35F * flyingTime); 
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    float ticks = entity.getEntityId() * 2 + entity.ticksExisted + partialTick;
    final float flyingTime = entity.flyingTime;
    final float flyingTimeLeft = 1.0F - flyingTime;
    final float downSwing = 0.5F;
    final float wingAngle = 0.4F;
    final float wingSpeed = 0.6F;
    final float cosTicks = flyingTime > 0.0F ? MathHelper.cos(ticks * wingSpeed) : 0.0F;
    final float sinTicks = flyingTime > 0.0F ? MathHelper.cos(ticks * wingSpeed + (float) Math.PI) : 0.0F;
    final float idleSwing = 0.035F * MathHelper.cos(ticks * 0.08F);
 
    // animate wings (combines flying and landing animations)
    this.leftWing1.rotateAngleX = 1.0472F - 0.7854F * flyingTime;
    this.leftWing1.rotateAngleY = 0.0F + ((cosTicks + downSwing) * wingAngle * 0.75F) * flyingTime;
    this.leftWing1.rotateAngleZ = 0.9908F - 0.8908F * flyingTime + idleSwing;
    
    this.leftWing2.rotateAngleY = 0.5236F * flyingTimeLeft + ((cosTicks + downSwing) * wingAngle) * flyingTime;
    this.leftWing3.rotateAngleY = 0.1745F * flyingTimeLeft + ((cosTicks + downSwing) * wingAngle) * flyingTime;
    
    this.rightWing1.rotateAngleX = this.leftWing1.rotateAngleX;
    this.rightWing1.rotateAngleY = 0.0F + ((sinTicks - downSwing) * 0.32F) * flyingTime;
    this.rightWing1.rotateAngleZ = -0.9908F + 0.8908F * flyingTime - idleSwing;
    
    this.rightWing2.rotateAngleY = -0.5236F * flyingTimeLeft + ((sinTicks - downSwing) * wingAngle) * flyingTime;
    this.rightWing3.rotateAngleY = -0.1745F * flyingTimeLeft + ((sinTicks - downSwing) * wingAngle) * flyingTime;
  }
  
  private ModelRenderer makeFoot(final float modelSize, final boolean isLeft) {
    final float offsetX = isLeft ? 1.0F : 0.0F;
    final float rotationToe = 0.3491F;
    
    final ModelRenderer foot = new ModelRenderer(this);
    foot.setRotationPoint(0.0F, 0.0F, 0.0F);

    final ModelRenderer frontToe1 = new ModelRenderer(this);
    frontToe1.setRotationPoint(offsetX - 1.0F, 10.0F, -2.0F);
    frontToe1.rotateAngleX = rotationToe;
    frontToe1.rotateAngleY = rotationToe;
    frontToe1.setTextureOffset(13, 38).addBox(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, modelSize);
    foot.addChild(frontToe1);

    final ModelRenderer frontToe2 = new ModelRenderer(this);
    frontToe2.setRotationPoint(offsetX, 10.0F, -2.0F);
    frontToe2.rotateAngleX = rotationToe;
    frontToe2.rotateAngleY = -rotationToe;
    frontToe2.setTextureOffset(13, 38).addBox(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, modelSize);
    foot.addChild(frontToe2);

    final ModelRenderer backToe = new ModelRenderer(this);
    backToe.setRotationPoint(offsetX, 10.0F, 1.0F);
    foot.addChild(backToe);
    backToe.rotateAngleX = -0.5236F;
    backToe.setTextureOffset(13, 33).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, modelSize);
    
    return foot;
  }
}