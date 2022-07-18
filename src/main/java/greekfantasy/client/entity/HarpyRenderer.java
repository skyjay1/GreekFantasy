package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.HarpyModel;
import greekfantasy.entity.monster.Harpy;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HarpyRenderer<T extends Harpy> extends HumanoidMobRenderer<T, HarpyModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/harpy.png");

    public HarpyRenderer(EntityRendererProvider.Context context) {
        super(context, new HarpyModel<>(context.bakeLayer(HarpyModel.HARPY_MODEL_RESOURCE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}