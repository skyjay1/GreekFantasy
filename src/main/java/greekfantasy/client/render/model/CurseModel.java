package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.projectile.ProjectileEntity;

public class CurseModel<T extends ProjectileEntity> extends EntityModel<T> {
  
  private final ModelRenderer curse;
  private final ModelRenderer inside;

  public CurseModel() {
    super();
    texWidth = 32;
    texHeight = 32;

    curse = new ModelRenderer(this);
    curse.setPos(0.0F, 21.0F, 0.0F);
//    curse.rotateAngleZ = 0.7854F;
    curse.texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    inside = new ModelRenderer(this);
    inside.setPos(0.0F, 0.0F, 0.0F);
    curse.addChild(inside);
    inside.texOffs(0, 12).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    curse.zRot = (entity.getId() + ageInTicks) * 0.3425F;
    inside.zRot = -curse.zRot;
  }

  @Override
  public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    curse.render(matrixStackIn, bufferIn, 15728880, packedOverlayIn);
  }

}
