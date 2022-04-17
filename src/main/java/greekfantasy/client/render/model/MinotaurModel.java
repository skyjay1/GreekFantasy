package greekfantasy.client.render.model;

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
    texWidth = 64;
    texHeight = 64;
        
    // nose
    this.head.texOffs(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, modelSize);

    // horns
    this.head.addChild(makeBullHorns(this, modelSize, true));
    this.head.addChild(makeBullHorns(this, modelSize, false));
  }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    if(entity.isCharging()) {
      final float ticks = entity.tickCount + partialTick;
      final float stompSpeed = entity.getStompingSpeed();
      final float limbSwingSin = MathHelper.cos(ticks * stompSpeed + (float)Math.PI);
      final float limbSwingCos = MathHelper.cos(ticks * stompSpeed) * 0.75F;
      float rightLegSwing = 0.38F * limbSwingSin;
      float leftLegSwing = 0.38F * limbSwingCos;
      // legs
      rightLegUpper.xRot = -0.2618F + limbSwingSin * 0.42F;
      leftLegUpper.xRot = -0.2618F + limbSwingCos * 0.42F;
      rightLegLower.xRot = 0.7854F + rightLegSwing;
      rightHoof.xRot = -0.5236F - rightLegSwing;
      leftLegLower.xRot = 0.7854F + leftLegSwing;
      leftHoof.xRot = -0.5236F - leftLegSwing;
      // head
      this.head.xRot = 0.558F;
    }
  }
  
  @Override
  public void renderToBuffer(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, 
      final float redIn, final float greenIn, final float blueIn, final float alphaIn) {
    matrixStackIn.pushPose();
    if(stomping) {
      matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(9.0F));
    }
    super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, redIn, greenIn, blueIn, alphaIn);
    matrixStackIn.popPose();
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
    horn3.setPos(0.0F, -3.0F, 0.0F);
    horn3.xRot = -0.5236F;
    horn3.texOffs(textureX, 59).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
    horn3.mirror = isLeft;
    
    final ModelRenderer horn2 = new ModelRenderer(model);
    horn2.setPos(-1.0F, -4.0F, -2.0F);
    horn2.xRot = -0.5236F;
    horn2.texOffs(textureX, 54).addBox(-0.51F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, modelSize);
    horn2.addChild(horn3);
    horn2.mirror = isLeft;
    
    final ModelRenderer horn1 = new ModelRenderer(model);
    horn1.setPos(horn1X, -7.0F, horn1Z);
    horn1.xRot = 1.3963F;
    horn1.yRot = 1.0472F * angleY;
    horn1.texOffs(textureX, 48).addBox(-1.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, modelSize);
    horn1.addChild(horn2);
    horn1.mirror = isLeft;

    return horn1;
  }
}
