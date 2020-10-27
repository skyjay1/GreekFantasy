package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.CyprianEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CyprianModel<T extends CyprianEntity> extends CentaurModel<T> {

  public CyprianModel(float modelSize) {
    super(modelSize);
    
    // nose
    this.bipedHead.setTextureOffset(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, modelSize);

    // horns
    this.bipedHead.addChild(MinotaurModel.makeBullHorns(this, modelSize, true));
    this.bipedHead.addChild(MinotaurModel.makeBullHorns(this, modelSize, false));
    
    // hide headwear
    this.bipedHeadwear.showModel = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return ImmutableList.of(); }  
}
