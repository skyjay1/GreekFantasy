package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.ArachneEyesLayer;
import greekfantasy.client.entity.model.ArachneModel;
import greekfantasy.entity.boss.Arachne;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ArachneRenderer<T extends Arachne> extends HumanoidMobRenderer<T, ArachneModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/arachne/arachne.png");

    public ArachneRenderer(final EntityRendererProvider.Context context) {
        super(context, new ArachneModel<T>(context.bakeLayer(ArachneModel.ARACHNE_MODEL_RESOURCE)), 0.8F);
        this.addLayer(new ArachneEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
