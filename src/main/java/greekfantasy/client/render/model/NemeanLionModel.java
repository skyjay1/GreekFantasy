package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import greekfantasy.entity.NemeanLionEntity;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class NemeanLionModel<T extends NemeanLionEntity> extends QuadrupedModel<T> {
  
  private final ModelRenderer mouth;
  private final ModelRenderer tail;
  private final ModelRenderer tail2;

  public NemeanLionModel() {
    super(0, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
    textureWidth = 64;
    textureHeight = 64;

    body = new ModelRenderer(this);
    body.setRotationPoint(-2.0F, 9.0F, 12.0F);
    body.rotateAngleX = 1.5708F;
    body.setTextureOffset(29, 0).addBox(-2.0F, -21.0F, -6.0F, 8.0F, 11.0F, 7.0F, 0.0F, false);
    body.setTextureOffset(35, 19).addBox(-1.0F, -10.0F, -6.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);
    body.setTextureOffset(0, 34).addBox(-3.0F, -23.0F, -7.0F, 10.0F, 4.0F, 10.0F, 0.0F, false);

    headModel = new ModelRenderer(this);
    headModel.setRotationPoint(0.0F, 10.0F, -10.0F);
    headModel.setTextureOffset(2, 2).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 4.0F, 0.0F, false);
    headModel.setTextureOffset(0, 13).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 3.0F, 2.0F, 0.0F, false);
    headModel.setTextureOffset(15, 17).addBox(-2.0F, 2.5F, -4.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
    headModel.setTextureOffset(21, 1).addBox(-3.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
    headModel.setTextureOffset(28, 1).addBox(1.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

    mouth = new ModelRenderer(this);
    mouth.setRotationPoint(0.0F, 3.0F, -3.0F);
    mouth.rotateAngleX = 0.5236F;
    mouth.setTextureOffset(15, 14).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 1.0F, 2.0F, 0.0F, false);
    headModel.addChild(mouth);

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
    legFrontRight.setTextureOffset(0, 19).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, 0.0F, false);

    legBackRight = new ModelRenderer(this);
    legBackRight.setRotationPoint(-3.0F, 14.0F, 7.0F);
    legBackRight.setTextureOffset(17, 19).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, false);

    legFrontLeft = new ModelRenderer(this);
    legFrontLeft.setRotationPoint(3.0F, 14.0F, -5.0F);
    legFrontLeft.setTextureOffset(0, 19).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, 0.0F, false);

    legBackLeft = new ModelRenderer(this);
    legBackLeft.setRotationPoint(3.0F, 14.0F, 7.0F);
    legBackLeft.setTextureOffset(17, 19).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 10.0F, 4.0F, 0.0F, false);
  }

  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(tail)); }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

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
  }
}
