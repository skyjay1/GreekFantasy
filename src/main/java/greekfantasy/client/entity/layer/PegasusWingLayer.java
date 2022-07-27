package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.PegasusModel;
import greekfantasy.entity.Pegasus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Variant;

import java.util.EnumMap;

public class PegasusWingLayer<T extends Pegasus> extends RenderLayer<T, PegasusModel<T>> {

    public static final EnumMap<Variant, ResourceLocation> WING_TEXTURE_MAP = new EnumMap<>(Variant.class);

    static {
        WING_TEXTURE_MAP.put(Variant.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/black.png"));
        WING_TEXTURE_MAP.put(Variant.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/brown.png"));
        WING_TEXTURE_MAP.put(Variant.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/chestnut.png"));
        WING_TEXTURE_MAP.put(Variant.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/creamy.png"));
        WING_TEXTURE_MAP.put(Variant.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/darkbrown.png"));
        WING_TEXTURE_MAP.put(Variant.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/gray.png"));
        WING_TEXTURE_MAP.put(Variant.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/white.png"));
    }

    public PegasusWingLayer(RenderLayerParent<T, PegasusModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // get packed light and a vertex builder bound to the correct texture
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        VertexConsumer vertexBuilder = multiBufferSource.getBuffer(getParentModel().renderType(getTextureLocation(entity)));

        // render wings
        poseStack.pushPose();
        if (entity.isBaby()) {
            float scale = 1.0F / 2.0F;
            float childBodyOffsetY = 20.0F;
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0.0D, (childBodyOffsetY / 16.0F), 0.0D);
        }
        this.getParentModel().renderWings(entity, poseStack, vertexBuilder, packedLight, packedOverlay, limbSwing, limbSwingAmount, partialTick);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return WING_TEXTURE_MAP.get(entity.getVariant());
    }
}