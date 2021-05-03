package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.HydraEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HydraModel<T extends HydraEntity> extends EntityModel<T> {
  
  private final ModelRenderer curse;
  private final ModelRenderer inside;

  public HydraModel() {
    super();
    textureWidth = 32;
    textureHeight = 32;

    curse = new ModelRenderer(this);
    curse.setRotationPoint(0.0F, 21.0F, 0.0F);
    curse.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    inside = new ModelRenderer(this);
    inside.setRotationPoint(0.0F, 0.0F, 0.0F);
    curse.addChild(inside);
    inside.setTextureOffset(0, 12).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
  }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    curse.rotateAngleZ = (entity.getEntityId() + ageInTicks) * 0.3425F;
    inside.rotateAngleZ = -curse.rotateAngleZ;
    curse.rotateAngleX = headPitch * 0.017453292F;
    curse.rotateAngleY = netHeadYaw * 0.017453292F;
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    curse.render(matrixStackIn, bufferIn, 15728880, packedOverlayIn);
  }

}
