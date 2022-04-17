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
    texWidth = 64;
    texHeight = 64;

    head = new ModelRenderer(this);
    head.setPos(0.0F, 10.0F, -4.0F);
    head.texOffs(0, 0).addBox(-4.0F, -10.0F, -10.0F, 8.0F, 8.0F, 10.0F, 0.0F, false);
    head.texOffs(27, 0).addBox(-3.0F, -6.0F, -15.0F, 6.0F, 3.0F, 5.0F, 0.0F, false);
    head.texOffs(51, 0).addBox(1.0F, -3.5F, -14.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    head.texOffs(51, 0).addBox(-2.0F, -3.5F, -14.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

    mouth = new ModelRenderer(this);
    mouth.setPos(0.0F, -3.0F, -10.0F);
    head.addChild(mouth);
    mouth.xRot = 0.5236F;
    mouth.texOffs(37, 12).addBox(-3.0F, 0.0F, -5.0F, 6.0F, 1.0F, 5.0F, 0.0F, false);

    final ModelRenderer leftHorn = new ModelRenderer(this);
    leftHorn.setPos(1.0F, -10.0F, -5.0F);
    head.addChild(leftHorn);
    leftHorn.xRot = 0.48F;
    leftHorn.zRot = 0.1745F;
    leftHorn.texOffs(56, 0).addBox(0.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

    final ModelRenderer rightHorn = new ModelRenderer(this);
    rightHorn.setPos(-1.0F, -10.0F, -5.0F);
    head.addChild(rightHorn);
    rightHorn.xRot = 0.48F;
    rightHorn.zRot = -0.1745F;
    rightHorn.texOffs(56, 0).addBox(-2.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

    body1 = new ModelRenderer(this);
    body1.setPos(0.0F, 9.0F, -4.5F);
    body1.xRot = -0.7854F;
    body1.texOffs(0, 20).addBox(-3.0F, -4.5F, -4.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);

    body2 = new ModelRenderer(this);
    body2.setPos(0.0F, -1.0F, 6.0F);
    body1.addChild(body2);
    body2.xRot = -0.5236F;
    body2.texOffs(0, 20).addBox(-2.99F, -2.0F, -3.5F, 6.0F, 6.0F, 8.0F, 0.0F, false);

    body3 = new ModelRenderer(this);
    body3.setPos(0.0F, 4.0F, 4.0F);
    body2.addChild(body3);
    body3.xRot = 0.3491F;
    body3.texOffs(0, 20).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 8.0F, 0.0F, false);

    body4 = new ModelRenderer(this);
    body4.setPos(0.0F, 0.0F, 8.0F);
    body3.addChild(body4);
    body4.xRot = 0.9599F;
    body4.texOffs(0, 35).addBox(-2.99F, -6.0F, 0.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    body5 = new ModelRenderer(this);
    body5.setPos(0.0F, 0.0F, 5.5F);
    body4.addChild(body5);
    body5.texOffs(0, 35).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    body6 = new ModelRenderer(this);
    body6.setPos(0.0F, 0.0F, 5.5F);
    body5.addChild(body6);
    body6.texOffs(0, 48).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 5.0F, 6.0F, 0.0F, false);

    body7 = new ModelRenderer(this);
    body7.setPos(0.0F, 0.0F, 5.5F);
    body6.addChild(body7);
    body7.texOffs(30, 24).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

    body8 = new ModelRenderer(this);
    body8.setPos(0.0F, 0.0F, 5.0F);
    body7.addChild(body8);
    body8.texOffs(30, 35).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 6.0F, 0.0F, false);
  }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    // head rotation
    head.yRot = rotationYaw * 0.017453292F;
    head.xRot = rotationPitch * 0.017453292F;
  }
  
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    
    // animate snake body
//    final float standingTime = entity.getStandingTime(partialTick);
//    final float standingTimeLeft = (1.0F - standingTime);
//    final float hidingTime = entity.getHidingTime(partialTick);
    final float limbSwingCos =MathHelper.cos(limbSwing);
    final float idleSwingCos = MathHelper.cos((entity.tickCount + partialTick) * 0.22F);
    // standing
//    this.body1.rotateAngleX = -0.7854F * standingTime - 1.4707F * hidingTime;
//    this.body2.rotateAngleX = -0.5236F * standingTime;
//    this.body3.rotateAngleX = 0.3491F * standingTime;
//    this.body4.rotateAngleX = 0.9599F * standingTime;
    this.mouth.xRot = (0.5236F + 0.06F * idleSwingCos);
    // slithering
//    body1.rotateAngleY = limbSwingCos * -0.4F * standingTimeLeft;
//    body2.rotateAngleY = limbSwingCos * 0.4F * standingTimeLeft;
//    body3.rotateAngleY = limbSwingCos * -0.75F * standingTimeLeft;
//    body4.rotateAngleY = limbSwingCos * 0.75F * standingTimeLeft;
    body4.yRot = limbSwingCos * 0.15F;
    body5.yRot = limbSwingCos * -0.60F;
    body6.yRot = limbSwingCos * 0.85F;
    body7.yRot = limbSwingCos * -0.65F;
    body8.yRot = limbSwingCos * 0.35F;
  }

  @Override
  protected Iterable<ModelRenderer> bodyParts() {
    return ImmutableList.of(body1);
  }

  @Override
  protected Iterable<ModelRenderer> headParts() {
    return ImmutableList.of(head);
  }  
}
