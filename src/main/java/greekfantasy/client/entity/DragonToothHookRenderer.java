package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.DragonToothHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DragonToothHookRenderer extends EntityRenderer<DragonToothHook> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(GreekFantasy.MODID, "textures/entity/dragon_tooth_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
    private static final double VIEW_BOBBING_SCALE = 960.0D;

    public DragonToothHookRenderer(EntityRendererProvider.Context parent) {
        super(parent);
    }

    // This code is copied from FishingHookRenderer#render with a check for the custom fishing rod item instead
    @Override
    public void render(DragonToothHook entity, float renderOffsetX, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        Player player = entity.getPlayerOwner();
        if (player != null) {
            poseStack.pushPose();
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RENDER_TYPE);
            FishingHookRenderer.vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1);
            FishingHookRenderer.vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1);
            FishingHookRenderer.vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0);
            FishingHookRenderer.vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0);
            poseStack.popPose();
            int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            ItemStack itemstack = player.getMainHandItem();
            if (!(itemstack.getItem() instanceof FishingRodItem)) {
                i = -i;
            }

            float f = player.getAttackAnim(partialTick);
            float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
            float f2 = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * ((float) Math.PI / 180F);
            double d0 = (double) Mth.sin(f2);
            double d1 = (double) Mth.cos(f2);
            double d2 = (double) i * 0.35D;
            double d3 = 0.8D;
            double d4;
            double d5;
            double d6;
            float f3;
            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
                double d7 = VIEW_BOBBING_SCALE / this.entityRenderDispatcher.options.fov().get();
                Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float) i * 0.525F, -0.1F);
                vec3 = vec3.scale(d7);
                vec3 = vec3.yRot(f1 * 0.5F);
                vec3 = vec3.xRot(-f1 * 0.7F);
                d4 = Mth.lerp((double) partialTick, player.xo, player.getX()) + vec3.x;
                d5 = Mth.lerp((double) partialTick, player.yo, player.getY()) + vec3.y;
                d6 = Mth.lerp((double) partialTick, player.zo, player.getZ()) + vec3.z;
                f3 = player.getEyeHeight();
            } else {
                d4 = Mth.lerp((double) partialTick, player.xo, player.getX()) - d1 * d2 - d0 * 0.8D;
                d5 = player.yo + (double) player.getEyeHeight() + (player.getY() - player.yo) * (double) partialTick - 0.45D;
                d6 = Mth.lerp((double) partialTick, player.zo, player.getZ()) - d0 * d2 + d1 * 0.8D;
                f3 = player.isCrouching() ? -0.1875F : 0.0F;
            }

            double d9 = Mth.lerp((double) partialTick, entity.xo, entity.getX());
            double d10 = Mth.lerp((double) partialTick, entity.yo, entity.getY()) + 0.25D;
            double d8 = Mth.lerp((double) partialTick, entity.zo, entity.getZ());
            float f4 = (float) (d4 - d9);
            float f5 = (float) (d5 - d10) + f3;
            float f6 = (float) (d6 - d8);
            VertexConsumer vertexconsumer1 = multiBufferSource.getBuffer(RenderType.lineStrip());
            PoseStack.Pose posestack$pose1 = poseStack.last();
            int j = 16;

            for (int k = 0; k <= 16; ++k) {
                stringVertex(f4, f5, f6, vertexconsumer1, posestack$pose1, FishingHookRenderer.fraction(k, 16), FishingHookRenderer.fraction(k + 1, 16));
            }

            poseStack.popPose();
            super.render(entity, renderOffsetX, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }

    public static void stringVertex(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose, float startPercent, float endPercent) {
        float f = x * startPercent;
        float f1 = y * (startPercent * startPercent + startPercent) * 0.5F + 0.25F;
        float f2 = z * startPercent;
        float f3 = x * endPercent - f;
        float f4 = y * (endPercent * endPercent + endPercent) * 0.5F + 0.25F - f1;
        float f5 = z * endPercent - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 /= f6;
        f4 /= f6;
        f5 /= f6;
        vertexConsumer.vertex(pose.pose(), f, f1, f2).color(255, 204, 0, 255).normal(pose.normal(), f3, f4, f5).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(DragonToothHook entity) {
        return TEXTURE_LOCATION;
    }
}