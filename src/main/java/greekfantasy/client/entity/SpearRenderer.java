package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.SpearModel;
import greekfantasy.entity.misc.Spear;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class SpearRenderer<T extends Spear> extends EntityRenderer<T> {

    private final SpearModel model;

    public SpearRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new SpearModel(context.bakeLayer(SpearModel.SPEAR_MODEL_RESOURCE));
    }

    @Override
    public void render(T entity, float renderOffsetX, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F));

        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(bufferSource, this.model.renderType(getTextureLocation(entity)), false, entity.hasFoil());
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(entity, renderOffsetX, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture();
    }

    public static class SpearItemStackRenderer extends BlockEntityWithoutLevelRenderer {
        private final EntityModelSet entityModelSet;
        private final ResourceLocation texture;

        private SpearModel spearModel;

        public SpearItemStackRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet, final ResourceLocation name) {
            super(dispatcher, entityModelSet);
            this.entityModelSet = entityModelSet;
            this.spearModel = new SpearModel(this.entityModelSet.bakeLayer(SpearModel.SPEAR_MODEL_RESOURCE));
            this.texture = new ResourceLocation(name.getNamespace(), "textures/entity/spear/" + name.getPath() + ".png");
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            this.spearModel = new SpearModel(this.entityModelSet.bakeLayer(SpearModel.SPEAR_MODEL_RESOURCE));
        }

        @Override
        public void renderByItem(ItemStack item, ItemTransforms.TransformType transform, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
            poseStack.pushPose();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            VertexConsumer vertexBuilder = ItemRenderer.getFoilBufferDirect(bufferSource, this.spearModel.renderType(texture), false, item.hasFoil());
            spearModel.renderToBuffer(poseStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }
}

