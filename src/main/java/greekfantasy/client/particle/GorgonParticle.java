package greekfantasy.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.GorgonModel;
import greekfantasy.client.render.GorgonRenderer;
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
import net.minecraft.util.math.vector.Vector3f;

public class GorgonParticle extends Particle {
  private final GorgonModel<GorgonEntity> model = new GorgonModel<>(0.0F);
  private final RenderType renderType = RenderType.getEntityTranslucent(GorgonRenderer.TEXTURE);
  
  public GorgonParticle(ClientWorld worldIn, double posX, double posY, double posZ, double motX, double motY, double motZ) {
    super(worldIn, posX, posY, posZ, motX, motY, motZ);
    GreekFantasy.LOGGER.info("Spawning gorgon particle...");
    this.particleGravity = 0.0F;
    this.age = 0;
    this.maxAge = 120;
  }

  @Override
  public IParticleRenderType getRenderType() { return IParticleRenderType.CUSTOM; }
  
  @Override
  public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTick) {
    float agePercent = (this.age) / this.maxAge;
    //float alpha = 0.05F + 0.5F * MathHelper.sin(agePercent * 3.1415927F);
    float alpha = 0.75F;
    MatrixStack matrixStack = new MatrixStack();
    
    matrixStack.rotate(renderInfo.getRotation());
    //matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
    //matrixStack.scale(1.0F, -1.0F, 1.0F);
    //matrixStack.translate(0.0D, -0.5D, 0.5D);
    
    matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
    matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
    matrixStack.translate(0.0D, -0.25D, 0.0D);
    
    IRenderTypeBuffer.Impl bufferImpl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
    IVertexBuilder vertexBuilder = bufferImpl.getBuffer(this.renderType);
    this.model.getModelHead().render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
    this.model.renderSnakeHair(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, this.age + partialTick);
    
    bufferImpl.finish();
  }
  
  public static class Factory implements IParticleFactory<BasicParticleType> {
    @Override
    public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return new GorgonParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
    }    
  }
}
