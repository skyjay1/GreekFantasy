package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SwineRenderer<T extends LivingEntity> extends LivingRenderer<T, PigModel<T>> {
  
  private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");
  
  public SwineRenderer(EntityRendererManager renderManagerIn) {
     super(renderManagerIn, new PigModel<>(), 0.7F);
  }
  
  // This method is a copy of the super methods but without firing additional render events (to avoid recursion)
  @Override
  public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    matrixStackIn.pushPose();
    this.model.attackTime = this.getAttackAnim(entityIn, partialTicks);

    boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
    this.model.riding = shouldSit;
    this.model.young = entityIn.isBaby();
    float f = MathHelper.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
    float f1 = MathHelper.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
    float f2 = f1 - f;
    if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
       LivingEntity livingentity = (LivingEntity)entityIn.getVehicle();
       f = MathHelper.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
       f2 = f1 - f;
       float f3 = MathHelper.wrapDegrees(f2);
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

    float f6 = MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot);
    if (entityIn.getPose() == Pose.SLEEPING) {
       Direction direction = entityIn.getBedOrientation();
       if (direction != null) {
          float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
          matrixStackIn.translate((double)((float)(-direction.getStepX()) * f4), 0.0D, (double)((float)(-direction.getStepZ()) * f4));
       }
    }

    float f7 = this.getBob(entityIn, partialTicks);
    this.setupRotations(entityIn, matrixStackIn, f7, f, partialTicks);
    matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
    this.scale(entityIn, matrixStackIn, partialTicks);
    matrixStackIn.translate(0.0D, (double)-1.501F, 0.0D);
    float f8 = 0.0F;
    float f5 = 0.0F;
    if (!shouldSit && entityIn.isAlive()) {
       f8 = MathHelper.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
       f5 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
       if (entityIn.isBaby()) {
          f5 *= 3.0F;
       }

       if (f8 > 1.0F) {
          f8 = 1.0F;
       }
    }

    this.model.prepareMobModel(entityIn, f5, f8, partialTicks);
    this.model.setupAnim(entityIn, f5, f8, f7, f2, f6);
    Minecraft minecraft = Minecraft.getInstance();
    boolean flag = this.isBodyVisible(entityIn);
    boolean flag1 = !flag && !entityIn.isInvisibleTo(minecraft.player);
    boolean flag2 = minecraft.shouldEntityAppearGlowing(entityIn);
    RenderType rendertype = this.getRenderType(entityIn, flag, flag1, flag2);
    if (rendertype != null) {
       IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
       int i = getOverlayCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));
       this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
    }

//    if (!entityIn.isSpectator()) {
//       for(LayerRenderer<T, M> layerrenderer : this.layerRenderers) {
//          layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
//       }
//    }

    matrixStackIn.popPose();
    
    // render nameplate
//    net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
//    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
//    if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.canRenderName(entityIn))) {
//       this.renderName(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
//    }
 }

  @Override
  public ResourceLocation getTextureLocation(T entity) {
     return PIG_TEXTURES;
  }
}
