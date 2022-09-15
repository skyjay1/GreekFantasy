package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.MerfolkModel;
import greekfantasy.entity.Merfolk;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class MerfolkRenderer<T extends Merfolk> extends MobRenderer<T, MerfolkModel<T>> {

    private static final ResourceLocation TEXTURE_DEFAULT = new ResourceLocation(GreekFantasy.MODID, "textures/entity/merfolk/default.png");
    private static final ResourceLocation TEXTURE_SLIM = new ResourceLocation(GreekFantasy.MODID, "textures/entity/merfolk/slim.png");

    public MerfolkRenderer(EntityRendererProvider.Context context) {
        super(context, new MerfolkModel<>(context.bakeLayer(MerfolkModel.MERFOLK_MODEL_RESOURCE)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return entity.isSlim() ? TEXTURE_SLIM : TEXTURE_DEFAULT;
    }
}
