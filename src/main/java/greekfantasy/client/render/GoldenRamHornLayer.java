package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.GoldenRamModel;
import greekfantasy.entity.GoldenRamEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SheepWoolModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class GoldenRamHornLayer<T extends GoldenRamEntity> extends LayerRenderer<T, GoldenRamModel<T>> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/golden_ram/golden_ram_horn.png");

  public GoldenRamHornLayer(IEntityRenderer<T, GoldenRamModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing,
      float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entityIn.isInvisible()) {
      this.getEntityModel().renderHorns(matrixStackIn, bufferIn.getBuffer(RenderType.getEntitySolid(TEXTURE)), packedLightIn, OverlayTexture.NO_OVERLAY);
    }
  }
}
