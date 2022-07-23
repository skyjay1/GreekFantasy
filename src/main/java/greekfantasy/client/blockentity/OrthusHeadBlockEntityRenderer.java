package greekfantasy.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.blockentity.MobHeadBlockEntity;
import greekfantasy.client.blockentity.model.OrthusHeadModel;
import greekfantasy.client.entity.OrthusRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

public class OrthusHeadBlockEntityRenderer extends MobHeadBlockEntityRenderer {

    public OrthusHeadBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        super(context, new OrthusHeadModel(context.getModelSet().bakeLayer(OrthusHeadModel.ORTHUS_HEAD_MODEL_RESOURCE)));
    }

    @Override
    public ResourceLocation getTexture() {
        return OrthusRenderer.TEXTURE;
    }

    @Override
    public float getScale() {
        return 1.0F;
    }

    @Override
    public void applyRotations(PoseStack poseStack, final boolean isOnWall) {
        // shift model down
        if(isOnWall) {
            poseStack.translate(0.0F, 8.0F / 16.0F, 6.0F / 16.0F);
        } else {
            poseStack.translate(0.0D, 13.0F / 16.0F, 0.0D / 16.0F);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(MobHeadBlockEntity blockEntity) {
        return true;
    }

    public static class OrthusHeadItemStackRenderer extends BlockEntityWithoutLevelRenderer {
        private static final ResourceLocation TEXTURE = OrthusRenderer.TEXTURE;
        private final EntityModelSet entityModelSet;
        private OrthusHeadModel model;

        public OrthusHeadItemStackRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
            super(dispatcher, entityModelSet);
            this.entityModelSet = entityModelSet;
            this.model = new OrthusHeadModel(this.entityModelSet.bakeLayer(OrthusHeadModel.ORTHUS_HEAD_MODEL_RESOURCE));
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            this.model = new OrthusHeadModel(this.entityModelSet.bakeLayer(OrthusHeadModel.ORTHUS_HEAD_MODEL_RESOURCE));
        }

        @Override
        public void renderByItem(ItemStack item, ItemTransforms.TransformType transform, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, -2.0F / 16.0F);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            VertexConsumer vertexBuilder = ItemRenderer.getFoilBufferDirect(bufferSource, this.model.renderType(TEXTURE), false, item.hasFoil());
            model.renderToBuffer(poseStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }

}
