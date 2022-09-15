package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.TritonModel;
import greekfantasy.entity.Triton;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TritonRenderer<T extends Triton> extends HumanoidMobRenderer<T, TritonModel<T>> {

    private static final ResourceLocation TEXTURE_DEFAULT = new ResourceLocation(GreekFantasy.MODID, "textures/entity/triton/default.png");
    private static final ResourceLocation TEXTURE_SLIM = new ResourceLocation(GreekFantasy.MODID, "textures/entity/triton/slim.png");

    public TritonRenderer(EntityRendererProvider.Context context) {
        super(context, new TritonModel<>(context.bakeLayer(TritonModel.TRITON_MODEL_RESOURCE)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return entity.isSlim() ? TEXTURE_SLIM : TEXTURE_DEFAULT;
    }
}
