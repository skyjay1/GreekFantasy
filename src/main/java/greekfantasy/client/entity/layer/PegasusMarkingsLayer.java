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
import net.minecraft.world.entity.animal.horse.Markings;

import java.util.EnumMap;

public class PegasusMarkingsLayer<T extends Pegasus> extends RenderLayer<T, PegasusModel<T>> {

    public static final EnumMap<Markings, ResourceLocation> COAT_TEXTURES = new EnumMap<>(Markings.class);

    static {
        COAT_TEXTURES.put(Markings.NONE, null);
        COAT_TEXTURES.put(Markings.WHITE, new ResourceLocation("textures/entity/horse/horse_markings_white.png"));
        COAT_TEXTURES.put(Markings.WHITE_FIELD, new ResourceLocation("textures/entity/horse/horse_markings_whitefield.png"));
        COAT_TEXTURES.put(Markings.WHITE_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_whitedots.png"));
        COAT_TEXTURES.put(Markings.BLACK_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_blackdots.png"));
    }

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
        return COAT_TEXTURES.get(entity.getMarkings());
    }
}