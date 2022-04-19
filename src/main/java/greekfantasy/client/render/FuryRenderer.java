package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.FuryHairLayer;
import greekfantasy.client.render.model.FuryModel;
import greekfantasy.entity.FuryEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class FuryRenderer<T extends FuryEntity> extends BipedRenderer<T, FuryModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/fury.png");

    public FuryRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new FuryModel<T>(0.0F), 0.5F);
        this.addLayer(new FuryHairLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
