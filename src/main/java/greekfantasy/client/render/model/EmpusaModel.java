package greekfantasy.client.render.model;

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
    
    this.leftArm = new ModelRenderer(this, 32, 48);
    this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.leftArm.setPos(5.0F, 2.5F, -2.0F);
    this.leftArm.mirror = true;
    
    this.rightArm = new ModelRenderer(this, 40, 16);
    this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.rightArm.setPos(-5.0F, 2.5F, -2.0F);
    
    // legs

    this.leftLeg = new ModelRenderer(this, 16, 48);
    this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
    this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    this.leftLeg.mirror = true;
    
    this.rightLeg = new ModelRenderer(this);
    this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
    this.rightLeg.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
    
    // chest
    
    chest = new ModelRenderer(this);
    chest.setPos(0.0F, 1.0F, -2.0F);
    chest.xRot = -0.2182F;
    chest.texOffs(0, 32).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);
    
    // wings
    
    leftWingArm = new ModelRenderer(this);
    leftWingArm.setPos(1.0F, 4.0F, 2.0F);
    leftWingArm.xRot = 0.0873F;
    leftWingArm.yRot = 0.3927F;
    leftWingArm.texOffs(47, 33).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, modelSize);
    leftWingArm.mirror = true;

    leftWing = new ModelRenderer(this);
    leftWing.setPos(3.0F, 0.0F, 4.0F);
    leftWing.texOffs(31, 33).addBox(-4.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, modelSize);
    leftWing.mirror = true;
    leftWingArm.addChild(leftWing);

    leftWing2 = new ModelRenderer(this);
    leftWing2.setPos(2.0F, -7.0F, 1.0F);
    leftWing2.yRot = 0.3491F;
    leftWing2.texOffs(46, 43).addBox(0.0F, -3.0F, -1.0F, 8.0F, 20.0F, 1.0F, modelSize);
    leftWing2.mirror = true;
    leftWing.addChild(leftWing2);

    rightWingArm = new ModelRenderer(this);
    rightWingArm.setPos(-1.0F, 4.0F, 2.0F);
    rightWingArm.xRot = 0.0873F;
    rightWingArm.yRot = -0.3927F;
    rightWingArm.texOffs(33, 33).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, modelSize);

    rightWing = new ModelRenderer(this);
    rightWing.setPos(-2.0F, 0.0F, 4.0F);
    rightWing.texOffs(31, 33).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, 0.0F, true);
    rightWingArm.addChild(rightWing);

    rightWing2 = new ModelRenderer(this);
    rightWing2.setPos(-3.0F, -8.0F, 1.0F);
    rightWing2.yRot = -0.3491F;
    rightWing2.texOffs(46, 43).addBox(-8.0F, -2.0F, -1.0F, 8.0F, 20.0F, 1.0F, 0.0F, true);
    rightWing.addChild(rightWing2);
  }
  
  @Override
  protected Iterable<ModelRenderer> bodyParts() { return Iterables.concat(super.bodyParts(), ImmutableList.of(this.chest, this.leftWingArm, this.rightWingArm)); }
    
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    if(entity.isDraining()) {
      // extend arms
      this.leftArm.xRot = -0.436F;
      this.leftArm.zRot = -0.698F;
      this.rightArm.xRot = -0.436F;
      this.rightArm.zRot = 0.698F;
    }
  }
  
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    float ticks = entity.getId() * 2 + entity.tickCount + partialTick;
    
    final float cosTicks = MathHelper.cos(ticks * 0.1F);
    this.leftWing.yRot = cosTicks * 0.035F;
    this.leftWing2.yRot = 0.3491F + cosTicks * 0.05F;
    
    this.rightWing.yRot = -cosTicks * 0.035F;
    this.rightWing2.yRot = -0.3491F - cosTicks * 0.05F;    
  }
  
  public ModelRenderer getHeadModel() {
    return this.head;
  }
  
}
