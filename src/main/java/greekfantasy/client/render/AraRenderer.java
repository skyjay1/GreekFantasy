package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.AraFlintLayer;
import greekfantasy.client.render.model.AraModel;
import greekfantasy.entity.AraEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class AraRenderer<T extends AraEntity> extends BipedRenderer<T, AraModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/ara.png");

    public AraRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new AraModel<T>(0.0F), 0.5F);
        this.addLayer(new AraFlintLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
