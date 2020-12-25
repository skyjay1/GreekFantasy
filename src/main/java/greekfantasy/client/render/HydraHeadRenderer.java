package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.HydraHeadModel;
import greekfantasy.entity.HydraEntity;
import greekfantasy.entity.HydraHeadEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class HydraHeadRenderer<T extends HydraHeadEntity> extends EntityRenderer<T> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra_head.png");
  private final HydraHeadModel<HydraHeadEntity> hydraHead;
    
  public HydraHeadRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn);
    hydraHead = new HydraHeadModel<>();
  }

  @Override
  public void render(final T entityIn, final float rotationYawIn, final float ageInTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    matrixStackIn.push();
    matrixStackIn.translate(0, entityIn.getHeight(), 0);
    matrixStackIn.scale(1.0F, -1.0F, 1.0F);
    final IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(getEntityTexture(entityIn)));
    super.render(entityIn, rotationYawIn, ageInTicks, matrixStackIn, bufferIn, packedLightIn);
    HydraEntity hydra = entityIn.getHydra();
    if(hydra != null) {
      int packedOverlay = LivingRenderer.getPackedOverlay(hydra, 0.0F);
      float partialTick = ageInTicks % 1.0F;
      float limbSwingAmount = MathHelper.lerp(partialTick, hydra.prevLimbSwingAmount, hydra.limbSwingAmount);
      float limbSwing = hydra.limbSwing - hydra.limbSwingAmount * (1.0F - partialTick);
      if (limbSwingAmount > 1.0F) {
         limbSwingAmount = 1.0F;
      }
      hydraHead.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, hydra.rotationYawHead, hydra.getPitch(partialTick));
      hydraHead.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      hydraHead.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
    matrixStackIn.pop();
  }
  
  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
}