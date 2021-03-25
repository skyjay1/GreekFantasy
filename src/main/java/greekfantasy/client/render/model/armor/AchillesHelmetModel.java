package greekfantasy.client.render.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class AchillesHelmetModel extends BipedModel<LivingEntity> {
  
  public AchillesHelmetModel(final float modelSize) {
    super(modelSize);

    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize, false);
    bipedHead.setTextureOffset(36, 5).addBox(-2.0F, -16.0F, -3.0F, 1.0F, 13.0F, 13.0F, modelSize, false);
    
    this.bipedHead.showModel = true;
    // hide unused parts
    this.bipedHeadwear.showModel = false;
    this.bipedBody.showModel = false;
    this.bipedLeftArm.showModel = false;
    this.bipedRightArm.showModel = false;
    this.bipedLeftLeg.showModel = false;
    this.bipedRightLeg.showModel = false;
  }
  
  @Override
  public void setRotationAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
  }
}
