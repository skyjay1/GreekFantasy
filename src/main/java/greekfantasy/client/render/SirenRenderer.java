package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.SirenModel;
import greekfantasy.entity.SirenEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SirenRenderer<T extends SirenEntity> extends BipedRenderer<T, SirenModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/siren.png");

    public SirenRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SirenModel<T>(0.0F), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
