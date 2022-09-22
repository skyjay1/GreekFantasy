package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.PythonModel;
import greekfantasy.entity.boss.Python;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PythonRenderer<T extends Python> extends MobRenderer<T, PythonModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/python.png");
    protected static final float SCALE = 1.4F;

    public PythonRenderer(EntityRendererProvider.Context context) {
        super(context, new PythonModel<>(context.bakeLayer(PythonModel.PYTHON_MODEL_RESOURCE)), 0.75F);
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}

