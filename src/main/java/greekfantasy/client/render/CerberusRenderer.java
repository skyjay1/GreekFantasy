package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.CerberusModel;
import greekfantasy.entity.CerberusEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CerberusRenderer<T extends CerberusEntity> extends MobRenderer<T, CerberusModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerberus/cerberus.png");
  public static final float SCALE = 1.9F;
  
  public CerberusRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CerberusModel<T>(0.0F), 1.0F);
    this.addLayer(new CerberusEyesLayer<>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float f) {
    // if the entity is spawning, shift the entity down
    if(entity.isSpawning()) {
      final float height = 2.4F;
      final float translate = 0.039F;
      final float translateY = height * entity.getSpawnPercent(f) - height;
      final float translateX = translate * (entity.getRNG().nextFloat() - 0.5F);
      final float translateZ = translate * (entity.getRNG().nextFloat() - 0.5F);
      matrix.translate(translateX, -translateY, translateZ);
    }
    matrix.scale(SCALE, SCALE, SCALE);
  }
}
