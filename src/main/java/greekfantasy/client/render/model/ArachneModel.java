package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.ArachneEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ArachneModel<T extends ArachneEntity> extends BipedModel<T> {
  
  private final ModelRenderer bipedChest;
  private final ModelRenderer bipedBodyConnector;
  private final ModelRenderer spiderNeck;
  private final ModelRenderer spiderBody;
  private final ModelRenderer spiderLeg1;
  private final ModelRenderer spiderFoot1;
  private final ModelRenderer spiderLeg2;
  private final ModelRenderer spiderFoot2;
  private final ModelRenderer spiderLeg3;
  private final ModelRenderer spiderFoot3;
  private final ModelRenderer spiderLeg4;
  private final ModelRenderer spiderFoot4;
  private final ModelRenderer spiderLeg5;
  private final ModelRenderer spiderFoot5;
  private final ModelRenderer spiderLeg6;
  private final ModelRenderer spiderFoot6;
  private final ModelRenderer spiderLeg7;
  private final ModelRenderer spiderFoot7;
  private final ModelRenderer spiderLeg8;
  private final ModelRenderer spiderFoot8;
  
  public ArachneModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);
    
    // human parts
    
    head = new ModelRenderer(this);
    head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    
    hat = new ModelRenderer(this);
    hat.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, false);
    
    ModelRenderer mouth = new ModelRenderer(this);
    mouth.setPos(0.0F, -2.0F, -4.0F);
    mouth.xRot = -0.7854F;
    mouth.texOffs(25, 0).addBox(1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    mouth.texOffs(25, 0).addBox(-2.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    head.addChild(mouth);

    rightArm = new ModelRenderer(this);
    rightArm.texOffs(42, 16).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, false);

    leftArm = new ModelRenderer(this);
    leftArm.texOffs(42, 16).addBox(0.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, true);

    bipedBodyConnector = new ModelRenderer(this);
    bipedBodyConnector.xRot = 0.7854F;
    bipedBodyConnector.texOffs(33, 50).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 3.0F, 2.0F, 0.0F, false);

    bipedChest = new ModelRenderer(this);
    bipedChest.xRot = -0.1745F;
    bipedChest.texOffs(0, 20).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, 0.0F, false);

    body = new ModelRenderer(this);
    body.texOffs(18, 16).addBox(-4.0F, -24.0F, -5.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

    // disable biped legs
    leftLeg.visible = false;
    rightLeg.visible = false;
    
    // spider parts

    spiderNeck = new ModelRenderer(this);
    spiderNeck.setPos(0.0F, 11.0F, 0.0F);
    spiderNeck.texOffs(0, 32).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    spiderBody = new ModelRenderer(this);
    spiderBody.setPos(0.0F, 11.0F, 3.0F);
    spiderBody.texOffs(0, 44).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 12.0F, 0.0F, false);

    spiderLeg1 = new ModelRenderer(this);
    spiderLeg1.setPos(-4.0F, 11.0F, 2.0F);
    spiderLeg1.texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot1 = new ModelRenderer(this);
    spiderFoot1.setPos(-13.0F, -1.0F, 0.0F);
    spiderLeg1.addChild(spiderFoot1);
    spiderFoot1.texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg2 = new ModelRenderer(this);
    spiderLeg2.setPos(4.0F, 11.0F, 2.0F);
    spiderLeg2.texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot2 = new ModelRenderer(this);
    spiderFoot2.setPos(13.0F, -1.0F, 0.0F);
    spiderLeg2.addChild(spiderFoot2);
    spiderFoot2.texOffs(32, 37).addBox(0.0F, 0.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg3 = new ModelRenderer(this);
    spiderLeg3.setPos(-4.0F, 11.0F, 1.0F);
    spiderLeg3.texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot3 = new ModelRenderer(this);
    spiderFoot3.setPos(-13.0F, -1.0F, 0.0F);
    spiderLeg3.addChild(spiderFoot3);
    spiderFoot3.texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg4 = new ModelRenderer(this);
    spiderLeg4.setPos(4.0F, 11.0F, 1.0F);
    spiderLeg4.texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot4 = new ModelRenderer(this);
    spiderFoot4.setPos(13.0F, -1.0F, 0.0F);
    spiderLeg4.addChild(spiderFoot4);
    spiderFoot4.texOffs(33, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg5 = new ModelRenderer(this);
    spiderLeg5.setPos(-4.0F, 11.0F, 0.0F);
    spiderLeg5.texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot5 = new ModelRenderer(this);
    spiderFoot5.setPos(-13.0F, -1.0F, 0.0F);
    spiderLeg5.addChild(spiderFoot5);
    spiderFoot5.texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg6 = new ModelRenderer(this);
    spiderLeg6.setPos(4.0F, 11.0F, 0.0F);
    spiderLeg6.texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot6 = new ModelRenderer(this);
    spiderFoot6.setPos(13.0F, -1.0F, 0.0F);
    spiderLeg6.addChild(spiderFoot6);
    spiderFoot6.texOffs(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg7 = new ModelRenderer(this);
    spiderLeg7.setPos(-4.0F, 11.0F, -1.0F);
    spiderLeg7.texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot7 = new ModelRenderer(this);
    spiderFoot7.setPos(-13.0F, -1.0F, 0.0F);
    spiderLeg7.addChild(spiderFoot7);
    spiderFoot7.texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg8 = new ModelRenderer(this);
    spiderLeg8.setPos(4.0F, 11.0F, -1.0F);
    spiderLeg8.texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot8 = new ModelRenderer(this);
    spiderFoot8.setPos(13.0F, -1.0F, 0.0F);
    spiderLeg8.addChild(spiderFoot8);
    spiderFoot8.texOffs(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);
  }
  
  @Override
  public Iterable<ModelRenderer> bodyParts() { return ImmutableList.of(body, bipedChest, hat, leftArm, rightArm, bipedBodyConnector, spiderNeck, spiderBody, spiderLeg1, spiderLeg2, spiderLeg3, spiderLeg4, spiderLeg5, spiderLeg6, spiderLeg7, spiderLeg8); }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    // reset biped rotation points
    head.setPos(0.0F, 0.0F, -3.0F);
    hat.setPos(0.0F, 0.0F, -3.0F);
    body.setPos(0.0F, 24.0F, 0.0F);
    bipedChest.setPos(0.01F, 1.0F, -5.0F);
    bipedBodyConnector.setPos(0.0F, 12.0F, -5.0F);
    leftArm.setPos(4.0F, 1.0F, -3.0F);
    rightArm.setPos(-4.0F, 1.0F, -3.0F);
    // animate spider legs
    final float legFrontBackZ = 0.7853982F;
    final float legMiddleZ = 0.58119464F;
    this.spiderLeg1.zRot = -legFrontBackZ;
    this.spiderLeg2.zRot = legFrontBackZ;
    this.spiderLeg3.zRot = -legMiddleZ;
    this.spiderLeg4.zRot = legMiddleZ;
    this.spiderLeg5.zRot = -legMiddleZ;
    this.spiderLeg6.zRot = legMiddleZ;
    this.spiderLeg7.zRot = -legFrontBackZ;
    this.spiderLeg8.zRot = legFrontBackZ;
    
    final float legFrontBackY = 0.7853982F;
    final float legMiddleY = 0.3926991F;
    this.spiderLeg1.yRot = legFrontBackY;
    this.spiderLeg2.yRot = -legFrontBackY;
    this.spiderLeg3.yRot = legMiddleY;
    this.spiderLeg4.yRot = -legMiddleY;
    this.spiderLeg5.yRot = -legMiddleY;
    this.spiderLeg6.yRot = legMiddleY;
    this.spiderLeg7.yRot = -legFrontBackY;
    this.spiderLeg8.yRot = legFrontBackY;
    
    float leg12Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
    float leg34Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * limbSwingAmount;
    float leg56Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * limbSwingAmount;
    float leg78Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 4.712389F) * 0.4F) * limbSwingAmount;
    
    float leg12Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
    float leg34Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 3.1415927F) * 0.4F) * limbSwingAmount;
    float leg56Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 1.5707964F) * 0.4F) * limbSwingAmount;
    float leg78Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 4.712389F) * 0.4F) * limbSwingAmount;
    
    this.spiderLeg1.yRot += leg12Y;
    this.spiderLeg2.yRot += -leg12Y;
    this.spiderLeg3.yRot += leg34Y;
    this.spiderLeg4.yRot += -leg34Y;
    this.spiderLeg5.yRot += leg56Y;
    this.spiderLeg6.yRot += -leg56Y;
    this.spiderLeg7.yRot += leg78Y;
    this.spiderLeg8.yRot += -leg78Y;
    
    this.spiderLeg1.zRot += leg12Z;
    this.spiderLeg2.zRot += -leg12Z;
    this.spiderLeg3.zRot += leg34Z;
    this.spiderLeg4.zRot += -leg34Z;
    this.spiderLeg5.zRot += leg56Z;
    this.spiderLeg6.zRot += -leg56Z;
    this.spiderLeg7.zRot += leg78Z;
    this.spiderLeg8.zRot += -leg78Z;
    
    // feet
    final float footFrontBackZ = 1.0472F;
    final float footMiddleZ = 1.0472F;
    this.spiderFoot1.zRot = -footFrontBackZ;
    this.spiderFoot2.zRot = footFrontBackZ;
    this.spiderFoot3.zRot = -footMiddleZ;
    this.spiderFoot4.zRot = footMiddleZ;
    this.spiderFoot5.zRot = -footMiddleZ;
    this.spiderFoot6.zRot = footMiddleZ;
    this.spiderFoot7.zRot = -footFrontBackZ;
    this.spiderFoot8.zRot = footFrontBackZ;
    
    final float footFrontBackY = 0.523599F;
    final float footMiddleY = 0.0F;
    this.spiderFoot1.yRot = -footFrontBackY;
    this.spiderFoot2.yRot = footFrontBackY;
    this.spiderFoot3.yRot = -footMiddleY;
    this.spiderFoot4.yRot = footMiddleY;
    this.spiderFoot5.yRot = -footMiddleY;
    this.spiderFoot6.yRot = footMiddleY;
    this.spiderFoot7.yRot = footFrontBackY;
    this.spiderFoot8.yRot = -footFrontBackY;
  }
  
}
