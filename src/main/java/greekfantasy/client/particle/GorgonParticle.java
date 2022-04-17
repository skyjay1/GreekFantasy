package greekfantasy.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.GorgonRenderer;
import greekfantasy.client.render.model.GorgonModel;
import greekfantasy.entity.GorgonEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class GorgonParticle extends Particle {
    private final GorgonModel<GorgonEntity> model;
    private final RenderType renderType;

    public GorgonParticle(ClientWorld worldIn, double posX, double posY, double posZ, double motX, double motY, double motZ) {
        super(worldIn, posX, posY, posZ, motX, motY, motZ);
        GreekFantasy.LOGGER.debug("Spawning gorgon particle at " + posX + ", " + posY + ", " + posZ);
        this.model = new GorgonModel<>(0.0F);
        this.renderType = RenderType.entityTranslucent(GorgonRenderer.GORGON_TEXTURE);
        this.gravity = 0.0F;
        this.age = 0;
        this.lifetime = 78;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    @Override
    public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTick) {
        float agePercent = (this.age + partialTick) / this.lifetime;
        float cosAge = MathHelper.sin(agePercent * (float) Math.PI);
        float disZ = 0.65F * (cosAge - 0.5F);
        this.alpha = 0.05F + 0.75F * (agePercent);

        MatrixStack matrixStack = new MatrixStack();

        matrixStack.mulPose(renderInfo.rotation());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));

        matrixStack.scale(-1.0F, 1.0F, -1.0F);
        matrixStack.translate(0.0D, 0.309999D, 1.15D + disZ);

        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(this.renderType);
        this.model.getHead().render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, this.alpha);
        this.model.renderSnakeHair(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, this.age + partialTick, this.alpha);

        renderTypeBuffer.endBatch();
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        @Override
        public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GorgonParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
