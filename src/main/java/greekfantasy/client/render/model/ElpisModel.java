package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.ElpisEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import net.minecraft.client.renderer.entity.model.BipedModel.ArmPose;

public class ElpisModel<T extends ElpisEntity> extends BipedModel<T> {
  
  private final ModelRenderer leftWing;
  private final ModelRenderer rightWing;
  
  private float alpha = 1.0f;
  
  public ElpisModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);
    
    rightLeg = new ModelRenderer(this);
    rightLeg.setPos(0.0F, 12.0F, -2.0F);
    rightLeg.texOffs(0, 16).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F, modelSize);
    rightLeg.texOffs(0, 16).addBox(0.1F, 0.0F, 0.0F, 4.0F, 12.0F, 4.0F, modelSize);
    
    this.leftLeg.visible = false;
    
    this.rightWing = new ModelRenderer(this, 0, 32);
    this.rightWing.setPos(0.0F, 1.0F, 2.0F);
    this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
    
    this.leftWing = new ModelRenderer(this, 0, 32);
    this.leftWing.setPos(0.0F, 1.0F, 2.0F);
    this.leftWing.mirror = true;
    this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);

  }
  
  @Override
  protected Iterable<ModelRenderer> bodyParts() { return Iterables.concat(super.bodyParts(), ImmutableList.of(this.rightWing, this.leftWing)); }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
    if(!entity.getItemInHand(Hand.MAIN_HAND).isEmpty() || !entity.getItemInHand(Hand.OFF_HAND).isEmpty()) {
      this.leftArmPose = this.rightArmPose = ArmPose.ITEM;
    } else {
      this.leftArmPose = this.rightArmPose = ArmPose.EMPTY;
    }
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch);
    // set leg rotation
    rightLeg.setPos(0.0F, 12.0F, -2.0F);
    rightLeg.xRot += 0.62831855F;
    // animate wings
    final float wingSpeed = 0.8F;
    final float wingAngle = 0.47123894F;
    this.rightWing.yRot = wingAngle + MathHelper.cos(ageInTicks * wingSpeed) * (float)Math.PI * 0.08F;
    this.rightWing.xRot = wingAngle;
    this.rightWing.zRot = wingAngle;
    this.leftWing.yRot = -this.rightWing.yRot;
    this.leftWing.zRot = -wingAngle;
    this.leftWing.xRot = wingAngle;
  }
  
  @Override
  public void renderToBuffer(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, 
      final float redIn, final float greenIn, final float blueIn, final float alphaIn) {
    // render with custom alpha
    super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, this.alpha);
  }
  
  public void setAlpha(final float a) {  alpha = a; }
  public void resetAlpha() { alpha = 1.0F; }
  public float getAlpha() { return alpha; }
}
