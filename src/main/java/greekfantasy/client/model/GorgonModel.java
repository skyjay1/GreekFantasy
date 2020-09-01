package greekfantasy.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.GorgonEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class GorgonModel<T extends GorgonEntity> extends BipedModel<T> {
  private final ModelRenderer chest;
  private final ModelRenderer upperTail;
  private final ModelRenderer midTail;
  private final ModelRenderer lowerTail;
  private final ModelRenderer lowerTail1;
  private final ModelRenderer lowerTail2;
  private final ModelRenderer lowerTail3;
  
  private final float snakeHair1Y = 1.7F;
  private final float snakeHair2Y = 1.03F;
  private final float snakeHair3Y = 0.62F;
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
    setRotationAngle(upperTail, -0.5236F, 0.0F, 0.0F);
    upperTail.setTextureOffset(0, 32).addBox(-3.999F, 0.0F, -4.0F, 8.0F, 10.0F, 4.0F, modelSize);

    midTail = new ModelRenderer(this);
    midTail.setRotationPoint(0.0F, 10.0F, -4.0F);
    upperTail.addChild(midTail);
    setRotationAngle(midTail, 1.0472F, 0.0F, 0.0F);
    midTail.setTextureOffset(0, 46).addBox(-4.001F, 0.0F, 0.0F, 8.0F, 6.0F, 4.0F, modelSize);

    lowerTail = new ModelRenderer(this);
    lowerTail.setRotationPoint(0.0F, 6.0F, 0.0F);
    midTail.addChild(lowerTail);
    lowerTail.rotateAngleX = 1.0472F;

    lowerTail1 = new ModelRenderer(this);
    lowerTail1.setRotationPoint(0.0F, 2.0F, 0.0F);
    lowerTail.addChild(lowerTail1);
    setRotationAngle(lowerTail1, 0.0F, 0.0F, -0.2618F);
    lowerTail1.setTextureOffset(25, 32).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 8.0F, 4.0F, modelSize);

    lowerTail2 = new ModelRenderer(this);
    lowerTail2.setRotationPoint(0.0F, 6.0F, 0.0F);
    lowerTail1.addChild(lowerTail2);
    setRotationAngle(lowerTail2, 0.0F, 0.0F, 0.5236F);
    lowerTail2.setTextureOffset(46, 32).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 8.0F, 3.0F, modelSize);

    lowerTail3 = new ModelRenderer(this);
    lowerTail3.setRotationPoint(0.0F, 7.0F, 0.0F);
    lowerTail2.addChild(lowerTail3);
    setRotationAngle(lowerTail3, 0.0F, 0.0F, -0.5236F);
    lowerTail3.setTextureOffset(46, 43).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 6.0F, 2.0F, modelSize);

    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, -6.0F);
    this.bipedLeftArm.mirror = true;
    
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, -6.0F);
    
    // disable biped legs
    bipedLeftLeg.showModel = false;
    bipedRightLeg.showModel = false;
    
    // snake hair
    // snakeHair1.setRotationPoint(-0.5F, -8.0F, 1.0F);
    // bipedHead.addChild(snakeHair1);
    // setRotationAngle(snakeHair1, 0.5236F, 0.2618F, -0.3491F);
