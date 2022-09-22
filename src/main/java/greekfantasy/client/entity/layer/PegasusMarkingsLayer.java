package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.client.entity.model.PegasusModel;
import greekfantasy.entity.Pegasus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class PegasusMarkingsLayer<T extends Pegasus> extends RenderLayer<T, PegasusModel<T>> {

    public PegasusMarkingsLayer(RenderLayerParent<T, PegasusModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        final ResourceLocation texture = getTextureLocation(entity);
        if (texture != null) {
            VertexConsumer vertexBuilder = multiBufferSource.getBuffer(RenderType.entityTranslucent(texture));
            getParentModel().renderToBuffer(poseStack, vertexBuilder, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return HalfHorseMarkingsLayer.MARKINGS_TEXTURES.get(entity.getMarkings());
    }
}