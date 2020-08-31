package greekfantasy.client.model;

import greekfantasy.entity.MinotaurEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MinotaurModel<T extends MinotaurEntity> extends HoofedBipedModel<T> {

  public MinotaurModel(float modelSize) {
    super(modelSize, true, false);
    textureWidth = 64;
    textureHeight = 64;
        
    // nose
    this.bipedHead.setTextureOffset(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, 0.0F, false);

    // horns
    this.bipedHead.addChild(makeHornModel(modelSize, true));
    this.bipedHead.addChild(makeHornModel(modelSize, false));
  }
  
  private ModelRenderer makeHornModel(final float modelSize, final boolean isLeft) {
    final int textureX = isLeft ? 54 : 47;
    final float horn1X = isLeft ? 3.0F : -3.0F;
    final float horn1Z = isLeft ? 0.0F : -2.0F;
    //final float horn2X = isLeft ? -7.0F : -1.0F;
    //final float horn3X = isLeft ? -7.0F : 0F;
    final float angleY = isLeft ? -1 : 1;

    final ModelRenderer horn3 = new ModelRenderer(this);
    horn3.setRotationPoint(0.0F, -3.0F, 0.0F);
    horn3.rotateAngleX = -0.5236F;
    horn3.setTextureOffset(textureX, 59).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, 0.0F, false);
    horn3.mirror = isLeft;
    
    final ModelRenderer horn2 = new ModelRenderer(this);
    horn2.setRotationPoint(-1.0F, -4.0F, -2.0F);
    horn2.rotateAngleX = -0.5236F;
    horn2.setTextureOffset(textureX, 54).addBox(-0.51F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, 0.0F, false);
    horn2.addChild(horn3);
    horn2.mirror = isLeft;
    
    final ModelRenderer horn1 = new ModelRenderer(this);
    horn1.setRotationPoint(horn1X, -7.0F, horn1Z);
    horn1.rotateAngleX = 1.3963F;
    horn1.rotateAngleY = 1.0472F * angleY;
    horn1.setTextureOffset(textureX, 48).addBox(-1.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
    horn1.addChild(horn2);
    horn1.mirror = isLeft;

    return horn1;
  }
}
