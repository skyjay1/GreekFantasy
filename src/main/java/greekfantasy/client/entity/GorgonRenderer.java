package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.GorgonModel;
import greekfantasy.entity.monster.Gorgon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class GorgonRenderer<T extends Gorgon> extends MobRenderer<T, GorgonModel<T>> {

    public static final ResourceLocation GORGON_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/gorgon/gorgon.png");
    public static final ResourceLocation MEDUSA_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/gorgon/medusa.png");
    protected static final float MEDUSA_SCALE = 1.18F;

    public GorgonRenderer(EntityRendererProvider.Context context) {
        super(context, new GorgonModel<>(context.bakeLayer(GorgonModel.GORGON_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    protected void scale(final T entity, PoseStack poseStack, float partialTick) {
        if (entity.isMedusa()) {
            poseStack.scale(MEDUSA_SCALE, MEDUSA_SCALE, MEDUSA_SCALE);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return entity.isMedusa() ? MEDUSA_TEXTURE : GORGON_TEXTURE;
    }
}

