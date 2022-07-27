package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.BronzeBullModel;
import greekfantasy.entity.boss.BronzeBull;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BronzeBullRenderer<T extends BronzeBull> extends MobRenderer<T, BronzeBullModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/bronze_bull.png");
    public static final float SCALE = 1.5F;

    public BronzeBullRenderer(EntityRendererProvider.Context context) {
        super(context, new BronzeBullModel<>(context.bakeLayer(BronzeBullModel.BULL_MODEL_RESOURCE)), 1.0F);
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
