package greekfantasy.client.render.model;

import greekfantasy.entity.TalosEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class TalosModel<T extends TalosEntity> extends BipedModel<T> {
  
  // body
  private final ModelRenderer bipedBodyMiddle;
  private final ModelRenderer bipedBodyLower;
  // arms
  private final ModelRenderer bipedRightArmUpper;
  private final ModelRenderer bipedRightArmMiddle;
  private final ModelRenderer bipedRightArmLower;
  private final ModelRenderer bipedLeftArmUpper;
  private final ModelRenderer bipedLeftArmMiddle;
  private final ModelRenderer bipedLeftArmLower;
  // legs
  private final ModelRenderer bipedRightLegMiddle;
  private final ModelRenderer bipedRightLegLower;
  private final ModelRenderer bipedLeftLegMiddle;
  private final ModelRenderer bipedLeftLegLower;
  
  public TalosModel(float modelSize) {
    super(modelSize, 0.0F, 128, 64);
    
    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, -1.0F, 0.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 6.0F, 8.0F, modelSize);
    
    bipedHeadwear.showModel = false;

    // Right arm    
    bipedRightArm = new ModelRenderer(this);
    bipedRightArm.setRotationPoint(-9.0F, -10.0F, 0.0F);
    bipedRightArm.setTextureOffset(66, 0).addBox(-5.5F, -5.0F, -3.0F, 8.0F, 4.0F, 6.0F, modelSize);
    
    bipedRightArmUpper = new ModelRenderer(this);
    bipedRightArmUpper.setTextureOffset(66, 11).addBox(-4.0F, -1.0F, -2.5F, 4.0F, 6.0F, 5.0F, modelSize);
    bipedRightArm.addChild(bipedRightArmUpper);
        
    bipedRightArmMiddle = new ModelRenderer(this);
    bipedRightArmMiddle.setTextureOffset(66, 23).addBox(-5.0F, 5.0F, -3.5F, 5.0F, 6.0F, 7.0F, modelSize);
    bipedRightArmUpper.addChild(bipedRightArmMiddle);
    
    bipedRightArmLower = new ModelRenderer(this);
    bipedRightArmLower.setTextureOffset(64, 37).addBox(-6.0F, 11.0F, -4.5F, 7.0F, 8.0F, 9.0F, modelSize);
    bipedRightArmMiddle.addChild(bipedRightArmLower);

    // Left arm
    bipedLeftArm = new ModelRenderer(this);
    bipedLeftArm.setRotationPoint(9.0F, -10.0F, 0.0F);
//    bipedLeftArm.mirror = true;
    bipedLeftArm.setTextureOffset(66, 0).addBox(-2.5F, -5.0F, -3.0F, 8.0F, 4.0F, 6.0F, modelSize);
    
    bipedLeftArmUpper = new ModelRenderer(this);
    bipedLeftArmUpper.setTextureOffset(66, 11).addBox(0.0F, -1.0F, -2.5F, 4.0F, 6.0F, 5.0F, modelSize);
//    bipedLeftArmUpper.mirror = true;
    bipedLeftArm.addChild(bipedLeftArmUpper);
    
    bipedLeftArmMiddle = new ModelRenderer(this);
    bipedLeftArmMiddle.setTextureOffset(66, 23).addBox(0.0F, 5.0F, -3.5F, 5.0F, 6.0F, 7.0F, modelSize);
//    bipedLeftArmMiddle.mirror = true;
    bipedLeftArmUpper.addChild(bipedLeftArmMiddle);
    
    bipedLeftArmLower = new ModelRenderer(this);
    bipedLeftArmLower.setTextureOffset(96, 37).addBox(-1.0F, 11.0F, -4.5F, 7.0F, 8.0F, 9.0F, modelSize);
//    bipedLeftArmLower.mirror = true;
    bipedLeftArmMiddle.addChild(bipedLeftArmLower);
    
    // Body
    bipedBody = new ModelRenderer(this);
    bipedBody.setRotationPoint(0.0F, 5.0F, 0.0F);
    bipedBody.setTextureOffset(0, 15).addBox(-9.0F, -16.0F, -5.0F, 18.0F, 8.0F, 11.0F, modelSize);
    
    bipedBodyMiddle = new ModelRenderer(this);
    bipedBodyMiddle.setTextureOffset(0, 35).addBox(-7.0F, -8.0F, -4.5F, 14.0F, 5.0F, 10.0F, modelSize);
    bipedBody.addChild(bipedBodyMiddle);
    
    bipedBodyLower = new ModelRenderer(this);
    bipedBodyLower.setTextureOffset(0, 51).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 4.0F, 9.0F, modelSize);
    bipedBodyLower.setTextureOffset(39, 53).addBox(-3.01F, 1.0F, -2.5F, 6.0F, 5.0F, 6.0F, modelSize);
    bipedBodyMiddle.addChild(bipedBodyLower);

    // Right leg
    bipedRightLeg = new ModelRenderer(this);
    bipedRightLeg.setRotationPoint(-3.0F, 9.0F, 0.5F);
    bipedRightLeg.setTextureOffset(103, 0).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    
    bipedRightLegMiddle = new ModelRenderer(this);
    bipedRightLegMiddle.setTextureOffset(103, 11).addBox(-4.5F, 4.0F, -2.5F, 5.0F, 6.0F, 5.0F, modelSize);
    bipedRightLeg.addChild(bipedRightLegMiddle);
    
    bipedRightLegLower = new ModelRenderer(this);
    bipedRightLegLower.setTextureOffset(103, 23).addBox(-5.0F, 10.0F, -3.0F, 6.0F, 5.0F, 6.0F, modelSize);
    bipedRightLegMiddle.addChild(bipedRightLegLower);

    // Left leg
    bipedLeftLeg = new ModelRenderer(this);
    bipedLeftLeg.setRotationPoint(3.0F, 9.0F, 0.5F);
    bipedLeftLeg.mirror = true;
    bipedLeftLeg.setTextureOffset(103, 0).addBox(0.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    
    bipedLeftLegMiddle = new ModelRenderer(this);
    bipedLeftLegMiddle.setTextureOffset(103, 11).addBox(-0.5F, 4.0F, -2.5F, 5.0F, 6.0F, 5.0F, modelSize);
    bipedLeftLegMiddle.mirror = true;
    bipedLeftLeg.addChild(bipedLeftLegMiddle);
    
    bipedLeftLegLower = new ModelRenderer(this);
    bipedLeftLegLower.setTextureOffset(103, 23).addBox(-1.0F, 10.0F, -3.0F, 6.0F, 5.0F, 6.0F, modelSize);
    bipedLeftLegLower.mirror = true;
    bipedLeftLegMiddle.addChild(bipedLeftLegLower);
  }
  
  public ModelRenderer getBodyModel() {
    return this.bipedBody;
  }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    // these represent the percent of spawn time spent on just that body part
    final float armPercentAmount = 0.32F;
    final float bodyPercentAmount = 0.32F;
    final float legPercentAmount = 0.32F;
    // these convert from the [0.0, 1.0) spawn percent to a comparable portion for each body part
    final float spawnPercentRaw = entity.getSpawnPercent(ageInTicks % 1.0F);
    final float spawnPercent = 1.04F - spawnPercentRaw;
    final float armPercent = MathHelper.clamp(spawnPercent, 0.0F, armPercentAmount) * (1.0F / armPercentAmount);
    final float bodyPercent = MathHelper.clamp(spawnPercent - armPercentAmount, 0.0F, bodyPercentAmount) * (1.0F / bodyPercentAmount);
    final float legPercent = MathHelper.clamp(spawnPercent - armPercentAmount - bodyPercentAmount, 0.0F, legPercentAmount) * (1.0F / legPercentAmount);
    // rotation points and spawn animation
    // head
    bipedHead.setRotationPoint(0, -1.0F + 16.01F * bodyPercent + 12F * legPercent, 0);
    bipedHead.rotateAngleX = 0;
    bipedHead.rotateAngleY *= spawnPercentRaw;
    bipedHead.rotateAngleZ *= spawnPercentRaw;
    // body
    bipedBody.setRotationPoint(0, 5 + 10 * bodyPercent + 12 * legPercent, 0);
    bipedBodyMiddle.setRotationPoint(0, -5 * bodyPercent, 0);
    bipedBodyLower.setRotationPoint(0, -4 * bodyPercent, 0);
    // arms
    bipedRightArm.setRotationPoint(-9.0F, -10.0F + 14 * bodyPercent + 12 * legPercent, 0.0F);
    bipedRightArmUpper.setRotationPoint(0, -3.99F * armPercent, 0);
    bipedRightArmMiddle.setRotationPoint(0, -5.99F * armPercent, 0);
    bipedRightArmLower.setRotationPoint(0, -5.99F * armPercent, 0);
    bipedLeftArm.setRotationPoint(9.0F, -10.0F + 14 * bodyPercent + 12 * legPercent, 0.0F);
    bipedLeftArmUpper.setRotationPoint(0, -3.99F * armPercent, 0);
    bipedLeftArmMiddle.setRotationPoint(0, -5.99F * armPercent, 0);
    bipedLeftArmLower.setRotationPoint(0, -5.99F * armPercent, 0);
    // legs
    bipedLeftLeg.setRotationPoint(3.0F, 9.0F + 12 * legPercent, 0.5F);
    bipedLeftLegMiddle.setRotationPoint(0, -6 * legPercent, 0);
    bipedLeftLegLower.setRotationPoint(0, -6 * legPercent, 0);
    bipedRightLeg.setRotationPoint(-3.0F, 9.0F + 12 * legPercent, 0.5F);
    bipedRightLegMiddle.setRotationPoint(0, -6 * legPercent, 0);
    bipedRightLegLower.setRotationPoint(0, -6 * legPercent, 0);
    // shooting animation
    if(entity.isShooting()) {
      bipedRightArm.rotateAngleX = -0.98F;
    }
  }
  
}
