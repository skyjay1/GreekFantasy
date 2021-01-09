package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.SatyrModel;
import greekfantasy.entity.SatyrEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

public class SatyrGroverLayer<T extends SatyrEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
  
  private static final ResourceLocation GROVER_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/grover_overlay.png");
  
  private final SatyrModel<T> layerModel = new SatyrModel<>(0.25F);

  public SatyrGroverLayer(IEntityRenderer<T, M> ientityrenderer) {
    super(ientityrenderer);
  }
  
  @Override
  public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) { 
    if(!entity.isInvisible() && entity.hasCustomName() && "Grover".equals(entity.getCustomName().getUnformattedComponentText())) {
      renderCopyCutoutModel(getEntityModel(), this.layerModel, GROVER_TEXTURE, matrixStack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F); 
    }
  }
}
