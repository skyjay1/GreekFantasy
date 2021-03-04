package greekfantasy.client.render.model.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public interface IHasName<T extends TileEntity> {
  
  public void renderName(final T entityIn, final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn);
  
  public default boolean canRenderName(final T entityIn) {
    return isWithinDistanceToRenderName(entityIn, 6.0D);
  }
  
  public static boolean isWithinDistanceToRenderName(final TileEntity entityIn, final double dis) {
    final Minecraft mc = Minecraft.getInstance();
    final EntityRendererManager renderManager = mc.getRenderManager();
    final Vector3d pos = Vector3d.copyCentered(entityIn.getPos());
    return renderManager.getDistanceToCamera(pos.x, pos.y, pos.z) < (dis * dis)
        && mc.objectMouseOver != null
        && mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK
        && mc.objectMouseOver.getHitVec().squareDistanceTo(pos) < 0.9D;
  }
  
  public static void renderNameplate(final TileEntity entityIn, final ITextComponent displayNameIn, final float offsetY, final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    final Minecraft mc = Minecraft.getInstance();
    final EntityRendererManager renderManager = mc.getRenderManager();
    matrixStackIn.push();
    matrixStackIn.translate(0.5, offsetY, 0.5);
    matrixStackIn.rotate(renderManager.getCameraOrientation());
    matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
    Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
    float f1 = mc.gameSettings.getTextBackgroundOpacity(0.25F);
    int j = (int) (f1 * 255.0F) << 24;
    FontRenderer fontrenderer = renderManager.getFontRenderer();
    float f2 = (float) (-fontrenderer.getStringPropertyWidth(displayNameIn) / 2);
    // actually render the nameplate
    fontrenderer.func_243247_a(displayNameIn, f2, 0, 553648127, false, matrix4f, bufferIn, true, j, packedLightIn);
    fontrenderer.func_243247_a(displayNameIn, f2, 0, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
    matrixStackIn.pop();
  }
}
