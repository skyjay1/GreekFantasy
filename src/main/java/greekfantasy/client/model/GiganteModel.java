package greekfantasy.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.CreatureEntity;

public class GiganteModel<T extends CreatureEntity> extends BipedModel<T> {

  public GiganteModel(float modelSize) {
    super(modelSize, 0.0F, 128, 64);
    
    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, -12.0F, 3.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize);
    bipedHead.setTextureOffset(30, 0).addBox(-5.0F, 4.0F, -5.5F, 10.0F, 7.0F, 0.0F, modelSize);
    
    bipedHeadwear = new ModelRenderer(this);
    bipedHeadwear.setRotationPoint(0.0F, -12.0F, 3.0F);
    bipedHeadwear.setTextureOffset(40, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize + 0.5F);

    bipedBody = new ModelRenderer(this);
    bipedBody.setRotationPoint(0.0F, 24.0F, 0.0F);
    bipedBody.setTextureOffset(0, 20).addBox(-6.0F, -32.0F, 0.0F, 12.0F, 16.0F, 6.0F, modelSize);

    bipedLeftArm = new ModelRenderer(this);
    bipedLeftArm.setRotationPoint(6.0F, -6.0F, 3.0F);
    bipedLeftArm.setTextureOffset(64, 20).addBox(0.0F, -2.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);

    bipedRightArm = new ModelRenderer(this);
    bipedRightArm.setRotationPoint(-6.0F, -6.0F, 3.0F);
    bipedRightArm.setTextureOffset(40, 20).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);
    
    bipedLeftLeg = new ModelRenderer(this);
    bipedLeftLeg.setRotationPoint(3.0F, 8.0F, 3.0F);
    bipedLeftLeg.setTextureOffset(64, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);
    bipedLeftLeg.mirror = true;

    bipedRightLeg = new ModelRenderer(this);
    bipedRightLeg.setRotationPoint(-3.0F, 8.0F, 3.0F);
    bipedRightLeg.setTextureOffset(40, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, modelSize);
  }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    bipedHead.setRotationPoint(0.0F, -12.0F, 3.0F);
    bipedHeadwear.setRotationPoint(0.0F, -12.0F, 3.0F);
    bipedBody.setRotationPoint(0.0F, 24.0F, 0.0F);
    bipedLeftArm.setRotationPoint(6.0F, -6.0F, 3.0F);
    bipedRightArm.setRotationPoint(-6.0F, -6.0F, 3.0F);
    bipedLeftLeg.setRotationPoint(3.0F, 8.0F, 3.0F);
    bipedRightLeg.setRotationPoint(-3.0F, 8.0F, 3.0F);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    // this corrects a model offset mistake :/
    matrixStackIn.push();
    matrixStackIn.translate(0, 0, -0.25D);
    super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    matrixStackIn.pop();
  }
  
}
