package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.GeryonModel;
import greekfantasy.entity.GeryonEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

public class GeryonClothingLayer<T extends GeryonEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
  
  private static final ResourceLocation GERYON_CLOTHES_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/geryon/geryon_overlay.png");
  
  private final GeryonModel<T> layerModel = new GeryonModel<>(0.25F);

  public GeryonClothingLayer(IEntityRenderer<T, M> ientityrenderer) {
    super(ientityrenderer);
  }
  
  @Override
  public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) { 
    renderCopyCutoutModel(getEntityModel(), this.layerModel, GERYON_CLOTHES_TEXTURE, matrixStack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F); 
  }
}
