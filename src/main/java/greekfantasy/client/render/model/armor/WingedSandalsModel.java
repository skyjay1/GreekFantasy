package greekfantasy.client.render.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class WingedSandalsModel extends BipedModel<LivingEntity> {
  
  private final ModelRenderer leftWing;
  private final ModelRenderer rightWing;

  public WingedSandalsModel(final float modelSize) {
    super(modelSize);

    leftWing = new ModelRenderer(this);
    leftWing.setRotationPoint(0.0F, 12.0F, 2.0F + modelSize);
    leftWing.setTextureOffset(16, 18).addBox(0.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, true);
    this.bipedLeftLeg.addChild(leftWing);

    rightWing = new ModelRenderer(this);
    rightWing.setRotationPoint(0.0F, 12.0F, 2.0F + modelSize);
    rightWing.setTextureOffset(16, 18).addBox(-5.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F, 0.0F, false);
    this.bipedRightLeg.addChild(rightWing);
    
    // hide unused parts
    this.bipedHead.showModel = false;
    this.bipedHeadwear.showModel = false;
    this.bipedBody.showModel = false;
    this.bipedLeftArm.showModel = false;
    this.bipedRightArm.showModel = false;
  }
  
  @Override
  public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    // animate wings
    final float wingSpeed = entity.isOnGround() ? 0.62F : 1.14F;
    final float wingSpan = 0.75854F;
    final float wingAngle = wingSpan - MathHelper.cos((ageInTicks + entity.getEntityId()) * wingSpeed) * wingSpan * 0.65F;
    this.rightWing.rotateAngleY = wingAngle;
    this.leftWing.rotateAngleY = -wingAngle;
  }
}
