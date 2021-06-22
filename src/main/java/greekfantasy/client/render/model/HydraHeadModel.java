package greekfantasy.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.HydraHeadEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

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

    neckSevered = new ModelRenderer(this);
    neckSevered.setRotationPoint(0.0F, 24.0F, 1.0F);
    neckSevered.setTextureOffset(0, 16).addBox(-2.0F, -9.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
    neckSevered.setTextureOffset(25, 22).addBox(-1.0F, -8.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

    neck = new ModelRenderer(this);
    neck.setRotationPoint(0.0F, 22.0F, 0.0F); // neck.setRotationPoint(0.0F, 22.0F, 4.0F);

    neck1 = new ModelRenderer(this);
    neck1.setRotationPoint(0.0F, 2.0F, -3.0F);
    neck1.rotateAngleX = 0.5236F;
    neck1.setTextureOffset(0, 16).addBox(-2.0F, -9.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
    neck1.setTextureOffset(25, 22).addBox(-1.0F, -8.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
    neck.addChild(neck1);

    neck2 = new ModelRenderer(this);
    neck2.setRotationPoint(0.0F, -9.0F, 0.0F);
    neck2.rotateAngleX = -0.3491F;
    neck2.setTextureOffset(0, 16).addBox(-2.01F, -10.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
    neck2.setTextureOffset(25, 22).addBox(-1.0F, -10.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
    neck1.addChild(neck2);

    neck3 = new ModelRenderer(this);
    neck3.setRotationPoint(0.0F, -10.0F, 0.0F);
    neck3.rotateAngleX = -0.1745F;
    neck3.setTextureOffset(0, 16).addBox(-2.0F, -10.0F, 0.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);
    neck3.setTextureOffset(25, 22).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
    neck2.addChild(neck3);

    head = new ModelRenderer(this);
    head.setRotationPoint(0.0F, -9.0F, 3.0F);
    neck3.addChild(head);
    head.setTextureOffset(0, 0).addBox(-3.0F, -8.0F, -4.0F, 6.0F, 8.0F, 8.0F, 0.0F, false);
    head.setTextureOffset(33, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
    head.setTextureOffset(38, 25).addBox(-2.5F, -3.25F, -9.5F, 5.0F, 3.0F, 4.0F, 0.0F, false);
    head.setTextureOffset(25, 22).addBox(-1.0F, -8.0F, 4.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

    mouth = new ModelRenderer(this);
    mouth.setRotationPoint(0.0F, -1.0F, -4.0F);
    mouth.setTextureOffset(33, 9).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
    head.addChild(mouth);

    ModelRenderer leftHorn = new ModelRenderer(this);
    leftHorn.setRotationPoint(3.0F, -6.0F, 1.0F);
    leftHorn.rotateAngleZ = 0.5236F;
    leftHorn.setTextureOffset(56, 27).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    head.addChild(leftHorn);

    ModelRenderer leftHorn2 = new ModelRenderer(this);
    leftHorn2.setRotationPoint(0.0F, -3.0F, 0.0F);
    leftHorn2.rotateAngleX = 0.5236F;
    leftHorn2.rotateAngleY = 0.0873F;
    leftHorn2.setTextureOffset(56, 22).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    leftHorn.addChild(leftHorn2);

    ModelRenderer leftHorn3 = new ModelRenderer(this);
    leftHorn3.setRotationPoint(0.0F, -3.0F, 0.0F);
    leftHorn3.rotateAngleX = 1.0472F;
    leftHorn3.rotateAngleY = 0.0873F;
    leftHorn3.setTextureOffset(56, 17).addBox(-2.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
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
    // copy head angles to severed portion
    neckSevered.rotateAngleX = neck1.rotateAngleX + neck.rotateAngleX;
    neckSevered.rotateAngleY = neck1.rotateAngleY + neck.rotateAngleY;
    neckSevered.rotateAngleZ = neck1.rotateAngleZ + neck.rotateAngleZ;
    neckSevered.setRotationPoint(neck.rotationPointX, neck.rotationPointY + 2, neck.rotationPointZ - 3);
  }

  @Override
  public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    final float idleSwingCos = MathHelper.cos((21 * entityIn.getEntityId() + entityIn.ticksExisted + partialTick) * 0.22F);
    // used to move the entire neck in a swinging motion (bite attack)
    final float swingPercent = entityIn.getSwingProgress(partialTick);
    // alternative that does not use cos: swing = 2.0D * Math.min(swingPercent - 0.5D, -(swingPercent - 0.5D)) + 1.0D;
    final float swing = MathHelper.cos((swingPercent - 0.5F) * (float)Math.PI);
    // animate mouth and neck parts
    mouth.rotateAngleX = (0.2618F + 0.45F * swing + 0.08F * idleSwingCos);
    neck1.rotateAngleX = 0.5236F + swing * 0.78F;
    neck2.rotateAngleX = -0.3491F + idleSwingCos * 0.04F;
    neck3.rotateAngleX = -0.1745F + idleSwingCos * 0.02F;
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
  }
  
  public void setSpawnPercent(final float spawnPercentIn) { spawnPercent = spawnPercentIn; }
  
  public void setSevered(final boolean severedIn) { severed = severedIn; }
  
  public void setCharred(final boolean charredIn) { charred = charredIn; }

}
