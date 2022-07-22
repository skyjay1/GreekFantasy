package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.CyclopsModel;
import greekfantasy.entity.monster.Cyclops;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CyclopsRenderer<T extends Cyclops> extends HumanoidMobRenderer<T, CyclopsModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cyclops.png");
    private static final float SCALE = 1.15F;

    public CyclopsRenderer(EntityRendererProvider.Context context) {
        super(context, new CyclopsModel<>(context.bakeLayer(CyclopsModel.CYCLOPS_MODEL_RESOURCE)), 0.75F);
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