package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.SpartiClothingLayer;
import greekfantasy.client.render.model.SpartiModel;
import greekfantasy.entity.SpartiEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SpartiRenderer<T extends SpartiEntity> extends BipedRenderer<T, SpartiModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/sparti/sparti.png");

    public SpartiRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SpartiModel<T>(0.0F), 0.5F);
        this.addLayer(new SpartiClothingLayer<>(this));
    }

    @Override
    protected void scale(final T entity, MatrixStack matrix, float ageInTicks) {
        // if the entity is spawning, shift the entity down
        if (entity.isSpawning()) {
            final float height = 1.99F;
            final float translate = 0.035F;
            final float translateY = height * entity.getSpawnPercent() - height;
            final float translateX = translate * (entity.getRandom().nextFloat() - 0.5F);
            final float translateZ = translate * (entity.getRandom().nextFloat() - 0.5F);
//      GreekFantasy.LOGGER.debug("tY=" + translateY + "; spawn=" + entity.getSpawnPercent());
            matrix.translate(translateX, translateY, translateZ);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}
