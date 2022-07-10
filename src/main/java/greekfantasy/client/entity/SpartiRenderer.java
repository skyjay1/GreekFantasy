package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.SpartiClothingLayer;
import greekfantasy.entity.Sparti;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;

public class SpartiRenderer extends HumanoidMobRenderer<Sparti, SkeletonModel<Sparti>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/sparti/sparti.png");

    public SpartiRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public SpartiRenderer(EntityRendererProvider.Context context, ModelLayerLocation model, ModelLayerLocation innerArmor, ModelLayerLocation outerArmor) {
        super(context, new SkeletonModel<>(context.bakeLayer(model)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel<>(context.bakeLayer(innerArmor)), new SkeletonModel<>(context.bakeLayer(outerArmor))));
        this.addLayer(new SpartiClothingLayer<>(this, context.getModelSet()));
    }

    @Override
    protected void scale(final Sparti entity, PoseStack poseStack, float ageInTicks) {
        // if the entity is spawning, shift the entity down
        if (entity.isSpawning()) {
            final float height = 1.99F;
            final float translateY = height * entity.getSpawnPercent() - height;
            poseStack.translate(0.0F, -translateY, 0.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Sparti entity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(Sparti entity) {
        return entity.isSpawning();
    }
}