package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.SatyrModel;
import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SatyrRenderer<T extends SatyrEntity> extends BipedRenderer<T, SatyrModel<T>> {
  
  private static final ResourceLocation TEXTURE_SATYR = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/satyr.png");
  private static final ResourceLocation TEXTURE_SHAMAN = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/shaman.png");

  private boolean isShaman;
  
  public SatyrRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new SatyrModel<T>(0.0F), 0.5F);
    this.addLayer(new SatyrPanfluteLayer<>(this));
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float partialTick, 
      final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
    if(entityIn.hasShamanTexture()) {
      this.isShaman = true;
      super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
      this.isShaman = false;
    }
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return isShaman ? TEXTURE_SHAMAN : TEXTURE_SATYR;
  }
}
