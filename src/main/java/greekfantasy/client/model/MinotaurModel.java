package greekfantasy.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.MinotaurEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class MinotaurModel<T extends MinotaurEntity> extends HoofedBipedModel<T> {
  
  private boolean stomping;

  public MinotaurModel(float modelSize) {
    super(modelSize, true, false);
    textureWidth = 64;
    textureHeight = 64;
        
    // nose
    this.bipedHead.setTextureOffset(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, modelSize);

    // horns
    this.bipedHead.addChild(makeBullHorns(this, modelSize, true));
    this.bipedHead.addChild(makeBullHorns(this, modelSize, false));
  }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    if(entity.isStomping()) {
      final float ticks = entity.ticksExisted + partialTick;
      final float stompSpeed = entity.getStompingSpeed();
      final float limbSwingSin = MathHelper.cos(ticks * stompSpeed + (float)Math.PI);
      final float limbSwingCos = MathHelper.cos(ticks * stompSpeed) * 0.75F;
      float rightLegSwing = 0.38F * limbSwingSin;
      float leftLegSwing = 0.38F * limbSwingCos;
      // legs
      rightLegUpper.rotateAngleX = -0.2618F + limbSwingSin * 0.42F;
      leftLegUpper.rotateAngleX = -0.2618F + limbSwingCos * 0.42F;
      rightLegLower.rotateAngleX = 0.7854F + rightLegSwing;
      rightHoof.rotateAngleX = -0.5236F - rightLegSwing;
      leftLegLower.rotateAngleX = 0.7854F + leftLegSwing;
      leftHoof.rotateAngleX = -0.5236F - leftLegSwing;
      // head
      this.bipedHead.rotateAngleX = 0.558F;
    }
  }
  
  @Override
  public void render(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, 
      final float redIn, final float greenIn, final float blueIn, final float alphaIn) {
    matrixStackIn.push();
    if(stomping) {
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(9.0F));
    }
    super.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, alphaIn);
    matrixStackIn.pop();
  }
  
  public boolean isStomping() { return stomping; }
  public void setStomping(final boolean isStomping) { stomping = isStomping; }
  public void resetStomping() { stomping = false; }

  public static ModelRenderer makeBullHorns(EntityModel<?> model, final float modelSize, final boolean isLeft) {
    final int textureX = isLeft ? 58 : 51;
    final float horn1X = isLeft ? 3.0F : -3.0F;
    final float horn1Z = isLeft ? 0.0F : -2.0F;
    //final float horn2X = isLeft ? -7.0F : -1.0F;
    //final float horn3X = isLeft ? -7.0F : 0F;
    final float angleY = isLeft ? -1 : 1;

    final ModelRenderer horn3 = new ModelRenderer(model);
    horn3.setRotationPoint(0.0F, -3.0F, 0.0F);
    horn3.rotateAngleX = -0.5236F;
    horn3.setTextureOffset(textureX, 59).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
    horn3.mirror = isLeft;
    
    final ModelRenderer horn2 = new ModelRenderer(model);
    horn2.setRotationPoint(-1.0F, -4.0F, -2.0F);
    horn2.rotateAngleX = -0.5236F;
    horn2.setTextureOffset(textureX, 54).addBox(-0.51F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
    horn2.addChild(horn3);
    horn2.mirror = isLeft;
    
    final ModelRenderer horn1 = new ModelRenderer(model);
    horn1.setRotationPoint(horn1X, -7.0F, horn1Z);
    horn1.rotateAngleX = 1.3963F;
    horn1.rotateAngleY = 1.0472F * angleY;
    horn1.setTextureOffset(textureX, 48).addBox(-1.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, modelSize);
    horn1.addChild(horn2);
    horn1.mirror = isLeft;

    return horn1;
  }
}
