package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.HydraHeadModel;
import greekfantasy.entity.HydraHeadEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class HydraHeadRenderer<T extends HydraHeadEntity> extends MobRenderer<T, HydraHeadModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra_head.png");
  private static final ResourceLocation TEXTURE_CHARRED = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra_head_charred.png");

  public HydraHeadRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new HydraHeadModel<T>(), 0.0F);
  }
  
  @Override
  protected void preRenderCallback(final T entity, MatrixStack matrix, float ageInTicks) {
    getEntityModel().setSpawnPercent(entity.getSpawnPercent());
    getEntityModel().setCharred(entity.isCharred());
    getEntityModel().setSevered(entity.isSevered());
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return !entity.isCharred() ? TEXTURE : TEXTURE_CHARRED;
  }
}