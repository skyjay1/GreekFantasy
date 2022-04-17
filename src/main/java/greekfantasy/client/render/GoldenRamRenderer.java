package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.client.render.layer.GoldenRamHornLayer;
import greekfantasy.client.render.layer.GoldenRamWoolLayer;
import greekfantasy.client.render.model.GoldenRamModel;
import greekfantasy.entity.GoldenRamEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class GoldenRamRenderer<T extends GoldenRamEntity> extends MobRenderer<T, GoldenRamModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");
    private static final float SCALE = 1.12F;

    public GoldenRamRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new GoldenRamModel<>(), 0.7F);
        addLayer(new GoldenRamHornLayer<>(this));
        addLayer(new GoldenRamWoolLayer<>(this));
    }

    @Override
    protected void scale(final T entity, MatrixStack matrix, float f) {
        matrix.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}

