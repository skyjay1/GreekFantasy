package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.HydraHeadEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class HydraHeadModel<T extends HydraHeadEntity> extends EntityModel<T> {
  
  private final ModelRenderer neck;
  private final ModelRenderer neckSevered;
  private final ModelRenderer neck1;
  private final ModelRenderer neck2;
  private final ModelRenderer neck3;
  private final ModelRenderer head;
  private final ModelRenderer mouth;
  
  private float spawnPercent;
  private boolean severed;
  private boolean charred;

  public HydraHeadModel() {
    super();
    textureWidth = 64;
    textureHeight = 32;

    neck = new ModelRenderer(this);
    neck.setRotationPoint(0.0F, 22.0F, 0.0F);

    neck1 = new ModelRenderer(this);
    neck1.setRotationPoint(0.0F, 2.0F, -3.0F);
    neck1.rotateAngleX = 0.7854F;
    neck1.setTextureOffset(0, 18).addBox(-2.0F, -7.0F, 0.0F, 4.0F, 8.0F, 6.0F, 0.0F, false);
    neck1.setTextureOffset(25, 24).addBox(-1.0F, -6.0F, 6.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
    neck.addChild(neck1);
    
    neckSevered = new ModelRenderer(this);
    neckSevered.setRotationPoint(0.0F, 24.0F, 1.0F);
    neckSevered.setTextureOffset(0, 18).addBox(-2.0F, -7.0F, 0.0F, 4.0F, 8.0F, 6.0F, -0.01F, false);
    neckSevered.setTextureOffset(25, 24).addBox(-1.0F, -6.0F, 6.0F, 2.0F, 6.0F, 2.0F, -0.01F, false);

    neck2 = new ModelRenderer(this);
    neck2.setRotationPoint(0.0F, -7.0F, 0.0F);
    neck2.rotateAngleX = -0.3491F;
    neck2.setTextureOffset(0, 18).addBox(-2.01F, -7.0F, 0.0F, 4.0F, 8.0F, 6.0F, 0.0F, false);
    neck2.setTextureOffset(25, 24).addBox(-1.0F, -7.0F, 6.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
    neck1.addChild(neck2);

    neck3 = new ModelRenderer(this);
    neck3.setRotationPoint(0.0F, -7.0F, 0.0F);
    neck3.rotateAngleX = -0.3491F;
    neck3.setTextureOffset(0, 18).addBox(-2.0F, -7.0F, 0.0F, 4.0F, 8.0F, 6.0F, 0.0F, false);
    neck3.setTextureOffset(25, 24).addBox(-1.0F, -7.0F, 6.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
    neck2.addChild(neck3);

    head = new ModelRenderer(this);
    head.setRotationPoint(0.0F, -6.0F, 3.0F);
    head.setTextureOffset(0, 0).addBox(-3.0F, -8.0F, -4.0F, 6.0F, 8.0F, 8.0F, 0.0F, false);
    head.setTextureOffset(33, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
    head.setTextureOffset(15, 16).addBox(-2.5F, -3.25F, -9.5F, 5.0F, 3.0F, 4.0F, 0.0F, false);
    head.setTextureOffset(34, 22).addBox(-1.0F, -8.0F, 4.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
    neck3.addChild(head);

    mouth = new ModelRenderer(this);
    mouth.setRotationPoint(0.0F, -1.0F, -4.0F);
    head.addChild(mouth);
    mouth.setTextureOffset(33, 9).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);

    ModelRenderer leftHorn = new ModelRenderer(this);
    leftHorn.setRotationPoint(3.0F, -6.0F, 1.0F);
    leftHorn.rotateAngleZ = 0.5236F;
    leftHorn.setTextureOffset(56, 27).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    head.addChild(leftHorn);

    ModelRenderer leftHorn2 = new ModelRenderer(this);
    leftHorn2.setRotationPoint(1.0F, -3.0F, 0.0F);
    leftHorn2.rotateAngleX = 0.5236F;
    leftHorn2.rotateAngleY = 0.0873F;
    leftHorn2.setTextureOffset(56, 22).addBox(-3.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    leftHorn.addChild(leftHorn2);

    ModelRenderer leftHorn3 = new ModelRenderer(this);
    leftHorn3.setRotationPoint(0.0F, -3.0F, 0.0F);
    leftHorn3.rotateAngleX = 1.0472F;
    leftHorn3.rotateAngleY = 0.0873F;
    leftHorn3.setTextureOffset(56, 17).addBox(-3.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
    leftHorn2.addChild(leftHorn3);

    ModelRenderer rightHorn1 = new ModelRenderer(this);
    rightHorn1.setRotationPoint(-3.0F, -6.0F, 1.0F);
    rightHorn1.rotateAngleZ = -0.5236F;
    rightHorn1.setTextureOffset(56, 27).addBox(0.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    head.addChild(rightHorn1);

    ModelRenderer rightHorn2 = new ModelRenderer(this);
    rightHorn2.setRotationPoint(1.0F, -3.0F, 0.0F);
    rightHorn2.rotateAngleX = 0.5236F;
    rightHorn2.rotateAngleY = -0.0873F;
    rightHorn2.setTextureOffset(56, 22).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    rightHorn1.addChild(rightHorn2);

    ModelRenderer rightHorn3 = new ModelRenderer(this);
    rightHorn3.setRotationPoint(0.0F, -3.0F, 0.0F);
    rightHorn3.rotateAngleX = 1.0472F;
    rightHorn3.rotateAngleY = -0.0873F;
    rightHorn3.setTextureOffset(56, 17).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
    rightHorn2.addChild(rightHorn3);

  }
  

  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    // rotate head angles
    head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
    head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
//    neck.setRotationPoint(0.0F, 22.0F, 0.0F);
//    neck1.setRotationPoint(0, 2, -3);
//    neck.rotateAngleX = MathHelper.sin(ageInTicks * 0.05F) * 0.25F;
//    neck.rotateAngleX = entityIn.rotationPitch * 0.017453292F;
//    neck.rotateAngleY = entityIn.rotationYaw * 0.017453292F;
//    neck.rotateAngleY = (float) Math.toRadians(-90.0D);
//    neck.rotateAngleY = 0;
    neckSevered.rotateAngleX = neck1.rotateAngleX + neck.rotateAngleX;
    neckSevered.rotateAngleY = neck1.rotateAngleY + neck.rotateAngleY;
    neckSevered.rotateAngleZ = neck1.rotateAngleZ + neck.rotateAngleZ;
    neckSevered.setRotationPoint(neck.rotationPointX, neck.rotationPointY + 1, neck.rotationPointZ - 3);
  }

  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    final float idleSwingCos = MathHelper.cos((21 * entityIn.getEntityId() + entityIn.ticksExisted + partialTick) * 0.22F);
    mouth.rotateAngleX = (0.2618F + 0.08F * idleSwingCos);
//    neck1.rotateAngleX = 0.7854F + idleSwingCos * 0.04F;
    neck2.rotateAngleX = -0.3491F + idleSwingCos * 0.04F;
    neck3.rotateAngleX = -0.3491F + idleSwingCos * 0.02F;
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    matrixStackIn.translate(0, 0.0D, 0.25D);
    // render the severed neck portion
    neckSevered.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    // render the rest of the head if growing
    if(!severed && !charred && spawnPercent > 0.0F) {
      // scale according to spawn percent and render normally
      float scale = spawnPercent;
      matrixStackIn.translate(0, (1.0F - scale), 0);
      matrixStackIn.scale(scale, scale, scale);
      // render neck (and all children)
      neck.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }
//    matrixStackIn.translate(0, -1.0D, 0);
  }
  
  public void setSpawnPercent(final float spawnPercentIn) { spawnPercent = spawnPercentIn; }
  
  public void setSevered(final boolean severedIn) { severed = severedIn; }
  
  public void setCharred(final boolean charredIn) { charred = charredIn; }

}
