package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.GoldenRamEntity;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class GoldenRamModel<T extends GoldenRamEntity> extends SheepModel<T> {
  
  protected final ModelRenderer horns;
  
  public GoldenRamModel() {
    super();
    // left horn
    ModelRenderer leftHorn = new ModelRenderer(this);
    leftHorn.setRotationPoint(3.0F, -2.0F, -4.0F);
    leftHorn.rotateAngleX = 0.0873F;
    leftHorn.rotateAngleY = 0.1745F;
    leftHorn.rotateAngleZ = 0.4363F;
    leftHorn.setTextureOffset(56, 0).addBox(-2.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    
    ModelRenderer leftHorn2 = new ModelRenderer(this);
    leftHorn2.setRotationPoint(-2.0F, -4.0F, -1.0F);
    leftHorn2.rotateAngleX = -0.7854F;
    leftHorn2.rotateAngleY = 0.2618F;
    leftHorn2.setTextureOffset(56, 0).addBox(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    leftHorn.addChild(leftHorn2);

    ModelRenderer leftHorn3 = new ModelRenderer(this);
    leftHorn3.setRotationPoint(0.0F, -4.0F, 0.0F);
    leftHorn3.rotateAngleX = -1.2217F;
    leftHorn3.rotateAngleY = 0.2618F;
    leftHorn3.setTextureOffset(56, 0).addBox(0.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    leftHorn2.addChild(leftHorn3);

    ModelRenderer leftHorn4 = new ModelRenderer(this);
    leftHorn4.setRotationPoint(1.0F, -4.0F, 0.0F);
    leftHorn4.rotateAngleX = -1.2217F;
    leftHorn4.rotateAngleY = 0.2618F;
    leftHorn4.setTextureOffset(58, 6).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
    leftHorn3.addChild(leftHorn4);

    ModelRenderer leftHorn5 = new ModelRenderer(this);
    leftHorn5.setRotationPoint(0.0F, -4.0F, 0.0F);
    leftHorn5.rotateAngleX = -1.0472F;
    leftHorn5.rotateAngleY = 0.2618F;
    leftHorn5.setTextureOffset(58, 6).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
    leftHorn4.addChild(leftHorn5);

    // right horn
    ModelRenderer rightHorn = new ModelRenderer(this);
    rightHorn.setRotationPoint(-3.0F, -2.0F, -4.0F);
    rightHorn.rotateAngleX = 0.0873F;
    rightHorn.rotateAngleY = -0.1745F;
    rightHorn.rotateAngleZ = -0.4363F;
    rightHorn.setTextureOffset(56, 0).addBox(0.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

    ModelRenderer rightHorn2 = new ModelRenderer(this);
    rightHorn2.setRotationPoint(2.0F, -4.0F, -1.0F);
    rightHorn2.rotateAngleX = -0.7854F;
    rightHorn2.rotateAngleY = -0.2618F;
    rightHorn2.setTextureOffset(56, 0).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    rightHorn.addChild(rightHorn2);

    ModelRenderer rightHorn3 = new ModelRenderer(this);
    rightHorn3.setRotationPoint(0.0F, -4.0F, 0.0F);
    rightHorn3.rotateAngleX = -1.2217F;
    rightHorn3.rotateAngleY = -0.2618F;
    rightHorn3.setTextureOffset(56, 0).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    rightHorn2.addChild(rightHorn3);

    ModelRenderer rightHorn4 = new ModelRenderer(this);
    rightHorn4.setRotationPoint(-1.0F, -4.0F, 0.0F);
    rightHorn4.rotateAngleX = -1.2217F;
    rightHorn4.rotateAngleY = -0.2618F;
    rightHorn4.setTextureOffset(58, 6).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
    rightHorn3.addChild(rightHorn4);

    ModelRenderer rightHorn5 = new ModelRenderer(this);
    rightHorn5.setRotationPoint(0.0F, -4.0F, 0.0F);
    rightHorn5.rotateAngleX = -1.0472F;
    rightHorn5.rotateAngleY = -0.2618F;
    rightHorn5.setTextureOffset(58, 6).addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, false);
    rightHorn4.addChild(rightHorn5);

    // combine horns
    horns = new ModelRenderer(this);
    horns.addChild(leftHorn);
    horns.addChild(rightHorn);
  }
  
  public void renderHorns(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final int packedLightIn, final int packedOverlayIn) {
    horns.copyModelAngles(headModel);
    horns.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
  }
  
}
