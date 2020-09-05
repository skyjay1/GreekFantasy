package greekfantasy.client.model;

import greekfantasy.entity.ShadeEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;

public class ShadeModel<T extends ShadeEntity> extends BipedModel<T> {

  public ShadeModel(float modelSize) {
    super(modelSize, 0.0F, 64, 32);
    this.bipedLeftLeg.showModel = false;
    this.bipedRightLeg.showModel = false;
  }
  
}
