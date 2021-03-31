package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.GoldenRamModel;
import greekfantasy.entity.GoldenRamEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SheepWoolModel;
import net.minecraft.util.ResourceLocation;

public class GoldenRamWoolLayer<T extends GoldenRamEntity> extends LayerRenderer<T, GoldenRamModel<T>> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/golden_ram/golden_ram_wool.png");

  private final SheepWoolModel<T> sheepModel = new SheepWoolModel<T>();

  public GoldenRamWoolLayer(IEntityRenderer<T, GoldenRamModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing,
      float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (entityIn.getSheared() || entityIn.isInvisible()) {
      return;
    }

    renderCopyCutoutModel(getEntityModel(), this.sheepModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing,
        limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
  }
}
