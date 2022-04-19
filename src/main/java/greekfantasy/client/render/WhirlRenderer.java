package greekfantasy.client.render;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.WhirlEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class WhirlRenderer<T extends WhirlEntity> extends EntityRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/curse.png");

    public WhirlRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }

}
