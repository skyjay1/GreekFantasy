package greekfantasy.client.render;

import java.util.EnumMap;

import greekfantasy.client.render.layer.PegasusMarkingsLayer;
import greekfantasy.client.render.layer.PegasusWingLayer;
import greekfantasy.client.render.model.PegasusModel;
import greekfantasy.entity.PegasusEntity;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.util.ResourceLocation;

public class PegasusRenderer<T extends PegasusEntity> extends AbstractHorseRenderer<T, PegasusModel<T>> {
  
  public static final EnumMap<CoatColors, ResourceLocation> BODY_TEXTURE_MAP = new EnumMap<>(CoatColors.class);
  
  static {
    BODY_TEXTURE_MAP.put(CoatColors.BLACK, new ResourceLocation("minecraft", "textures/entity/horse/horse_black.png"));
    BODY_TEXTURE_MAP.put(CoatColors.BROWN, new ResourceLocation("minecraft", "textures/entity/horse/horse_brown.png"));
    BODY_TEXTURE_MAP.put(CoatColors.CHESTNUT, new ResourceLocation("minecraft", "textures/entity/horse/horse_chestnut.png"));
    BODY_TEXTURE_MAP.put(CoatColors.CREAMY, new ResourceLocation("minecraft", "textures/entity/horse/horse_creamy.png"));
    BODY_TEXTURE_MAP.put(CoatColors.DARKBROWN, new ResourceLocation("minecraft", "textures/entity/horse/horse_darkbrown.png"));
    BODY_TEXTURE_MAP.put(CoatColors.GRAY, new ResourceLocation("minecraft", "textures/entity/horse/horse_gray.png"));
    BODY_TEXTURE_MAP.put(CoatColors.WHITE, new ResourceLocation("minecraft", "textures/entity/horse/horse_white.png"));
  }
  
  public PegasusRenderer(final EntityRendererManager renderManagerIn) {
    this(renderManagerIn, new PegasusModel<>(0.0F));
  }
  
  public PegasusRenderer(final EntityRendererManager renderManagerIn, final PegasusModel<T> model) {
    super(renderManagerIn, model, 1.0F);
    this.addLayer(new PegasusMarkingsLayer<T>(this));
    this.addLayer(new PegasusWingLayer<T>(this));
  }

  @Override
  public ResourceLocation getEntityTexture(T entity) {
    return BODY_TEXTURE_MAP.get(entity.getCoatColor());
  }
}
