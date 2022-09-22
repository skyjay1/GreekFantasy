package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.DrakainaModel;
import greekfantasy.entity.monster.Drakaina;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;

public class DrakainaRenderer<T extends Drakaina> extends MobRenderer<T, DrakainaModel<T>> {

    protected static final EnumMap<Drakaina.Variant, ResourceLocation> TEXTURE_MAP = new EnumMap<>(Drakaina.Variant.class);

    static {
        TEXTURE_MAP.put(Drakaina.Variant.GREEN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/drakaina/green.png"));
        TEXTURE_MAP.put(Drakaina.Variant.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/drakaina/brown.png"));
        TEXTURE_MAP.put(Drakaina.Variant.RED, new ResourceLocation(GreekFantasy.MODID, "textures/entity/drakaina/red.png"));
    }

    public DrakainaRenderer(EntityRendererProvider.Context context) {
        super(context, new DrakainaModel<>(context.bakeLayer(DrakainaModel.DRAKAINA_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE_MAP.get(entity.getVariant());
    }
}

