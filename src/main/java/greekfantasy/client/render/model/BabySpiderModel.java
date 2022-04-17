package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.BabySpiderEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class BabySpiderModel<T extends BabySpiderEntity> extends SegmentedModel<T> {
  
  private final ModelRenderer spiderHead;
  private final ModelRenderer spiderBody;
  private final ModelRenderer spiderLeg1;
  private final ModelRenderer spiderLeg2;
  private final ModelRenderer spiderLeg3;
  private final ModelRenderer spiderLeg4;
  private final ModelRenderer spiderLeg5;
  private final ModelRenderer spiderLeg6;
  private final ModelRenderer spiderLeg7;
  private final ModelRenderer spiderLeg8;
  
  public BabySpiderModel(float modelSize) {
    super();
    texWidth = 32;
    texHeight = 32;

    spiderHead = new ModelRenderer(this);
    spiderHead.setPos(0.0F, 21.0F, -3.0F);
    spiderHead.texOffs(0, 0).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 3.0F, 3.0F, 0.0F, false);

    spiderBody = new ModelRenderer(this);
    spiderBody.setPos(0.0F, 15.0F, 0.0F);
    spiderBody.texOffs(0, 10).addBox(-3.0F, 2.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

    spiderLeg1 = new ModelRenderer(this);
    spiderLeg1.setPos(-4.0F, 21.0F, 2.0F);
//    spiderLeg1.rotateAngleY = 0.7854F;
//    spiderLeg1.rotateAngleZ = -0.7854F;
    spiderLeg1.texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

    spiderLeg2 = new ModelRenderer(this);
    spiderLeg2.setPos(4.0F, 21.0F, 2.0F);
//    spiderLeg2.rotateAngleY = -0.7854F;
//    spiderLeg2.rotateAngleZ = 0.7854F;
    spiderLeg2.texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

    spiderLeg3 = new ModelRenderer(this);
    spiderLeg3.setPos(-4.0F, 21.0F, 1.0F);
//    spiderLeg3.rotateAngleY = 0.2618F;
//    spiderLeg3.rotateAngleZ = -0.6109F;
    spiderLeg3.texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

    spiderLeg4 = new ModelRenderer(this);
    spiderLeg4.setPos(4.0F, 21.0F, 1.0F);
//    spiderLeg4.rotateAngleY = -0.2618F;
//    spiderLeg4.rotateAngleZ = 0.6109F;
    spiderLeg4.texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

    spiderLeg5 = new ModelRenderer(this);
    spiderLeg5.setPos(-4.0F, 21.0F, 0.0F);
//    spiderLeg5.rotateAngleY = -0.2618F;
//    spiderLeg5.rotateAngleZ = -0.6109F;
    spiderLeg5.texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

    spiderLeg6 = new ModelRenderer(this);
    spiderLeg6.setPos(4.0F, 21.0F, 0.0F);
//    spiderLeg6.rotateAngleY = 0.2618F;
//    spiderLeg6.rotateAngleZ = 0.6109F;
    spiderLeg6.texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

    spiderLeg7 = new ModelRenderer(this);
    spiderLeg7.setPos(-4.0F, 21.0F, -1.0F);
//    spiderLeg7.rotateAngleY = -0.7854F;
//    spiderLeg7.rotateAngleZ = -0.7854F;
    spiderLeg7.texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);

    spiderLeg8 = new ModelRenderer(this);
    spiderLeg8.setPos(4.0F, 21.0F, -1.0F);
//    spiderLeg8.rotateAngleY = 0.7854F;
//    spiderLeg8.rotateAngleZ = 0.7854F;
    spiderLeg8.texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, 0.0F, false);
  }
  
  @Override
  public Iterable<ModelRenderer> parts() { return ImmutableList.of(spiderHead, spiderBody, spiderLeg1, spiderLeg2, spiderLeg3, spiderLeg4, spiderLeg5, spiderLeg6, spiderLeg7, spiderLeg8); }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    // head rotations
    this.spiderHead.xRot = headPitch * 0.017453292F;
    this.spiderHead.yRot = netHeadYaw * 0.017453292F;
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
  }  
}
