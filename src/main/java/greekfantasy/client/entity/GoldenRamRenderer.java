package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.client.entity.layer.GoldenRamFurLayer;
import greekfantasy.client.entity.layer.GoldenRamHornLayer;
import greekfantasy.client.entity.model.GoldenRamModel;
import greekfantasy.entity.GoldenRam;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GoldenRamRenderer<T extends GoldenRam> extends MobRenderer<T, GoldenRamModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");
    private static final float SCALE = 1.12F;

    public GoldenRamRenderer(final EntityRendererProvider.Context context) {
        super(context, new GoldenRamModel<>(context.bakeLayer(GoldenRamModel.RAM_MODEL_RESOURCE)), 0.7F);
        this.addLayer(new GoldenRamHornLayer<>(this));
        this.addLayer(new GoldenRamFurLayer<>(this, context.getModelSet()));
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}

