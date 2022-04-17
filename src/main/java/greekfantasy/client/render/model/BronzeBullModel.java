package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import greekfantasy.entity.BronzeBullEntity;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class BronzeBullModel<T extends BronzeBullEntity> extends QuadrupedModel<T> {
  private final ModelRenderer mouth;
  protected ModelRenderer tail;
  protected ModelRenderer tail2;

  public BronzeBullModel() {
    super(0, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
    texWidth = 128;
    texHeight = 64;

    body = new ModelRenderer(this);
    body.setPos(0.0F, 5.0F, 2.0F);
    body.xRot = 1.5708F;
    body.texOffs(0, 21).addBox(-7.0F, -15.0F, -4.0F, 14.0F, 16.0F, 18.0F, 0.0F, false);
    body.texOffs(65, 26).addBox(-6.0F, 1.0F, -4.0F, 12.0F, 13.0F, 16.0F, 0.0F, false);

    head = new ModelRenderer(this);
    head.setPos(0.0F, -3.0F, -13.0F);
    head.texOffs(0, 0).addBox(-5.0F, -5.0F, -8.0F, 10.0F, 12.0F, 8.0F, 0.0F, false);
    head.texOffs(29, 0).addBox(-3.0F, 2.0F, -10.0F, 6.0F, 5.0F, 2.0F, 0.0F, false);

    mouth = new ModelRenderer(this);
    mouth.setPos(0.0F, 7.0F, 0.0F);
    head.addChild(mouth);
    mouth.texOffs(47, 28).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 2.0F, 8.0F, 0.0F, false);

    // horns
    
    ModelRenderer hornLeft = new ModelRenderer(this);
    hornLeft.setPos(4.0F, -3.0F, -5.0F);
    hornLeft.xRot = 1.3963F;
    hornLeft.yRot = -1.0472F;
    hornLeft.texOffs(47, 17).addBox(-1.0F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, 0.0F, false);
    head.addChild(hornLeft);

    ModelRenderer hornLeft2 = new ModelRenderer(this);
    hornLeft2.setPos(1.0F, -6.0F, -2.0F);
    hornLeft2.xRot = -0.5236F;
    hornLeft2.texOffs(48, 8).addBox(-2.01F, -5.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);
    hornLeft.addChild(hornLeft2);

    ModelRenderer hornLeft3 = new ModelRenderer(this);
    hornLeft3.setPos(0.0F, -5.0F, 0.0F);
    hornLeft3.xRot = -0.5236F;
    hornLeft3.texOffs(49, 0).addBox(-1.5F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
    hornLeft2.addChild(hornLeft3);

    ModelRenderer hornRight1 = new ModelRenderer(this);
    hornRight1.setPos(-4.0F, -3.0F, -5.0F);
    hornRight1.xRot = 1.3963F;
    hornRight1.yRot = 1.0472F;
    hornRight1.texOffs(47, 17).addBox(-2.0F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, 0.0F, false);
    head.addChild(hornRight1);

    ModelRenderer hornRight2 = new ModelRenderer(this);
    hornRight2.setPos(1.0F, -6.0F, -2.0F);
    hornRight1.addChild(hornRight2);
    hornRight2.xRot = -0.5236F;
    hornRight2.texOffs(48, 8).addBox(-2.99F, -5.0F, 0.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);

    ModelRenderer hornRight3 = new ModelRenderer(this);
    hornRight3.setPos(-1.0F, -5.0F, 0.0F);
    hornRight2.addChild(hornRight3);
    hornRight3.xRot = -0.5236F;
    hornRight3.texOffs(49, 0).addBox(-1.5F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

    // legs
    
    leg2 = new ModelRenderer(this);
    leg2.setPos(-6.0F, 6.0F, 12.0F);
    leg2.texOffs(91, 0).addBox(-2.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, 0.0F, true);

    leg0 = new ModelRenderer(this);
    leg0.setPos(6.0F, 6.0F, 12.0F);
    leg0.texOffs(91, 0).addBox(-4.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, 0.0F, false);

    leg3 = new ModelRenderer(this);
    leg3.setPos(-7.0F, 6.0F, -8.0F);
    leg3.texOffs(62, 0).addBox(-2.0F, 0.0F, -3.0F, 7.0F, 18.0F, 7.0F, 0.0F, true);

    leg1 = new ModelRenderer(this);
    leg1.setPos(7.0F, 6.0F, -8.0F);
    leg1.texOffs(62, 0).addBox(-5.0F, 0.0F, -3.0F, 7.0F, 18.0F, 7.0F, 0.0F, false);

    tail = new ModelRenderer(this);
    tail.setPos(0.0F, -6.0F, 16.0F);
    tail.texOffs(116, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

    tail2 = new ModelRenderer(this);
    tail2.setPos(0.0F, 8.0F, -2.0F);
    tail2.texOffs(116, 11).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
    tail2.texOffs(116, 20).addBox(-1.5F, 3.0F, -0.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
    tail.addChild(tail2);
  }

  @Override
  protected Iterable<ModelRenderer> bodyParts() { return Iterables.concat(super.bodyParts(), ImmutableList.of(tail)); }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    float goringPercent = entity.getGoringPercent(1.0F);
    if(goringPercent > 0) {
      // head animation
      head.xRot += 0.5F;
      head.yRot += MathHelper.cos(goringPercent * (float)Math.PI * 10.0F) * 0.4F;
      body.xRot += 0.25F;
      body.setPos(0.0F, 7.0F, 2.0F);
      head.setPos(0.0F, 0.0F, -16.0F);
      tail.setPos(0.0F, -8.0F, 13.0F);
    } else {
      body.setPos(0.0F, 5.0F, 2.0F);
      head.setPos(0.0F, -3.0F, -13.0F);
      tail.setPos(0.0F, -6.0F, 16.0F);
      head.zRot = 0.0F;
      head.yRot = 0.0F;
      body.xRot = 1.5708F;
    }
    // mouth animation
    float firingPercent = entity.getFiringPercent(1.0F);
    if(firingPercent > 0) {
      mouth.xRot = 0.56F;
    } else {
      mouth.xRot = 0;
    }
  }
  
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    // goring animation
    float goringPercent = entity.getGoringPercent(partialTick);
    if(goringPercent > 0) {
      head.zRot = MathHelper.cos(goringPercent * (float)Math.PI * 16.0F) * 0.44F;
    }
    // tail animation
    float limbSwingCos = MathHelper.cos(limbSwing) * limbSwingAmount;
    float idleSwing = 0.1F * MathHelper.cos((entity.tickCount + partialTick) * 0.08F);
    float tailSwing = 0.42F * limbSwingCos;
    tail.xRot = 0.5236F + tailSwing;
    tail2.xRot = 0.2618F + tailSwing * 0.6F;
    tail.zRot = idleSwing;
    tail2.zRot = idleSwing * 0.85F;
    body.zRot = limbSwingCos * 0.08F;
  }
  
  
}
