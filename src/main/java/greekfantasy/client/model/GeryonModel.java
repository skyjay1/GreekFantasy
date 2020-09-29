package greekfantasy.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.CreatureEntity;

public class GeryonModel<T extends CreatureEntity> extends GiganteModel<T> {
  
  protected ModelRenderer bipedLeftHead;
  protected ModelRenderer bipedRightHead;

  public GeryonModel(float modelSize) {
    super(modelSize);
    
    bipedLeftHead = new ModelRenderer(this);
    bipedLeftHead.setTextureOffset(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize - 1.0F);
    
    bipedRightHead = new ModelRenderer(this);
    bipedRightHead.setTextureOffset(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize - 1.0F);    
  }
  
  @Override
  protected Iterable<ModelRenderer> getHeadParts() { return ImmutableList.of(this.bipedHead, this.bipedLeftHead, this.bipedRightHead); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
    // bipedHead.setRotationPoint(0.0F, -12.0F, 3.0F);
    bipedLeftHead.copyModelAngles(bipedHead);
    bipedLeftHead.setRotationPoint(-8.0F, -11F, 3.0F);
    bipedRightHead.copyModelAngles(bipedHead);
    bipedRightHead.setRotationPoint(8.0F, -11F, 3.0F);
  }  
  
  @Override
  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
    // TODO smash attack animation
  }
}
