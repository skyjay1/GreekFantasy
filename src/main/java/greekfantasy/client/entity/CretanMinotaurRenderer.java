package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Minotaur;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CretanMinotaurRenderer<T extends Minotaur> extends MinotaurRenderer<T> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/cretan_minotaur.png");
    public static final float SCALE = 1.75F;

    public CretanMinotaurRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float partialTick) {
        super.scale(entity, poseStack, partialTick);
        // scale the entity
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}