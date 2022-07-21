package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.HalfHorseLayer;
import greekfantasy.client.entity.layer.QuiverLayer;
import greekfantasy.client.entity.model.CentaurModel;
import greekfantasy.entity.Centaur;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Variant;

import java.util.EnumMap;

public class CentaurRenderer<T extends Centaur> extends HumanoidMobRenderer<T, CentaurModel<T>> {

    public static final EnumMap<Variant, ResourceLocation> BODY_TEXTURE_MAP = new EnumMap<>(Variant.class);

    static {
        BODY_TEXTURE_MAP.put(Variant.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/black.png"));
        BODY_TEXTURE_MAP.put(Variant.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/brown.png"));
        BODY_TEXTURE_MAP.put(Variant.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/chestnut.png"));
        BODY_TEXTURE_MAP.put(Variant.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/creamy.png"));
        BODY_TEXTURE_MAP.put(Variant.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/darkbrown.png"));
        BODY_TEXTURE_MAP.put(Variant.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/gray.png"));
        BODY_TEXTURE_MAP.put(Variant.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/white.png"));
    }

    public CentaurRenderer(final EntityRendererProvider.Context context) {
        super(context, new CentaurModel<T>(context.bakeLayer(CentaurModel.CENTAUR_MODEL_RESOURCE)), 0.75F);
        this.addLayer(new HalfHorseLayer<>(this, context.getModelSet()));
        this.addLayer(new QuiverLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return BODY_TEXTURE_MAP.get(entity.getVariant());
    }
}
