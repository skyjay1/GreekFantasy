package greekfantasy.client.render.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.GorgonEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

public class GorgonModel<T extends GorgonEntity> extends BipedModel<T> {
  private final ModelRenderer chest;
  private final ModelRenderer upperTail;
  private final ModelRenderer midTail;
  private final ModelRenderer lowerTail;
  private final ModelRenderer lowerTail1;
  private final ModelRenderer lowerTail2;
  private final ModelRenderer lowerTail3;
  
  private final ModelRenderer snakeHair;
  private final List<ModelRenderer> snakeHair1 = new ArrayList<>();
  private final List<ModelRenderer> snakeHair2 = new ArrayList<>();
  private final List<ModelRenderer> snakeHair3 = new ArrayList<>();

  public GorgonModel(final float modelSize) {
    super(modelSize);
    textureWidth = 64;
    textureHeight = 64;

    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, 0.0F, -2.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize);
    
    bipedHeadwear.showModel = false;

    bipedBody = new ModelRenderer(this);
    bipedBody.setRotationPoint(0.0F, 24.0F, 0.0F);
    bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 4.0F, modelSize);

    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 1.0F, -4.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(0, 17).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);

    upperTail = new ModelRenderer(this);
    upperTail.setRotationPoint(0.0F, 12.0F, 0.0F);
    upperTail.rotateAngleX = -0.5236F;
    upperTail.setTextureOffset(0, 32).addBox(-3.999F, 0.0F, -4.0F, 8.0F, 10.0F, 4.0F, modelSize);

    midTail = new ModelRenderer(this);
    midTail.setRotationPoint(0.0F, 10.0F, -4.0F);
    upperTail.addChild(midTail);
    midTail.rotateAngleX = 1.0472F;
    midTail.setTextureOffset(0, 46).addBox(-4.001F, 0.0F, 0.0F, 8.0F, 6.0F, 4.0F, modelSize);

    lowerTail = new ModelRenderer(this);
    lowerTail.setRotationPoint(0.0F, 6.0F, 0.0F);
    midTail.addChild(lowerTail);
    lowerTail.rotateAngleX = 1.0472F;

    lowerTail1 = new ModelRenderer(this);
    lowerTail1.setRotationPoint(0.0F, 2.0F, 0.0F);
    lowerTail.addChild(lowerTail1);
    lowerTail1.setTextureOffset(25, 32).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 8.0F, 4.0F, modelSize);

    lowerTail2 = new ModelRenderer(this);
    lowerTail2.setRotationPoint(0.0F, 6.0F, 0.0F);
    lowerTail1.addChild(lowerTail2);
    lowerTail2.setTextureOffset(46, 32).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 8.0F, 3.0F, modelSize);

    lowerTail3 = new ModelRenderer(this);
    lowerTail3.setRotationPoint(0.0F, 7.0F, 0.0F);
    lowerTail2.addChild(lowerTail3);
    lowerTail3.setTextureOffset(46, 43).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 6.0F, 2.0F, modelSize);

    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftArm.setRotationPoint(4.0F, 2.0F, -2.0F);
    this.bipedLeftArm.mirror = true;
    
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedRightArm.setRotationPoint(-4.0F, 2.0F, -2.0F);
    
    // disable biped legs
    bipedLeftLeg.showModel = false;
    bipedRightLeg.showModel = false;
    
    this.snakeHair = new ModelRenderer(this);
    this.snakeHair.setRotationPoint(0.0F, 0.0F, 0.0F);
  
    makeSnakes(snakeHair1, 3.8F, (float) Math.PI / 6.0F, modelSize);
    makeSnakes(snakeHair2, 2.25F, (float) Math.PI / 4.0F, modelSize);
    makeSnakes(snakeHair3, 1.25F, (float) Math.PI / 3.0F, modelSize);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.bipedLeftArm, this.bipedRightArm, this.chest, this.upperTail, this.bipedHeadwear); }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    // set arm poses
    final ItemStack item = entity.getHeldItem(Hand.MAIN_HAND);
    if (entity.isMedusa() && item.getItem() instanceof net.minecraft.item.BowItem && entity.isAggressive()) {
       if (entity.getPrimaryHand() == HandSide.RIGHT) {
          this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       } else {
          this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
       }
    } else {
      this.rightArmPose = this.leftArmPose = BipedModel.ArmPose.EMPTY;
    }
    // super method
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    bipedLeftArm.rotationPointZ = -2.0F;
    bipedRightArm.rotationPointZ = -2.0F;
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    // animate snake body
    final float limbSwingCos = (float) Math.cos(limbSwing);
    upperTail.rotateAngleY = limbSwingCos * 0.1F;
    lowerTail1.rotateAngleZ = limbSwingCos * 0.67F;
    lowerTail2.rotateAngleZ = limbSwingCos * -0.75F;
    lowerTail3.rotateAngleZ = limbSwingCos * 0.4F;
  }
  
  public void renderSnakeHair(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final int packedLightIn, 
      final int packedOverlayIn, final float ticks, final float colorAlpha) {
    // living animations for each list
    animateSnakes(snakeHair1, ticks, 1.7F);
    animateSnakes(snakeHair2, ticks, 1.03F);
    animateSnakes(snakeHair3, ticks, 0.82F);
    // render each list
    this.snakeHair.copyModelAngles(this.bipedHead);
    this.snakeHair.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, colorAlpha);
  }
  
  private void animateSnakes(final List<ModelRenderer> list, final float ticks, final float baseAngleX) {
    int i = 0;
    for(final ModelRenderer m : list) {
      // update rotation angles
      m.rotateAngleX = baseAngleX + (float) Math.cos(ticks * 0.15 + i * 2.89F) * 0.08F;
      i++;
    }
  }
  
  private void makeSnakes(final List<ModelRenderer> list, final float radius, final float deltaAngle, final float modelSize) {
    for(double angle = 0.0D, count = 1.0D; angle < Math.PI * 2; angle += deltaAngle) {
      final float ptX = (float) (Math.cos(angle) * radius);
      final float ptZ = (float) (Math.sin(angle) * radius);
      final float angY = (float) (angle - (deltaAngle * 2 * count));
      final ModelRenderer snake = makeSnake(this, ptX, -8.5F, ptZ, 0, angY, 0, 46, 52);
      list.add(snake);
      this.snakeHair.addChild(snake);
      count++;
    }
  }
  
  public static ModelRenderer makeSnake(final Model model, final float rotX, final float rotY, final float rotZ, 
      final float angleX, final float angleY, final float angleZ, final int textureX, final int textureY) {
    final ModelRenderer snakeHair1 = new ModelRenderer(model);
    snakeHair1.setRotationPoint(rotX, rotY, rotZ);
    snakeHair1.rotateAngleX = angleX;
    snakeHair1.rotateAngleY = angleY;
    snakeHair1.rotateAngleZ = angleZ;
    snakeHair1.setTextureOffset(textureX, textureY).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F);

    final ModelRenderer snakeHair2 = new ModelRenderer(model);
    snakeHair2.setRotationPoint(0.0F, -3.0F, 0.0F);
    snakeHair1.addChild(snakeHair2);
    snakeHair2.rotateAngleX = 0.5236F;
    snakeHair2.setTextureOffset(textureX, textureY + 4).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F);

    final ModelRenderer snakeHair3 = new ModelRenderer(model);
    snakeHair3.setRotationPoint(0.0F, -3.0F, -0.5F);
    snakeHair2.addChild(snakeHair3);
    snakeHair3.rotateAngleX = 0.5236F;
    snakeHair3.setTextureOffset(textureX, textureY + 8).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F);
    
    return snakeHair1;
  }
}
