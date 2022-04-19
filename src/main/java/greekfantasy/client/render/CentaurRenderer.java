package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.CentaurHorseLayer;
import greekfantasy.client.render.layer.CentaurQuiverLayer;
import greekfantasy.client.render.model.CentaurModel;
import greekfantasy.entity.CentaurEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;

public class CentaurRenderer<T extends CentaurEntity> extends BipedRenderer<T, CentaurModel<T>> {

    public static final EnumMap<CoatColors, ResourceLocation> BODY_TEXTURE_MAP = new EnumMap<>(CoatColors.class);

    static {
        BODY_TEXTURE_MAP.put(CoatColors.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/black.png"));
        BODY_TEXTURE_MAP.put(CoatColors.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/brown.png"));
        BODY_TEXTURE_MAP.put(CoatColors.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/chestnut.png"));
        BODY_TEXTURE_MAP.put(CoatColors.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/creamy.png"));
        BODY_TEXTURE_MAP.put(CoatColors.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/darkbrown.png"));
        BODY_TEXTURE_MAP.put(CoatColors.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/gray.png"));
        BODY_TEXTURE_MAP.put(CoatColors.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/white.png"));
    }

    public CentaurRenderer(final EntityRendererManager renderManagerIn) {
        this(renderManagerIn, new CentaurModel<T>(0.0F));
    }

    public CentaurRenderer(final EntityRendererManager renderManagerIn, final CentaurModel<T> model) {
        super(renderManagerIn, model, 0.75F);
        this.addLayer(new CentaurHorseLayer<T>(this));
        this.addLayer(new CentaurQuiverLayer<T>(this));
        //this.addLayer(new HeldItemLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return BODY_TEXTURE_MAP.get(entity.getCoatColor());
    }
}
