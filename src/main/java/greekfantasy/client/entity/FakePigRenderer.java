package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class FakePigRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, PigModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/pig/pig.png");

    public FakePigRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.7F);
    }

    // copy of LivingRenderer#render but without Forge events
    @Override
    public void render(T entity, float renderOffsetX, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        this.model.attackTime = this.getAttackAnim(entity, partialTick);

        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = entity.isBaby();
        float f = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float f1 = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity.getVehicle();
            f = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        if (isEntityUpsideDown(entity)) {
            f6 *= -1.0F;
            f2 *= -1.0F;
        }

        if (entity.getPose() == Pose.SLEEPING) {
            Direction direction = entity.getBedOrientation();
            if (direction != null) {
                float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                poseStack.translate((double)((float)(-direction.getStepX()) * f4), 0.0D, (double)((float)(-direction.getStepZ()) * f4));
            }
        }

        float f7 = this.getBob(entity, partialTick);
        this.setupRotations(entity, poseStack, f7, f, partialTick);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, poseStack, partialTick);
        poseStack.translate(0.0D, (double)-1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entity.isAlive()) {
            f8 = Mth.lerp(partialTick, entity.animationSpeedOld, entity.animationSpeed);
            f5 = entity.animationPosition - entity.animationSpeed * (1.0F - partialTick);
            if (entity.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        this.model.prepareMobModel(entity, f5, f8, partialTick);
        this.model.setupAnim(entity, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(entity);
        boolean flag1 = !flag && !entity.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entity);
        RenderType rendertype = this.getRenderType(entity, flag, flag1, flag2);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(rendertype);
            int i = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTick));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
        }

        if (!entity.isSpectator()) {
            for(RenderLayer<T, PigModel<T>> renderlayer : this.layers) {
                renderlayer.render(poseStack, multiBufferSource, packedLight, entity, f5, f8, partialTick, f7, f2, f6);
            }
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
