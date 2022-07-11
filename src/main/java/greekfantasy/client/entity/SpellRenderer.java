package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.SpellModel;
import greekfantasy.entity.misc.Curse;
import greekfantasy.entity.misc.CurseOfCirce;
import greekfantasy.entity.misc.HealingSpell;
import greekfantasy.entity.misc.PoisonSpit;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;

public abstract class SpellRenderer<T extends Projectile> extends EntityRenderer<T> {

    protected SpellModel<T> entityModel;
    protected boolean usePackedLight;

    public SpellRenderer(final EntityRendererProvider.Context context) {
        this(context, true);
    }

    public SpellRenderer(final EntityRendererProvider.Context context, boolean usePackedLight) {
        super(context);
        this.entityModel = new SpellModel<T>(context.bakeLayer(SpellModel.SPELL_MODEL_RESOURCE));
        this.usePackedLight = usePackedLight;
    }

    @Override
    public void render(T entity, float rotationYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.entityModel.prepareMobModel(entity, 0.0F, 0.0F, partialTick);
        this.entityModel.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);
        final VertexConsumer vertexConsumer = multiBufferSource.getBuffer(entityModel.renderType(getTextureLocation(entity)));
        final int light = usePackedLight ? packedLight : 15728880;
        this.entityModel.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static class CurseRenderer extends SpellRenderer<Curse> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/curse.png");

        public CurseRenderer(EntityRendererProvider.Context context) {
            super(context, false);
        }

        @Override
        public ResourceLocation getTextureLocation(final Curse entity) {
            return TEXTURE;
        }
    }

    public static class CurseOfCirceRenderer extends SpellRenderer<CurseOfCirce> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/curse_of_circe.png");

        public CurseOfCirceRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(final CurseOfCirce entity) {
            return TEXTURE;
        }
    }

    public static class HealingSpellRenderer extends SpellRenderer<HealingSpell> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/healing_spell.png");

        public HealingSpellRenderer(EntityRendererProvider.Context context) {
            super(context, false);
        }

        @Override
        public ResourceLocation getTextureLocation(final HealingSpell entity) {
            return TEXTURE;
        }
    }

    public static class PoisonSpitRenderer extends SpellRenderer<PoisonSpit> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/poison_spit.png");

        public PoisonSpitRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(final PoisonSpit entity) {
            return TEXTURE;
        }
    }

}
