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
    super(renderManagerIn, new CerberusModel<T>(0.0F), 0.5F);
    this.addLayer(new CerberusEyesLayer<>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float f) {
    matrix.scale(SCALE, SCALE, SCALE);
  }
}
