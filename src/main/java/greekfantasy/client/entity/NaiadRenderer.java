package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.NymphModel;
import greekfantasy.entity.Naiad;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class NaiadRenderer<T extends Naiad> extends MobRenderer<T, NymphModel<T>> {

    public static final Map<Naiad.Variant, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    static {
        TEXTURE_MAP.put(Naiad.Variant.RIVER, new ResourceLocation(GreekFantasy.MODID, "textures/entity/naiad/river.png"));
        TEXTURE_MAP.put(Naiad.Variant.OCEAN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/naiad/ocean.png"));
    }

    public NaiadRenderer(EntityRendererProvider.Context context) {
        super(context, new NymphModel<>(context.bakeLayer(NymphModel.NYMPH_LAYER_LOCATION), false), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE_MAP.get(entity.getVariant());
    }
}
