package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.GiganteModel;
import greekfantasy.entity.Gigante;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GiganteRenderer<T extends Gigante> extends HumanoidMobRenderer<T, GiganteModel<T>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/gigante.png");
    public static final float SCALE = 1.9F;

    public GiganteRenderer(EntityRendererProvider.Context context) {
        super(context, new GiganteModel<>(context.bakeLayer(GiganteModel.GIGANTE_MODEL_RESOURCE)), 0.75F);
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}