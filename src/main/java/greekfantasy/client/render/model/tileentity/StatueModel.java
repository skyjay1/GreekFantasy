package greekfantasy.client.render.model.tileentity;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
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
  protected ModelRenderer bipedRightArmSlim;
  protected ModelRenderer bipedLeftArmSlim;
  protected ModelRenderer bipedRightLeg;
  protected ModelRenderer bipedLeftLeg;
  
  // layers
  protected ModelRenderer bipedHeadwear;
  protected ModelRenderer bipedLeftArmwear;
  protected ModelRenderer bipedRightArmwear;
  protected ModelRenderer bipedLeftArmwearSlim;
  protected ModelRenderer bipedRightArmwearSlim;
  protected ModelRenderer bipedLeftLegwear;
  protected ModelRenderer bipedRightLegwear;
  protected ModelRenderer bipedBodyWear;
//  protected ModelRenderer bipedBodyChestWear;
  
  private static final EnumMap<ModelPart, Collection<ModelRenderer>> ROTATION_MAP = new EnumMap<>(ModelPart.class);
  
  public StatueModel() {
    this(0.0F, 0.0F);
  }

  public StatueModel(final float modelSizeIn, final float yOffsetIn) {
    super(RenderType::entityCutoutNoCull);
    this.texWidth = 64;
    this.texHeight = 64;
    // head
    this.bipedHead = new ModelRenderer(this, 0, 0);
    this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSizeIn);
    this.bipedHead.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
    // body
    this.bipedBody = new ModelRenderer(this, 16, 16);
    this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedBody.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
    this.bipedBodyChest = new ModelRenderer(this);
    this.bipedBodyChest.setPos(0.0F, 1.0F, -2.0F);
    this.bipedBodyChest.xRot = -0.2182F;
    this.bipedBodyChest.texOffs(19, 20).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSizeIn);
    // full-size arms
    this.bipedLeftArm = new ModelRenderer(this, 32, 48);
    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedLeftArm.setPos(5.0F, 2.0F + yOffsetIn, 0.0F);
    this.bipedLeftArm.mirror = true;
    this.bipedRightArm = new ModelRenderer(this, 40, 16);
    this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedRightArm.setPos(-5.0F, 2.0F + yOffsetIn, 0.0F);
    // slim arms
    this.bipedLeftArmSlim = new ModelRenderer(this, 32, 48);
    this.bipedLeftArmSlim.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedLeftArmSlim.setPos(5.0F, 2.5F + yOffsetIn, 0.0F);
    this.bipedLeftArmSlim.mirror = true;
    this.bipedRightArmSlim = new ModelRenderer(this, 40, 16);
    this.bipedRightArmSlim.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedRightArmSlim.setPos(-5.0F, 2.5F + yOffsetIn, 0.0F);
    // legs
    this.bipedRightLeg = new ModelRenderer(this, 16, 48);
    this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedRightLeg.setPos(-2.0F, 12.0F + yOffsetIn, 0.0F);
    this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
    //this.bipedLeftLeg.mirror = true;
    this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn);
    this.bipedLeftLeg.setPos(2.0F, 12.0F + yOffsetIn, 0.0F);
    // layers
    // head
    this.bipedHeadwear = new ModelRenderer(this, 32, 0);
    this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSizeIn + 0.5F);
    this.bipedHeadwear.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
    // arms
    this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
    this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn + 0.25F);
    this.bipedLeftArmwear.setPos(5.0F, 2.0F + yOffsetIn, 0.0F);
    this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
    this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn + 0.25F);
    this.bipedRightArmwear.setPos(-5.0F, 2.0F + yOffsetIn, 0.0F); // 10.0F
    // slim arms
    this.bipedLeftArmwearSlim = new ModelRenderer(this, 48, 48);
    this.bipedLeftArmwearSlim.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSizeIn + 0.25F);
    this.bipedLeftArmwearSlim.setPos(5.0F, 2.5F + yOffsetIn, 0.0F);
    this.bipedRightArmwearSlim = new ModelRenderer(this, 40, 32);
    this.bipedRightArmwearSlim.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSizeIn + 0.25F);
    this.bipedRightArmwearSlim.setPos(-5.0F, 2.5F + yOffsetIn, 0.0F); // 10.0F
    // legs
    this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
    this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn + 0.25F);
    this.bipedLeftLegwear.setPos(1.9F, 12.0F + yOffsetIn, 0.0F);
    this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
    this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn + 0.25F);
    this.bipedRightLegwear.setPos(-1.9F, 12.0F + yOffsetIn, 0.0F);
    // body
    this.bipedBodyWear = new ModelRenderer(this, 16, 32);
    this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSizeIn + 0.25F);
    this.bipedBodyWear.setPos(0.0F, 0.0F + yOffsetIn, 0.0F);
