package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.HydraHeadEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HydraHeadModel<T extends HydraHeadEntity> extends EntityModel<T> {
  
  private final ModelRenderer neck;
  private final ModelRenderer neck1;
  private final ModelRenderer neck2;
  private final ModelRenderer neck3;
  private final ModelRenderer head;
  private final ModelRenderer mouth;
  private final ModelRenderer leftHorn;
  private final ModelRenderer leftHorn2;
  private final ModelRenderer leftHorn3;

  public HydraHeadModel() {
    super();
    textureWidth = 64;
    textureHeight = 32;

    neck = new ModelRenderer(this);
    neck.setRotationPoint(0.0F, 22.0F, 4.0F);

    neck1 = new ModelRenderer(this);
    neck1.setRotationPoint(0.0F, 2.0F, -3.0F);
    neck.addChild(neck1);
    neck1.rotateAngleX = 0.7854F;
    neck1.setTextureOffset(0, 18).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);
    neck1.setTextureOffset(25, 24).addBox(-1.0F, -6.0F, 6.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

    neck2 = new ModelRenderer(this);
    neck2.setRotationPoint(0.0F, -7.0F, 0.0F);
    neck1.addChild(neck2);
    neck2.rotateAngleX = -0.3491F;
    neck2.setTextureOffset(0, 18).addBox(-3.01F, -7.0F, 0.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);
    neck2.setTextureOffset(25, 24).addBox(-1.0F, -7.0F, 6.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

    neck3 = new ModelRenderer(this);
    neck3.setRotationPoint(0.0F, -7.0F, 0.0F);
    neck2.addChild(neck3);
    neck3.rotateAngleX = -0.3491F;
    neck3.setTextureOffset(0, 18).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);
    neck3.setTextureOffset(25, 24).addBox(-1.0F, -7.0F, 6.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

    head = new ModelRenderer(this);
    head.setRotationPoint(0.0F, -6.0F, 3.0F);
    neck3.addChild(head);
    head.rotateAngleX = -0.0873F;
    head.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    head.setTextureOffset(33, 0).addBox(-4.0F, -4.0F, -10.0F, 8.0F, 3.0F, 6.0F, 0.0F, false);
    head.setTextureOffset(34, 22).addBox(-1.0F, -8.0F, 4.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

    mouth = new ModelRenderer(this);
    mouth.setRotationPoint(0.0F, -1.0F, -4.0F);
    head.addChild(mouth);
    mouth.rotateAngleX = 0.2618F;
    mouth.setTextureOffset(33, 9).addBox(-4.0F, 0.0F, -6.0F, 8.0F, 1.0F, 6.0F, 0.0F, false);

    leftHorn = new ModelRenderer(this);
    leftHorn.setRotationPoint(4.0F, -6.0F, 1.0F);
    head.addChild(leftHorn);
    leftHorn.rotateAngleZ = 0.7854F;
    leftHorn.setTextureOffset(56, 27).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

    leftHorn2 = new ModelRenderer(this);
    leftHorn2.setRotationPoint(0.0F, -3.0F, 0.0F);
    leftHorn.addChild(leftHorn2);
    leftHorn2.rotateAngleX = 0.5236F;
    leftHorn2.rotateAngleY = 0.0873F;
    leftHorn2.setTextureOffset(56, 22).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

    leftHorn3 = new ModelRenderer(this);
    leftHorn3.setRotationPoint(0.0F, -3.0F, 0.0F);
    leftHorn2.addChild(leftHorn3);
    leftHorn3.rotateAngleX = 1.0472F;
    leftHorn3.rotateAngleY = 0.0873F;
    leftHorn3.setTextureOffset(56, 17).addBox(-2.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
  }
  

  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    
  }

  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    neck.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }

}
