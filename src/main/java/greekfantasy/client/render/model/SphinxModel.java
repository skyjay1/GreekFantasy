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
    headWear.setPos(0.0F, 10.0F, -9.0F);
    headWear.texOffs(0, 34).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.25F, false);
    
    wings = new ModelRenderer(this);
    wings.setPos(0.0F, -2.0F, 4.0F);
    body.addChild(wings);

    leftWing = new ModelRenderer(this);
    leftWing.setPos(2.0F, 0.0F, 0.0F);
    leftWing.xRot = 2.618F;
    leftWing.zRot = 1.5708F;
    leftWing.texOffs(23, 34).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, true);
    wings.addChild(leftWing);

    leftWing2 = new ModelRenderer(this);
    leftWing2.setPos(0.0F, 6.0F, 0.0F);
    leftWing2.xRot = 0.5236F;
    leftWing2.texOffs(23, 41).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    leftWing.addChild(leftWing2);

    leftWing3 = new ModelRenderer(this);
    leftWing3.setPos(0.0F, 5.0F, 0.0F);
    leftWing3.xRot = 0.5236F;
    leftWing3.texOffs(23, 47).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    leftWing2.addChild(leftWing3);

    rightWing = new ModelRenderer(this);
    rightWing.setPos(-2.0F, 0.0F, 0.0F);
    rightWing.xRot = 0.5236F;
    rightWing.zRot = 1.5708F;
    rightWing.texOffs(23, 34).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, true);
    wings.addChild(rightWing);

    rightWing2 = new ModelRenderer(this);
    rightWing2.setPos(0.0F, 6.0F, 0.0F);
    rightWing2.xRot = -0.5236F;
    rightWing2.texOffs(23, 41).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    rightWing.addChild(rightWing2);

    rightWing3 = new ModelRenderer(this);
    rightWing3.setPos(0.0F, 5.0F, 0.0F);
    rightWing3.xRot = -0.5236F;
    rightWing3.texOffs(23, 47).addBox(-5.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, true);
    rightWing2.addChild(rightWing3);
  }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    headWear.copyFrom(head);
  }
  
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    // animate wings
    final float ticks = entity.getId() * 2 + entity.tickCount + partialTick;
    final float cosTicks = MathHelper.cos(ticks * 0.1F);
    // left wing
    leftWing.xRot = 2.618F + cosTicks * 0.03F;
    leftWing2.xRot = 0.5236F + cosTicks * 0.03F;
    leftWing3.xRot = 0.5236F + cosTicks * 0.05F;
    // right wing
    rightWing.xRot = 0.5236F - cosTicks * 0.03F;
    rightWing2.xRot = -leftWing2.xRot;
    rightWing3.xRot = -leftWing3.xRot;
  }
  
  @Override
  protected Iterable<ModelRenderer> headParts() { return Iterables.concat(super.headParts(), ImmutableList.of(headWear)); }

  @Override
  protected ModelRenderer makeHeadModel() {
    ModelRenderer head = new ModelRenderer(this);
    head.setPos(0.0F, 10.0F, -8.0F);
    head.texOffs(0, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
    return head;
  }

  @Override
  protected boolean isSitting(T entity) {
    return entity.isSitting();
  }
}
