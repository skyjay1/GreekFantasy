package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.Whirl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhirlRenderer<T extends Whirl> extends EntityRenderer<T> {

    // dummy texture that will not be used in any model
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/curse.png");

    public WhirlRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }

}
