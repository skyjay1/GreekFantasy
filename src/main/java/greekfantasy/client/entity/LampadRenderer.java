package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.NymphModel;
import greekfantasy.entity.Dryad;
import greekfantasy.entity.Lampad;
import greekfantasy.entity.NymphVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class LampadRenderer<T extends Lampad> extends MobRenderer<T, NymphModel<T>> {

    public static final Map<NymphVariant, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    static {
        TEXTURE_MAP.put(Lampad.Variant.CRIMSON, new ResourceLocation(GreekFantasy.MODID, "textures/entity/lampad/crimson.png"));
        TEXTURE_MAP.put(Lampad.Variant.WARPED, new ResourceLocation(GreekFantasy.MODID, "textures/entity/lampad/warped.png"));
        TEXTURE_MAP.put(Lampad.Variant.POMEGRANATE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/lampad/pomegranate.png"));
    }

    public LampadRenderer(EntityRendererProvider.Context context) {
        super(context, new NymphModel<>(context.bakeLayer(NymphModel.NYMPH_LAYER_LOCATION), false), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE_MAP.get(entity.getVariant());
    }
}
