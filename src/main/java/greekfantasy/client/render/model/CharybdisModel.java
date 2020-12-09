package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.CharybdisEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CharybdisModel extends AgeableModel<CharybdisEntity> {
  
  private final ModelRenderer body;
  private final ModelRenderer body1;
  private final ModelRenderer body2;
  private final ModelRenderer body3;
  private final ModelRenderer body4;
  private final ModelRenderer head;
  private final ModelRenderer arms;
  
  private final ModelRenderer[] body1Fringe = new ModelRenderer[4];
  private final ModelRenderer[] body2Fringe = new ModelRenderer[12];
  private final ModelRenderer[] body3Fringe = new ModelRenderer[12];
  private final ModelRenderer[] body4Fringe = new ModelRenderer[4];
  private final CharybdisModelHelper.ArmModel[] armsArray = new CharybdisModelHelper.ArmModel[12];
  
  public CharybdisModel(float modelSize) {
    super();
    textureWidth = 128;
    textureHeight = 128;
    
    final float r90 = (float) Math.toRadians(90);
    final float r180 = (float) Math.toRadians(180);
    final float r270 = (float) Math.toRadians(270);
   
    body = new ModelRenderer(this);
    body.setRotationPoint(0.0F, 24.0F, 0.0F);

    body1 = new ModelRenderer(this);
    body1.setRotationPoint(0.0F, -30.0F, 0.0F);
    body.addChild(body1);
    body1.setTextureOffset(0, 0).addBox(-12.0F, -2.0F, -12.0F, 24.0F, 6.0F, 24.0F, 0.0F, false);

    body1.addChild(CharybdisModelHelper.makeBody1FringeModel(this, 0, body1Fringe, 0));
    body1.addChild(CharybdisModelHelper.makeBody1FringeModel(this, r90, body1Fringe, 1));
    body1.addChild(CharybdisModelHelper.makeBody1FringeModel(this, r180, body1Fringe, 2));
    body1.addChild(CharybdisModelHelper.makeBody1FringeModel(this, r270, body1Fringe, 3));
   
    body2 = new ModelRenderer(this);
    body2.setRotationPoint(0.0F, -26.0F, 0.0F);
    body.addChild(body2);
    body2.setTextureOffset(0, 57).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 10.0F, 16.0F, 0.0F, false);

    body2.addChild(CharybdisModelHelper.makeBody2FringeModel(this, 0, body2Fringe, 0));
    body2.addChild(CharybdisModelHelper.makeBody2FringeModel(this, r90, body2Fringe, 3));
    body2.addChild(CharybdisModelHelper.makeBody2FringeModel(this, r180, body2Fringe, 6));
    body2.addChild(CharybdisModelHelper.makeBody2FringeModel(this, r270, body2Fringe, 9));

    body3 = new ModelRenderer(this);
    body3.setRotationPoint(0.0F, 0.0F, 0.0F);
    body.addChild(body3);
    body3.setTextureOffset(64, 57).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F, 0.0F, false);

    body3.addChild(CharybdisModelHelper.makeBody3FringeModel(this, 0, body3Fringe, 0));
    body3.addChild(CharybdisModelHelper.makeBody3FringeModel(this, r90, body3Fringe, 3));
    body3.addChild(CharybdisModelHelper.makeBody3FringeModel(this, r180, body3Fringe, 6));
    body3.addChild(CharybdisModelHelper.makeBody3FringeModel(this, r270, body3Fringe, 9));

    body4 = new ModelRenderer(this);
    body4.setRotationPoint(0.0F, 0.0F, 0.0F);
    body.addChild(body4);
    body4.setTextureOffset(104, 57).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);

    body4.addChild(CharybdisModelHelper.makeBody4FringeModel(this, 0, body4Fringe, 0));
    body4.addChild(CharybdisModelHelper.makeBody4FringeModel(this, r90, body4Fringe, 1));
    body4.addChild(CharybdisModelHelper.makeBody4FringeModel(this, r180, body4Fringe, 2));
    body4.addChild(CharybdisModelHelper.makeBody4FringeModel(this, r270, body4Fringe, 3));

    // head and mouth
    head = new ModelRenderer(this);
    head.setRotationPoint(0.0F, 24.0F, 0.0F);
    head.setTextureOffset(0, 30).addBox(-10.0F, -32.01F, -10.0F, 20.0F, 6.0F, 20.0F, 0.0F, false);
    head.setTextureOffset(80, 40).addBox(-6.0F, -26.0F, -6.0F, 12.0F, 4.0F, 12.0F, 0.0F, false);

    // teeth
    head.addChild(CharybdisModelHelper.makeTeethModel(this, 0));
    head.addChild(CharybdisModelHelper.makeTeethModel(this, r90));
    head.addChild(CharybdisModelHelper.makeTeethModel(this, r180));
    head.addChild(CharybdisModelHelper.makeTeethModel(this, r270));

    // arms
    arms = new ModelRenderer(this);
    arms.setRotationPoint(0.0F, -31.0F, 0.0F);
    arms.addChild(CharybdisModelHelper.makeArmsModel(this, 0, armsArray, 0));
    arms.addChild(CharybdisModelHelper.makeArmsModel(this, r90, armsArray, 3));
    arms.addChild(CharybdisModelHelper.makeArmsModel(this, r180, armsArray, 6));
    arms.addChild(CharybdisModelHelper.makeArmsModel(this, r270, armsArray, 9));
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.body, this.arms); }
  
  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return ImmutableList.of(this.head); }
  
  @Override
  public void setRotationAngles(CharybdisEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
    // animate arms
    final float swirlingTime = entity.isSwirling() ? entity.getSwirlPercent() : 0;
    final float throwingTime = entity.isThrowing() ? entity.getThrowPercent() : 0;
    final float throwingTimeLeft = 1.0F - throwingTime;
    final float throwingZ = 0.9F - 2F * Math.abs(throwingTime - 0.5F);
    final float swirlingMult = 0.058F + 0.14F * Math.min(swirlingTime * 10.0F, 1.0F);
    for(int i = 0, l = armsArray.length; i < l; i++) {
      armsArray[i].setRotationAngles(entity, MathHelper.cos(ageInTicks * swirlingMult + i * 1.62F), throwingTimeLeft, throwingZ);
    }
    // animate fringes
    // body1
    final float idleCos = MathHelper.cos(ageInTicks * 0.064F);
    float cosX = idleCos * 0.44F;
    for(int i = 0, l = body1Fringe.length; i < l; i++) {
      body1Fringe[i].rotateAngleX = cosX;
    }
    // body2
    cosX = idleCos * 0.62F;
    for(int i = 0, l = body2Fringe.length; i < l; i++) {
      body2Fringe[i].rotateAngleY = cosX;
    }
    // body3
    cosX = idleCos * 0.58F;
    for(int i = 0, l = body3Fringe.length; i < l; i++) {
      body3Fringe[i].rotateAngleY = cosX;
    }
    // body4
    cosX = idleCos * 0.54F;
    for(int i = 0, l = body4Fringe.length; i < l; i++) {
      body4Fringe[i].rotateAngleY = cosX;
    }
  }
}
