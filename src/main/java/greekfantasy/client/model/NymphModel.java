package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;

public class NymphModel<T extends MobEntity> extends BipedModel<T> {

  private final ModelRenderer bipedChest;

  public NymphModel(final float modelSize) {
    super(modelSize);

    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, 0.0F, 1.5F);
    bipedHead.setTextureOffset(0, 0).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, modelSize);
    bipedHead.setTextureOffset(21, 0).addBox(-3.5F, 0.0F, 2.5F, 7.0F, 6.0F, 1.0F, modelSize);

    bipedBody = new ModelRenderer(this, 0, 16);
    bipedBody.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 12.0F, 3.0F, modelSize);

    bipedChest = new ModelRenderer(this, 30, 7);
    bipedChest.setRotationPoint(0.0F, 1.0F, 1.0F);
    bipedChest.rotateAngleX = -0.1745F;
    bipedChest.addBox(-2.99F, 0.0F, -1.0F, 6.0F, 4.0F, 1.0F, modelSize);

    bipedLeftArm = new ModelRenderer(this, 19, 16);
    bipedLeftArm.setRotationPoint(3.0F, 2.0F, 1.5F);
    bipedLeftArm.addBox(0.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);

    bipedRightArm = new ModelRenderer(this, 29, 16);
    bipedRightArm.setRotationPoint(-3.0F, 2.0F, 1.5F);
    bipedRightArm.addBox(-2.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);

    bipedLeftLeg = new ModelRenderer(this, 40, 16);
    bipedLeftLeg.setRotationPoint(1.5F, 12.0F, 1.5F);
    bipedLeftLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);

    bipedRightLeg = new ModelRenderer(this, 52, 16);
    bipedRightLeg.setRotationPoint(-1.5F, 12.0F, 1.5F);
    bipedRightLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.bipedChest, this.bipedLeftArm, this.bipedRightArm, this.bipedLeftLeg, this.bipedRightLeg); }

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
  }
}