package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.CerberusModel;
import greekfantasy.entity.CerberusEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CerberusRenderer<T extends CerberusEntity> extends MobRenderer<T, CerberusModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerberus.png");

  public CerberusRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CerberusModel<T>(0.0F), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float partialTick, 
      final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    final float scale = 1.9F;
    matrixStackIn.push();
    matrixStackIn.scale(scale, scale, scale);
    super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
    matrixStackIn.pop();
  }
}
