package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.model.TalosModel;
import greekfantasy.entity.TalosEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class TalosRenderer<T extends TalosEntity> extends BipedRenderer<T, TalosModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/talos.png");
    private static final float SCALE = 2.0F;

    public TalosRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new TalosModel<T>(0.0F), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(final T entity, MatrixStack matrix, float f) {
        matrix.scale(SCALE, SCALE, SCALE);
    }
}
