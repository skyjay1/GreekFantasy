package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.UnicornHornLayer;
import greekfantasy.client.render.model.UnicornModel;
import greekfantasy.entity.UnicornEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class UnicornRenderer<T extends UnicornEntity> extends MobRenderer<T,UnicornModel<T>> {
  
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/unicorn/unicorn.png");
 
  public UnicornRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new UnicornModel<T>(0.0F), 0.5F);
    this.addLayer(new UnicornHornLayer<T>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE;
  }
}
