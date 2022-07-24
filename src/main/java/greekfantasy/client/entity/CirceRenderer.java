package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.CirceModel;
import greekfantasy.entity.monster.Circe;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CirceRenderer<T extends Circe> extends HumanoidMobRenderer<T, CirceModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/circe.png");

    public CirceRenderer(EntityRendererProvider.Context context) {
        super(context, new CirceModel<>(context.bakeLayer(CirceModel.CIRCE_MODEL_RESOURCE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}