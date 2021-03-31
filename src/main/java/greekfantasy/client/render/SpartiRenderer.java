package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.SpartiClothingLayer;
import greekfantasy.client.render.model.SpartiModel;
import greekfantasy.entity.SpartiEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SpartiRenderer<T extends SpartiEntity> extends BipedRenderer<T, SpartiModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/sparti/sparti.png");
  
  public SpartiRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new SpartiModel<T>(0.0F), 0.5F);
    this.addLayer(new SpartiClothingLayer<>(this));
  }
  
  @Override
  public void render(final T entityIn, final float rotationYawIn, final float partialTick, 
      final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    matrixStackIn.push();
    // if the entity is spawning, shift the entity down
    if(entityIn.isSpawning()) {
      final float height = 1.99F;
      final float translate = 0.035F;
      final float translateY = height * entityIn.getSpawnPercent(partialTick) - height;
      final float translateX = translate * (entityIn.getRNG().nextFloat() - 0.5F);
      final float translateZ = translate * (entityIn.getRNG().nextFloat() - 0.5F);
      matrixStackIn.translate(translateX, translateY, translateZ);
    }
    super.render(entityIn, rotationYawIn, partialTick, matrixStackIn, bufferIn, packedLightIn);
    matrixStackIn.pop();
  }
  
  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
