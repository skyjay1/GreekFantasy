package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import greekfantasy.entity.NemeanLionEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class SphinxModel<T extends NemeanLionEntity> extends BigCatModel<T> {
  
  protected final ModelRenderer headWear;
  protected final ModelRenderer wings;
  protected final ModelRenderer leftWing;
  protected final ModelRenderer leftWing2;
  protected final ModelRenderer leftWing3;
  protected final ModelRenderer rightWing;
  protected final ModelRenderer rightWing2;
  protected final ModelRenderer rightWing3;

  public SphinxModel() {
    super(64, 64);
    
    headWear = new ModelRenderer(this);
    headWear.setRotationPoint(0.0F, 10.0F, -9.0F);
    headWear.setTextureOffset(0, 34).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.25F, false);
    
    wings = new ModelRenderer(this);
    wings.setRotationPoint(0.0F, -2.0F, 4.0F);
    body.addChild(wings);

    leftWing = new ModelRenderer(this);
    leftWing.setRotationPoint(2.0F, 0.0F, 0.0F);
    leftWing.rotateAngleX = 2.618F;
    leftWing.rotateAngleZ = 1.5708F;
    leftWing.setTextureOffset(23, 34).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, true);
    wings.addChild(leftWing);

    leftWing2 = new ModelRenderer(this);
    leftWing2.setRotationPoint(0.0F, 6.0F, 0.0F);
    leftWing2.rotateAngleX = 0.5236F;
    leftWing2.setTextureOffset(23, 41).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    leftWing.addChild(leftWing2);

    leftWing3 = new ModelRenderer(this);
    leftWing3.setRotationPoint(0.0F, 5.0F, 0.0F);
    leftWing3.rotateAngleX = 0.5236F;
    leftWing3.setTextureOffset(23, 47).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    leftWing2.addChild(leftWing3);

    rightWing = new ModelRenderer(this);
    rightWing.setRotationPoint(-2.0F, 0.0F, 0.0F);
    rightWing.rotateAngleX = 0.5236F;
    rightWing.rotateAngleZ = 1.5708F;
    rightWing.setTextureOffset(23, 34).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, true);
    wings.addChild(rightWing);

    rightWing2 = new ModelRenderer(this);
    rightWing2.setRotationPoint(0.0F, 6.0F, 0.0F);
    rightWing2.rotateAngleX = -0.5236F;
    rightWing2.setTextureOffset(23, 41).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    rightWing.addChild(rightWing2);

    rightWing3 = new ModelRenderer(this);
    rightWing3.setRotationPoint(0.0F, 5.0F, 0.0F);
    rightWing3.rotateAngleX = -0.5236F;
    rightWing3.setTextureOffset(23, 47).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    rightWing2.addChild(rightWing3);
  }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    headWear.copyModelAngles(headModel);
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    // animate wings
    final float ticks = entity.getEntityId() * 2 + entity.ticksExisted + partialTick;
    final float cosTicks = MathHelper.cos(ticks * 0.1F);
    // left wing
    leftWing.rotateAngleX = 2.618F + cosTicks * 0.03F;
    leftWing2.rotateAngleX = 0.5236F + cosTicks * 0.03F;
    leftWing3.rotateAngleX = 0.5236F + cosTicks * 0.05F;
    // right wing
    rightWing.rotateAngleX = 0.5236F - cosTicks * 0.03F;
    rightWing2.rotateAngleX = -leftWing2.rotateAngleX;
    rightWing3.rotateAngleX = -leftWing3.rotateAngleX;
  }
  
  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return Iterables.concat(super.getHeadParts(), ImmutableList.of(headWear)); }

  @Override
  protected ModelRenderer makeHeadModel() {
    ModelRenderer head = new ModelRenderer(this);
    head.setRotationPoint(0.0F, 10.0F, -8.0F);
    head.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
    return head;
  }

  @Override
  protected boolean isSitting(T entity) {
    return entity.isSitting();
  }
}
