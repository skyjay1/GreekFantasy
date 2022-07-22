package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.client.entity.model.FuryModel;
import greekfantasy.entity.monster.Fury;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class FuryHairLayer<T extends Fury> extends RenderLayer<T, FuryModel<T>> {

    public FuryHairLayer(RenderLayerParent<T, FuryModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        final float aggroPercent = entity.getAggroPercent(partialTick);
        if (aggroPercent > 0.05F) {
            final float scale = 0.25F + (aggroPercent * 0.75F);
            // get packed light and a vertex builder bound to the correct texture
            int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
            VertexConsumer vertexBuilder = multiBufferSource.getBuffer(getParentModel().renderType(this.getTextureLocation(entity)));
            // render snake hair
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            this.getParentModel().setupSnakeAnim(ageInTicks);
            this.getParentModel().getHair().render(poseStack, vertexBuilder, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }
}