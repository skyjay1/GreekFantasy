package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PlayerSkyjayLayer<T extends PlayerEntity> extends LayerRenderer<T, PlayerModel<T>> {
  
  public PlayerSkyjayLayer(IEntityRenderer<T, PlayerModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    final ItemStack item = entity.getHeldItem(Hand.MAIN_HAND);
    if(!entity.isInvisible() && !item.isEmpty() && item.hasDisplayName() && "skyjay1".equals(item.getDisplayName().getUnformattedComponentText())) {
      // prepare to render fire on top of head
      final float scale = 0.5F;
      matrixStackIn.push();
      this.getEntityModel().getModelHead().translateRotate(matrixStackIn);
      matrixStackIn.translate(0.5D * scale, -0.5, -0.5D * scale);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      matrixStackIn.scale(scale, -scale, -scale);
      // render fire here
      // note: packed light flag 15728640 uses world light, 15728880 uses constant/full light
      Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.SOUL_FIRE.getDefaultState(), 
          matrixStackIn, bufferIn, 15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
      // finish rendering
      matrixStackIn.pop();
    }
  }
}