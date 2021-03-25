package greekfantasy.client.render.model;

import greekfantasy.entity.NemeanLionEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class NemeanLionModel<T extends NemeanLionEntity> extends BigCatModel<T> {
  
  protected final ModelRenderer mouth;

  public NemeanLionModel() {
    super(64, 64);
    body.setTextureOffset(0, 34).addBox(-5.0F, -11.0F, -4.0F, 10.0F, 4.0F, 10.0F, 0.0F, false);
    
    mouth = new ModelRenderer(this);
    mouth.setRotationPoint(0.0F, 3.0F, -3.0F);
    mouth.rotateAngleX = 0.5236F;
    mouth.setTextureOffset(15, 14).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 1.0F, 2.0F, 0.0F, false);
    headModel.addChild(mouth);
  }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
  }
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    final float idleSwingCos = MathHelper.cos((entity.ticksExisted + partialTick) * 0.22F);
    mouth.rotateAngleX = (0.5236F + 0.06F * idleSwingCos);
  }

  @Override
  protected ModelRenderer makeHeadModel() {
    ModelRenderer head = new ModelRenderer(this);
    head.setRotationPoint(0.0F, 10.0F, -10.0F);
    head.setTextureOffset(2, 2).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 4.0F, 0.0F, false);
    head.setTextureOffset(0, 13).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 3.0F, 2.0F, 0.0F, false);
    head.setTextureOffset(15, 17).addBox(-2.0F, 2.5F, -4.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
    head.setTextureOffset(21, 1).addBox(-3.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
    head.setTextureOffset(28, 1).addBox(1.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);    
    return head;
  }

  @Override
  protected boolean isSitting(T entity) {
    return entity.isSitting();
  }
}
