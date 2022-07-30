package greekfantasy.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.blockentity.MobHeadBlockEntity;
import greekfantasy.client.blockentity.model.CerberusHeadModel;
import greekfantasy.client.entity.CerberusRenderer;
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

public class CerberusHeadBlockEntityRenderer extends MobHeadBlockEntityRenderer {

    public CerberusHeadBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        super(context, new CerberusHeadModel(context.getModelSet().bakeLayer(CerberusHeadModel.CERBERUS_HEAD_MODEL_RESOURCE)));
    }

    @Override
    public ResourceLocation getTexture() {
        return CerberusRenderer.TEXTURE;
    }

    @Override
    public float getScale() {
        return CerberusRenderer.SCALE;
    }

    @Override
    public void applyRotations(PoseStack poseStack, final boolean isOnWall) {
        // do nothing
    }

    @Override
    public boolean shouldRenderOffScreen(MobHeadBlockEntity blockEntity) {
        return true;
    }

    public static class CerberusHeadItemStackRenderer extends BlockEntityWithoutLevelRenderer {
        private static final ResourceLocation TEXTURE = CerberusRenderer.TEXTURE;
        private final EntityModelSet entityModelSet;
        private CerberusHeadModel model;

        public CerberusHeadItemStackRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
            super(dispatcher, entityModelSet);
            this.entityModelSet = entityModelSet;
            this.model = new CerberusHeadModel(this.entityModelSet.bakeLayer(CerberusHeadModel.CERBERUS_HEAD_MODEL_RESOURCE));
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            this.model = new CerberusHeadModel(this.entityModelSet.bakeLayer(CerberusHeadModel.CERBERUS_HEAD_MODEL_RESOURCE));
        }

        @Override
        public void renderByItem(ItemStack item, ItemTransforms.TransformType transform, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
            poseStack.pushPose();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            VertexConsumer vertexBuilder = ItemRenderer.getFoilBufferDirect(bufferSource, this.model.renderType(TEXTURE), false, item.hasFoil());
            model.renderToBuffer(poseStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }

}
