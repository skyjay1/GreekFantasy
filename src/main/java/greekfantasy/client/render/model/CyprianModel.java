package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.CyprianEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CyprianModel<T extends CyprianEntity> extends CentaurModel<T> {

  public CyprianModel(float modelSize) {
    super(modelSize);
    
    // nose
    this.head.texOffs(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, modelSize);

    // horns
    this.head.addChild(MinotaurModel.makeBullHorns(this, modelSize, true));
    this.head.addChild(MinotaurModel.makeBullHorns(this, modelSize, false));
    
    // hide headwear
    this.hat.visible = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> headParts() { return ImmutableList.of(); }  
}
