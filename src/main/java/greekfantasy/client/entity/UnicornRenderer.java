package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.UnicornHornLayer;
import greekfantasy.client.entity.model.UnicornModel;
import greekfantasy.entity.Unicorn;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class UnicornRenderer<T extends Unicorn> extends MobRenderer<T, UnicornModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/unicorn/unicorn.png");

    public UnicornRenderer(EntityRendererProvider.Context context) {
        super(context, new UnicornModel<T>(context.bakeLayer(UnicornModel.UNICORN_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new UnicornHornLayer<T>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
