package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.SpartiModel;
import greekfantasy.entity.SpartiEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

public class SpartiClothingLayer<T extends SpartiEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    private static final ResourceLocation SPARTI_CLOTHES_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/sparti/sparti_overlay.png");

    private final SpartiModel<T> layerModel = new SpartiModel<>(0.25F);

    public SpartiClothingLayer(IEntityRenderer<T, M> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            coloredCutoutModelCopyLayerRender(getParentModel(), this.layerModel, SPARTI_CLOTHES_TEXTURE, matrixStack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }
}
