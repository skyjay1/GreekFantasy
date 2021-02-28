package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.math.MathHelper;

public class PegasusModel<T extends AbstractHorseEntity> extends HorseModel<T> {

  private final ModelRenderer leftWing;
  private final ModelRenderer rightWing;

  public PegasusModel(float modelSize) {
    super(modelSize);
    
    leftWing = new ModelRenderer(this);
    leftWing.setRotationPoint(5.0F, -8.0F, -7.0F);
    leftWing.rotateAngleY = -1.5708F;
    leftWing.setTextureOffset(0, 0).addBox(-6.0F, 0.0F, 0.0F, 11.0F, 20.0F, 1.0F, modelSize, true);

    rightWing = new ModelRenderer(this);
    rightWing.setRotationPoint(-5.0F, -8.0F, -7.0F);
    rightWing.rotateAngleY = 1.5708F;
    rightWing.setTextureOffset(0, 0).addBox(-6.0F, 0.0F, 0.0F, 11.0F, 20.0F, 1.0F, modelSize, false);
  }
  
  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    // calculate wing rotations
    final float wingSpan = 0.6F;
    final float wingSpeed = 0.08F + (entityIn.isBeingRidden() ? 0.32F : 0);
    final float wingAngle = 1.5708F + MathHelper.cos((entityIn.ticksExisted + entityIn.getEntityId() * 3 + partialTick) * wingSpeed) * wingSpan;
    // update rotations
    leftWing.rotateAngleX = body.rotateAngleX;
    rightWing.rotateAngleX = body.rotateAngleX;
    leftWing.rotateAngleZ = -wingAngle + body.rotateAngleZ;
    rightWing.rotateAngleZ = wingAngle + body.rotateAngleZ;
  }

  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    // copy rotation point from body
    this.leftWing.setRotationPoint(5.0F + this.body.rotationPointX, -8.0F + this.body.rotationPointY, -7.0F + this.body.rotationPointZ);
    this.rightWing.setRotationPoint(-5.0F + this.body.rotationPointX, -8.0F + this.body.rotationPointY, -7.0F + this.body.rotationPointZ);
  }
  
  public void renderWings(T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTick) {
    // actually render the wings
    this.leftWing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    this.rightWing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
