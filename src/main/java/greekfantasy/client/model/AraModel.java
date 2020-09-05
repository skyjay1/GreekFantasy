package greekfantasy.client.model;

import greekfantasy.entity.AraEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class AraModel<T extends AraEntity> extends BipedModel<T> {

  public AraModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);
    
    this.bipedBody.setTextureOffset(0, 33).addBox(-1.0F, 1.0F, -2.0F, 4.0F, 4.0F, 3.0F, modelSize);
    
    // arms
    
    bipedLeftArm = new ModelRenderer(this);
    bipedLeftArm.setRotationPoint(4.0F, 2.0F, 2.0F);
    bipedLeftArm.setTextureOffset(40, 16).addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);

    bipedRightArm = new ModelRenderer(this);
    bipedRightArm.setRotationPoint(-4.0F, 2.0F, 2.0F);
    bipedRightArm.setTextureOffset(32, 48).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);

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
