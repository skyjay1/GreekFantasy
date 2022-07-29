package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.client.entity.model.HalfHorseModel;
import greekfantasy.entity.util.HasHorseVariant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Markings;

import java.util.EnumMap;

public class HalfHorseMarkingsLayer<T extends Mob & HasHorseVariant, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public static final EnumMap<Markings, ResourceLocation> MARKINGS_TEXTURES = new EnumMap<>(Markings.class);

    static {
        MARKINGS_TEXTURES.put(Markings.NONE, null);
        MARKINGS_TEXTURES.put(Markings.WHITE, new ResourceLocation("textures/entity/horse/horse_markings_white.png"));
        MARKINGS_TEXTURES.put(Markings.WHITE_FIELD, new ResourceLocation("textures/entity/horse/horse_markings_whitefield.png"));
        MARKINGS_TEXTURES.put(Markings.WHITE_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_whitedots.png"));
        MARKINGS_TEXTURES.put(Markings.BLACK_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_blackdots.png"));
    }

    private EntityModel<T> model;

    public HalfHorseMarkingsLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new HalfHorseModel(modelSet.bakeLayer(HalfHorseModel.HALF_HORSE_MODEL_RESOURCE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        final ResourceLocation texture = getTextureLocation(entity);
        if (texture != null) {
            getParentModel().copyPropertiesTo(model);
            model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer vertexBuilder = multiBufferSource.getBuffer(RenderType.entityTranslucent(texture));
            model.renderToBuffer(poseStack, vertexBuilder, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return MARKINGS_TEXTURES.get(entity.getMarkings());
    }
}