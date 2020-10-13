package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.OrthusModel;
import greekfantasy.entity.OrthusEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class OrthusRenderer<T extends OrthusEntity> extends MobRenderer<T, OrthusModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/orthus/orthus.png");

  public OrthusRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new OrthusModel<T>(0.0F), 0.5F);
    this.addLayer(new OrthusEyesLayer<>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float partialTick, 
      final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    final float scale = 1.2F;
    matrixStackIn.push();
    matrixStackIn.scale(scale, scale, scale);
    super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
    matrixStackIn.pop();
  }
}
