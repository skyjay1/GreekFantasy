package greekfantasy.client.render.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.FuryEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class FuryModel<T extends FuryEntity> extends BipedModel<T> {
  
  private final ModelRenderer chest;
  private final ModelRenderer leftWingArm;
  private final ModelRenderer leftWing;
  private final ModelRenderer leftWing2;
  private final ModelRenderer rightWingArm;
  private final ModelRenderer rightWing;
  private final ModelRenderer rightWing2;
  
  private final ModelRenderer snakeHair;
  private final ModelRenderer[] snakeHair1;
  private final ModelRenderer[] snakeHair2;
  private final ModelRenderer[] snakeHair3;

  public FuryModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);

    bipedHead = new ModelRenderer(this);
    bipedHead.setRotationPoint(0.0F, 4.0F, 0.0F);
    bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
    
    bipedHeadwear.setRotationPoint(0.0F, 4.0F, 0.0F);

    bipedBody = new ModelRenderer(this);
    bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
    bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 4.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

    bipedRightArm = new ModelRenderer(this);
    bipedRightArm.setRotationPoint(-5.0F, 5.0F, 0.0F);
    bipedRightArm.setTextureOffset(40, 16).addBox(-2.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, false);

    bipedLeftArm = new ModelRenderer(this);
    bipedLeftArm.setRotationPoint(5.0F, 5.0F, 0.0F);
    bipedLeftArm.setTextureOffset(32, 48).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, false);
    bipedLeftArm.mirror = true;

    bipedRightLeg = new ModelRenderer(this);
    bipedRightLeg.setRotationPoint(-2.0F, 16.0F, 0.0F);
    bipedRightLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

    bipedLeftLeg = new ModelRenderer(this);
    bipedLeftLeg.setRotationPoint(2.0F, 16.0F, 0.0F);
    bipedLeftLeg.setTextureOffset(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
    bipedLeftLeg.mirror = true;
    
    // chest
    
    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 5.0F, -2.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(0, 32).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);
    
    // wings
    
    leftWingArm = new ModelRenderer(this);
    leftWingArm.setRotationPoint(1.0F, 8.0F, 2.0F);
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
    leftWing2.setTextureOffset(46, 43).addBox(0.0F, -3.0F, -1.0F, 8.0F, 20.0F, 1.0F, modelSize);
    leftWing2.mirror = true;
    leftWing.addChild(leftWing2);

    rightWingArm = new ModelRenderer(this);
    rightWingArm.setRotationPoint(-1.0F, 8.0F, 2.0F);
    rightWingArm.rotateAngleX = 0.0873F;
    rightWingArm.rotateAngleY = -0.3927F;
    rightWingArm.setTextureOffset(33, 33).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, modelSize);

    rightWing = new ModelRenderer(this);
    rightWing.setRotationPoint(-2.0F, 0.0F, 4.0F);
    rightWing.setTextureOffset(31, 33).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, 0.0F, true);
    rightWingArm.addChild(rightWing);

    rightWing2 = new ModelRenderer(this);
    rightWing2.setRotationPoint(-3.0F, -8.0F, 1.0F);
    rightWing2.setTextureOffset(46, 43).addBox(-8.0F, -2.0F, -1.0F, 8.0F, 20.0F, 1.0F, 0.0F, true);
    rightWing.addChild(rightWing2);
    
    // snake hair
    
    this.snakeHair = new ModelRenderer(this);
    this.snakeHair.setRotationPoint(0.0F, 4.0F, 0.0F);
    snakeHair1 = makeSnakes(3.8F, (float) Math.PI / 6.0F, modelSize).toArray(new ModelRenderer[0]);
    snakeHair2 = makeSnakes(2.25F, (float) Math.PI / 4.0F, modelSize).toArray(new ModelRenderer[0]);
    snakeHair3 = makeSnakes(1.25F, (float) Math.PI / 3.0F, modelSize).toArray(new ModelRenderer[0]);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.chest, this.leftWingArm, this.rightWingArm)); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    bipedHead.setRotationPoint(0.0F, 4.0F, 0.0F);    
    snakeHair.copyModelAngles(this.bipedHead);
    bipedHeadwear.setRotationPoint(0.0F, 4.0F, 0.0F);
    bipedRightArm.setRotationPoint(-5.0F, 5.0F, 0.0F);
    bipedLeftArm.setRotationPoint(5.0F, 5.0F, 0.0F);
    bipedRightLeg.setRotationPoint(-2.0F, 16.0F, 0.0F);
    bipedLeftLeg.setRotationPoint(2.0F, 16.0F, 0.0F);
    final float flyingLeft = 1.0F - entity.flyingTime * 0.74F;
    bipedLeftLeg.rotateAngleX *= flyingLeft;
    bipedRightLeg.rotateAngleX *= flyingLeft;
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    final float flying = (entity.flyingTime > 0.01F && entity.ticksExisted > 5) ? MathHelper.clamp(entity.flyingTime, 0.0F, 1.0F) : 0.0F;
    float ticks = entity.getEntityId() * 2 + entity.ticksExisted + partialTick;
    final float cosTicks = MathHelper.cos(ticks * (0.2F + flying * 0.25F));
    // left wing
    this.leftWing.rotateAngleY = cosTicks * (0.035F + flying * 0.21F);
    this.leftWing2.rotateAngleY = 0.3491F + cosTicks * (0.05F + flying * 0.11F);
    // right wing
    this.rightWing.rotateAngleY = -cosTicks * (0.035F + flying * 0.21F);
    this.rightWing2.rotateAngleY = -0.3491F - cosTicks * (0.05F + flying * 0.11F);
  }

  private void animateSnakes(final ModelRenderer[] list, final float ticks, final float baseAngleX) {
    for(int i = 0, l = list.length; i < l; i++) {
      // update rotation angles
      list[i].rotateAngleX = baseAngleX + (float) Math.cos(ticks * 0.32 + i * 2.89F) * 0.08F;
    }
  }
  
  public void renderSnakeHair(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final int packedLightIn, 
      final int packedOverlayIn, final float ticks, final float aggroTime) {
    // living animations for each list
    animateSnakes(snakeHair1, ticks, 1.7F);
    animateSnakes(snakeHair2, ticks, 1.03F);
    animateSnakes(snakeHair3, ticks, 0.82F);
    // scale the hair before rendering
    matrixStackIn.push();
    this.snakeHair.copyModelAngles(this.bipedHead);
    final float scale = 0.25F + (aggroTime * 0.75F);
    matrixStackIn.scale(scale, scale, scale);
    this.snakeHair.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
    matrixStackIn.pop();
  }
  
  private List<ModelRenderer> makeSnakes(final float radius, final float deltaAngle, final float modelSize) {
    final List<ModelRenderer> list = new ArrayList<>();
    for(double angle = 0.0D, count = 1.0D; angle < Math.PI * 2; angle += deltaAngle) {
      final float ptX = (float) (Math.cos(angle) * radius);
      final float ptZ = (float) (Math.sin(angle) * radius);
      final float angY = (float) (angle - (deltaAngle * 2 * count));
      final ModelRenderer snake = GorgonModel.makeSnake(this, ptX, -8.5F, ptZ, 0, angY, 0, 0, 52);
      list.add(snake);
      this.snakeHair.addChild(snake);
      count++;
    }
    return list;
  }
  
}
