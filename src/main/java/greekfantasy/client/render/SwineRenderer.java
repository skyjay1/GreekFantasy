package greekfantasy.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class SwineRenderer<T extends LivingEntity>  extends LivingRenderer<T, PigModel<T>> {
  
  private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");
//  private static final RenderType RENDER_TYPE = RenderType.getEntityCutoutNoCull(PIG_TEXTURES);

  public SwineRenderer(EntityRendererManager renderManagerIn) {
     super(renderManagerIn, new PigModel<>(), 0.7F);
  }
  
//  public void render(final RenderPlayerEvent.Pre event, final T entityIn) {
//    final float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
//    final int packedLightIn = this.renderManager.getPackedLight(entityIn, partialTicks);
////    Vector3d vector3d = this.getRenderOffset(entityIn, partialTicks);
//    final MatrixStack matrixStackIn = new MatrixStack();
//    IRenderTypeBuffer irendertypebuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();    
////    IVertexBuilder ivertexbuilder = irendertypebuffer.getBuffer(RenderType.getEntityCutoutNoCull(PIG_TEXTURES));
//    
//    double dX = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
//    double dY = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
//    double dZ = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
//    float rotationYawIn = MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw);
//    Vector3d vector3d = this.getRenderOffset(entityIn, partialTicks);
//    double d2 = dX + vector3d.getX();
//    double d3 = dY + vector3d.getY();
//    double d0 = dZ + vector3d.getZ();
//    matrixStackIn.push();
//    //matrixStackIn.translate(d2, d3, d0);
//    //matrixStackIn.translate(dX, dY, dZ);
//    this.render(entityIn, rotationYawIn, partialTicks, matrixStackIn, irendertypebuffer, packedLightIn);
//    if (entityIn.canRenderOnFire()) {
////       renderManager.renderFire(matrixStackIn, irendertypebuffer, entityIn);
//    }
//
//    matrixStackIn.translate(-vector3d.getX(), -vector3d.getY(), -vector3d.getZ());
//    if (renderManager.options.entityShadows /*&& renderManager.renderShadow*/ && this.shadowSize > 0.0F && !entityIn.isInvisible()) {
//       double d1 = renderManager.getDistanceToCamera(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
//       float sh = (float)((1.0D - d1 / 256.0D) * (double)this.shadowOpaque);
//       if (sh > 0.0F) {
////          renderManager.renderShadow(matrixStackIn, irendertypebuffer, entityIn, sh, partialTicks, event.getPlayer().world, this.shadowSize);
//       }
//    }
//
////    if (renderManager.debugBoundingBox && !entityIn.isInvisible() && !Minecraft.getInstance().isReducedDebug()) {
////       this.renderDebugBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), entityIn, partialTicks);
////    }
//    
//    
////    this.render(entityIn, entityIn.rotationYaw, partialTicks, matrixStackIn, irendertypebuffer, packedLightIn); // , LivingRenderer.getPackedOverlay(entityIn, 0.0F)
//
//  }
//  
//  @Override
//  public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
//    matrixStackIn.push();
//    this.entityModel.swingProgress = this.getSwingProgress(entityIn, partialTicks);
//
//    boolean shouldSit = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
//    this.entityModel.isSitting = shouldSit;
//    this.entityModel.isChild = entityIn.isChild();
//    float f = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
//    float f1 = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
//    float f2 = f1 - f;
//    if (shouldSit && entityIn.getRidingEntity() instanceof LivingEntity) {
//       LivingEntity livingentity = (LivingEntity)entityIn.getRidingEntity();
//       f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
//       f2 = f1 - f;
//       float f3 = MathHelper.wrapDegrees(f2);
//       if (f3 < -85.0F) {
//          f3 = -85.0F;
//       }
//
//       if (f3 >= 85.0F) {
//          f3 = 85.0F;
//       }
//
//       f = f1 - f3;
//       if (f3 * f3 > 2500.0F) {
//          f += f3 * 0.2F;
//       }
//
//       f2 = f1 - f;
//    }
//
//    float f6 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
//    if (entityIn.getPose() == Pose.SLEEPING) {
//       Direction direction = entityIn.getBedDirection();
//       if (direction != null) {
//          float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
//          matrixStackIn.translate((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
//       }
//    }
//
//    float f7 = this.handleRotationFloat(entityIn, partialTicks);
//    this.applyRotations(entityIn, matrixStackIn, f7, f, partialTicks);
//    matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
//    this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
//    matrixStackIn.translate(0.0D, (double)-1.501F, 0.0D);
//    float f8 = 0.0F;
//    float f5 = 0.0F;
//    if (!shouldSit && entityIn.isAlive()) {
//       f8 = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
//       f5 = entityIn.limbSwing - entityIn.limbSwingAmount * (1.0F - partialTicks);
//       if (entityIn.isChild()) {
//          f5 *= 3.0F;
//       }
//
//       if (f8 > 1.0F) {
//          f8 = 1.0F;
//       }
//    }
//
//    this.entityModel.setLivingAnimations(entityIn, f5, f8, partialTicks);
//    this.entityModel.setRotationAngles(entityIn, f5, f8, f7, f2, f6);
//    Minecraft minecraft = Minecraft.getInstance();
//    boolean flag = this.isVisible(entityIn);
//    boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(minecraft.player);
//    boolean flag2 = minecraft.isEntityGlowing(entityIn);
//    RenderType rendertype = this.func_230496_a_(entityIn, flag, flag1, flag2);
//    if (rendertype != null) {
//       IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
//       int i = getPackedOverlay(entityIn, this.getOverlayProgress(entityIn, partialTicks));
//       this.entityModel.render(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
//    }
//
//    if (!entityIn.isSpectator()) {
//       for(LayerRenderer<T, PigModel<T>> layerrenderer : this.layerRenderers) {
//          layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
//       }
//    }
//
//    matrixStackIn.pop();
//    // render nameplate
//    net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
//    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
//    if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.canRenderName(entityIn))) {
//       this.renderName(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
//    }
// }

  @Override
  public ResourceLocation getEntityTexture(T entity) {
     return PIG_TEXTURES;
  }
}
