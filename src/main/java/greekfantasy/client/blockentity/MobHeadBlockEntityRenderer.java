package greekfantasy.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import greekfantasy.block.MobHeadBlock;
import greekfantasy.blockentity.MobHeadBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public abstract class MobHeadBlockEntityRenderer implements BlockEntityRenderer<MobHeadBlockEntity> {

    protected final BlockEntityRendererProvider.Context context;
    protected final Model model;

    public MobHeadBlockEntityRenderer(final BlockEntityRendererProvider.Context context, final Model model) {
        this.context = context;
        this.model = model;
    }

    @Override
    public void render(MobHeadBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        // determine texture, rotations, and style
        final float rotation = blockEntity.getBlockState().getValue(MobHeadBlock.FACING).toYRot();
        final ResourceLocation texture = getTexture();
        final float scale = getScale();
        // prepare to render model
        poseStack.pushPose();

        // apply wall rotations, if any
        applyRotations(poseStack, blockEntity.onWall());
        poseStack.translate(0.5D, 1.0F, 0.5D);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        //poseStack.translate(-0.5D / scale, 0.0D, -0.5D / scale);
        VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        // render the model
        model.renderToBuffer(poseStack, vertexBuilder, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    public abstract ResourceLocation getTexture();

    public abstract float getScale();

    public abstract void applyRotations(PoseStack poseStack, final boolean isOnWall);

}
