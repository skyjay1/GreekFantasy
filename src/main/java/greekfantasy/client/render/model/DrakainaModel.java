package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

public class DrakainaModel<T extends MobEntity> extends BipedModel<T> {
  protected ModelRenderer upperTail;
  protected ModelRenderer midTail;
  protected ModelRenderer lowerTail;
  protected ModelRenderer lowerTail1;
  protected ModelRenderer lowerTail2;
  protected ModelRenderer lowerTail3;

  public DrakainaModel(final float modelSize) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;

    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, 0.0F, -2.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    
    bipedHeadwear = new ModelRenderer(this);
    bipedHeadwear.setRotationPoint(0.0F, 0.0F, -2.0F);
    bipedHeadwear.setTextureOffset(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.5F);

    bipedBody = new ModelRenderer(this);
    bipedBody.setRotationPoint(0.0F, 24.0F, 0.0F);
    bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 4.0F, modelSize);

    upperTail = new ModelRenderer(this);
    upperTail.setRotationPoint(0.0F, 12.0F, 0.0F);
    upperTail.rotateAngleX = -0.5236F;
    upperTail.setTextureOffset(0, 32).addBox(-3.999F, 0.0F, -4.0F, 8.0F, 10.0F, 4.0F, modelSize);

    midTail = new ModelRenderer(this);
    midTail.setRotationPoint(0.0F, 10.0F, -4.0F);
    upperTail.addChild(midTail);
    midTail.rotateAngleX = 1.0472F;
    midTail.setTextureOffset(0, 46).addBox(-4.001F, 0.0F, 0.0F, 8.0F, 6.0F, 4.0F, modelSize);

    lowerTail = new ModelRenderer(this);
    lowerTail.setRotationPoint(0.0F, 6.0F, 0.0F);
    midTail.addChild(lowerTail);
    lowerTail.rotateAngleX = 1.0472F;

    lowerTail1 = new ModelRenderer(this);
    lowerTail1.setRotationPoint(0.0F, 2.0F, 0.0F);
    lowerTail.addChild(lowerTail1);
    lowerTail1.setTextureOffset(25, 32).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 8.0F, 4.0F, modelSize);

    lowerTail2 = new ModelRenderer(this);
    lowerTail2.setRotationPoint(0.0F, 6.0F, 0.0F);
    lowerTail1.addChild(lowerTail2);
    lowerTail2.setTextureOffset(46, 32).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 8.0F, 3.0F, modelSize);

    lowerTail3 = new ModelRenderer(this);
    lowerTail3.setRotationPoint(0.0F, 7.0F, 0.0F);
    lowerTail2.addChild(lowerTail3);
    lowerTail3.setTextureOffset(46, 43).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 6.0F, 2.0F, modelSize);

    bipedLeftArm = new ModelRenderer(this, 32, 48);
    bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    bipedLeftArm.setRotationPoint(4.0F, 2.0F, -2.0F);
    bipedLeftArm.mirror = true;
    
    bipedRightArm = new ModelRenderer(this, 40, 16);
    bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    bipedRightArm.setRotationPoint(-4.0F, 2.0F, -2.0F);
    
    // disable biped legs
    bipedLeftLeg.showModel = false;
    bipedRightLeg.showModel = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.bipedLeftArm, this.bipedRightArm, this.upperTail, this.bipedHeadwear); }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    // set arm poses
    final ItemStack item = entity.getHeldItem(Hand.MAIN_HAND);
    if (canAnimateBow(entity, item)) {
       if (entity.getPrimaryHand() == HandSide.RIGHT) {
          this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       } else {
          this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       }
    } else {
      this.rightArmPose = this.leftArmPose = BipedModel.ArmPose.EMPTY;
    }
    // super method
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    bipedLeftArm.rotationPointZ = -2.0F;
    bipedRightArm.rotationPointZ = -2.0F;
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    // animate snake body
    final float limbSwingCos = (float) Math.cos(limbSwing);
    upperTail.rotateAngleY = limbSwingCos * 0.1F;
    lowerTail1.rotateAngleZ = limbSwingCos * 0.67F;
    lowerTail2.rotateAngleZ = limbSwingCos * -0.75F;
    lowerTail3.rotateAngleZ = limbSwingCos * 0.4F;
  }
  
  public boolean canAnimateBow(final T entity, final ItemStack heldItem) {
    return heldItem.getItem() instanceof net.minecraft.item.BowItem && entity.isAggressive();
  }
}
