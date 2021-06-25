package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.BabySpiderModel;
import greekfantasy.entity.BabySpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class BabySpiderRenderer<T extends BabySpiderEntity> extends MobRenderer<T, BabySpiderModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/baby_spider.png");

  public BabySpiderRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new BabySpiderModel<T>(0.0F), 0.4F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
