package greekfantasy.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import greekfantasy.GreekFantasy;
import greekfantasy.block.VaseBlock;
import greekfantasy.blockentity.VaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class VaseBlockEntityRenderer implements BlockEntityRenderer<VaseBlockEntity> {

    protected final BlockEntityRendererProvider.Context context;

    public VaseBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(VaseBlockEntity vaseBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        final ItemStack itemstack = vaseBlockEntity.getItem(0);
        if (!itemstack.isEmpty()) {
            final float scale = 0.315F;
            final float rotation = vaseBlockEntity.getBlockState().getValue(VaseBlock.FACING).toYRot();
            poseStack.pushPose();
            // transforms
            poseStack.translate(0.5D, 0.70D, 0.5D);
            poseStack.mulPose(Vector3f.YN.rotationDegrees(rotation));
            poseStack.scale(scale, scale, scale);
            // render the item stack
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, packedLight,
                    OverlayTexture.NO_OVERLAY, poseStack, bufferSource, 0);
            // finish rendering
            poseStack.popPose();
            // render nameplate
            if (vaseBlockEntity.hasCustomName() && shouldRenderName(vaseBlockEntity)) {
                renderNameTag(vaseBlockEntity, vaseBlockEntity.getCustomName(), poseStack, bufferSource, packedLight);
            }
        }
    }

    protected boolean shouldRenderName(final BlockEntity blockEntity) {
        final Minecraft mc = Minecraft.getInstance();
        final EntityRenderDispatcher renderManager = mc.getEntityRenderDispatcher();
        final Vec3 pos = Vec3.atCenterOf(blockEntity.getBlockPos());
        final double distance = 6.0D;
        if(renderManager.distanceToSqr(pos.x, pos.y, pos.z) < (distance * distance)
                && mc.hitResult != null
                && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = new BlockPos(mc.hitResult.getLocation());
            BlockState blockState = blockEntity.getLevel().getBlockState(blockPos);
            return blockPos.equals(blockEntity.getBlockPos()) && blockState.is(blockEntity.getBlockState().getBlock());
        }
        return false;
    }

    protected void renderNameTag(VaseBlockEntity blockEntity, Component name, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        float height = (float) blockEntity.getBlockState().getShape(blockEntity.getLevel(), blockEntity.getBlockPos(), CollisionContext.empty()).max(Direction.Axis.Y);
        float f = height + 0.5F;

        poseStack.pushPose();

        poseStack.translate(0.5D, (double)f, 0.5D);
        poseStack.mulPose(context.getBlockEntityRenderDispatcher().camera.rotation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int j = (int)(f1 * 255.0F) << 24;
        Font font = context.getFont();
        float f2 = (float)(-font.width(name) / 2);
        font.drawInBatch(name, f2, 0.0F, 553648127, false, matrix4f, multiBufferSource, true, j, packedLight);
        font.drawInBatch(name, f2, 0.0F, -1, false, matrix4f, multiBufferSource, false, 0, packedLight);

        poseStack.popPose();
    }
}
