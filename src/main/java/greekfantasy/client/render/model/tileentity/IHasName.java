package greekfantasy.client.render.model.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

public interface IHasName<T extends TileEntity> {
  
  public void renderName(final T entityIn, final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn);
  
  public default boolean canRenderName(final T entityIn) {
    return isWithinDistanceToRenderName(entityIn, 6.0D);
  }
  
  public static boolean isWithinDistanceToRenderName(final TileEntity entityIn, final double dis) {
    final Minecraft mc = Minecraft.getInstance();
    final EntityRendererManager renderManager = mc.getEntityRenderDispatcher();
    final Vector3d pos = Vector3d.atCenterOf(entityIn.getBlockPos());
    return renderManager.distanceToSqr(pos.x, pos.y, pos.z) < (dis * dis)
        && mc.hitResult != null
        && mc.hitResult.getType() == RayTraceResult.Type.BLOCK
        && mc.hitResult.getLocation().distanceToSqr(pos) < 0.9D;
  }
  
  public static void renderNameplate(final TileEntity entityIn, final ITextComponent displayNameIn, final float offsetY, final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    final Minecraft mc = Minecraft.getInstance();
    final EntityRendererManager renderManager = mc.getEntityRenderDispatcher();
    matrixStackIn.pushPose();
    matrixStackIn.translate(0.5, offsetY, 0.5);
    matrixStackIn.mulPose(renderManager.cameraOrientation());
    matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
    Matrix4f matrix4f = matrixStackIn.last().pose();
    float f1 = mc.options.getBackgroundOpacity(0.25F);
    int j = (int) (f1 * 255.0F) << 24;
    FontRenderer fontrenderer = renderManager.getFont();
    float f2 = (float) (-fontrenderer.width(displayNameIn) / 2);
    // actually render the nameplate
    fontrenderer.drawInBatch(displayNameIn, f2, 0, 553648127, false, matrix4f, bufferIn, true, j, packedLightIn);
    fontrenderer.drawInBatch(displayNameIn, f2, 0, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
    matrixStackIn.popPose();
  }
}
