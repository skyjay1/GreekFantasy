package greekfantasy.client.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.GiantBoarModel;
import greekfantasy.entity.boss.GiantBoar;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class GiantBoarRenderer<T extends GiantBoar> extends MobRenderer<T, GiantBoarModel<T>> {

    private static final ResourceLocation HOGLIN_TEXTURE = new ResourceLocation("textures/entity/hoglin/hoglin.png");
    private static final ResourceLocation GIANT_BOAR_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/giant_boar.png");
    public static final float SCALE = 1.9F;

    protected boolean isAlphaLayer;

    public GiantBoarRenderer(final EntityRendererProvider.Context context) {
        super(context, new GiantBoarModel<>(context.bakeLayer(ModelLayers.HOGLIN)), 1.0F);
    }

    @Override
    public void render(final T entity, final float renderOffsetX, final float partialTick, final PoseStack poseStack,
                       final MultiBufferSource multiBufferSource, final int packedLight) {
        poseStack.pushPose();
        // scale the models
        final float spawnPercent = entity.getSpawnPercent(partialTick);
        final float scale = 1.0F + (SCALE - 1.0F) * spawnPercent;
        poseStack.scale(scale, scale, scale);

        // render the base model (erymanthian texture)
        this.getModel().setColorAlpha(1.0F);
        super.render(entity, renderOffsetX, partialTick, poseStack, multiBufferSource, packedLight);

        // prepare to render transparent layer
        if (spawnPercent < 0.99F) {
            isAlphaLayer = true;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            // render transparent layer
            this.model.setColorAlpha(1.0F - spawnPercent);
            super.render(entity, renderOffsetX, partialTick, poseStack, multiBufferSource, packedLight);
            RenderSystem.disableBlend();
            isAlphaLayer = false;
        }
        poseStack.popPose();
    }

    @Override
    protected boolean isShaking(T entity) {
        return entity.isConverting();
    }

    @Override
    protected void scale(final T entity, PoseStack matrix, float ageInTicks) {
        // scaling is handled in render method to avoid redundant spawn size calculations
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return GIANT_BOAR_TEXTURE;
    }

    @Override
    @Nullable
    protected RenderType getRenderType(final T entity, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
        if(isAlphaLayer && (isVisible || isVisibleToPlayer)) {
            return RenderType.entityTranslucent(HOGLIN_TEXTURE, isGlowing);
        }
        return super.getRenderType(entity, isVisible, isVisibleToPlayer, isGlowing);
    }
}
