package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.ElpisEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ElpisModel<T extends ElpisEntity> extends BipedModel<T> {
  
  private final ModelRenderer leftWing;
  private final ModelRenderer rightWing;
  
  private float alpha = 1.0f;
  
  public ElpisModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);
    
    bipedRightLeg = new ModelRenderer(this);
    bipedRightLeg.setRotationPoint(0.0F, 12.0F, -2.0F);
    bipedRightLeg.setTextureOffset(0, 16).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F, modelSize);
    bipedRightLeg.setTextureOffset(0, 16).addBox(0.1F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F, modelSize);
    
    this.bipedLeftLeg.showModel = false;
    
    this.rightWing = new ModelRenderer(this, 0, 32);
    this.rightWing.setRotationPoint(0.0F, 1.0F, 2.0F);
    this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
    
    this.leftWing = new ModelRenderer(this, 0, 32);
    this.leftWing.setRotationPoint(0.0F, 1.0F, 2.0F);
    this.leftWing.mirror = true;
    this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);

  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightWing, this.leftWing)); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch);
    // set leg rotation
    bipedRightLeg.setRotationPoint(0.0F, 12.0F, -2.0F);
    bipedRightLeg.rotateAngleX += 0.62831855F;
    // animate wings
    final float wingSpeed = 0.8F;
    final float wingAngle = 0.47123894F;
    this.rightWing.rotateAngleY = wingAngle + MathHelper.cos(ageInTicks * wingSpeed) * (float)Math.PI * 0.08F;
    this.rightWing.rotateAngleX = wingAngle;
    this.rightWing.rotateAngleZ = wingAngle;
    this.leftWing.rotateAngleY = -this.rightWing.rotateAngleY;
    this.leftWing.rotateAngleZ = -wingAngle;
    this.leftWing.rotateAngleX = wingAngle;
  }
  
  @Override
  public void render(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, 
      final float redIn, final float greenIn, final float blueIn, final float alphaIn) {
    // render with custom alpha
    super.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, this.alpha);
  }
  
  public void setAlpha(final float a) {  alpha = a; }
  public void resetAlpha() { alpha = 1.0F; }
  public float getAlpha() { return alpha; }
}
