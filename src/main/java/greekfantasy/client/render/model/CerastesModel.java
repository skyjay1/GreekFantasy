package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;

import greekfantasy.entity.CerastesEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CerastesModel<T extends CerastesEntity> extends AgeableModel<T> {

  private final ModelRenderer head;
  private final ModelRenderer mouth;
  private final ModelRenderer tongue;
  
  private final ModelRenderer body1;
  private final ModelRenderer body2;
  private final ModelRenderer body3;
  private final ModelRenderer body4;
  private final ModelRenderer body5;
  private final ModelRenderer body6;
  private final ModelRenderer body7;

  public CerastesModel(final float modelSize) {
    super();
    texWidth = 64;
    texHeight = 32;

    head = new ModelRenderer(this);
    head.setPos(0.0F, 10.0F, -6.0F);
    head.texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 6.0F, modelSize);
    head.texOffs(25, 0).addBox(-2.0F, -1.0F, -10.0F, 4.0F, 2.0F, 4.0F, modelSize);
    head.texOffs(43, 5).addBox(-1.5F, 0.75F, -9.5F, 3.0F, 1.0F, 0.0F, modelSize);

    mouth = new ModelRenderer(this);
    mouth.setPos(0.0F, 1.0F, -6.0F);
    head.addChild(mouth);
    mouth.xRot = 0.5236F;
    mouth.texOffs(25, 7).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 4.0F, modelSize);

    tongue = new ModelRenderer(this);
    tongue.setPos(0.0F, 0.0F, 0.0F);
    mouth.addChild(tongue);
    tongue.texOffs(41, 0).addBox(-0.5F, 0.1F, -4.0F, 1.0F, 0.0F, 4.0F, modelSize);

    head.addChild(makeRamHorn(this, modelSize, 3.0F, -2.0F, -4.0F, true));
    head.addChild(makeRamHorn(this, modelSize, -4.0F, -2.0F, -4.0F, false));
    head.addChild(makePapillae(this, modelSize, true));
    head.addChild(makePapillae(this, modelSize, false));

    body1 = new ModelRenderer(this);
    body1.setPos(0.0F, 8.0F, -6.5F);
    body1.texOffs(0, 13).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

    body2 = new ModelRenderer(this);
    body2.setPos(0.0F, 0.0F, 6.0F);
    body1.addChild(body2);
    body2.texOffs(0, 13).addBox(-2.01F, 0.01F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

    body3 = new ModelRenderer(this);
    body3.setPos(0.0F, 4.0F, 5.5F);
    body2.addChild(body3);
    body3.texOffs(0, 13).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

    body4 = new ModelRenderer(this);
    body4.setPos(0.0F, 0.0F, 5.5F);
    body3.addChild(body4);
    body4.texOffs(0, 13).addBox(-2.01F, -4.01F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

    body5 = new ModelRenderer(this);
    body5.setPos(0.0F, 0.0F, 5.5F);
    body4.addChild(body5);
    body5.texOffs(0, 13).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

    body6 = new ModelRenderer(this);
    body6.setPos(0.0F, 0.0F, 5.5F);
    body5.addChild(body6);
    body6.texOffs(21, 14).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 6.0F, 0.0F, false);

    body7 = new ModelRenderer(this);
    body7.setPos(0.0F, 0.0F, 5.5F);
    body6.addChild(body7);
    body7.texOffs(22, 14).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
  }
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
    final float standingTime = entity.getStandingTime(partialTick);
    final float hidingTime = entity.getHidingTime(partialTick);
    // rotation points
    head.y = 21.5F - 10.25F * standingTime + 6.5F * hidingTime;
    head.z = -6.0F + 6.0F * hidingTime;
    body1.y = head.y - 2.0F;
    body1.z = head.z - 0.5F;
    // head rotation
    head.yRot = rotationYaw * 0.017453292F;
    head.xRot = rotationPitch * 0.017453292F;
    // tongue
    tongue.z = -4.0F * (1.0F - 2.0F * Math.abs(entity.getTongueTime() - 0.5F));
  }
  
  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    
    // animate snake body
    final float standingTime = entity.getStandingTime(partialTick);
    final float standingTimeLeft = (1.0F - standingTime);
    final float hidingTime = entity.getHidingTime(partialTick);
    final float limbSwingCos = entity.isHiding() ? 1.0F : MathHelper.cos(limbSwing);
    final float idleSwingCos = MathHelper.cos((entity.tickCount + partialTick) * 0.22F);
    // standing
    this.body1.xRot = -0.7854F * standingTime - 1.4707F * hidingTime;
    this.body2.xRot = -0.5236F * standingTime;
    this.body3.xRot = 0.3491F * standingTime;
    this.body4.xRot = 0.9599F * standingTime;
    this.mouth.xRot = (0.5236F + 0.06F * idleSwingCos) * standingTime;
    // slithering
    body1.yRot = limbSwingCos * -0.4F * standingTimeLeft;
    body2.yRot = limbSwingCos * 0.4F * standingTimeLeft;
    body3.yRot = limbSwingCos * -0.65F * standingTimeLeft;
    body4.yRot = limbSwingCos * 0.75F * standingTimeLeft;
    body5.yRot = limbSwingCos * -0.65F;
    body6.yRot = limbSwingCos * 0.65F;
    body7.yRot = limbSwingCos * -0.25F;
  }

  @Override
  protected Iterable<ModelRenderer> bodyParts() {
    return ImmutableList.of(body1);
  }

  @Override
  protected Iterable<ModelRenderer> headParts() {
    return ImmutableList.of(head);
  }
  
  public static ModelRenderer makeRamHorn(final EntityModel<?> model, final float modelSize, 
      final float rotX, final float rotY, final float rotZ, final boolean isLeft) {
    final float angleY = isLeft ? 0.1745F : -0.1745F;
    
    final ModelRenderer horn1 = new ModelRenderer(model);
    horn1.setPos(rotX, rotY, rotZ);
    horn1.xRot = -0.2618F;
    horn1.texOffs(58, 0).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, modelSize);
    horn1.mirror = isLeft;

    final ModelRenderer horn2 = new ModelRenderer(model);
    horn2.setPos(0.0F, -4.0F, 0.0F);
    horn2.xRot = -0.7854F;
    horn2.yRot = angleY;
    horn2.texOffs(58, 6).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, modelSize);
    horn2.mirror = isLeft;
    horn1.addChild(horn2);

    final ModelRenderer horn3 = new ModelRenderer(model);
    horn3.setPos(0.0F, -4.0F, 0.0F);
    horn3.xRot = -1.2217F;
    horn3.yRot = angleY;
    horn3.texOffs(58, 13).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, modelSize);
    horn3.mirror = isLeft;
    horn2.addChild(horn3);

    final ModelRenderer horn4 = new ModelRenderer(model);
    horn4.setPos(0.0F, -4.0F, 0.0F);
    horn4.xRot = -1.2217F;
    horn4.yRot = angleY;
    horn4.texOffs(58, 18).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, modelSize);
    horn4.mirror = isLeft;
    horn3.addChild(horn4);

    final ModelRenderer horn5 = new ModelRenderer(model);
    horn5.setPos(0.0F, -3.0F, 0.0F);
    horn5.xRot = -1.0472F;
    horn5.yRot = angleY;
    horn5.texOffs(58, 22).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, modelSize);
    horn5.mirror = isLeft;
    horn4.addChild(horn5);
    
    return horn1;
    
  }
  
  public static ModelRenderer makePapillae(final EntityModel<?> model, final float modelSize, final boolean isLeft) {
    final float rotX = isLeft ? 1.0F : -2.0F;
    final float angleZ = isLeft ? 0.1745F : -0.1745F;
    final ModelRenderer papillae = new ModelRenderer(model);
    papillae.setPos(rotX, -4.0F, -4.0F);
    papillae.xRot = 0.48F;
    papillae.zRot = angleZ;
    papillae.texOffs(59, 13).addBox(0.0F, -4.0F, -1.0F, 1.0F, 4.0F, 1.0F, modelSize);
    return papillae;
    
  }
  
}
