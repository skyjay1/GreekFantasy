package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.NemeanLionModel;
import greekfantasy.entity.boss.NemeanLion;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NemeanLionRenderer<T extends NemeanLion> extends MobRenderer<T, NemeanLionModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/nemean_lion.png");
    public static final float SCALE = 2.2F;

    public NemeanLionRenderer(final EntityRendererProvider.Context context) {
        super(context, new NemeanLionModel<>(context.bakeLayer(NemeanLionModel.NEMEAN_LION_MODEL_RESOURCE)), 1.0F);
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float f) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
