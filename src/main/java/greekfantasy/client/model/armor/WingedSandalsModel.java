package greekfantasy.client.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class WingedSandalsModel  extends BipedModel<LivingEntity> {
  
  private final ModelRenderer leftWing1;
  private final ModelRenderer leftWing2;
  private final ModelRenderer rightWing1;
  private final ModelRenderer rightWing2;

  public WingedSandalsModel(final float modelSize) {
    super(modelSize);

    leftWing1 = makeWing(this, modelSize, true);
    leftWing2 = makeWing(this, modelSize, false);
    this.bipedLeftLeg.addChild(leftWing1);
    this.bipedLeftLeg.addChild(leftWing2);

    rightWing1 = makeWing(this, modelSize, true);
    rightWing2 = makeWing(this, modelSize, false);
    this.bipedRightLeg.addChild(rightWing1);
    this.bipedRightLeg.addChild(rightWing2);
    
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
    final float wingSpeed = entity.isOnGround() ? 0.64F : 1.14F;
    final float wingSpan = 0.7854F;
    final float wingAngle = wingSpan + MathHelper.cos((ageInTicks + entity.getEntityId()) * wingSpeed) * wingSpan * 0.5F;
    this.leftWing1.rotateAngleY = this.rightWing1.rotateAngleY = -wingAngle;
    this.leftWing2.rotateAngleY = this.rightWing2.rotateAngleY = wingAngle;    
  }

  public static ModelRenderer makeWing(final EntityModel<?> model, final float modelSize, final boolean isLeft) {
    final ModelRenderer wing = new ModelRenderer(model, 16, 25);
    if(isLeft) {
      wing.setRotationPoint(0.5F, 11.0F, 2.0F);
      wing.addBox(0.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F, modelSize);
      wing.mirror = true;
    } else {
      wing.setRotationPoint(-0.5F, 11.0F, 2.0F);
      wing.addBox(-5.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F, modelSize);
    }
    return wing;
  }
}
