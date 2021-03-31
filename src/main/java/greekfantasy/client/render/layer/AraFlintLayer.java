package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.client.render.model.AraModel;
import greekfantasy.entity.AraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class AraFlintLayer<T extends AraEntity> extends LayerRenderer<T, AraModel<T>> {
  
  public AraFlintLayer(IEntityRenderer<T, AraModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible()) {
      // prepare to render flint inside body opening
      final float scale = 0.25F;
      final float tx = (4.0F / 16.0F) * scale;
      final float ty = (12.0F / 16.0F) * scale;
      final float tz = (-2.0F / 16.0F) * scale;
      final float spin = 180.0F + (180.0F) * MathHelper.cos((entity.ticksExisted + entity.getEntityId() * 3) * 0.08F);
      matrixStackIn.push();
      // transforms
      this.getEntityModel().getBodyModel().translateRotate(matrixStackIn);
      matrixStackIn.translate(tx, ty, tz);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      matrixStackIn.scale(scale, -scale, -scale);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(spin));
      // render the item stack
      ItemStack stack = new ItemStack(Items.FLINT);
      Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED, 
          packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
      // finish rendering
      matrixStackIn.pop();
    }
  }
}