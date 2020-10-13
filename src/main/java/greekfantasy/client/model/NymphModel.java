package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.NaiadEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;

public class NymphModel<T extends MobEntity> extends BipedModel<T> {

  private final ModelRenderer bipedChest;

  public NymphModel(final float modelSize) {
    super(modelSize);

    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, 0.0F, 1.5F);
    bipedHead.setTextureOffset(0, 0).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, modelSize);
    bipedHead.setTextureOffset(21, 0).addBox(-3.5F, 0.0F, 2.5F, 7.0F, 6.0F, 1.0F, modelSize);
    
    // hide headwear
    bipedHeadwear.showModel = false;

    bipedBody = new ModelRenderer(this, 0, 16);
    bipedBody.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 12.0F, 3.0F, modelSize);

    bipedChest = new ModelRenderer(this, 30, 7);
    bipedChest.setRotationPoint(0.0F, 1.0F, 1.0F);
    bipedChest.rotateAngleX = -0.1745F;
    bipedChest.addBox(-2.99F, 0.0F, -1.0F, 6.0F, 4.0F, 1.0F, modelSize);

    bipedLeftArm = new ModelRenderer(this, 19, 16);
    bipedLeftArm.setRotationPoint(3.0F, 2.0F, 1.5F);
    bipedLeftArm.addBox(0.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);

    bipedRightArm = new ModelRenderer(this, 29, 16);
    bipedRightArm.setRotationPoint(-3.0F, 2.0F, 1.5F);
    bipedRightArm.addBox(-2.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, modelSize);

    bipedLeftLeg = new ModelRenderer(this, 40, 16);
    bipedLeftLeg.setRotationPoint(1.5F, 12.0F, 1.5F);
    bipedLeftLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);

    bipedRightLeg = new ModelRenderer(this, 52, 16);
    bipedRightLeg.setRotationPoint(-1.5F, 12.0F, 1.5F);
    bipedRightLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, modelSize);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.bipedChest)); }
  
  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);    
    // correct rotation points
    bipedHead.setRotationPoint(0.0F, 0.0F, 1.5F);
    bipedLeftArm.setRotationPoint(3.0F, 2.0F, 1.5F);
    bipedRightArm.setRotationPoint(-3.0F, 2.0F, 1.5F);
    bipedLeftLeg.setRotationPoint(1.5F, 12.0F, 1.5F);
    bipedRightLeg.setRotationPoint(-1.5F, 12.0F, 1.5F);
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    // animate legs (swimming)
    if(entity.isSwimming() || (entity.isInWater() && !entity.isOnGround())) {
      final float ticks = entity.ticksExisted + partialTick + entity.getEntityId();
      final float legAngle = 0.32F;
      final float legSpeed = 0.09F;
      final float cosTicks = MathHelper.cos(ticks * legSpeed) * legAngle;
      final float sinTicks = MathHelper.cos(ticks * legSpeed + (float) Math.PI) * legAngle;
      bipedRightLeg.rotateAngleX = cosTicks;
      bipedLeftLeg.rotateAngleX = sinTicks;
    }
  }
  
  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    // this corrects a model offset mistake :/
    matrixStackIn.push();
    matrixStackIn.translate(0, 0, -0.125D);
    super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    matrixStackIn.pop();
  }
}