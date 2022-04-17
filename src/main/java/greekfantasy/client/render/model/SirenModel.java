package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.SirenEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class SirenModel<T extends SirenEntity> extends BipedModel<T> {
  private final ModelRenderer chest;
  private final ModelRenderer upperTail;
  private final ModelRenderer midTail;
  private final ModelRenderer lowerTail1;
  private final ModelRenderer lowerTail2;
  
  private final float swimSpeed = 0.14F;

  public SirenModel(final float modelSize) {
    super(modelSize, 0.0F, 64, 64);

    chest = new ModelRenderer(this);
    chest.setPos(0.0F, 1.0F, -2.0F);
    chest.xRot = -0.2182F;
    chest.texOffs(0, 17).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);

    upperTail = new ModelRenderer(this);
    upperTail.setPos(0.0F, 10.0F, 0.0F);
    upperTail.xRot = -0.2618F;
    upperTail.texOffs(0, 32).addBox(-4.01F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, modelSize);

    midTail = new ModelRenderer(this);
    midTail.setPos(0.0F, 6.0F, -2.0F);
    upperTail.addChild(midTail);
    midTail.xRot = 0.5236F;
    midTail.texOffs(0, 46).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 6.0F, 4.0F, modelSize);

    lowerTail1 = new ModelRenderer(this);
    lowerTail1.setPos(0.0F, 6.0F, 0.0F);
    midTail.addChild(lowerTail1);
    lowerTail1.xRot = 0.2618F;
    lowerTail1.texOffs(0, 23).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 4.0F, modelSize);

    lowerTail2 = new ModelRenderer(this);
    lowerTail2.setPos(0.0F, 5.0F, 2.0F);
    lowerTail1.addChild(lowerTail2);
    lowerTail2.texOffs(0, 57).addBox(-5.0F, -2.0F, -0.5F, 10.0F, 6.0F, 1.0F, modelSize);

    this.leftArm = new ModelRenderer(this, 32, 48);
    this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.leftArm.setPos(5.0F, 2.5F, 0.0F);
    this.leftArm.mirror = true;
    
    this.rightArm = new ModelRenderer(this, 40, 16);
    this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.rightArm.setPos(-5.0F, 2.5F, 0.0F);
    
    // hide biped legs
    this.leftLeg.visible = false;
    this.rightLeg.visible = false;
  }
  
  @Override
  protected Iterable<ModelRenderer> bodyParts() { return ImmutableList.of(this.body, this.chest /* TODO use layer */, this.leftArm, this.rightArm, this.upperTail, this.hat); }

  @Override
  public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
      float headPitch) {
    super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    // swimming animation
    final float swimming = entityIn.getSwimmingPercent(ageInTicks);
    if(swimming > 0) {
      final float ticks = ageInTicks + entityIn.getId();
      final float cosTicks = MathHelper.cos(ticks * swimSpeed * 0.75F) * 0.5F + 0.5F;
      final float minX = -0.09F;
      final float maxX = 0.18F;
      final float minZ = 0.52F;
      final float maxZ = 1.08F;
      // animate arms
      rightArm.xRot = (-minX - cosTicks * (maxX - minX)) * swimming;
      rightArm.zRot = (minZ + cosTicks * (maxZ - minZ)) * swimming;
      leftArm.xRot = (-minX - cosTicks * (maxX - minX)) * swimming;
      leftArm.zRot = (-minZ - cosTicks * (maxZ - minZ)) * swimming;
    }
    // singing animation
    if(entityIn.isCharming()) {
      head.xRot = -0.24F;
    }
  }
    
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    if(entity.isSwimming() || entity.isInWater()) {
      final float ticks = entity.tickCount + partialTick + entity.getId();
      final float tailAngle = 0.21F;
      // animate tail
      final float cosTail = MathHelper.cos(ticks * swimSpeed) * tailAngle;
      upperTail.xRot = -0.2618F + cosTail * 0.4F;
      midTail.xRot = 0.5236F + cosTail * 0.6F;
      lowerTail1.xRot = 0.2618F + cosTail * 0.8F;
      lowerTail2.xRot = cosTail * 0.8F;
    }
  }
  
  public void renderChest(T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    this.chest.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}