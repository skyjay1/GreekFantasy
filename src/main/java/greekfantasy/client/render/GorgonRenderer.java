package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.GorgonHairLayer;
import greekfantasy.client.render.model.GorgonModel;
import greekfantasy.entity.GorgonEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class GorgonRenderer<T extends GorgonEntity> extends BipedRenderer<T, GorgonModel<T>> {

    public static final ResourceLocation GORGON_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/gorgon.png");
    public static final ResourceLocation MEDUSA_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/medusa.png");
    protected static final float MEDUSA_SCALE = 1.18F;

    public GorgonRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new GorgonModel<T>(0.0F), 0.5F);
        this.addLayer(new GorgonHairLayer<>(this));
    }

    @Override
    protected void scale(final T entity, MatrixStack matrix, float f) {
        if (entity.isMedusa()) {
            matrix.scale(MEDUSA_SCALE, MEDUSA_SCALE, MEDUSA_SCALE);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return entity.isMedusa() ? MEDUSA_TEXTURE : GORGON_TEXTURE;
    }
}
