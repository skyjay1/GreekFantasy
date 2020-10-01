package greekfantasy.client.render;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.ElpisModel;
import greekfantasy.entity.ElpisEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class ElpisRenderer<T extends ElpisEntity> extends BipedRenderer<T, ElpisModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/elpis.png");
  private static final float SCALE = 0.4F;
  
  public ElpisRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new ElpisModel<T>(0.0F), 0.0F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float ageInTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    matrixStackIn.push();
    matrixStackIn.scale(SCALE, SCALE, SCALE);
    RenderSystem.enableAlphaTest();
    RenderSystem.defaultAlphaFunc();
    RenderSystem.enableBlend();
    this.entityModel.setAlpha(entityIn.getAlpha(ageInTicks));
    super.render(entityIn, rotationYawIn, ageInTicks, matrixStackIn, bufferIn, packedLightIn);
    RenderSystem.disableAlphaTest();
    RenderSystem.disableBlend();
    matrixStackIn.pop();
  }
  
  @Override
  @Nullable
  protected RenderType func_230496_a_(final T entity, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    final ResourceLocation tex = this.getEntityTexture(entity);
    return entity.isGlowing() ? RenderType.getOutline(tex) : RenderType.getEntityTranslucent(tex);
  }
}
