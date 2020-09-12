package greekfantasy.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.block.StatueBlock;
import greekfantasy.client.model.tileentity.StatueModel;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class StatueTileEntityRenderer extends TileEntityRenderer<StatueTileEntity> {
    
  protected StatueModel<StatueTileEntity> model;
  
  public StatueTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
    this.model = new StatueModel<StatueTileEntity>();
  }

  @Override
  public void render(StatueTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {
    // get the right TileEntity (always use the lower one)
    final boolean upper = tileEntityIn.getBlockState().get(StatueBlock.HALF) == DoubleBlockHalf.UPPER;
    StatueTileEntity te = tileEntityIn;
    if(upper) {
      final TileEntity temp = tileEntityIn.getWorld().getTileEntity(tileEntityIn.getPos().down());
      if(temp instanceof StatueTileEntity) {
        te = (StatueTileEntity)temp;
      }
    }
    // determine texture, rotations, and style
    final boolean isFemaleModel = te.isStatueFemale();
    final float rotation = te.getBlockState().get(StatueBlock.HORIZONTAL_FACING).getHorizontalAngle();
    final float translateY = upper ? 0.95F : 1.95F;
    final ResourceLocation texture = te.getStatueMaterial().getTexture(isFemaleModel);
    // actually render the model
    matrixStackIn.push();
    matrixStackIn.translate(0.5D, (double)translateY, 0.5D);
    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotation));
    IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(texture));
    this.model.setRotationAngles(te, partialTicks);
    this.model.render(matrixStackIn, vertexBuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F, upper, isFemaleModel);
    renderHeldItems(te, partialTicks, matrixStackIn, bufferIn, 15728640, OverlayTexture.NO_OVERLAY, upper);
    matrixStackIn.pop();
  }
  
  private void renderHeldItems(StatueTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
      IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, boolean upper) {
    if(upper) {
      ItemStack itemstackRight = tileEntityIn.getItem(HandSide.RIGHT);
      ItemStack itemstackLeft = tileEntityIn.getItem(HandSide.LEFT);
      if (!itemstackLeft.isEmpty() || !itemstackRight.isEmpty()) {
         matrixStackIn.push();
         this.renderItem(tileEntityIn, itemstackRight, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
         this.renderItem(tileEntityIn, itemstackLeft, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
         matrixStackIn.pop();
      }
    }
  }

  private void renderItem(StatueTileEntity tileEntity, ItemStack stack, ItemCameraTransforms.TransformType transform,
      HandSide handSide, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn) {
    if (!stack.isEmpty()) {
      matrixStackIn.push();
      this.model.translateHand(handSide, matrixStackIn);
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F));
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      boolean flag = handSide == HandSide.LEFT;
      matrixStackIn.translate((double) ((float) (flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
      Minecraft.getInstance().getItemRenderer().renderItem(null, stack, transform, flag, matrixStackIn, bufferIn,
          tileEntity.getWorld(), packedLightIn, packedOverlayIn);
      matrixStackIn.pop();
    }
  }

}
