package greekfantasy.client.model;

import greekfantasy.entity.NymphEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class NymphModel<T extends NymphEntity> extends BipedModel<T> {

  private final ModelRenderer chest;

  public NymphModel(float modelSize) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;

    this.bipedHead = new ModelRenderer(this);
    this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F); // 0, 0, 1.5F
    // head
    this.bipedHead.setTextureOffset(0, 0).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 7.0F, 6.0F, modelSize);
    // hair
    this.bipedHead.setTextureOffset(24, 7).addBox(-3.0F, 0.0F, 3.0F, 6.0F, 6.0F, 0.0F, modelSize);
    // default headwear (remove)
    this.bipedHeadwear.showModel = false;
    
    this.bipedBody = new ModelRenderer(this);
    this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
    this.bipedBody.setTextureOffset(0, 13).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 12.0F, 4.0F, modelSize);    

    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, -20.0F, 2.0F);
    chest.rotateAngleX = -0.1745F;
    chest.setTextureOffset(3, 17).addBox(-2.99F, 22.0F, 0.0F, 6.0F, 4.0F, 1.0F, 0.0F, false);
    this.bipedBody.addChild(chest);

    this.bipedLeftArm = new ModelRenderer(this);
    this.bipedLeftArm.setRotationPoint(1.0F, 2.0F, 1.5F);
    this.bipedLeftArm.setTextureOffset(20, 13).addBox(-2.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);
    this.bipedLeftArm.mirror = true;

    this.bipedRightArm = new ModelRenderer(this);
    this.bipedRightArm.setRotationPoint(0.0F, 2.0F, 1.5F);
    this.bipedRightArm.setTextureOffset(30, 13).addBox(0.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);

    this.bipedLeftLeg = new ModelRenderer(this);
    this.bipedLeftLeg.setRotationPoint(1.5F, 12.0F, 1.5F);
    this.bipedLeftLeg.setTextureOffset(0, 49).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);
    this.bipedLeftLeg.mirror = true;

    this.bipedRightLeg = new ModelRenderer(this);
    this.bipedRightLeg.setRotationPoint(-1.5F, 12.0F, 1.5F);
    this.bipedRightLeg.setTextureOffset(12, 49).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);
  }

  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    this.rightArmPose = BipedModel.ArmPose.EMPTY;
    this.leftArmPose = BipedModel.ArmPose.EMPTY;
    ItemStack itemstack = entityIn.getHeldItem(Hand.MAIN_HAND);
    if (itemstack.getItem() instanceof net.minecraft.item.BowItem && entityIn.isAggressive()) {
      if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
        this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
      } else {
        this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
      }
    }

    super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
  }

  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    ItemStack itemstack = entityIn.getHeldItemMainhand();
    if (entityIn.isAggressive() && (itemstack.isEmpty() || !(itemstack.getItem() instanceof net.minecraft.item.BowItem))) {
      float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
      float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
      this.bipedRightArm.rotateAngleX = (-(float) Math.PI / 2F);
      this.bipedLeftArm.rotateAngleX = (-(float) Math.PI / 2F);
      this.bipedRightArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
      this.bipedLeftArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
      ModelHelper.func_239101_a_(this.bipedRightArm, this.bipedLeftArm, ageInTicks);
    }

  }
}