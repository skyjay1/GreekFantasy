package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GreekFantasy;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.ResourceLocation;

public class MadCowEyesLayer<T extends CowEntity, M extends EntityModel<T>> extends LayerRenderer<T, CowModel<T>> {

  private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/mad_cow_eyes.png");
  
  private final CowModel<T> layerModel;
  
  public MadCowEyesLayer(IEntityRenderer<T, CowModel<T>> ientityrenderer) {
    super(ientityrenderer);
    layerModel = new CowModel<>();
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible()) {
      coloredCutoutModelCopyLayerRender(getParentModel(), this.layerModel, EYES_TEXTURE, matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
    }
  }

}
