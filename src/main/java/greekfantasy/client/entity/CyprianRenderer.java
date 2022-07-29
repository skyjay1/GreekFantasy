package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.HalfHorseLayer;
import greekfantasy.client.entity.layer.HalfHorseMarkingsLayer;
import greekfantasy.client.entity.layer.QuiverLayer;
import greekfantasy.client.entity.model.CyprianModel;
import greekfantasy.entity.monster.Cyprian;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Variant;

import java.util.EnumMap;

public class CyprianRenderer<T extends Cyprian> extends HumanoidMobRenderer<T, CyprianModel<T>> {

    public static final EnumMap<Variant, ResourceLocation> BODY_TEXTURE_MAP = new EnumMap<>(Variant.class);

    static {
        BODY_TEXTURE_MAP.put(Variant.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyprian/black.png"));
        BODY_TEXTURE_MAP.put(Variant.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyprian/brown.png"));
        BODY_TEXTURE_MAP.put(Variant.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyprian/chestnut.png"));
        BODY_TEXTURE_MAP.put(Variant.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyprian/creamy.png"));
        BODY_TEXTURE_MAP.put(Variant.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyprian/darkbrown.png"));
        BODY_TEXTURE_MAP.put(Variant.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyprian/gray.png"));
        BODY_TEXTURE_MAP.put(Variant.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyprian/white.png"));
    }

    public CyprianRenderer(final EntityRendererProvider.Context context) {
        super(context, new CyprianModel<T>(context.bakeLayer(CyprianModel.CYPRIAN_MODEL_RESOURCE)), 0.75F);
        this.addLayer(new HalfHorseLayer<>(this, context.getModelSet()));
        this.addLayer(new HalfHorseMarkingsLayer<>(this, context.getModelSet()));
        this.addLayer(new QuiverLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return BODY_TEXTURE_MAP.get(entity.getVariant());
    }
}
