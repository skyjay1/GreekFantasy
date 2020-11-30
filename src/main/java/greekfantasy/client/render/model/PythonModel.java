package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.PythonEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class PythonModel<T extends PythonEntity> extends AgeableModel<T> {

  private final ModelRenderer head;
  private final ModelRenderer mouth;
  
  private final ModelRenderer body1;
  private final ModelRenderer body2;
  private final ModelRenderer body3;
  private final ModelRenderer body4;
  private final ModelRenderer body5;
  private final ModelRenderer body6;
  private final ModelRenderer body7;
  private final ModelRenderer body8;

  public PythonModel(final float modelSize) {
    super();
    textureWidth = 64;
    textureHeight = 64;

    head = new ModelRenderer(this);
    head.setRotationPoint(0.0F, 10.0F, -4.0F);
    head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -10.0F, 8.0F, 8.0F, 10.0F, 0.0F, false);
    head.setTextureOffset(27, 0).addBox(-3.0F, -6.0F, -15.0F, 6.0F, 3.0F, 5.0F, 0.0F, false);
    head.setTextureOffset(51, 0).addBox(1.0F, -3.5F, -14.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    head.setTextureOffset(51, 0).addBox(-2.0F, -3.5F, -14.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

    mouth = new ModelRenderer(this);
    mouth.setRotationPoint(0.0F, -3.0F, -10.0F);
    head.addChild(mouth);
    mouth.rotateAngleX = 0.5236F;
    mouth.setTextureOffset(37, 12).addBox(-3.0F, 0.0F, -5.0F, 6.0F, 1.0F, 5.0F, 0.0F, false);

    final ModelRenderer leftHorn = new ModelRenderer(this);
    leftHorn.setRotationPoint(1.0F, -10.0F, -5.0F);
    head.addChild(leftHorn);
    leftHorn.rotateAngleX = 0.48F;
    leftHorn.rotateAngleZ = 0.1745F;
    leftHorn.setTextureOffset(56, 0).addBox(0.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

    final ModelRenderer rightHorn = new ModelRenderer(this);
    rightHorn.setRotationPoint(-1.0F, -10.0F, -5.0F);
    head.addChild(rightHorn);
    rightHorn.rotateAngleX = 0.48F;
    rightHorn.rotateAngleZ = -0.1745F;
    rightHorn.setTextureOffset(56, 0).addBox(-2.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

    body1 = new ModelRenderer(this);
    body1.setRotationPoint(0.0F, 9.0F, -4.5F);
    body1.rotateAngleX = -0.7854F;
    body1.setTextureOffset(0, 20).addBox(-3.0F, -4.5F, -4.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);

    body2 = new ModelRenderer(this);
    body2.setRotationPoint(0.0F, -1.0F, 6.0F);
    body1.addChild(body2);
    body2.rotateAngleX = -0.5236F;
    body2.setTextureOffset(0, 20).addBox(-2.99F, -2.0F, -3.5F, 6.0F, 6.0F, 8.0F, 0.0F, false);

    body3 = new ModelRenderer(this);
    body3.setRotationPoint(0.0F, 4.0F, 4.0F);
    body2.addChild(body3);
    body3.rotateAngleX = 0.3491F;
    body3.setTextureOffset(0, 20).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);

    body4 = new ModelRenderer(this);
    body4.setRotationPoint(0.0F, 0.0F, 8.0F);
    body3.addChild(body4);
    body4.rotateAngleX = 0.9599F;
    body4.setTextureOffset(0, 35).addBox(-2.99F, -6.0F, 0.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    body5 = new ModelRenderer(this);
    body5.setRotationPoint(0.0F, 0.0F, 5.5F);
    body4.addChild(body5);
    body5.setTextureOffset(0, 35).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    body6 = new ModelRenderer(this);
    body6.setRotationPoint(0.0F, 0.0F, 5.5F);
    body5.addChild(body6);
    body6.setTextureOffset(0, 48).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);

    body7 = new ModelRenderer(this);
    body7.setRotationPoint(0.0F, 0.0F, 5.5F);
    body6.addChild(body7);
    body7.setTextureOffset(30, 24).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

    body8 = new ModelRenderer(this);
    body8.setRotationPoint(0.0F, 0.0F, 5.0F);
    body7.addChild(body8);
    body8.setTextureOffset(30, 35).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 6.0F, 0.0F, false);
  }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    // head rotation
    head.rotateAngleY = rotationYaw * 0.017453292F;
    head.rotateAngleX = rotationPitch * 0.017453292F;
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    
    // animate snake body
//    final float standingTime = entity.getStandingTime(partialTick);
//    final float standingTimeLeft = (1.0F - standingTime);
//    final float hidingTime = entity.getHidingTime(partialTick);
    final float limbSwingCos =MathHelper.cos(limbSwing);
    final float idleSwingCos = MathHelper.cos((entity.ticksExisted + partialTick) * 0.22F);
    // standing
//    this.body1.rotateAngleX = -0.7854F * standingTime - 1.4707F * hidingTime;
//    this.body2.rotateAngleX = -0.5236F * standingTime;
//    this.body3.rotateAngleX = 0.3491F * standingTime;
//    this.body4.rotateAngleX = 0.9599F * standingTime;
    this.mouth.rotateAngleX = (0.5236F + 0.06F * idleSwingCos);
    // slithering
//    body1.rotateAngleY = limbSwingCos * -0.4F * standingTimeLeft;
//    body2.rotateAngleY = limbSwingCos * 0.4F * standingTimeLeft;
//    body3.rotateAngleY = limbSwingCos * -0.75F * standingTimeLeft;
//    body4.rotateAngleY = limbSwingCos * 0.75F * standingTimeLeft;
    body4.rotateAngleY = limbSwingCos * 0.15F;
    body5.rotateAngleY = limbSwingCos * -0.60F;
    body6.rotateAngleY = limbSwingCos * 0.85F;
    body7.rotateAngleY = limbSwingCos * -0.65F;
    body8.rotateAngleY = limbSwingCos * 0.35F;
  }

  @Override
  protected Iterable<ModelRenderer> getBodyParts() {
    return ImmutableList.of(body1);
  }

  @Override
  protected Iterable<ModelRenderer> getHeadParts() {
    return ImmutableList.of(head);
  }  
}
