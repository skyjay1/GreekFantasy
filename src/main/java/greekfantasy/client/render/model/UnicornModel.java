package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.UnicornEntity;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class UnicornModel<T extends UnicornEntity> extends HorseModel<T> {
  
  private final ModelRenderer horn;
  
  public UnicornModel(float modelSize) {
    super(modelSize);
    this.horn = new ModelRenderer(this);
    this.horn.setRotationPoint(0.0F, -4.0F, -11.0F);
    horn.setTextureOffset(0, 0).addBox(-0.5F, -21.0F, 0.5F, 1.0F, 5.0F, 1.0F, modelSize); // z -3.5
    horn.setTextureOffset(0, 6).addBox(-1.0F, -16.0F, 0.0F, 2.0F, 5.0F, 2.0F, modelSize); // z -4.0
    
  }
  
  public void renderHorn(T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    this.horn.copyModelAngles(this.head);
    this.horn.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
