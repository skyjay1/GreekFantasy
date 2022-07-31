package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.MakhaiHeldItemLayer;
import greekfantasy.client.entity.model.MakhaiModel;
import greekfantasy.entity.Makhai;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MakhaiRenderer<T extends Makhai> extends HumanoidMobRenderer<T, MakhaiModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/makhai.png");

    public MakhaiRenderer(EntityRendererProvider.Context context) {
        super(context, new MakhaiModel<T>(context.bakeLayer(MakhaiModel.MAKHAI_MODEL_RESOURCE)), 0.5F);
        addLayer(new MakhaiHeldItemLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
