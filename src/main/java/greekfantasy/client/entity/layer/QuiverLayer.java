package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.CentaurModel;
import greekfantasy.entity.util.HasHorseVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;

public class QuiverLayer<T extends Mob & HasHorseVariant, M extends CentaurModel<T>> extends RenderLayer<T, M> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/centaur/quiver.png");

    public QuiverLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // check if entity is holding a bow
        if (entity.getMainHandItem().getItem() instanceof BowItem || entity.getOffhandItem().getItem() instanceof BowItem) {
            // render model
            int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(getParentModel().renderType(getTextureLocation(entity)));
            this.getParentModel().renderQuiver(entity, poseStack, vertexConsumer, packedLight, packedOverlay, limbSwing, limbSwingAmount);
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
