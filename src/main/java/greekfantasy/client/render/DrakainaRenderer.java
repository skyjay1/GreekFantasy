package greekfantasy.client.render;

import java.util.EnumMap;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.DrakainaModel;
import greekfantasy.entity.DrakainaEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class DrakainaRenderer<T extends DrakainaEntity> extends BipedRenderer<T, DrakainaModel<T>> {

  public static final EnumMap<DrakainaEntity.Variant, ResourceLocation> TEXTURE_MAP = new EnumMap<>(DrakainaEntity.Variant.class);
  
  static {
    TEXTURE_MAP.put(DrakainaEntity.Variant.GREEN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/drakaina/green_drakaina.png"));
    TEXTURE_MAP.put(DrakainaEntity.Variant.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/drakaina/brown_drakaina.png"));
    TEXTURE_MAP.put(DrakainaEntity.Variant.RED, new ResourceLocation(GreekFantasy.MODID, "textures/entity/drakaina/red_drakaina.png"));
  }
  
  public DrakainaRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new DrakainaModel<T>(0.0F), 0.5F);
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE_MAP.get(entity.getVariant());
  }
}
