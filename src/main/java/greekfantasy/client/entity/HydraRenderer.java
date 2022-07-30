package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.HydraBodyModel;
import greekfantasy.entity.boss.Hydra;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HydraRenderer<T extends Hydra> extends MobRenderer<T, HydraBodyModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra_body.png");
    private static final float SCALE = 1.35F;


    public HydraRenderer(final EntityRendererProvider.Context context) {
        super(context, new HydraBodyModel<>(context.bakeLayer(HydraBodyModel.HYDRA_BODY_MODEL_RESOURCE)), 0.5F);
    }

    @Override
    protected void scale(final T entity, PoseStack matrix, float partialTick) {
        matrix.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
