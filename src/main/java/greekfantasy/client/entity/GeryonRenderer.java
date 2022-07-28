package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.GeryonClothingLayer;
import greekfantasy.client.entity.model.GeryonModel;
import greekfantasy.entity.boss.Geryon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GeryonRenderer<T extends Geryon> extends HumanoidMobRenderer<T, GeryonModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/geryon/geryon.png");
    private static final float SCALE = 2.0F;

    public GeryonRenderer(EntityRendererProvider.Context context) {
        super(context, new GeryonModel<T>(context.bakeLayer(GeryonModel.GERYON_MODEL_RESOURCE)), 1.0F);
        this.addLayer(new GeryonClothingLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float f) {
        // if the entity is spawning, shift the entity down
        if (entity.isSpawning()) {
            final float height = 4.96F;
            final float translateY = height * entity.getSpawnPercent(f) - height;
            poseStack.translate(0.0D, -translateY, 0.0D);
        }
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    protected boolean isShaking(T entity) {
        return entity.isSpawning();
    }
}
