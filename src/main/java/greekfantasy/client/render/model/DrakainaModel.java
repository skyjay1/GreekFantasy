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
    texWidth = 64;
    texHeight = 64;

    head = new ModelRenderer(this);
    head.setPos(0.0F, 0.0F, -2.0F);
    head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    
    hat = new ModelRenderer(this);
    hat.setPos(0.0F, 0.0F, -2.0F);
    hat.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.5F);

    body = new ModelRenderer(this);
    body.setPos(0.0F, 24.0F, 0.0F);
    body.texOffs(16, 16).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 4.0F, modelSize);

    upperTail = new ModelRenderer(this);
    upperTail.setPos(0.0F, 12.0F, 0.0F);
    upperTail.xRot = -0.5236F;
    upperTail.texOffs(0, 32).addBox(-3.999F, 0.0F, -4.0F, 8.0F, 10.0F, 4.0F, modelSize);

    midTail = new ModelRenderer(this);
    midTail.setPos(0.0F, 10.0F, -4.0F);
    upperTail.addChild(midTail);
    midTail.xRot = 1.0472F;
    midTail.texOffs(0, 46).addBox(-4.001F, 0.0F, 0.0F, 8.0F, 6.0F, 4.0F, modelSize);

    lowerTail = new ModelRenderer(this);
    lowerTail.setPos(0.0F, 6.0F, 0.0F);
    midTail.addChild(lowerTail);
    lowerTail.xRot = 1.0472F;

    lowerTail1 = new ModelRenderer(this);
    lowerTail1.setPos(0.0F, 2.0F, 0.0F);
    lowerTail.addChild(lowerTail1);
    lowerTail1.texOffs(25, 32).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 8.0F, 4.0F, modelSize);

    lowerTail2 = new ModelRenderer(this);
    lowerTail2.setPos(0.0F, 6.0F, 0.0F);
    lowerTail1.addChild(lowerTail2);
    lowerTail2.texOffs(46, 32).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 8.0F, 3.0F, modelSize);

    lowerTail3 = new ModelRenderer(this);
    lowerTail3.setPos(0.0F, 7.0F, 0.0F);
    lowerTail2.addChild(lowerTail3);
    lowerTail3.texOffs(46, 43).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 6.0F, 2.0F, modelSize);

    leftArm = new ModelRenderer(this, 32, 48);
    leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    leftArm.setPos(4.0F, 2.0F, -2.0F);
    leftArm.mirror = true;
    
    rightArm = new ModelRenderer(this, 40, 16);
    rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    rightArm.setPos(-4.0F, 2.0F, -2.0F);
    
    // disable biped legs
    leftLeg.visible = false;
    rightLeg.visible = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> bodyParts() { return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.upperTail, this.hat); }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    // set arm poses
    final ItemStack item = entity.getItemInHand(Hand.MAIN_HAND);
    if (canAnimateBow(entity, item)) {
       if (entity.getMainArm() == HandSide.RIGHT) {
          this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       } else {
          this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       }
    } else {
      this.rightArmPose = this.leftArmPose = BipedModel.ArmPose.EMPTY;
    }
    // super method
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    leftArm.z = -2.0F;
    rightArm.z = -2.0F;
  }
  
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    // animate snake body
    final float limbSwingCos = (float) Math.cos(limbSwing);
    upperTail.yRot = limbSwingCos * 0.1F;
    lowerTail1.zRot = limbSwingCos * 0.67F;
    lowerTail2.zRot = limbSwingCos * -0.75F;
    lowerTail3.zRot = limbSwingCos * 0.4F;
  }
  
  public boolean canAnimateBow(final T entity, final ItemStack heldItem) {
    return heldItem.getItem() instanceof net.minecraft.item.BowItem && entity.isAggressive();
  }
}
