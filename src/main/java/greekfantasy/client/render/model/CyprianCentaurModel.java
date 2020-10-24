package greekfantasy.client.render.model;

import greekfantasy.entity.CyprianEntity;

public class CyprianCentaurModel<T extends CyprianEntity> extends CentaurModel<T> {

  public CyprianCentaurModel(float modelSize) {
    super(modelSize);
    
    // nose
    this.bipedHead.setTextureOffset(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, modelSize);

    // horns
    this.bipedHead.addChild(MinotaurModel.makeBullHorns(this, modelSize, true));
    this.bipedHead.addChild(MinotaurModel.makeBullHorns(this, modelSize, false));
    
    // hide headwear
    this.bipedHeadwear.showModel = false;
  }
}