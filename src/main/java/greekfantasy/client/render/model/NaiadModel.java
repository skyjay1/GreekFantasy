package greekfantasy.client.render.model;

import greekfantasy.entity.NaiadEntity;
import net.minecraft.util.math.MathHelper;

public class NaiadModel<T extends NaiadEntity> extends NymphModel<T> {

  public NaiadModel(final float modelSize) {
    super(modelSize);
  }
  
  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);    
    // correct rotation points
    bipedHead.setRotationPoint(0.0F, 0.0F, 1.5F);
    bipedLeftArm.setRotationPoint(3.0F, 2.0F, 1.5F);
    bipedRightArm.setRotationPoint(-3.0F, 2.0F, 1.5F);
    bipedLeftLeg.setRotationPoint(1.5F, 12.0F, 1.5F);
    bipedRightLeg.setRotationPoint(-1.5F, 12.0F, 1.5F);
    // swimming animation
    final float swimming = entityIn.animateSwimmingPercent();
    if(swimming > 0.0F) {
      // animate legs
      final float ticks = ageInTicks + entityIn.getEntityId();
      final float legAngle = 0.28F;
      final float legSpeed = 0.18F;
      final float cosTicks = MathHelper.cos(ticks * legSpeed) * legAngle;
      final float sinTicks = MathHelper.cos(ticks * legSpeed + (float) Math.PI) * legAngle;
      bipedRightLeg.rotateAngleX = cosTicks * swimming;
      bipedLeftLeg.rotateAngleX = sinTicks * swimming;
      // animate arms
      if(!entityIn.isSwingInProgress) {
        final float cosArms = MathHelper.cos(ticks * legSpeed * 0.5F) * 0.5F + 0.5F;
        final float minX = -0.09F;
        final float maxX = 0.18F;
        final float minZ = 0.52F;
        final float maxZ = 1.08F;
        bipedRightArm.rotateAngleX = (-minX - cosArms * (maxX - minX)) * swimming;
        bipedRightArm.rotateAngleZ = (minZ + cosArms * (maxZ - minZ)) * swimming;
        bipedLeftArm.rotateAngleX = (-minX - cosArms * (maxX - minX)) * swimming;
        bipedLeftArm.rotateAngleZ = (-minZ - cosArms * (maxZ - minZ)) * swimming;
      }
    }
  }
}