package greekfantasy.client.render;

import java.util.HashMap;
import java.util.Map;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.NymphModel;
import greekfantasy.entity.LampadEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class LampadRenderer<T extends LampadEntity> extends BipedRenderer<T, NymphModel<T>> {
  
  public static final Map<LampadEntity.Variant, ResourceLocation> TEXTURE_MAP = new HashMap<>();
  
  static {
    TEXTURE_MAP.put(LampadEntity.Variant.CRIMSON, new ResourceLocation(GreekFantasy.MODID, "textures/entity/lampad/crimson.png"));
    TEXTURE_MAP.put(LampadEntity.Variant.POMEGRANATE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/lampad/pomegranate.png"));
    TEXTURE_MAP.put(LampadEntity.Variant.WARPED, new ResourceLocation(GreekFantasy.MODID, "textures/entity/lampad/warped.png"));
  }
  
  public LampadRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new NymphModel<T>(0.0F), 0.25F);
  }

  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless
   * you call Render.bindEntityTexture.
   */
  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE_MAP.get(entity.getVariant());
  }
}
