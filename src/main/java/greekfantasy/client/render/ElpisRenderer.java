package greekfantasy.client.render;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.ElpisModel;
import greekfantasy.entity.ElpisEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class ElpisRenderer<T extends ElpisEntity> extends BipedRenderer<T, ElpisModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/elpis.png");
  private static final float SCALE = 0.4F;
  
  public ElpisRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new ElpisModel<T>(0.0F), 0.0F);
    // remove render layers
    this.layers.clear();
    // add custom implementation of render layers (specifically, the held item layer)
    addLayer(new HeadLayer<>(this, 1.0F, 1.0F, 1.0F));
    addLayer(new ElpisRenderer.HeldItemLayer<>(this));
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float ageInTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    matrixStackIn.pushPose();
    matrixStackIn.scale(SCALE, SCALE, SCALE);
    RenderSystem.enableAlphaTest();
    RenderSystem.defaultAlphaFunc();
    RenderSystem.enableBlend();
    this.model.setAlpha(entityIn.getAlpha(ageInTicks));
    super.render(entityIn, rotationYawIn, ageInTicks, matrixStackIn, bufferIn, packedLightIn);
    RenderSystem.disableAlphaTest();
    RenderSystem.disableBlend();
    matrixStackIn.popPose();
  }
  
  @Override
  @Nullable
  protected RenderType getRenderType(final T entity, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    final ResourceLocation tex = this.getTextureLocation(entity);
    return entity.isGlowing() ? RenderType.outline(tex) : RenderType.entityTranslucent(tex);
  }
  
  public static class HeldItemLayer<T extends LivingEntity, M extends EntityModel<T> & IHasArm> extends net.minecraft.client.renderer.entity.layers.HeldItemLayer<T, M> {

    public HeldItemLayer(IEntityRenderer<T, M> renderer) {
      super(renderer);
    }
    
    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
        float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      final float unscale = 1 / ElpisRenderer.SCALE;
      matrixStackIn.scale(unscale, unscale, unscale);
      matrixStackIn.translate(-0.4D, -0.42D, 0.18D);
      super.render(matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
  }
}
