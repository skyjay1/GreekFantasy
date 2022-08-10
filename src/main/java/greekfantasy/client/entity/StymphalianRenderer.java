package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.StymphalianModel;
import greekfantasy.entity.monster.Stymphalian;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StymphalianRenderer<T extends Stymphalian> extends MobRenderer<T, StymphalianModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/stymphalian.png");

    public StymphalianRenderer(EntityRendererProvider.Context context) {
        super(context, new StymphalianModel<>(context.bakeLayer(StymphalianModel.STYMPHALIAN_MODEL_RESOURCE)), 0.25F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}