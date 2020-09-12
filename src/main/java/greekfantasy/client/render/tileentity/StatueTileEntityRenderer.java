package greekfantasy.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.client.model.tileentity.StatueModel;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class StatueTileEntityRenderer extends TileEntityRenderer<StatueTileEntity> {
  
  protected static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/limestone.png");

  protected StatueModel<StatueTileEntity> model;
  
  public StatueTileEntityRenderer(final TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
    this.model = new StatueModel<StatueTileEntity>();
  }

  @Override
  public void render(StatueTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
      int packedLightIn, int packedOverlayIn) {
    // render biped model
    matrixStackIn.push();
    //matrixStackIn.translate(0.0D, 0.5D, 0.0D);
    //matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
    IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
    this.model.setRotationAngles(tileEntityIn, partialTicks);
    // packedLightIn = 15728880 is full light
    this.model.render(matrixStackIn, vertexBuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    matrixStackIn.pop();
  }

}
