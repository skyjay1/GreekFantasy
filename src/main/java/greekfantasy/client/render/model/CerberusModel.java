package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.CerberusEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CerberusModel<T extends CerberusEntity> extends AgeableModel<T> {

  private final ModelRenderer body;
  private final ModelRenderer neck1;
  private final ModelRenderer head1;
  private final ModelRenderer mouth1;
  private final ModelRenderer neck2;
  private final ModelRenderer head2;
  private final ModelRenderer mouth2;
  private final ModelRenderer neck3;
  private final ModelRenderer head3;
  private final ModelRenderer mouth3;
  private final ModelRenderer legFrontRight;
  private final ModelRenderer legBackRight;
  private final ModelRenderer legFrontLeft;
  private final ModelRenderer legBackLeft;
  private final ModelRenderer tail1;
  private final ModelRenderer tail2;
  
  float color = 1.0F;

  public CerberusModel(final float modelSize) {
    super();
    textureWidth = 64;
    textureHeight = 64;

    body = new ModelRenderer(this);
    body.setRotationPoint(0.0F, 24.0F, 0.0F);
    body.setTextureOffset(0, 29).addBox(-5.0F, -18.0F, -6.0F, 10.0F, 8.0F, 6.0F, modelSize);
    body.setTextureOffset(0, 12).addBox(-4.0F, -17.0F, 0.0F, 8.0F, 7.0F, 9.0F, modelSize);

    neck1 = new ModelRenderer(this);
    neck1.setRotationPoint(0.0F, 12.0F, -6.0F);
    neck1.setTextureOffset(50, 18).addBox(-1.5F, -4.0F, -3.0F, 3.0F, 4.0F, 3.0F, modelSize);

    head1 = new ModelRenderer(this);
    head1.setRotationPoint(0.0F, -4.0F, -3.0F);
    neck1.addChild(head1);
    head1.setTextureOffset(0, 0).addBox(-2.5F, -2.0F, -5.0F, 5.0F, 6.0F, 5.0F, modelSize);
    head1.setTextureOffset(21, 0).addBox(-1.5F, 1.0F, -9.0F, 3.0F, 2.0F, 4.0F, modelSize);
    head1.setTextureOffset(16, 0).addBox(-2.5F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, modelSize);
    head1.setTextureOffset(16, 0).addBox(0.5F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);

    mouth1 = new ModelRenderer(this);
    mouth1.setRotationPoint(0.0F, 3.0F, -5.0F);
    head1.addChild(mouth1);
    mouth1.setTextureOffset(21, 6).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, modelSize);

    neck2 = new ModelRenderer(this);
    neck2.setRotationPoint(4.0F, 14.0F, -5.0F);
    neck2.rotateAngleY = -0.5236F;
    neck2.setTextureOffset(50, 17).addBox(-2.0F, -4.0F, -3.0F, 3.0F, 4.0F, 3.0F, modelSize);

    head2 = new ModelRenderer(this);
    head2.setRotationPoint(0.0F, -4.0F, -3.0F);
    neck2.addChild(head2);
    head2.setTextureOffset(0, 0).addBox(-3.0F, -2.0F, -5.0F, 5.0F, 6.0F, 5.0F, modelSize);
    head2.setTextureOffset(21, 0).addBox(-2.0F, 1.0F, -9.0F, 3.0F, 2.0F, 4.0F, modelSize);
    head2.setTextureOffset(16, 0).addBox(-3.0F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, modelSize);
    head2.setTextureOffset(16, 0).addBox(0.0F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);

    mouth2 = new ModelRenderer(this);
    mouth2.setRotationPoint(-1.0F, 3.0F, -5.0F);
    head2.addChild(mouth2);
    mouth2.setTextureOffset(21, 6).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, modelSize);

    neck3 = new ModelRenderer(this);
    neck3.setRotationPoint(-4.0F, 14.0F, -5.0F);
    neck3.rotateAngleY = 0.5236F;
    neck3.setTextureOffset(50, 17).addBox(-1.0F, -4.0F, -3.0F, 3.0F, 4.0F, 3.0F, modelSize);

    head3 = new ModelRenderer(this);
    head3.setRotationPoint(0.0F, -4.0F, -3.0F);
    neck3.addChild(head3);
    head3.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -5.0F, 5.0F, 6.0F, 5.0F, modelSize);
    head3.setTextureOffset(21, 0).addBox(-1.0F, 1.0F, -9.0F, 3.0F, 2.0F, 4.0F, modelSize);
    head3.setTextureOffset(16, 0).addBox(-2.0F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, modelSize);
    head3.setTextureOffset(16, 0).addBox(1.0F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);

    mouth3 = new ModelRenderer(this);
    mouth3.setRotationPoint(0.0F, 3.0F, -5.0F);
    head3.addChild(mouth3);
    mouth3.setTextureOffset(21, 6).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, modelSize);

    legFrontRight = new ModelRenderer(this);
    legFrontRight.setRotationPoint(-3.0F, 14.0F, -2.0F);
    legFrontRight.setTextureOffset(37, 0).addBox(-1.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, modelSize);
    legFrontRight.setTextureOffset(33, 10).addBox(-1.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, modelSize);

    legBackRight = new ModelRenderer(this);
    legBackRight.setRotationPoint(-3.0F, 14.0F, 7.0F);
    legBackRight.setTextureOffset(37, 0).addBox(-1.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, modelSize);
    legBackRight.setTextureOffset(33, 10).addBox(-1.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, modelSize);

    legFrontLeft = new ModelRenderer(this);
    legFrontLeft.setRotationPoint(3.0F, 14.0F, -2.0F);
    legFrontLeft.setTextureOffset(37, 0).addBox(-2.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, modelSize);
    legFrontLeft.setTextureOffset(33, 10).addBox(-2.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, modelSize);

    legBackLeft = new ModelRenderer(this);
    legBackLeft.setRotationPoint(3.0F, 14.0F, 7.0F);
    legBackLeft.setTextureOffset(37, 0).addBox(-2.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, modelSize);
    legBackLeft.setTextureOffset(33, 10).addBox(-2.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, modelSize);

    tail1 = new ModelRenderer(this);
    tail1.setRotationPoint(0.0F, 7.0F, 9.0F);
    tail1.setTextureOffset(50, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 6.0F, 2.0F, modelSize);

    tail2 = new ModelRenderer(this);
    tail2.setRotationPoint(0.0F, 6.0F, -2.0F);
    tail1.addChild(tail2);
    tail2.setTextureOffset(50, 8).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, modelSize);
  }

  protected Iterable<ModelRenderer> getHeadParts() {
    return ImmutableList.of(this.neck1, this.neck2, this.neck3);
  }

  protected Iterable<ModelRenderer> getBodyParts() {
    return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.tail1);
  }

  public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    // limb swing and ticks
    final float limbSwingCos = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
    final float limbSwingSin = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
    final float ticks = entity.ticksExisted + partialTick;
    
    // animate tail
    float idleSwing = 0.12F * MathHelper.cos(ticks * 0.11F);
    float tailSwing = 0.42F * limbSwingCos;
    tail1.rotateAngleX = 0.5854F + tailSwing;
    tail2.rotateAngleX = 0.2618F + tailSwing * 0.6F;
    tail1.rotateAngleZ = idleSwing;
    tail2.rotateAngleZ = idleSwing * 0.85F;
  
    // animate legs
    final float legAngle = 1.2F;
    this.legBackRight.rotateAngleX = limbSwingCos * legAngle;
    this.legBackLeft.rotateAngleX = limbSwingSin * legAngle;
    this.legFrontRight.rotateAngleX = limbSwingSin * legAngle;
    this.legFrontLeft.rotateAngleX = limbSwingCos * legAngle;
    
    // animate mouths
    final float howlingTimeLeft = 1.0F - entity.getSummonPercent(partialTick);
    final float mouthAngle = 0.26F;
    this.mouth1.rotateAngleX = (mouthAngle - MathHelper.cos((ticks + 0.0F) * 0.28F) * mouthAngle * 0.3F) * howlingTimeLeft;
    this.mouth2.rotateAngleX = (mouthAngle - MathHelper.cos((ticks + 0.9F) * 0.19F) * mouthAngle * 0.3F) * howlingTimeLeft;
    this.mouth3.rotateAngleX = (mouthAngle - MathHelper.cos((ticks + 0.4F) * 0.24F) * mouthAngle * 0.3F) * howlingTimeLeft;
  }

  @Override
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw,
      float rotationPitch) {
    // set color if the entity is spawning
    color = entity.isSpawning() ? entity.getSpawnPercent(partialTick) : 1.0F;
    // prepare animations
    final float ticks = entity.ticksExisted + partialTick;
    final float howlingTime = entity.getSummonPercent(partialTick);
    final float howlingTimeLeft = 1.0F - howlingTime;
    // update neck angles
    final float howlingAngle = howlingTime > 0 ? -4.0F * (float)Math.pow(2.0F * howlingTime - 1.0F, 2) + 5.0F : 1.0F;
    final float neckAngleX = 0.10F;
    final float neck1X = -0.2618F * howlingAngle + MathHelper.cos((ticks + 0.1F) * 0.049F) * neckAngleX * howlingTimeLeft;
    final float neck2X = -0.1745F * howlingAngle + MathHelper.cos((ticks + 0.9F) * 0.059F) * neckAngleX * howlingTimeLeft;
    final float neck3X = -0.1745F * howlingAngle + MathHelper.cos((ticks + 1.5F) * 0.055F) * neckAngleX * howlingTimeLeft;
    this.neck1.rotateAngleX = neck1X;
    this.neck2.rotateAngleX = neck2X;
    this.neck3.rotateAngleX = neck3X;
    
    // update head angles
    final float offsetX = 0.2309F;
    final float pitch = rotationPitch * 0.017453292F;
    final float yaw = rotationYaw * 0.017453292F * howlingTimeLeft;
    this.head1.rotateAngleX = (pitch - neck1.rotateAngleX) * howlingTimeLeft;
    this.head1.rotateAngleY = yaw;
    this.head2.rotateAngleX = (pitch - neck2.rotateAngleX) * howlingTimeLeft;
    this.head2.rotateAngleY = (yaw * 0.8F + offsetX);
    this.head3.rotateAngleX = (pitch - neck3.rotateAngleX) * howlingTimeLeft;
    this.head3.rotateAngleY = (yaw * 0.8F - offsetX);
  }
  
  @Override
  public void render(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, 
      final float redIn, final float greenIn, final float blueIn, final float alphaIn) {
    super.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, color, color, alphaIn);
  }
}
