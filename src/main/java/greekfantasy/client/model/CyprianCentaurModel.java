package greekfantasy.client.model;

import greekfantasy.entity.CentaurEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CyprianCentaurModel<T extends CentaurEntity> extends CentaurModel<T> {

  public CyprianCentaurModel(float modelSize) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;
        
    // nose
    this.bipedHead.setTextureOffset(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, 0.0F, false);

    // horns
    this.bipedHead.addChild(MinotaurModel.makeBullHorns(this, modelSize, true));
    this.bipedHead.addChild(MinotaurModel.makeBullHorns(this, modelSize, false));
  }
}
