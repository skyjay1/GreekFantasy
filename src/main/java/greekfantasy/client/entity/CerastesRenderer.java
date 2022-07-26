package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.CerastesModel;
import greekfantasy.entity.Cerastes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CerastesRenderer<T extends Cerastes> extends MobRenderer<T, CerastesModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerastes.png");

    public CerastesRenderer(EntityRendererProvider.Context context) {
        super(context, new CerastesModel<>(context.bakeLayer(CerastesModel.CERASTES_LAYER_LOCATION)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}