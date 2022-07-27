package greekfantasy.client.entity;

import greekfantasy.client.entity.layer.HalfHorseLayer;
import greekfantasy.client.entity.layer.PegasusMarkingsLayer;
import greekfantasy.client.entity.layer.PegasusWingLayer;
import greekfantasy.client.entity.model.PegasusModel;
import greekfantasy.entity.Pegasus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PegasusRenderer<T extends Pegasus> extends MobRenderer<T, PegasusModel<T>> {

    public PegasusRenderer(EntityRendererProvider.Context context) {
        super(context, new PegasusModel<>(context.bakeLayer(PegasusModel.PEGASUS_MODEL_RESOURCE)), 0.75F);
        this.addLayer(new PegasusMarkingsLayer<T>(this));
        this.addLayer(new PegasusWingLayer<T>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return HalfHorseLayer.TEXTURE_MAP.get(entity.getVariant());
    }
}
