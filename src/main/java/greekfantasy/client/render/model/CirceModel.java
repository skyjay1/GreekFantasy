package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import greekfantasy.entity.CirceEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CirceModel<T extends CirceEntity> extends BipedModel<T> {
  
  private final ModelRenderer chest;
  
  public CirceModel(float modelSize) {
    super(modelSize, 0.0F, 64, 64);
    // left arm
    bipedLeftArm = new ModelRenderer(this, 32, 48);
    bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
    bipedLeftArm.mirror = true;
    // right arm
    bipedRightArm = new ModelRenderer(this, 40, 16);
    bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
    bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
    // chest
    chest = new ModelRenderer(this);
    chest.setRotationPoint(0.0F, 1.0F, -2.0F);
    chest.rotateAngleX = -0.2182F;
    chest.setTextureOffset(0, 32).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);
  }
  
  @Override
  protected Iterable<ModelRenderer> getBodyParts() { return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.chest)); }
  
  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
  }
  
}
