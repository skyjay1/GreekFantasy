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
    this.head.setRotationPoint(-1.0F, 13.5F, -7.0F);
    
    this.head.addChild(getHeadModel(this, 4.0F, 0.0F, 2.0F, -0.42F));
    this.head.addChild(getHeadModel(this, -1.0F, 0.0F, 2.0F, 0.42F));
    
    this.body = new ModelRenderer(this, 18, 14);
    this.body.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, modelSize);
    this.body.setRotationPoint(0.0F, 14.0F, 2.0F);
    this.body.rotateAngleX = 1.5707964F;
    
    this.mane = new ModelRenderer(this, 21, 0);
    this.mane.addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, modelSize);
    this.mane.setRotationPoint(-1.0F, 14.0F, -3.0F);
    this.mane.rotateAngleX = this.body.rotateAngleX;
    
    this.legBackRight = new ModelRenderer(this, 0, 18);
    this.legBackRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legBackRight.setRotationPoint(-2.5F, 16.0F, 7.0F);
    
    this.legBackLeft = new ModelRenderer(this, 0, 18);
    this.legBackLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legBackLeft.setRotationPoint(0.5F, 16.0F, 7.0F);
    
    this.legFrontRight = new ModelRenderer(this, 0, 18);
    this.legFrontRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legFrontRight.setRotationPoint(-2.5F, 16.0F, -4.0F);
    
    this.legFrontLeft = new ModelRenderer(this, 0, 18);
    this.legFrontLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.legFrontLeft.setRotationPoint(0.5F, 16.0F, -4.0F);
    
    this.tail = new ModelRenderer(this, 9, 18);
    this.tail.setRotationPoint(-1.0F, 12.0F, 8.0F);
    this.tailChild = new ModelRenderer(this, 9, 18);
    this.tailChild.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, modelSize);
    this.tail.addChild(this.tailChild);
  }
  
  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return ImmutableList.of(this.head); }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.tail, this.mane); }

  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
      this.tail.rotateAngleY = -0.8F + MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

      this.body.rotateAngleX = 1.5707964F;
      this.mane.rotateAngleX = this.body.rotateAngleX;

      this.legBackRight.setRotationPoint(-2.5F, 16.0F, 7.0F);
      this.legBackLeft.setRotationPoint(0.5F, 16.0F, 7.0F);
      this.legFrontRight.setRotationPoint(-2.5F, 16.0F, -4.0F);
      this.legFrontLeft.setRotationPoint(0.5F, 16.0F, -4.0F);
      
      this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
      this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
      this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
  }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    this.head.rotateAngleX = rotationPitch * 0.017453292F;
    this.head.rotateAngleY = rotationYaw * 0.017453292F;
  }
  
  public static ModelRenderer getHeadModel(final Model model, final float rotX, final float rotY, final float rotZ,
      final float angleY) {
    final ModelRenderer head = new ModelRenderer(model);
    head.setRotationPoint(rotX, rotY, rotZ);
    head.setTextureOffset(0, 0).addBox(-4.0F, -3.0F, -4.0F, 6.0F, 6.0F, 4.0F, 0.0F);
    head.setTextureOffset(16, 14).addBox(-4.0F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    head.setTextureOffset(16, 14).addBox(0.0F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    head.setTextureOffset(0, 10).addBox(-2.5F, -0.012F, -7.0F, 3.0F, 3.0F, 4.0F, 0.0F);
    head.rotateAngleY = angleY;
    return head;
  }
}
