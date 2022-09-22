package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.ShadeModel;
import greekfantasy.entity.monster.Shade;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ShadeRenderer<T extends Shade> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/shade.png");

    public ShadeRenderer(EntityRendererProvider.Context context) {
        super(context, new ShadeModel(context.bakeLayer(ShadeModel.SHADE_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float partialTick) {
        // bob the entity up and down
        float ticks = entity.getId() * 2 + entity.tickCount + partialTick;
        final float translateY = 0.15F * Mth.cos(ticks * 0.1F) + 0.25F;
        poseStack.translate(0.0D, translateY, 0.0D);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}