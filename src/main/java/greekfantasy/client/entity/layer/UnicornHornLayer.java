package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.UnicornModel;
import greekfantasy.entity.Unicorn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class UnicornHornLayer<T extends Unicorn> extends RenderLayer<T, UnicornModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/unicorn/horn.png");

    public UnicornHornLayer(RenderLayerParent<T, UnicornModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isBaby()) {
            // get packed light and a vertex builder bound to the correct texture
            int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(getParentModel().renderType(TEXTURE));
            // render horn
            this.getParentModel().renderHorn(entity, poseStack, vertexConsumer, packedLight, packedOverlay, limbSwing, limbSwingAmount);
        }
    }
}