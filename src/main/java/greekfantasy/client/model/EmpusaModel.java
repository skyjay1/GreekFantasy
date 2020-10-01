package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import greekfantasy.entity.EmpusaEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class EmpusaModel<T extends EmpusaEntity> extends BipedModel<T> {
  
  private final ModelRenderer chest;
  private final ModelRenderer leftWingArm;
  private final ModelRenderer leftWing;
  private final ModelRenderer leftWing2;
  private final ModelRenderer rightWingArm;
  private final ModelRenderer rightWing;
  private final ModelRenderer rightWing2;

  public EmpusaModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);

    // arms
    
    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, -2.0F);
    this.bipedLeftArm.mirror = true;
    
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, -2.0F);
    
    // legs

    this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
    this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
    this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftLeg.mirror = true;
    
    this.bipedRightLeg = new ModelRenderer(this);
    this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
    this.bipedRightLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    
    // chest
    
    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 1.0F, -2.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(0, 32).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);
    
    // wings
    
    leftWingArm = new ModelRenderer(this);
    leftWingArm.setRotationPoint(1.0F, 4.0F, 2.0F);
    leftWingArm.rotateAngleX = 0.0873F;
    leftWingArm.rotateAngleY = 0.3927F;
    leftWingArm.setTextureOffset(47, 33).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, modelSize);
    leftWingArm.mirror = true;

    leftWing = new ModelRenderer(this);
    leftWing.setRotationPoint(3.0F, 0.0F, 4.0F);
    leftWing.setTextureOffset(31, 33).addBox(-4.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, modelSize);
    leftWing.mirror = true;
    leftWingArm.addChild(leftWing);

    leftWing2 = new ModelRenderer(this);
    leftWing2.setRotationPoint(2.0F, -7.0F, 1.0F);
    leftWing2.rotateAngleY = 0.3491F;
    leftWing2.setTextureOffset(46, 43).addBox(0.0F, -3.0F, -1.0F, 8.0F, 20.0F, 1.0F, modelSize);
    leftWing2.mirror = true;
    leftWing.addChild(leftWing2);

    rightWingArm = new ModelRenderer(this);
    rightWingArm.setRotationPoint(-1.0F, 4.0F, 2.0F);
    rightWingArm.rotateAngleX = 0.0873F;
    rightWingArm.rotateAngleY = -0.3927F;
    rightWingArm.setTextureOffset(33, 33).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, modelSize);

    rightWing = new ModelRenderer(this);
    rightWing.setRotationPoint(-2.0F, 0.0F, 4.0F);
    rightWing.setTextureOffset(31, 33).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, 0.0F, true);
    rightWingArm.addChild(rightWing);

    rightWing2 = new ModelRenderer(this);
    rightWing2.setRotationPoint(-3.0F, -8.0F, 1.0F);
    rightWing2.rotateAngleY = -0.3491F;
    rightWing2.setTextureOffset(46, 43).addBox(-8.0F, -2.0F, -1.0F, 8.0F, 20.0F, 1.0F, 0.0F, true);
    rightWing.addChild(rightWing2);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.chest, this.leftWingArm, this.rightWingArm)); }
    
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    if(entity.isDraining()) {
      // extend arms
      this.bipedLeftArm.rotateAngleX = -0.436F;
      this.bipedLeftArm.rotateAngleZ = -0.698F;
      this.bipedRightArm.rotateAngleX = -0.436F;
      this.bipedRightArm.rotateAngleZ = 0.698F;
    }
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    float ticks = entity.getEntityId() * 2 + entity.ticksExisted + partialTick;
    
    final float cosTicks = MathHelper.cos(ticks * 0.1F);
    this.leftWing.rotateAngleY = cosTicks * 0.035F;
    this.leftWing2.rotateAngleY = 0.3491F + cosTicks * 0.05F;
    
    this.rightWing.rotateAngleY = -cosTicks * 0.035F;
    this.rightWing2.rotateAngleY = -0.3491F - cosTicks * 0.05F;    
  }
  
  public ModelRenderer getHeadModel() {
    return this.bipedHead;
  }
  
}
