package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.GeryonEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public class GeryonModel<T extends GeryonEntity> extends GiganteModel<T> {
  
  protected ModelRenderer bipedLeftHead;
  protected ModelRenderer bipedRightHead;

  public GeryonModel(float modelSize) {
    super(modelSize);
    
    bipedLeftHead = new ModelRenderer(this);
    bipedLeftHead.setTextureOffset(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize - 1.0F);
    
    bipedRightHead = new ModelRenderer(this);
    bipedRightHead.setTextureOffset(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize - 1.0F);    
  }
  
  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return ImmutableList.of(this.bipedHead, this.bipedLeftHead, this.bipedRightHead); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    // head rotations
    bipedLeftHead.copyModelAngles(bipedHead);
    bipedLeftHead.setRotationPoint(-8.0F, -11F, 3.0F);
    bipedRightHead.copyModelAngles(bipedHead);
    bipedRightHead.setRotationPoint(8.0F, -11F, 3.0F);
    // smash animation
    final float smashTime = entity.getSmashPercent(partialTick);
    final float summonTime = entity.getSummonPercent(partialTick);
    if(summonTime > 0) {
      final ModelRenderer arm = this.getArmForSide(entity.getPrimaryHand().opposite());
      arm.rotateAngleX = -1.5708F * summonTime;
      arm.rotateAngleY = 0.680678F * summonTime * (entity.getPrimaryHand() == HandSide.RIGHT ? -1 : 1);
    }
    else if(smashTime > 0) {
      // when smashTime is >= downTrigger, arms will move downwards
      final float downTrigger = 0.9F;
      final float downMult = 12.5F;
      // maximum x and y angles
      final float smashAngleX = 2.02F;
      final float smashAngleY = 0.52F;
      bipedRightArm.rotateAngleX = -downMult * Math.min((1.0F - downTrigger) * smashTime, -0.5F * (smashTime - 0.95F)) * smashAngleX;
      bipedRightArm.rotateAngleY = -(Math.min(smashTime * 1.35F, 1.0F)) * smashAngleY;
      bipedLeftArm.rotateAngleX = bipedRightArm.rotateAngleX;
      bipedLeftArm.rotateAngleY = -bipedRightArm.rotateAngleY;
    }
  }  
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
  }
}
