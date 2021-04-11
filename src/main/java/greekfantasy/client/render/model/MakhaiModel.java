package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import greekfantasy.entity.MakhaiEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MakhaiModel<T extends MakhaiEntity> extends BipedModel<T> {
  
  public final ModelRenderer bipedHead2;
  public final ModelRenderer bipedRightArm2;
  public final ModelRenderer bipedRightArm3;
  public final ModelRenderer bipedLeftArm2;
  public final ModelRenderer bipedLeftArm3;

  public MakhaiModel(float modelSize) {
    super(modelSize);
    this.textureWidth = 64;
    this.textureHeight = 32;
    
    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(-4.0F, 0.0F, 0.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    
    bipedHeadwear = new ModelRenderer(this);
    bipedHeadwear.setRotationPoint(-4.0F, 0.0F, 0.0F);
    bipedHeadwear.setTextureOffset(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, false);

    bipedHead2 = new ModelRenderer(this);
    bipedHead2.setRotationPoint(4.0F, 0.0F, 0.0F);
    bipedHead2.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

    bipedRightArm = new ModelRenderer(this);
    bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
    bipedRightArm.setTextureOffset(40, 16).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

    bipedRightArm2 = new ModelRenderer(this);
    bipedRightArm2.setRotationPoint(-4.0F, 2.0F, 0.0F);
    bipedRightArm2.setTextureOffset(40, 16).addBox(-3.0F, 0.0F, -3.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

    bipedRightArm3 = new ModelRenderer(this);
    bipedRightArm3.setRotationPoint(-5.0F, 2.0F, 0.0F);
    bipedRightArm3.setTextureOffset(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

    bipedLeftArm = new ModelRenderer(this);
    bipedLeftArm.setRotationPoint(5.0F, 3.0F, 0.0F);
    bipedLeftArm.setTextureOffset(40, 16).addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

    bipedLeftArm2 = new ModelRenderer(this);
    bipedLeftArm2.setRotationPoint(4.0F, 2.0F, 0.0F);
    bipedLeftArm2.setTextureOffset(40, 16).addBox(0.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

    bipedLeftArm3 = new ModelRenderer(this);
    bipedLeftArm3.setRotationPoint(4.0F, 2.0F, 0.0F);
    bipedLeftArm3.setTextureOffset(40, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);

    bipedBody = new ModelRenderer(this);
    bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
    bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

    bipedRightLeg = new ModelRenderer(this);
    bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
    bipedRightLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

    bipedLeftLeg = new ModelRenderer(this);
    bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
    bipedLeftLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);
  }
  
  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return Iterables.concat(super.getHeadParts(), ImmutableList.of(this.bipedHead2)); }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.bipedLeftArm2, this.bipedLeftArm3, this.bipedRightArm2, this.bipedRightArm3)); }
  
  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    // update head angles
    bipedHead2.rotateAngleX = bipedHead.rotateAngleX;
    bipedHead2.rotateAngleY = bipedHead.rotateAngleY * -1.0F + 3.14F;
    bipedHead2.rotateAngleZ = bipedHead.rotateAngleZ + 0.1309F;
    // update arm angles
    // right arms
    float x = bipedRightArm.rotateAngleX * 0.85F;
    float y = bipedRightArm.rotateAngleY * 0.85F;
    float z = bipedRightArm.rotateAngleZ * 0.85F;
    // right arm 2
    bipedRightArm2.rotateAngleX = -x + 1.1345F;
    bipedRightArm2.rotateAngleY = -y + -1.5272F;
    bipedRightArm2.rotateAngleZ = -z + -0.48F;
    // right arm 3
    bipedRightArm3.rotateAngleX = x + 1.0908F;
    bipedRightArm3.rotateAngleY = y + 0.5672F;
    bipedRightArm3.rotateAngleZ = z + 0.9163F;
    // right arm 1
    bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
    bipedRightArm.rotateAngleX += -1.1345F;
    bipedRightArm.rotateAngleY += 0.5236F;
    bipedRightArm.rotateAngleZ += -0.1745F;
    // left arms
    x = bipedLeftArm.rotateAngleX * 0.85F;
    y = bipedLeftArm.rotateAngleY * 0.85F;
    z = bipedLeftArm.rotateAngleZ * 0.85F;
    // left arm 2
    bipedLeftArm2.rotateAngleX = -x + 0.7854F;
    bipedLeftArm2.rotateAngleY = -y + 0.0873F;
    bipedLeftArm2.rotateAngleZ = -z + -0.0873F;
    // left arm 3
    bipedLeftArm3.rotateAngleX = x + 1.0908F;
    bipedLeftArm3.rotateAngleY = y + 1.3963F;
    bipedLeftArm3.rotateAngleZ = z + 0.2182F;
    // left arm 1
    bipedLeftArm.setRotationPoint(5.0F, 3.0F, 0.0F);
    bipedLeftArm.rotateAngleX += -0.6109F ;
    bipedLeftArm.rotateAngleY += -0.3491F;
    bipedLeftArm.rotateAngleZ += -0.1745F;
  }
  
  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
  }

  

}
