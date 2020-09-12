package greekfantasy.client.model.tileentity;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;

public class StatueModel<T extends StatueTileEntity> extends Model implements IHasArm, IHasHead {
  
  protected ModelRenderer bipedHead;
  protected ModelRenderer bipedBody;
  protected ModelRenderer bipedBodyChest;
  protected ModelRenderer bipedRightArm;
  protected ModelRenderer bipedLeftArm;
  protected ModelRenderer bipedRightLeg;
  protected ModelRenderer bipedLeftLeg;
  
  private static final EnumMap<ModelPart, ModelRenderer> ROTATION_MAP = new EnumMap<>(ModelPart.class);
  
  public StatueModel() {
    this(0.0F, 0.0F);
  }

  public StatueModel(final float modelSizeIn, final float yOffsetIn) {
    super(RenderType::getEntityCutoutNoCull);
    this.textureWidth = 64;
    this.textureHeight = 64;
    this.bipedHead = new ModelRenderer(this, 0, 0);
    this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSizeIn);
    this.bipedHead.setRotationPoint(0.0F, 0.0F + yOffsetIn, 0.0F);
    this.bipedBody = new ModelRenderer(this, 16, 16);
    this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedBody.setRotationPoint(0.0F, 0.0F + yOffsetIn, 0.0F);
    this.bipedBodyChest = new ModelRenderer(this);
    this.bipedBodyChest.setRotationPoint(0.0F, 1.0F, -2.0F);
    this.bipedBodyChest.rotateAngleX = -0.2182F;
    this.bipedBodyChest.setTextureOffset(19, 20).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSizeIn);
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + yOffsetIn, 0.0F);
    this.bipedLeftArm = new ModelRenderer(this, 40, 16);
    this.bipedLeftArm.mirror = true;
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + yOffsetIn, 0.0F);
    this.bipedRightLeg = new ModelRenderer(this, 0, 16);
    this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + yOffsetIn, 0.0F);
    this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
    this.bipedLeftLeg.mirror = true;
    this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + yOffsetIn, 0.0F);
    
    ROTATION_MAP.put(ModelPart.HEAD, this.bipedHead);
    ROTATION_MAP.put(ModelPart.BODY, this.bipedBody);
    ROTATION_MAP.put(ModelPart.LEFT_ARM, this.bipedLeftArm);
    ROTATION_MAP.put(ModelPart.RIGHT_ARM, this.bipedRightArm);
    ROTATION_MAP.put(ModelPart.LEFT_LEG, this.bipedLeftLeg);
    ROTATION_MAP.put(ModelPart.RIGHT_LEG, this.bipedRightLeg);
  }
  
  protected Iterable<ModelRenderer> getUpperParts() { 
    return ImmutableList.of(this.bipedHead, this.bipedBody, this.bipedBodyChest, this.bipedLeftArm, this.bipedRightArm); 
  }
  
  protected Iterable<ModelRenderer> getLowerParts() { 
    return ImmutableList.of(this.bipedLeftLeg, this.bipedRightLeg); 
  }

  public void setRotationAngles(T entity, float partialTicks) {
    for(final Entry<ModelPart, ModelRenderer> e : ROTATION_MAP.entrySet()) {
      final Vector3f rotations = entity.getRotations(e.getKey());
      final ModelRenderer model = e.getValue();
      model.rotateAngleX = rotations.getX();
      model.rotateAngleY = rotations.getY();
      model.rotateAngleZ = rotations.getZ();
    }
  }
  
  public void setChestVisibility(final boolean isChestVisible) {
    this.bipedBodyChest.showModel = isChestVisible;
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    // nothing here
  }
  
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha, final boolean isUpper) {
    final Iterable<ModelRenderer> parts = isUpper ? this.getUpperParts() : this.getLowerParts();
    parts.forEach(m -> m.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
  }

  @Override
  public ModelRenderer getModelHead() {
    return this.bipedHead;
  }

  @Override
  public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
    this.getArmForSide(sideIn).translateRotate(matrixStackIn);
  }
  
  protected ModelRenderer getArmForSide(HandSide side) {
    return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
 }
}
