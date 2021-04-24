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
    
    bipedHead = new ModelRenderer(this);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    
    bipedHeadwear = new ModelRenderer(this);
    bipedHeadwear.setTextureOffset(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, false);

    bipedRightArm = new ModelRenderer(this);
    bipedRightArm.setTextureOffset(42, 16).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, false);

    bipedLeftArm = new ModelRenderer(this);
    bipedLeftArm.setTextureOffset(42, 16).addBox(0.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, true);

    bipedBodyConnector = new ModelRenderer(this);
    bipedBodyConnector.rotateAngleX = 0.7854F;
    bipedBodyConnector.setTextureOffset(33, 50).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 3.0F, 2.0F, 0.0F, false);

    bipedChest = new ModelRenderer(this);
    bipedChest.rotateAngleX = -0.1745F;
    bipedChest.setTextureOffset(0, 20).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, 0.0F, false);

    bipedBody = new ModelRenderer(this);
    bipedBody.setTextureOffset(18, 16).addBox(-4.0F, -24.0F, -5.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

    // disable biped legs
    bipedLeftLeg.showModel = false;
    bipedRightLeg.showModel = false;
    
    // spider parts

    spiderNeck = new ModelRenderer(this);
    spiderNeck.setRotationPoint(0.0F, 11.0F, 0.0F);
    spiderNeck.setTextureOffset(0, 32).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    spiderBody = new ModelRenderer(this);
    spiderBody.setRotationPoint(0.0F, 11.0F, 3.0F);
    spiderBody.setTextureOffset(0, 44).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 12.0F, 0.0F, false);

    spiderLeg1 = new ModelRenderer(this);
    spiderLeg1.setRotationPoint(-4.0F, 11.0F, 2.0F);
    spiderLeg1.setTextureOffset(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot1 = new ModelRenderer(this);
    spiderFoot1.setRotationPoint(-13.0F, -1.0F, 0.0F);
    spiderLeg1.addChild(spiderFoot1);
    spiderFoot1.setTextureOffset(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg2 = new ModelRenderer(this);
    spiderLeg2.setRotationPoint(4.0F, 11.0F, 2.0F);
    spiderLeg2.setTextureOffset(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot2 = new ModelRenderer(this);
    spiderFoot2.setRotationPoint(13.0F, -1.0F, 0.0F);
    spiderLeg2.addChild(spiderFoot2);
    spiderFoot2.setTextureOffset(32, 37).addBox(0.0F, 0.0F, -1.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg3 = new ModelRenderer(this);
    spiderLeg3.setRotationPoint(-4.0F, 11.0F, 1.0F);
    spiderLeg3.setTextureOffset(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot3 = new ModelRenderer(this);
    spiderFoot3.setRotationPoint(-13.0F, -1.0F, 0.0F);
    spiderLeg3.addChild(spiderFoot3);
    spiderFoot3.setTextureOffset(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg4 = new ModelRenderer(this);
    spiderLeg4.setRotationPoint(4.0F, 11.0F, 1.0F);
    spiderLeg4.setTextureOffset(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot4 = new ModelRenderer(this);
    spiderFoot4.setRotationPoint(13.0F, -1.0F, 0.0F);
    spiderLeg4.addChild(spiderFoot4);
    spiderFoot4.setTextureOffset(33, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg5 = new ModelRenderer(this);
    spiderLeg5.setRotationPoint(-4.0F, 11.0F, 0.0F);
    spiderLeg5.setTextureOffset(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot5 = new ModelRenderer(this);
    spiderFoot5.setRotationPoint(-13.0F, -1.0F, 0.0F);
    spiderLeg5.addChild(spiderFoot5);
    spiderFoot5.setTextureOffset(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg6 = new ModelRenderer(this);
    spiderLeg6.setRotationPoint(4.0F, 11.0F, 0.0F);
    spiderLeg6.setTextureOffset(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot6 = new ModelRenderer(this);
    spiderFoot6.setRotationPoint(13.0F, -1.0F, 0.0F);
    spiderLeg6.addChild(spiderFoot6);
    spiderFoot6.setTextureOffset(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg7 = new ModelRenderer(this);
    spiderLeg7.setRotationPoint(-4.0F, 11.0F, -1.0F);
    spiderLeg7.setTextureOffset(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot7 = new ModelRenderer(this);
    spiderFoot7.setRotationPoint(-13.0F, -1.0F, 0.0F);
    spiderLeg7.addChild(spiderFoot7);
    spiderFoot7.setTextureOffset(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);

    spiderLeg8 = new ModelRenderer(this);
    spiderLeg8.setRotationPoint(4.0F, 11.0F, -1.0F);
    spiderLeg8.setTextureOffset(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

    spiderFoot8 = new ModelRenderer(this);
    spiderFoot8.setRotationPoint(13.0F, -1.0F, 0.0F);
    spiderLeg8.addChild(spiderFoot8);
    spiderFoot8.setTextureOffset(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);
  }
  
  @Override
  public Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(bipedBody, bipedChest, bipedHeadwear, bipedLeftArm, bipedRightArm, bipedBodyConnector, spiderNeck, spiderBody, spiderLeg1, spiderLeg2, spiderLeg3, spiderLeg4, spiderLeg5, spiderLeg6, spiderLeg7, spiderLeg8); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    // reset biped rotation points
    bipedHead.setRotationPoint(0.0F, 0.0F, -3.0F);
    bipedHeadwear.setRotationPoint(0.0F, 0.0F, -3.0F);
    bipedBody.setRotationPoint(0.0F, 24.0F, 0.0F);
    bipedChest.setRotationPoint(0.01F, 1.0F, -5.0F);
    bipedBodyConnector.setRotationPoint(0.0F, 12.0F, -5.0F);
    bipedLeftArm.setRotationPoint(4.0F, 1.0F, -3.0F);
    bipedRightArm.setRotationPoint(-4.0F, 1.0F, -3.0F);
    // animate spider legs
    final float legFrontBackZ = 0.7853982F;
    final float legMiddleZ = 0.58119464F;
    this.spiderLeg1.rotateAngleZ = -legFrontBackZ;
    this.spiderLeg2.rotateAngleZ = legFrontBackZ;
    this.spiderLeg3.rotateAngleZ = -legMiddleZ;
    this.spiderLeg4.rotateAngleZ = legMiddleZ;
    this.spiderLeg5.rotateAngleZ = -legMiddleZ;
    this.spiderLeg6.rotateAngleZ = legMiddleZ;
    this.spiderLeg7.rotateAngleZ = -legFrontBackZ;
    this.spiderLeg8.rotateAngleZ = legFrontBackZ;
    
    final float legFrontBackY = 0.7853982F;
    final float legMiddleY = 0.3926991F;
    this.spiderLeg1.rotateAngleY = legFrontBackY;
    this.spiderLeg2.rotateAngleY = -legFrontBackY;
    this.spiderLeg3.rotateAngleY = legMiddleY;
    this.spiderLeg4.rotateAngleY = -legMiddleY;
    this.spiderLeg5.rotateAngleY = -legMiddleY;
    this.spiderLeg6.rotateAngleY = legMiddleY;
    this.spiderLeg7.rotateAngleY = -legFrontBackY;
    this.spiderLeg8.rotateAngleY = legFrontBackY;
    
    float leg12Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
    float leg34Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * limbSwingAmount;
    float leg56Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * limbSwingAmount;
    float leg78Y = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 4.712389F) * 0.4F) * limbSwingAmount;
    
    float leg12Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
    float leg34Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 3.1415927F) * 0.4F) * limbSwingAmount;
    float leg56Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 1.5707964F) * 0.4F) * limbSwingAmount;
    float leg78Z = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 4.712389F) * 0.4F) * limbSwingAmount;
    
    this.spiderLeg1.rotateAngleY += leg12Y;
    this.spiderLeg2.rotateAngleY += -leg12Y;
    this.spiderLeg3.rotateAngleY += leg34Y;
    this.spiderLeg4.rotateAngleY += -leg34Y;
    this.spiderLeg5.rotateAngleY += leg56Y;
    this.spiderLeg6.rotateAngleY += -leg56Y;
    this.spiderLeg7.rotateAngleY += leg78Y;
    this.spiderLeg8.rotateAngleY += -leg78Y;
    
    this.spiderLeg1.rotateAngleZ += leg12Z;
    this.spiderLeg2.rotateAngleZ += -leg12Z;
    this.spiderLeg3.rotateAngleZ += leg34Z;
    this.spiderLeg4.rotateAngleZ += -leg34Z;
    this.spiderLeg5.rotateAngleZ += leg56Z;
    this.spiderLeg6.rotateAngleZ += -leg56Z;
    this.spiderLeg7.rotateAngleZ += leg78Z;
    this.spiderLeg8.rotateAngleZ += -leg78Z;
    
    // feet
    final float footFrontBackZ = 1.0472F;
    final float footMiddleZ = 1.0472F;
    this.spiderFoot1.rotateAngleZ = -footFrontBackZ;
    this.spiderFoot2.rotateAngleZ = footFrontBackZ;
    this.spiderFoot3.rotateAngleZ = -footMiddleZ;
    this.spiderFoot4.rotateAngleZ = footMiddleZ;
    this.spiderFoot5.rotateAngleZ = -footMiddleZ;
    this.spiderFoot6.rotateAngleZ = footMiddleZ;
    this.spiderFoot7.rotateAngleZ = -footFrontBackZ;
    this.spiderFoot8.rotateAngleZ = footFrontBackZ;
    
    final float footFrontBackY = 0.523599F;
    final float footMiddleY = 0.0F;
    this.spiderFoot1.rotateAngleY = -footFrontBackY;
    this.spiderFoot2.rotateAngleY = footFrontBackY;
    this.spiderFoot3.rotateAngleY = -footMiddleY;
    this.spiderFoot4.rotateAngleY = footMiddleY;
    this.spiderFoot5.rotateAngleY = -footMiddleY;
    this.spiderFoot6.rotateAngleY = footMiddleY;
    this.spiderFoot7.rotateAngleY = footFrontBackY;
    this.spiderFoot8.rotateAngleY = -footFrontBackY;
  }
  
}
