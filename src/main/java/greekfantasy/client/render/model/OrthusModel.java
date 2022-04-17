package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.OrthusEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class OrthusModel<T extends OrthusEntity> extends AgeableModel<T> {
  
  private final ModelRenderer head;
  //private final ModelRenderer leftHead;
  //private final ModelRenderer rightHead;

  private final ModelRenderer body;
  private final ModelRenderer mane;
  private final ModelRenderer legBackRight;
  private final ModelRenderer legBackLeft;
  private final ModelRenderer legFrontRight; 
  private final ModelRenderer legFrontLeft; 
  private final ModelRenderer tail; 
  private final ModelRenderer tailChild;
  
  public OrthusModel(final float modelSize) {
    super();
    this.head = new ModelRenderer(this, 0, 0);
    this.head.setPos(-1.0F, 13.5F, -7.0F);
    
    this.head.addChild(getHeadModel(this, 4.0F, 0.0F, 2.0F, -0.42F));
    this.head.addChild(getHeadModel(this, -1.0F, 0.0F, 2.0F, 0.42F));
    
    this.body = new ModelRenderer(this, 18, 14);
    this.body.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, modelSize);
    this.body.setPos(0.0F, 14.0F, 2.0F);
    this.body.xRot = 1.5707964F;
    
    this.mane = new ModelRenderer(this, 21, 0);
    this.mane.addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, modelSize);
    this.mane.setPos(-1.0F, 14.0F, -3.0F);
    this.mane.xRot = this.body.xRot;
    
    this.legBackRight = new ModelRenderer(this, 0, 18);
    this.legBackRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legBackRight.setPos(-2.5F, 16.0F, 7.0F);
    
    this.legBackLeft = new ModelRenderer(this, 0, 18);
    this.legBackLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legBackLeft.setPos(0.5F, 16.0F, 7.0F);
    
    this.legFrontRight = new ModelRenderer(this, 0, 18);
    this.legFrontRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legFrontRight.setPos(-2.5F, 16.0F, -4.0F);
    
    this.legFrontLeft = new ModelRenderer(this, 0, 18);
    this.legFrontLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legFrontLeft.setPos(0.5F, 16.0F, -4.0F);
    
    this.tail = new ModelRenderer(this, 9, 18);
    this.tail.setPos(-1.0F, 12.0F, 8.0F);
    this.tailChild = new ModelRenderer(this, 9, 18);
    this.tailChild.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.tail.addChild(this.tailChild);
  }
  
  @Override
  protected Iterable<ModelRenderer> headParts() { return ImmutableList.of(this.head); }
  
  @Override
  protected Iterable<ModelRenderer> bodyParts() { return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.tail, this.mane); }

  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    // tail
    if (entity.isAngry()) {
      this.tail.yRot = 0.0F;
    } else {
      this.tail.yRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
    // sitting / walking
    if (entity.isInSittingPose() || entity.isOrderedToSit()) {
      this.mane.setPos(-1.0F, 16.0F, -3.0F);
      this.mane.xRot = 1.2566371F;
      this.mane.yRot = 0.0F;
      
      this.body.setPos(0.0F, 18.0F, 0.0F);
      this.body.xRot = 0.7853982F;
      
      this.tail.setPos(-1.0F, 21.0F, 6.0F);
      
      this.legBackRight.setPos(-2.5F, 22.7F, 2.0F);
      this.legBackRight.xRot = 4.712389F;
      this.legBackLeft.setPos(0.5F, 22.7F, 2.0F);
      this.legBackLeft.xRot = 4.712389F;
      
      this.legFrontRight.xRot = 5.811947F;
      this.legFrontRight.setPos(-2.49F, 17.0F, -4.0F);
      this.legFrontLeft.xRot = 5.811947F;
      this.legFrontLeft.setPos(0.51F, 17.0F, -4.0F);
    } else {
      this.body.setPos(0.0F, 14.0F, 2.0F);
      this.body.xRot = 1.5707964F;
      this.mane.setPos(-1.0F, 14.0F, -3.0F);
      this.mane.xRot = this.body.xRot;
      
      this.tail.setPos(-1.0F, 12.0F, 8.0F);      

      this.legBackRight.setPos(-2.5F, 16.0F, 7.0F);
      this.legBackLeft.setPos(0.5F, 16.0F, 7.0F);
      this.legFrontRight.setPos(-2.5F, 16.0F, -4.0F);
      this.legFrontLeft.setPos(0.5F, 16.0F, -4.0F);
      
      this.legBackRight.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.legBackLeft.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
      this.legFrontRight.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
      this.legFrontLeft.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    this.head.xRot = rotationPitch * 0.017453292F;
    this.head.yRot = rotationYaw * 0.017453292F;
    this.tail.xRot = 0.4F + 0.8F * limbSwingAmount;
  }
  
  public static ModelRenderer getHeadModel(final Model model, final float rotX, final float rotY, final float rotZ,
      final float angleY) {
    final ModelRenderer head = new ModelRenderer(model);
    head.setPos(rotX, rotY, rotZ);
    head.texOffs(0, 0).addBox(-4.0F, -3.0F, -4.0F, 6.0F, 6.0F, 4.0F, 0.0F);
    head.texOffs(16, 14).addBox(-4.0F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    head.texOffs(16, 14).addBox(0.0F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    head.texOffs(0, 10).addBox(-2.5F, -0.012F, -7.0F, 3.0F, 3.0F, 4.0F, 0.0F);
    head.yRot = angleY;
    return head;
  }
}
