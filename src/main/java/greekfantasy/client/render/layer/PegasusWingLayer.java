package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.PegasusModel;
import greekfantasy.entity.PegasusEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;

public class PegasusWingLayer<T extends PegasusEntity> extends LayerRenderer<T, PegasusModel<T>> {

    public static final EnumMap<CoatColors, ResourceLocation> WING_TEXTURE_MAP = new EnumMap<>(CoatColors.class);

    static {
        WING_TEXTURE_MAP.put(CoatColors.BLACK, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/black.png"));
        WING_TEXTURE_MAP.put(CoatColors.BROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/brown.png"));
        WING_TEXTURE_MAP.put(CoatColors.CHESTNUT, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/chestnut.png"));
        WING_TEXTURE_MAP.put(CoatColors.CREAMY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/creamy.png"));
        WING_TEXTURE_MAP.put(CoatColors.DARKBROWN, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/darkbrown.png"));
        WING_TEXTURE_MAP.put(CoatColors.GRAY, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/gray.png"));
        WING_TEXTURE_MAP.put(CoatColors.WHITE, new ResourceLocation(GreekFantasy.MODID, "textures/entity/pegasus/white.png"));
    }

    public PegasusWingLayer(IEntityRenderer<T, PegasusModel<T>> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            // get packed light and a vertex builder bound to the correct texture
            int packedOverlay = LivingRenderer.getOverlayCoords(entity, 0.0F);
            IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

            // render wings
            matrixStackIn.pushPose();
            if (entity.isBaby()) {
                float scale = 1.0F / 2.0F;
                float childBodyOffsetY = 20.0F;
                matrixStackIn.scale(scale, scale, scale);
                matrixStackIn.translate(0.0D, (childBodyOffsetY / 16.0F), 0.0D);
            }
            this.getParentModel().renderWings(entity, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, limbSwing, limbSwingAmount, partialTick);
            matrixStackIn.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return WING_TEXTURE_MAP.get(entity.getCoatColor());
    }
}