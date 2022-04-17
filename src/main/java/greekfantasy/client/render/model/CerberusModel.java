package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.entity.CerberusEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.Model;
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
    texWidth = 64;
    texHeight = 64;

    body = new ModelRenderer(this);
    body.setPos(0.0F, 24.0F, 0.0F);
    body.texOffs(0, 29).addBox(-5.0F, -18.0F, -6.0F, 10.0F, 8.0F, 6.0F, modelSize);
    body.texOffs(0, 12).addBox(-4.0F, -17.0F, 0.0F, 8.0F, 7.0F, 9.0F, modelSize);

    neck1 = new ModelRenderer(this);
    head1 = new ModelRenderer(this);
    mouth1 = new ModelRenderer(this);
    initCerberusHead(this, neck1, head1, mouth1, 0.0F, 12.0F, -6.0F, 0.0F);    
    
    neck2 = new ModelRenderer(this);
    head2 = new ModelRenderer(this);
    mouth2 = new ModelRenderer(this);
    initCerberusHead(this, neck2, head2, mouth2, 4.0F, 14.0F, -5.0F, -0.5236F);

    neck3 = new ModelRenderer(this);
    head3 = new ModelRenderer(this);
    mouth3 = new ModelRenderer(this);
    initCerberusHead(this, neck3, head3, mouth3, -4.0F, 14.0F, -5.0F, 0.5236F);

    legFrontRight = makeLegModel(this, -3.0F, 14.0F, -2.0F);
    legBackRight = makeLegModel(this, -3.0F, 14.0F, 7.0F);
    legFrontLeft = makeLegModel(this, 3.0F, 14.0F, -2.0F);
    legBackLeft = makeLegModel(this, 3.0F, 14.0F, 7.0F);

    tail1 = new ModelRenderer(this);
    tail1.setPos(0.0F, 7.0F, 9.0F);
    tail1.texOffs(50, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 6.0F, 2.0F, modelSize);

    tail2 = new ModelRenderer(this);
    tail2.setPos(0.0F, 6.0F, -2.0F);
    tail1.addChild(tail2);
    tail2.texOffs(50, 8).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, modelSize);
  }

  protected Iterable<ModelRenderer> headParts() {
    return ImmutableList.of(this.neck1, this.neck2, this.neck3);
  }

  protected Iterable<ModelRenderer> bodyParts() {
    return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.tail1);
  }

  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    // limb swing and ticks
    final float limbSwingCos = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
    final float limbSwingSin = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
    final float ticks = entity.tickCount + partialTick;
    
    // animate tail
    float idleSwing = 0.12F * MathHelper.cos(ticks * 0.11F);
    float tailSwing = 0.42F * limbSwingCos;
    tail1.xRot = 0.5854F + tailSwing;
    tail2.xRot = 0.2618F + tailSwing * 0.6F;
    tail1.zRot = idleSwing;
    tail2.zRot = idleSwing * 0.85F;
  
    // animate legs
    final float legAngle = 1.2F;
    this.legBackRight.xRot = limbSwingCos * legAngle;
    this.legBackLeft.xRot = limbSwingSin * legAngle;
    this.legFrontRight.xRot = limbSwingSin * legAngle;
    this.legFrontLeft.xRot = limbSwingCos * legAngle;
    
    // animate mouths
    final float howlingTimeLeft = 1.0F - entity.getSummonPercent(partialTick);
    final float mouthAngle = 0.26F;
    this.mouth1.xRot = (mouthAngle - MathHelper.cos((ticks + 0.0F) * 0.28F) * mouthAngle * 0.3F) * howlingTimeLeft;
    this.mouth2.xRot = (mouthAngle - MathHelper.cos((ticks + 0.9F) * 0.19F) * mouthAngle * 0.3F) * howlingTimeLeft;
    this.mouth3.xRot = (mouthAngle - MathHelper.cos((ticks + 0.4F) * 0.24F) * mouthAngle * 0.3F) * howlingTimeLeft;
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw,
      float rotationPitch) {
    // set color if the entity is spawning
    color = entity.isSpawning() ? entity.getSpawnPercent(partialTick) : 1.0F;
    // prepare animations
    final float ticks = entity.tickCount + partialTick;
    final float howlingTime = entity.getSummonPercent(partialTick);
    final float howlingTimeLeft = 1.0F - howlingTime;
    // update neck angles
    final float howlingAngle = howlingTime > 0 ? -4.0F * (float)Math.pow(2.0F * howlingTime - 1.0F, 2) + 5.0F : 1.0F;
    final float neckAngleX = 0.10F;
    final float neck1X = -0.2618F * howlingAngle + MathHelper.cos((ticks + 0.1F) * 0.049F) * neckAngleX * howlingTimeLeft;
    final float neck2X = -0.1745F * howlingAngle + MathHelper.cos((ticks + 0.9F) * 0.059F) * neckAngleX * howlingTimeLeft;
    final float neck3X = -0.1745F * howlingAngle + MathHelper.cos((ticks + 1.5F) * 0.055F) * neckAngleX * howlingTimeLeft;
    this.neck1.xRot = neck1X;
    this.neck2.xRot = neck2X;
    this.neck3.xRot = neck3X;
    
    // update head angles
    final float offsetX = 0.2309F;
    final float pitch = rotationPitch * 0.017453292F;
    final float yaw = rotationYaw * 0.017453292F * howlingTimeLeft;
    this.head1.xRot = (pitch - neck1.xRot) * howlingTimeLeft;
    this.head1.yRot = yaw;
    this.head2.xRot = (pitch - neck2.xRot) * howlingTimeLeft;
    this.head2.yRot = (yaw * 0.8F + offsetX);
    this.head3.xRot = (pitch - neck3.xRot) * howlingTimeLeft;
    this.head3.yRot = (yaw * 0.8F - offsetX);
  }
  
  @Override
  public void renderToBuffer(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, 
      final float redIn, final float greenIn, final float blueIn, final float alphaIn) {
    super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, color, color, alphaIn);
  }
  
  public static ModelRenderer makeLegModel(final Model model, final float rotX, final float rotY, final float rotZ) {
    final ModelRenderer leg = new ModelRenderer(model);
    leg.setPos(rotX, rotY, rotZ);
    leg.texOffs(37, 0).addBox(-2.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, 0.0F);
    leg.texOffs(33, 10).addBox(-2.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, 0.0F);
    return leg;
  }
  
  public static void initCerberusHead(final Model model, final ModelRenderer neck, final ModelRenderer head, 
      final ModelRenderer mouth, final float rotX, final float rotY, final float rotZ, final float neckY) {
    initCerberusHead(model, head, mouth, 0.0F, -4.0F, -3.0F);
    neck.setPos(rotX, rotY, rotZ);
    neck.yRot = neckY;
    neck.texOffs(50, 17).addBox(-1.5F, -4.0F, -3.0F, 3.0F, 4.0F, 3.0F, 0.0F);
    neck.addChild(head);
  }
  
  public static void initCerberusHead(final Model model, final ModelRenderer head, final ModelRenderer mouth, 
      final float rotX, final float rotY, final float rotZ) {
    // init head boxes and rotation points
    head.setPos(rotX, rotY, rotZ);
    head.texOffs(0, 0).addBox(-2.5F, -2.0F, -5.0F, 5.0F, 6.0F, 5.0F, 0.0F);
    head.texOffs(21, 0).addBox(-1.5F, 1.0F, -9.0F, 3.0F, 2.0F, 4.0F, 0.0F);
    head.texOffs(16, 0).addBox(-2.5F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F);
    head.texOffs(16, 0).addBox(0.5F, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, true);
    // init mouth
    mouth.setPos(0.0F, 3.0F, -5.0F);
    mouth.texOffs(21, 6).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, 0.0F);
    head.addChild(mouth);
  }
}
