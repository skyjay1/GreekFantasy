package greekfantasy.client.render.model;

import greekfantasy.entity.AraEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;

public class AraModel<T extends AraEntity> extends BipedModel<T> {
  
  public AraModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);
    
    // eyes
    
    head.texOffs(15, 33).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 1.0F, 1.0F, modelSize);
    head.texOffs(15, 33).addBox(1.0F, -4.0F, -4.0F, 2.0F, 1.0F, 1.0F, modelSize);
    
    // chest
    
    body.texOffs(0, 33).addBox(-1.0F, 1.0F, -2.0F, 4.0F, 4.0F, 3.0F, modelSize);
    
    // arms
    
    leftArm = new ModelRenderer(this, 32, 48);
    leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    leftArm.setPos(5.0F, 2.5F, 0.0F);
    leftArm.mirror = true;
    
    rightArm = new ModelRenderer(this, 40, 16);
    rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    rightArm.setPos(-5.0F, 2.5F, 0.0F);

    // legs
    
    leftLeg = new ModelRenderer(this);
    leftLeg.setPos(2.0F, 12.0F, 2.0F);
    leftLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);

    rightLeg = new ModelRenderer(this);
    rightLeg.setPos(-2.0F, 12.0F, 2.0F);
    rightLeg.texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
  }
  
  public ModelRenderer getBodyModel() {
    return this.body;
  }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    if(entity.isHoldingWeapon()) {
      // this is referenced from IllagerModel#setRotationAngles (Vindicator)
      ModelHelper.swingWeaponDown(rightArm, leftArm, entity, this.attackTime, ageInTicks);
    }
  }
  
}