//    for(double a = 0.0D, r = 3.85D, s = Math.PI / 6.0D; a < Math.PI * 2; a += s) {
//      final float ptX = (float) (Math.cos(a) * r);
//      final float ptZ = (float) (Math.sin(a) * r);
//      final float angY = (float) (a - (s * 2) * (snakeHair.size() + 1));
//      final ModelRenderer snake = makeSnake(modelSize, ptX, -8.0F, ptZ, 0, angY, 0);
//      snakeHair.add(snake);
//      bipedHead.addChild(snake);
//    }
    makeSnakes(snakeHair1, 3.8F, (float) Math.PI / 6.0F, modelSize);
    makeSnakes(snakeHair2, 2.25F, (float) Math.PI / 4.0F, modelSize);
    makeSnakes(snakeHair3, 1.25F, (float) Math.PI / 3.0F, modelSize);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.bipedBody, this.bipedLeftArm, this.bipedRightArm, this.chest, this.upperTail, this.bipedHeadwear); }

  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    float ticks = entity.ticksExisted + partialTick;

    // animate snake hair
    for(int i = 0, l = snakeHair1.size(); i < l && snakeHair1.get(i).showModel; i++) {
      snakeHair1.get(i).rotateAngleX = snakeHair1Y + (float) Math.cos((ticks + i * 4) * 0.15) * 0.08F;
    }
    for(int i = 0, l = snakeHair2.size(); i < l && snakeHair2.get(i).showModel; i++) {
      snakeHair2.get(i).rotateAngleX = snakeHair2Y + (float) Math.cos((ticks + i * 4) * 0.15) * 0.08F;
    }
    for(int i = 0, l = snakeHair3.size(); i < l && snakeHair3.get(i).showModel; i++) {
      snakeHair3.get(i).rotateAngleX = snakeHair3Y + (float) Math.cos((ticks + i * 4) * 0.15) * 0.08F;
    }
    // animate snake body
    final float limbSwingCos = (float) Math.cos(limbSwing);
    upperTail.rotateAngleY = limbSwingCos * 0.1F;
    lowerTail1.rotateAngleZ = limbSwingCos * 0.67F;
    lowerTail2.rotateAngleZ = limbSwingCos * -0.75F;
    lowerTail3.rotateAngleZ = limbSwingCos * 0.4F;
    
  }
  
  public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
  
  public void setSnakeHairVisibility(final boolean visible) {
    for(final ModelRenderer r : this.snakeHair1) {
      r.showModel = visible;
    }
    for(final ModelRenderer r : this.snakeHair2) {
      r.showModel = visible;
    }
    for(final ModelRenderer r : this.snakeHair3) {
      r.showModel = visible;
    }
  }
  
  private void makeSnakes(final List<ModelRenderer> list, final float radius, final float deltaAngle, final float modelSize) {
    for(double angle = 0.0D; angle < Math.PI * 2; angle += deltaAngle) {
      final float ptX = (float) (Math.cos(angle) * radius);
      final float ptZ = (float) (Math.sin(angle) * radius);
      final float angY = (float) (angle - (deltaAngle * 2) * (list.size() + 1));
      final ModelRenderer snake = makeSnake(modelSize, ptX, -8.0F, ptZ, 0, angY, 0);
      list.add(snake);
      bipedHead.addChild(snake);
    }
  }
  
  private ModelRenderer makeSnake(final float modelSize, final float rotX, final float rotY, final float rotZ, 
      final float angleX, final float angleY, final float angleZ) {
    final ModelRenderer snakeHair1 = new ModelRenderer(this);
    snakeHair1.setRotationPoint(rotX, rotY, rotZ);
    setRotationAngle(snakeHair1, angleX, angleY, angleZ);
    snakeHair1.setTextureOffset(46, 52).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, modelSize);

    final ModelRenderer snakeHair2 = new ModelRenderer(this);
    snakeHair2.setRotationPoint(0.0F, -3.0F, 0.0F);
    snakeHair1.addChild(snakeHair2);
    setRotationAngle(snakeHair2, 0.5236F, 0.0F, 0.0F);
    snakeHair2.setTextureOffset(46, 56).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, modelSize);

    final ModelRenderer snakeHair3 = new ModelRenderer(this);
    snakeHair3.setRotationPoint(0.0F, -3.0F, -0.5F);
    snakeHair2.addChild(snakeHair3);
    setRotationAngle(snakeHair3, 0.5236F, 0.0F, 0.0F);
    snakeHair3.setTextureOffset(46, 60).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, modelSize);
    
    return snakeHair1;
  }
}
