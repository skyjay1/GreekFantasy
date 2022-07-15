package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.AraFlintLayer;
import greekfantasy.client.entity.model.AraModel;
import greekfantasy.entity.monster.Ara;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AraRenderer<T extends Ara> extends HumanoidMobRenderer<T, AraModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/ara.png");


    public AraRenderer(EntityRendererProvider.Context context) {
        super(context, new AraModel<>(context.bakeLayer(AraModel.ARA_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new AraFlintLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}