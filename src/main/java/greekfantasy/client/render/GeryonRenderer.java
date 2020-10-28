package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.GeryonModel;
import greekfantasy.entity.GeryonEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class GeryonRenderer<T extends GeryonEntity> extends BipedRenderer<T, GeryonModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/geryon/geryon.png");
  private static final float SCALE = 2.0F;
  
  public GeryonRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new GeryonModel<T>(0.0F), 0.0F);
    this.addLayer(new GeryonClothingLayer<>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float f) {
    // if the entity is spawning, shift the entity down
    if(entity.isSpawning()) {
      final float height = 4.96F;
      final float translate = 0.019F;
      final float translateY = height * entity.getSpawnPercent(f) - height;
      final float translateX = translate * (entity.getRNG().nextFloat() - 0.5F);
      final float translateZ = translate * (entity.getRNG().nextFloat() - 0.5F);
      matrix.translate(translateX, -translateY, translateZ);
    }
    matrix.scale(SCALE, SCALE, SCALE);
  }
}
