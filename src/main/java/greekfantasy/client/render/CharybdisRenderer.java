package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.CharybdisModel;
import greekfantasy.entity.CharybdisEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class CharybdisRenderer extends MobRenderer<CharybdisEntity, CharybdisModel> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/charybdis.png");
  private static final float SCALE = 4.0F;
  
  public CharybdisRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CharybdisModel(0.0F), 1.0F);
  }
  
  @Override
  protected void preRenderCallback(final CharybdisEntity entity, MatrixStack matrix, float f) {
    final float s = SCALE * entity.getSpawnPercent();
    matrix.scale(s, s, s);
  }

  @Override
  public ResourceLocation getEntityTexture(final CharybdisEntity entity) {
    return TEXTURE;
  }
}
