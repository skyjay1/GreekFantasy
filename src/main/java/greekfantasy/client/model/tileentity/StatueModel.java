package greekfantasy.client.model.tileentity;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;

public class StatueModel<T extends StatueTileEntity> extends Model {
  
  public ModelRenderer bipedHead;
  public ModelRenderer bipedBody;
  public ModelRenderer bipedRightArm;
  public ModelRenderer bipedLeftArm;
  public ModelRenderer bipedRightLeg;
  public ModelRenderer bipedLeftLeg;
  
  public BipedModel.ArmPose leftArmPose = BipedModel.ArmPose.EMPTY;
  public BipedModel.ArmPose rightArmPose = BipedModel.ArmPose.EMPTY;
  
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
    return ImmutableList.of(this.bipedHead, this.bipedBody, this.bipedLeftArm, this.bipedRightArm); 
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
}
