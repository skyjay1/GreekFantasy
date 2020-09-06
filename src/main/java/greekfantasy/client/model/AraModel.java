package greekfantasy.client.model;

import greekfantasy.entity.AraEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class AraModel<T extends AraEntity> extends BipedModel<T> {
  
  public AraModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);
    
    this.bipedBody.setTextureOffset(0, 33).addBox(-1.0F, 1.0F, -2.0F, 4.0F, 4.0F, 3.0F, modelSize);
    
    // arms
    
    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
    this.bipedLeftArm.mirror = true;
    
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);

    // legs
    
    bipedLeftLeg = new ModelRenderer(this);
    bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 2.0F);
    bipedLeftLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);

    bipedRightLeg = new ModelRenderer(this);
    bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 2.0F);
    bipedRightLeg.setTextureOffset(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
  }
  
  public ModelRenderer getBodyModel() {
    return this.bipedBody;
  }
  
}
