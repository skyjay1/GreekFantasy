package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import greekfantasy.entity.BronzeBullEntity;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class BronzeBullModel<T extends BronzeBullEntity> extends QuadrupedModel<T> {
  private final ModelRenderer mouth;
  protected ModelRenderer tail;
  protected ModelRenderer tail2;

  public BronzeBullModel() {
    super(0, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
    textureWidth = 128;
    textureHeight = 64;

    body = new ModelRenderer(this);
    body.setRotationPoint(0.0F, 5.0F, 2.0F);
    body.rotateAngleX = 1.5708F;
    body.setTextureOffset(0, 21).addBox(-7.0F, -15.0F, -4.0F, 14.0F, 16.0F, 18.0F, 0.0F, false);
    body.setTextureOffset(65, 26).addBox(-6.0F, 1.0F, -4.0F, 12.0F, 13.0F, 16.0F, 0.0F, false);

    headModel = new ModelRenderer(this);
    headModel.setRotationPoint(0.0F, -3.0F, -13.0F);
    headModel.setTextureOffset(0, 0).addBox(-5.0F, -5.0F, -8.0F, 10.0F, 12.0F, 8.0F, 0.0F, false);
    headModel.setTextureOffset(29, 0).addBox(-3.0F, 2.0F, -10.0F, 6.0F, 5.0F, 2.0F, 0.0F, false);

    mouth = new ModelRenderer(this);
    mouth.setRotationPoint(0.0F, 7.0F, 0.0F);
    headModel.addChild(mouth);
    mouth.setTextureOffset(47, 28).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 2.0F, 8.0F, 0.0F, false);

    // horns
    
    ModelRenderer hornLeft = new ModelRenderer(this);
    hornLeft.setRotationPoint(4.0F, -3.0F, -5.0F);
    hornLeft.rotateAngleX = 1.3963F;
    hornLeft.rotateAngleY = -1.0472F;
    hornLeft.setTextureOffset(47, 17).addBox(-1.0F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, 0.0F, false);
    headModel.addChild(hornLeft);

    ModelRenderer hornLeft2 = new ModelRenderer(this);
    hornLeft2.setRotationPoint(1.0F, -6.0F, -2.0F);
    hornLeft2.rotateAngleX = -0.5236F;
    hornLeft2.setTextureOffset(48, 8).addBox(-2.01F, -5.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);
    hornLeft.addChild(hornLeft2);

    ModelRenderer hornLeft3 = new ModelRenderer(this);
    hornLeft3.setRotationPoint(0.0F, -5.0F, 0.0F);
    hornLeft3.rotateAngleX = -0.5236F;
    hornLeft3.setTextureOffset(49, 0).addBox(-1.5F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
    hornLeft2.addChild(hornLeft3);

    ModelRenderer hornRight1 = new ModelRenderer(this);
    hornRight1.setRotationPoint(-4.0F, -3.0F, -5.0F);
    hornRight1.rotateAngleX = 1.3963F;
    hornRight1.rotateAngleY = 1.0472F;
    hornRight1.setTextureOffset(47, 17).addBox(-2.0F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, 0.0F, false);
    headModel.addChild(hornRight1);

    ModelRenderer hornRight2 = new ModelRenderer(this);
    hornRight2.setRotationPoint(1.0F, -6.0F, -2.0F);
    hornRight1.addChild(hornRight2);
    hornRight2.rotateAngleX = -0.5236F;
    hornRight2.setTextureOffset(48, 8).addBox(-2.99F, -5.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);

    ModelRenderer hornRight3 = new ModelRenderer(this);
    hornRight3.setRotationPoint(-1.0F, -5.0F, 0.0F);
    hornRight2.addChild(hornRight3);
    hornRight3.rotateAngleX = -0.5236F;
    hornRight3.setTextureOffset(49, 0).addBox(-1.5F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

    // legs
    
    legFrontRight = new ModelRenderer(this);
    legFrontRight.setRotationPoint(-6.0F, 6.0F, 12.0F);
    legFrontRight.setTextureOffset(91, 0).addBox(-2.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, 0.0F, true);

    legBackRight = new ModelRenderer(this);
    legBackRight.setRotationPoint(6.0F, 6.0F, 12.0F);
    legBackRight.setTextureOffset(91, 0).addBox(-4.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, 0.0F, false);

    legFrontLeft = new ModelRenderer(this);
    legFrontLeft.setRotationPoint(-7.0F, 6.0F, -8.0F);
    legFrontLeft.setTextureOffset(62, 0).addBox(-2.0F, 0.0F, -3.0F, 7.0F, 18.0F, 7.0F, 0.0F, true);

    legBackLeft = new ModelRenderer(this);
    legBackLeft.setRotationPoint(7.0F, 6.0F, -8.0F);
    legBackLeft.setTextureOffset(62, 0).addBox(-5.0F, 0.0F, -3.0F, 7.0F, 18.0F, 7.0F, 0.0F, false);

    tail = new ModelRenderer(this);
    tail.setRotationPoint(0.0F, -6.0F, 16.0F);
    tail.setTextureOffset(116, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

    tail2 = new ModelRenderer(this);
    tail2.setRotationPoint(0.0F, 8.0F, -2.0F);
    tail2.setTextureOffset(116, 11).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
    tail2.setTextureOffset(116, 20).addBox(-1.5F, 3.0F, -0.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
    tail.addChild(tail2);
  }

  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(tail)); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    float goringPercent = entity.getGoringPercent(1.0F);
    if(goringPercent > 0) {
      // head animation
      headModel.rotateAngleX += 0.5F;
      headModel.rotateAngleY += MathHelper.cos(goringPercent * (float)Math.PI * 10.0F) * 0.4F;
      body.rotateAngleX += 0.25F;
      body.setRotationPoint(0.0F, 7.0F, 2.0F);
      headModel.setRotationPoint(0.0F, 0.0F, -16.0F);
      tail.setRotationPoint(0.0F, -8.0F, 13.0F);
    } else {
      body.setRotationPoint(0.0F, 5.0F, 2.0F);
      headModel.setRotationPoint(0.0F, -3.0F, -13.0F);
      tail.setRotationPoint(0.0F, -6.0F, 16.0F);
      headModel.rotateAngleZ = 0.0F;
      headModel.rotateAngleY = 0.0F;
      body.rotateAngleX = 1.5708F;
    }
    // mouth animation
    float firingPercent = entity.getFiringPercent(1.0F);
    if(firingPercent > 0) {
      mouth.rotateAngleX = 0.56F;
    } else {
      mouth.rotateAngleX = 0;
    }
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    // goring animation
    float goringPercent = entity.getGoringPercent(partialTick);
    if(goringPercent > 0) {
      headModel.rotateAngleZ = MathHelper.cos(goringPercent * (float)Math.PI * 16.0F) * 0.44F;
    }
    // tail animation
    float limbSwingCos = MathHelper.cos(limbSwing) * limbSwingAmount;
    float idleSwing = 0.1F * MathHelper.cos((entity.ticksExisted + partialTick) * 0.08F);
    float tailSwing = 0.42F * limbSwingCos;
    tail.rotateAngleX = 0.5236F + tailSwing;
    tail2.rotateAngleX = 0.2618F + tailSwing * 0.6F;
    tail.rotateAngleZ = idleSwing;
    tail2.rotateAngleZ = idleSwing * 0.85F;
    body.rotateAngleZ = limbSwingCos * 0.08F;
  }
  
  
}
