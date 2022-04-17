package greekfantasy.client.render;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.ShadeModel;
import greekfantasy.entity.ShadeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ShadeRenderer<T extends ShadeEntity> extends BipedRenderer<T, ShadeModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/shade.png");

  public ShadeRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new ShadeModel<T>(0.0F), 0.0F);
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float partialTick, 
      final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    float ticks = entityIn.getId() * 2 + entityIn.tickCount + partialTick;
    final float translateY = 0.15F * MathHelper.cos(ticks * 0.1F) - 0.25F;
    matrixStackIn.pushPose();
    matrixStackIn.translate(0.0D, translateY, 0.0D);
    super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
    matrixStackIn.popPose();
  }
  
  @Override
  @Nullable
  protected RenderType getRenderType(final T entity, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    ResourceLocation tex = this.getTextureLocation(entity);
    return entity.isGlowing() ? RenderType.outline(tex) : RenderType.entityTranslucent(tex);
  }
}