//    this.bipedBodyChestWear = new ModelRenderer(this);
//    this.bipedBodyChestWear.setRotationPoint(0.0F, 1.0F, -2.0F);
//    this.bipedBodyChestWear.rotateAngleX = -0.2182F;
//    this.bipedBodyChestWear.setTextureOffset(19, 36).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSizeIn + 0.25F);
    
    ROTATION_MAP.put(ModelPart.HEAD, ImmutableList.of(this.bipedHead, this.bipedHeadwear));
    ROTATION_MAP.put(ModelPart.BODY, ImmutableList.of(this.bipedBody, this.bipedBodyChest, this.bipedBodyWear/*, this.bipedBodyChestWear*/));
    ROTATION_MAP.put(ModelPart.LEFT_ARM, ImmutableList.of(this.bipedLeftArm, this.bipedLeftArmSlim, this.bipedLeftArmwear, this.bipedLeftArmwearSlim));
    ROTATION_MAP.put(ModelPart.RIGHT_ARM, ImmutableList.of(this.bipedRightArm, this.bipedRightArmSlim, this.bipedRightArmwear, this.bipedRightArmwearSlim));
    ROTATION_MAP.put(ModelPart.LEFT_LEG, ImmutableList.of(this.bipedLeftLeg, this.bipedLeftLegwear));
    ROTATION_MAP.put(ModelPart.RIGHT_LEG, ImmutableList.of(this.bipedRightLeg, this.bipedRightLegwear));
  }
  
  protected Iterable<ModelRenderer> getUpperParts() { 
    return ImmutableList.of(this.bipedHead, this.bipedBody, this.bipedBodyChest, this.bipedHeadwear, this.bipedBodyWear/*, this.bipedBodyChestWear*/); 
  }
  
  protected Iterable<ModelRenderer> getLowerParts() { 
    return ImmutableList.of(this.bipedLeftLeg, this.bipedRightLeg, this.bipedLeftLegwear, this.bipedRightLegwear); 
  }
  
  protected Iterable<ModelRenderer> getSlimArms() { 
    return ImmutableList.of(this.bipedLeftArmSlim, this.bipedRightArmSlim, this.bipedLeftArmwearSlim, this.bipedRightArmwearSlim); 
  }
  
  protected Iterable<ModelRenderer> getArms() { 
    return ImmutableList.of(this.bipedLeftArm, this.bipedRightArm, this.bipedLeftArmwear, this.bipedRightArmwear); 
  }

  public void setRotationAngles(final T entity, final float partialTicks) {
    final StatuePose pose = entity.getStatuePose();
    for(final Entry<ModelPart, Collection<ModelRenderer>> e : ROTATION_MAP.entrySet()) {
      // set the rotations for each part in the list
      final Vector3f rotations = pose.getAngles(e.getKey());
      for(final ModelRenderer m : e.getValue()) {
        m.xRot = rotations.x();
        m.yRot = rotations.y();
        m.zRot = rotations.z();
      };
    }
    // reset body rotations
    this.bipedBody.xRot = 0.0F;
    this.bipedBody.yRot = 0.0F;
    this.bipedBody.zRot = 0.0F;
    this.bipedBodyWear.xRot = 0.0F;
    this.bipedBodyWear.yRot = 0.0F;
    this.bipedBodyWear.zRot = 0.0F;
    this.bipedBodyChest.xRot = -0.2182F;
    this.bipedBodyChest.yRot = 0.0F;
    this.bipedBodyChest.zRot = 0.0F;
  }

  public void rotateAroundBody(final Vector3f bodyRotations, final MatrixStack matrixStackIn, final float partialTicks) {
    // rotate entire model around body rotations
    if (bodyRotations.z() != 0.0F) {
      matrixStackIn.mulPose(Vector3f.ZP.rotation(bodyRotations.z()));
    }
    if (bodyRotations.y() != 0.0F) {
      matrixStackIn.mulPose(Vector3f.YP.rotation(bodyRotations.y()));
    }
    if (bodyRotations.x() != 0.0F) {
      matrixStackIn.mulPose(Vector3f.XP.rotation(bodyRotations.x()));
    }
  }

  @Override
  public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha) {
    // nothing here
  }
  
  public void render(final T entity, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
      float green, float blue, float alpha, final boolean isUpper, final boolean isFemaleModel) {
    // update which parts can be shown for male/female
    this.bipedBodyChest.visible = isFemaleModel;
    // determine which parts this block will be rendering
    final Iterable<ModelRenderer> parts;
    final Iterable<ModelRenderer> arms;
    if(isUpper) {
      parts = getUpperParts();
      arms = (isFemaleModel ? getSlimArms() : getArms());
    } else {
      parts = getLowerParts();
      arms = ImmutableList.of();
    }
    parts.forEach(m -> m.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
    arms.forEach(m -> m.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
  }

  @Override
  public ModelRenderer getHead() {
    return this.bipedHead;
  }

  @Override
  public void translateToHand(HandSide sideIn, MatrixStack matrixStackIn) {
    this.getArmForSide(sideIn).translateAndRotate(matrixStackIn);
  }
  
  protected ModelRenderer getArmForSide(HandSide side) {
    return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
 }
}
