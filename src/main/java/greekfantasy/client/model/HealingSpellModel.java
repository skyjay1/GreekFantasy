package greekfantasy.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.HealingSpellEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HealingSpellModel<T extends HealingSpellEntity> extends EntityModel<T> {
  
  private final ModelRenderer cross;
  
  public HealingSpellModel(final float modelSize) {
    textureWidth = 16;
    textureHeight = 16;

    cross = new ModelRenderer(this);
    cross.setRotationPoint(0.0F, 0.0F, 0.0F);
    cross.setTextureOffset(0, 0).addBox(0.0F, -8.0F, -4.0F, 0.0F, 8.0F, 8.0F, modelSize);
    cross.setTextureOffset(0, 8).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 0.0F, modelSize);
  }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    // do nothing
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    cross.render(matrixStackIn, bufferIn, 15728880, packedOverlayIn);
  }

}
