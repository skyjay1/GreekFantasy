package greekfantasy.client.render;

import java.util.EnumMap;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.CentaurModel;
import greekfantasy.entity.CentaurEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.util.ResourceLocation;

public class CentaurRenderer<T extends CentaurEntity> extends MobRenderer<T,CentaurModel<T>> {
  
  public static final EnumMap<CoatColors, ResourceLocation> TEXTURE_MAP = new EnumMap<>(CoatColors.class);
  static {
    // TODO make unique textures and then update these fields
    TEXTURE_MAP.put(CoatColors.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/centaur_chestnut.png"));
    TEXTURE_MAP.put(CoatColors.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/centaur_chestnut.png"));
    TEXTURE_MAP.put(CoatColors.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/centaur_chestnut.png"));
    TEXTURE_MAP.put(CoatColors.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/centaur_chestnut.png"));
    TEXTURE_MAP.put(CoatColors.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/centaur_chestnut.png"));
    TEXTURE_MAP.put(CoatColors.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/centaur_chestnut.png"));
    TEXTURE_MAP.put(CoatColors.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/centaur_chestnut.png"));
  }
  
  public CentaurRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new CentaurModel<T>(0.0F), 0.5F);
    this.addLayer(new CentaurHorseLayer<T>(this));
    this.addLayer(new CentaurQuiverLayer<T>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(final T entity) {
    return TEXTURE_MAP.get(entity.getCoatColor());
  }
}
