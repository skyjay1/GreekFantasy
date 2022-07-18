package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.NymphModel;
import greekfantasy.entity.Dryad;
import greekfantasy.entity.NymphVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class DryadRenderer<T extends Dryad> extends MobRenderer<T, NymphModel<T>> {

    public static final Map<NymphVariant, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    static {
        TEXTURE_MAP.put(Dryad.Variant.ACACIA, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/acacia.png"));
        TEXTURE_MAP.put(Dryad.Variant.BIRCH, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/birch.png"));
        TEXTURE_MAP.put(Dryad.Variant.DARK_OAK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/dark_oak.png"));
        TEXTURE_MAP.put(Dryad.Variant.JUNGLE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/jungle.png"));
        TEXTURE_MAP.put(Dryad.Variant.OAK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/oak.png"));
        TEXTURE_MAP.put(Dryad.Variant.OLIVE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/olive.png"));
        TEXTURE_MAP.put(Dryad.Variant.SPRUCE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/spruce.png"));
    }

    public DryadRenderer(EntityRendererProvider.Context context) {
        super(context, new NymphModel<>(context.bakeLayer(NymphModel.NYMPH_LAYER_LOCATION), false), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE_MAP.get(entity.getVariant());
    }
}
