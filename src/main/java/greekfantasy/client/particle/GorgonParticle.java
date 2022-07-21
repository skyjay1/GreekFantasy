package greekfantasy.client.particle;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import greekfantasy.client.entity.GorgonRenderer;
import greekfantasy.client.entity.model.GorgonModel;
import greekfantasy.entity.monster.Gorgon;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class GorgonParticle extends Particle {
    private final GorgonModel<Gorgon> model;
    private final RenderType renderType;

    public GorgonParticle(ClientLevel level, double posX, double posY, double posZ, double motX, double motY, double motZ) {
        super(level, posX, posY, posZ, motX, motY, motZ);
        this.model = new GorgonModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(GorgonModel.GORGON_MODEL_RESOURCE));
        this.renderType = RenderType.entityTranslucent(GorgonRenderer.GORGON_TEXTURE);
        this.gravity = 0.0F;
        this.age = 0;
        this.lifetime = 78;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTick) {
        float agePercent = (this.age + partialTick) / this.lifetime;
        float cosAge = Mth.sin(agePercent * (float) Math.PI);
        float disZ = 0.65F * (cosAge - 0.5F);
        this.alpha = 0.05F + 0.75F * (agePercent);

        PoseStack poseStack = new PoseStack();

        poseStack.mulPose(renderInfo.rotation());
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));

        poseStack.scale(-1.0F, 1.0F, -1.0F);
        poseStack.translate(0.0D, 0.309999D, 1.15D + disZ);

        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(this.renderType);
        this.model.renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, this.alpha);
        multibuffersource$buffersource.endBatch();
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GorgonParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
