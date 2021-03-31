package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.SatyrGroverLayer;
import greekfantasy.client.render.layer.SatyrPanfluteLayer;
import greekfantasy.client.render.layer.SatyrShamanLayer;
import greekfantasy.client.render.model.SatyrModel;
import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SatyrRenderer<T extends SatyrEntity> extends BipedRenderer<T, SatyrModel<T>> {
  
  private static final ResourceLocation TEXTURE_SATYR = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/satyr.png");
  private static final ResourceLocation TEXTURE_GROVER = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/grover.png");

  public SatyrRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new SatyrModel<T>(0.0F), 0.5F);
    this.addLayer(new SatyrShamanLayer<>(this));
    this.addLayer(new SatyrPanfluteLayer<>(this));
    this.addLayer(new SatyrGroverLayer<>(this));
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float partialTick, 
      final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return (entity.hasCustomName() && "Grover".equals(entity.getCustomName().getUnformattedComponentText())) ? TEXTURE_GROVER : TEXTURE_SATYR;
  }
}
