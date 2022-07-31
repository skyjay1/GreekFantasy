package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Talos;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class TalosCrackinessLayer<T extends Talos, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation[] CRACKINESS = {
            new ResourceLocation(GreekFantasy.MODID, "textures/entity/talos/talos_crackiness_high.png"),
            new ResourceLocation(GreekFantasy.MODID, "textures/entity/talos/talos_crackiness_medium.png"),
            new ResourceLocation(GreekFantasy.MODID, "textures/entity/talos/talos_crackiness_low.png")
    };

    public TalosCrackinessLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int crackiness = entity.getCrackiness();
        if (crackiness >= 0 && crackiness < 3 && !entity.isInvisible()) {
            ResourceLocation texture = CRACKINESS[crackiness];
            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(texture));
            getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 0.5F);
        }
    }
}
