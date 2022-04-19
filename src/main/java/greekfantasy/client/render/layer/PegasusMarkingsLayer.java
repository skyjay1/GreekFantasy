package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import greekfantasy.client.render.model.PegasusModel;
import greekfantasy.entity.PegasusEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.horse.CoatTypes;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;

public class PegasusMarkingsLayer<T extends PegasusEntity> extends LayerRenderer<T, PegasusModel<T>> {

    public static final EnumMap<CoatTypes, ResourceLocation> COAT_TEXTURES = new EnumMap<>(CoatTypes.class);

    static {
        COAT_TEXTURES.put(CoatTypes.NONE, null);
        COAT_TEXTURES.put(CoatTypes.WHITE, new ResourceLocation("textures/entity/horse/horse_markings_white.png"));
        COAT_TEXTURES.put(CoatTypes.WHITE_FIELD, new ResourceLocation("textures/entity/horse/horse_markings_whitefield.png"));
        COAT_TEXTURES.put(CoatTypes.WHITE_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_whitedots.png"));
        COAT_TEXTURES.put(CoatTypes.BLACK_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_blackdots.png"));
    }

    public PegasusMarkingsLayer(IEntityRenderer<T, PegasusModel<T>> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        final ResourceLocation texture = getTextureLocation(entity);
        if (texture != null && !entity.isInvisible()) {
            IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(texture));
            getParentModel().renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, LivingRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
//      renderCopyCutoutModel(this.getEntityModel(), this.getEntityModel(), texture, matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return COAT_TEXTURES.get(entity.getCoatType());
    }
}