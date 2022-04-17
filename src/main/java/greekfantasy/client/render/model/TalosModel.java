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
    
    head = new ModelRenderer(this);
    head.setPos(0.0F, -1.0F, 0.0F);
    head.texOffs(0, 0).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 6.0F, 8.0F, modelSize);
    
    hat.visible = false;

    // Right arm    
    rightArm = new ModelRenderer(this);
    rightArm.setPos(-9.0F, -10.0F, 0.0F);
    rightArm.texOffs(66, 0).addBox(-5.5F, -5.0F, -3.0F, 8.0F, 4.0F, 6.0F, modelSize);
    
    bipedRightArmUpper = new ModelRenderer(this);
    bipedRightArmUpper.texOffs(66, 11).addBox(-4.0F, -1.0F, -2.5F, 4.0F, 6.0F, 5.0F, modelSize);
    rightArm.addChild(bipedRightArmUpper);
        
    bipedRightArmMiddle = new ModelRenderer(this);
    bipedRightArmMiddle.texOffs(66, 23).addBox(-5.0F, 5.0F, -3.5F, 5.0F, 6.0F, 7.0F, modelSize);
    bipedRightArmUpper.addChild(bipedRightArmMiddle);
    
    bipedRightArmLower = new ModelRenderer(this);
    bipedRightArmLower.texOffs(64, 37).addBox(-6.0F, 11.0F, -4.5F, 7.0F, 8.0F, 9.0F, modelSize);
    bipedRightArmMiddle.addChild(bipedRightArmLower);

    // Left arm
    leftArm = new ModelRenderer(this);
    leftArm.setPos(9.0F, -10.0F, 0.0F);
//    bipedLeftArm.mirror = true;
    leftArm.texOffs(66, 0).addBox(-2.5F, -5.0F, -3.0F, 8.0F, 4.0F, 6.0F, modelSize);
    
    bipedLeftArmUpper = new ModelRenderer(this);
    bipedLeftArmUpper.texOffs(66, 11).addBox(0.0F, -1.0F, -2.5F, 4.0F, 6.0F, 5.0F, modelSize);
//    bipedLeftArmUpper.mirror = true;
    leftArm.addChild(bipedLeftArmUpper);
    
    bipedLeftArmMiddle = new ModelRenderer(this);
    bipedLeftArmMiddle.texOffs(66, 23).addBox(0.0F, 5.0F, -3.5F, 5.0F, 6.0F, 7.0F, modelSize);
//    bipedLeftArmMiddle.mirror = true;
    bipedLeftArmUpper.addChild(bipedLeftArmMiddle);
    
    bipedLeftArmLower = new ModelRenderer(this);
    bipedLeftArmLower.texOffs(96, 37).addBox(-1.0F, 11.0F, -4.5F, 7.0F, 8.0F, 9.0F, modelSize);
//    bipedLeftArmLower.mirror = true;
    bipedLeftArmMiddle.addChild(bipedLeftArmLower);
    
    // Body
    body = new ModelRenderer(this);
    body.setPos(0.0F, 5.0F, 0.0F);
    body.texOffs(0, 15).addBox(-9.0F, -16.0F, -5.0F, 18.0F, 8.0F, 11.0F, modelSize);
    
    bipedBodyMiddle = new ModelRenderer(this);
    bipedBodyMiddle.texOffs(0, 35).addBox(-7.0F, -8.0F, -4.5F, 14.0F, 5.0F, 10.0F, modelSize);
    body.addChild(bipedBodyMiddle);
    
    bipedBodyLower = new ModelRenderer(this);
    bipedBodyLower.texOffs(0, 51).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 4.0F, 9.0F, modelSize);
    bipedBodyLower.texOffs(39, 53).addBox(-3.01F, 1.0F, -2.5F, 6.0F, 5.0F, 6.0F, modelSize);
    bipedBodyMiddle.addChild(bipedBodyLower);

    // Right leg
    rightLeg = new ModelRenderer(this);
    rightLeg.setPos(-3.0F, 9.0F, 0.5F);
    rightLeg.texOffs(103, 0).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    
    bipedRightLegMiddle = new ModelRenderer(this);
    bipedRightLegMiddle.texOffs(103, 11).addBox(-4.5F, 4.0F, -2.5F, 5.0F, 6.0F, 5.0F, modelSize);
    rightLeg.addChild(bipedRightLegMiddle);
    
    bipedRightLegLower = new ModelRenderer(this);
    bipedRightLegLower.texOffs(103, 23).addBox(-5.0F, 10.0F, -3.0F, 6.0F, 5.0F, 6.0F, modelSize);
    bipedRightLegMiddle.addChild(bipedRightLegLower);

    // Left leg
    leftLeg = new ModelRenderer(this);
    leftLeg.setPos(3.0F, 9.0F, 0.5F);
    leftLeg.mirror = true;
    leftLeg.texOffs(103, 0).addBox(0.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, modelSize);
    
    bipedLeftLegMiddle = new ModelRenderer(this);
    bipedLeftLegMiddle.texOffs(103, 11).addBox(-0.5F, 4.0F, -2.5F, 5.0F, 6.0F, 5.0F, modelSize);
    bipedLeftLegMiddle.mirror = true;
    leftLeg.addChild(bipedLeftLegMiddle);
    
    bipedLeftLegLower = new ModelRenderer(this);
    bipedLeftLegLower.texOffs(103, 23).addBox(-1.0F, 10.0F, -3.0F, 6.0F, 5.0F, 6.0F, modelSize);
    bipedLeftLegLower.mirror = true;
    bipedLeftLegMiddle.addChild(bipedLeftLegLower);
  }
  
  public ModelRenderer getBodyModel() {
    return this.body;
  }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
    head.setPos(0, -1.0F + 16.01F * bodyPercent + 12F * legPercent, 0);
    head.xRot = 0;
    head.yRot *= spawnPercentRaw;
    head.zRot *= spawnPercentRaw;
    // body
    body.setPos(0, 5 + 10 * bodyPercent + 12 * legPercent, 0);
    bipedBodyMiddle.setPos(0, -5 * bodyPercent, 0);
    bipedBodyLower.setPos(0, -4 * bodyPercent, 0);
    // arms
    rightArm.setPos(-9.0F, -10.0F + 14 * bodyPercent + 12 * legPercent, 0.0F);
    bipedRightArmUpper.setPos(0, -3.99F * armPercent, 0);
    bipedRightArmMiddle.setPos(0, -5.99F * armPercent, 0);
    bipedRightArmLower.setPos(0, -5.99F * armPercent, 0);
    leftArm.setPos(9.0F, -10.0F + 14 * bodyPercent + 12 * legPercent, 0.0F);
    bipedLeftArmUpper.setPos(0, -3.99F * armPercent, 0);
    bipedLeftArmMiddle.setPos(0, -5.99F * armPercent, 0);
    bipedLeftArmLower.setPos(0, -5.99F * armPercent, 0);
    // legs
    leftLeg.setPos(3.0F, 9.0F + 12 * legPercent, 0.5F);
    bipedLeftLegMiddle.setPos(0, -6 * legPercent, 0);
    bipedLeftLegLower.setPos(0, -6 * legPercent, 0);
    rightLeg.setPos(-3.0F, 9.0F + 12 * legPercent, 0.5F);
    bipedRightLegMiddle.setPos(0, -6 * legPercent, 0);
    bipedRightLegLower.setPos(0, -6 * legPercent, 0);
    // shooting animation
    if(entity.isShooting()) {
      rightArm.xRot = -0.98F;
    }
  }
  
}
