package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.TalosModel;
import greekfantasy.entity.boss.Talos;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TalosRenderer<T extends Talos> extends MobRenderer<T, TalosModel<T>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/talos.png");
    public static final float SCALE = 2.0F;

    public TalosRenderer(EntityRendererProvider.Context context) {
        super(context, new TalosModel<>(context.bakeLayer(TalosModel.TALOS_MODEL_RESOURCE)), 0.75F);
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