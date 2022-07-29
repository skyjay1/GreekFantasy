package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.client.entity.model.HalfHorseModel;
import greekfantasy.entity.util.HasHorseVariant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;

import java.util.EnumMap;

public class HalfHorseLayer<T extends LivingEntity & HasHorseVariant, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public static final EnumMap<Variant, ResourceLocation> BODY_TEXTURES = new EnumMap<>(Variant.class);

    static {
        BODY_TEXTURES.put(Variant.BLACK, new ResourceLocation("minecraft", "textures/entity/horse/horse_black.png"));
        BODY_TEXTURES.put(Variant.BROWN, new ResourceLocation("minecraft", "textures/entity/horse/horse_brown.png"));
        BODY_TEXTURES.put(Variant.CHESTNUT, new ResourceLocation("minecraft", "textures/entity/horse/horse_chestnut.png"));
        BODY_TEXTURES.put(Variant.CREAMY, new ResourceLocation("minecraft", "textures/entity/horse/horse_creamy.png"));
        BODY_TEXTURES.put(Variant.DARKBROWN, new ResourceLocation("minecraft", "textures/entity/horse/horse_darkbrown.png"));
        BODY_TEXTURES.put(Variant.GRAY, new ResourceLocation("minecraft", "textures/entity/horse/horse_gray.png"));
        BODY_TEXTURES.put(Variant.WHITE, new ResourceLocation("minecraft", "textures/entity/horse/horse_white.png"));
    }

    private final HalfHorseModel<T> layerModel;

    public HalfHorseLayer(RenderLayerParent<T, M> parent, EntityModelSet entityModelSet) {
        super(parent);
        this.layerModel = new HalfHorseModel<>(entityModelSet.bakeLayer(HalfHorseModel.HALF_HORSE_MODEL_RESOURCE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // set up rotations
        layerModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        layerModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // render model
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(layerModel.renderType(getTextureLocation(entity)));
        layerModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected ResourceLocation getTextureLocation(final T entity) {
        return BODY_TEXTURES.get(entity.getVariant());
    }
}
