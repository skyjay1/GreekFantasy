package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.CerberusEyesLayer;
import greekfantasy.client.entity.model.CerberusModel;
import greekfantasy.entity.boss.Cerberus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CerberusRenderer<T extends Cerberus> extends MobRenderer<T, CerberusModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerberus/cerberus.png");
    public static final float SCALE = 1.9F;

    public CerberusRenderer(EntityRendererProvider.Context context) {
        super(context, new CerberusModel<T>(context.bakeLayer(CerberusModel.CERBERUS_MODEL_RESOURCE)), 1.0F);
        this.addLayer(new CerberusEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float partialTick) {
        // if the entity is spawning, shift the entity down
        if (entity.isSpawning()) {
            final float height = 2.4F;
            final float translateY = height * entity.getSpawnPercent(partialTick) - height;
            poseStack.translate(0.0F, -translateY, 0.0F);
        }
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    protected boolean isShaking(T entity) {
        return entity.isSpawning();
    }
}
