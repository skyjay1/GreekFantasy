package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.client.render.model.FuryModel;
import greekfantasy.entity.FuryEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class FuryHairLayer<T extends FuryEntity> extends LayerRenderer<T, FuryModel<T>> {

    public FuryHairLayer(IEntityRenderer<T, FuryModel<T>> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible() && entity.aggroTime > 0) {
            // get packed light and a vertex builder bound to the correct texture
            int packedOverlay = LivingRenderer.getOverlayCoords(entity, 0.0F);
            IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
            // render snake hair
            matrixStackIn.pushPose();
            this.getParentModel().renderSnakeHair(matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, entity.tickCount + partialTick, entity.getAggroPercent(partialTick % 1.0F));
            matrixStackIn.popPose();
        }
    }
}