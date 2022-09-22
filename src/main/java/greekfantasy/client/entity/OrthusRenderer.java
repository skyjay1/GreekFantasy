package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.OrthusEyesLayer;
import greekfantasy.client.entity.model.OrthusModel;
import greekfantasy.entity.Orthus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class OrthusRenderer<T extends Orthus> extends MobRenderer<T, OrthusModel<T>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/orthus/orthus.png");

    public OrthusRenderer(EntityRendererProvider.Context context) {
        super(context, new OrthusModel<>(context.bakeLayer(OrthusModel.ORTHUS_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new OrthusEyesLayer<>(this));
    }

    @Override
    protected float getBob(T entity, float partialTick) {
        return entity.getTailAngle();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}