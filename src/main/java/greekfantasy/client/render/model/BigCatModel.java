package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public abstract class BigCatModel<T extends LivingEntity> extends QuadrupedModel<T> {
  
  protected ModelRenderer tail;
  protected ModelRenderer tail2;
  
  private Vector3f headPoints;

  public BigCatModel(int tWidth, int tHeight) {
    super(0, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
    textureWidth = tWidth;
    textureHeight = tHeight;

    body = new ModelRenderer(this);
    body.setRotationPoint(0.0F, 12.0F, 0.0F);
    body.rotateAngleX = 1.5708F;
    body.setTextureOffset(29, 0).addBox(-4.0F, -9.0F, -3.0F, 8.0F, 11.0F, 7.0F, 0.0F, false);
    body.setTextureOffset(35, 19).addBox(-3.0F, 2.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);

    tail = new ModelRenderer(this);
    tail.setRotationPoint(0.0F, 10.0F, 10.0F);
    tail.rotateAngleX = 0.5236F;
    tail.setTextureOffset(42, 35).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

    tail2 = new ModelRenderer(this);
    tail2.setRotationPoint(0.0F, 5.0F, -1.0F);
    tail.addChild(tail2);
    tail2.rotateAngleX = 0.5236F;
    tail2.setTextureOffset(47, 35).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
    tail2.setTextureOffset(42, 42).addBox(-1.0F, 3.0F, -0.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);

    legFrontRight = new ModelRenderer(this);
    legFrontRight.setRotationPoint(-3.0F, 14.0F, -5.0F);
    legFrontRight.setTextureOffset(0, 19).addBox(-1.5F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);
    legFrontRight.setTextureOffset(18, 19).addBox(-1.0F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
    legFrontRight.addChild(makeClawModel(-1.75F, 1.0F));

    legBackRight = new ModelRenderer(this);
    legBackRight.setRotationPoint(-3.0F, 14.0F, 7.0F);
    legBackRight.setTextureOffset(0, 19).addBox(-0.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);
    legBackRight.setTextureOffset(18, 19).addBox(-0.5F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
    legBackRight.addChild(makeClawModel(-1.25F, 1.0F));

    legFrontLeft = new ModelRenderer(this);
    legFrontLeft.setRotationPoint(3.0F, 14.0F, -5.0F);
    legFrontLeft.setTextureOffset(0, 19).addBox(-2.5F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);
    legFrontLeft.setTextureOffset(18, 19).addBox(-2.0F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
    legFrontLeft.addChild(makeClawModel(-1.75F, 0.0F));

    legBackLeft = new ModelRenderer(this);
    legBackLeft.setRotationPoint(3.0F, 14.0F, 7.0F);
    legBackLeft.setTextureOffset(18, 19).addBox(-2.5F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
    legBackLeft.setTextureOffset(0, 19).addBox(-2.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);
    legBackLeft.addChild(makeClawModel(-1.25F, -1.0F));

    headModel = makeHeadModel();
    headPoints = new Vector3f(headModel.rotationPointX, headModel.rotationPointY, headModel.rotationPointZ);
  }

  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(tail)); }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    if(isSitting(entity)) {
      // reset rotation points
      body.setRotationPoint(0.0F, 18.0F, 0.0F);
      getHeadParts().forEach(m -> m.setRotationPoint(headPoints.getX(), headPoints.getY() + 2.0F, headPoints.getZ() + 2.0F));
      legBackRight.setRotationPoint(-3.0F, 22.0F, 8.0F);
      legBackLeft.setRotationPoint(3.0F, 22.0F, 8.0F);
      tail.setRotationPoint(0.0F, 22.0F, 8.0F);
      // reset rotation angles
      body.rotateAngleX = 1.0472F;
      legBackRight.rotateAngleX = legBackLeft.rotateAngleX = -1.4708F;
      legFrontRight.rotateAngleX = legFrontLeft.rotateAngleX = 0F;
      legBackRight.rotateAngleY = 0.2F;
      legBackLeft.rotateAngleY = -0.2F;
    } else {
      // reset rotation points
      body.setRotationPoint(0.0F, 12.0F, 0.0F);
      getHeadParts().forEach(m -> m.setRotationPoint(headPoints.getX(), headPoints.getY(), headPoints.getZ()));
      legBackRight.setRotationPoint(-3.0F, 14.0F, 7.0F);
      legBackLeft.setRotationPoint(3.0F, 14.0F, 7.0F);
      tail.setRotationPoint(0.0F, 10.0F, 10.0F);
      // reset rotation angles
      body.rotateAngleX = 1.5708F;
      legBackRight.rotateAngleY = legBackLeft.rotateAngleY = 0.0F;
    }
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    float limbSwingCos = MathHelper.cos(limbSwing) * limbSwingAmount;
    float idleSwing = 0.1F * MathHelper.cos((entity.ticksExisted + partialTick) * 0.08F);
    float tailSwing = 0.42F * limbSwingCos;
    tail.rotateAngleX = 0.6854F + tailSwing;
    tail2.rotateAngleX = 0.3491F + tailSwing * 0.6F;
    tail.rotateAngleZ = idleSwing;
    tail2.rotateAngleZ = idleSwing * 0.85F;
    body.rotateAngleZ = limbSwingCos * 0.12F;
    // reset angles when sitting
    if(isSitting(entity)) {
      tail.rotateAngleX += 0.7F;
    }
  }
  
  /**
   * Create a model for the claws
   * @param startX should be -1.75F for front, and -1.25F for back
   * @param rotX changes for each claw
   * @return the claws model that was created
   */
  protected ModelRenderer makeClawModel(final float startX, final float rotX) {
    final ModelRenderer claws = new ModelRenderer(this);
    claws.setRotationPoint(rotX, 9.0F, -1.5F);
    claws.rotateAngleX = -0.7854F;
    claws.setTextureOffset(0, 29).addBox(startX, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
    return claws;
  }
  
  protected abstract ModelRenderer makeHeadModel();
  
  protected abstract boolean isSitting(T entity);
}
