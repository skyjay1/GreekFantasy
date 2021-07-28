package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.NemeanLionModel;
import greekfantasy.entity.NemeanLionEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class NemeanLionRenderer<T extends NemeanLionEntity> extends MobRenderer<T, NemeanLionModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/nemean_lion.png");
  public static final float SCALE = 2.2F;
  
  public NemeanLionRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new NemeanLionModel<>(), 1.0F);
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float f) {
    matrix.scale(SCALE, SCALE, SCALE);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
