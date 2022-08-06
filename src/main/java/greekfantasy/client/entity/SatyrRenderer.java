package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.SatyrGroverLayer;
import greekfantasy.client.entity.layer.SatyrPanfluteLayer;
import greekfantasy.client.entity.layer.SatyrShamanLayer;
import greekfantasy.client.entity.model.DrakainaModel;
import greekfantasy.client.entity.model.SatyrModel;
import greekfantasy.entity.Satyr;
import greekfantasy.entity.monster.Drakaina;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Variant;

import java.util.EnumMap;

public class SatyrRenderer<T extends Satyr> extends MobRenderer<T, SatyrModel<T>> {

    private static final ResourceLocation TEXTURE_GROVER = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/grover.png");

    public static final EnumMap<Variant, ResourceLocation> BODY_TEXTURE_MAP = new EnumMap<>(Variant.class);

    static {
        BODY_TEXTURE_MAP.put(Variant.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/black.png"));
        BODY_TEXTURE_MAP.put(Variant.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/brown.png"));
        BODY_TEXTURE_MAP.put(Variant.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/chestnut.png"));
        BODY_TEXTURE_MAP.put(Variant.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/creamy.png"));
        BODY_TEXTURE_MAP.put(Variant.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/darkbrown.png"));
        BODY_TEXTURE_MAP.put(Variant.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/gray.png"));
        BODY_TEXTURE_MAP.put(Variant.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/white.png"));
    }

    public SatyrRenderer(EntityRendererProvider.Context context) {
        super(context, new SatyrModel<>(context.bakeLayer(SatyrModel.SATYR_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new SatyrShamanLayer<>(this, context.getModelSet()));
        this.addLayer(new SatyrPanfluteLayer<>(this));
        this.addLayer(new SatyrGroverLayer<>(this, context.getModelSet()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        if (entity.hasCustomName() && "Grover".equals(entity.getCustomName().getString())) {
            return TEXTURE_GROVER;
        } else {
            return BODY_TEXTURE_MAP.get(entity.getVariant());
        }
    }
}

