package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.ElpisModel;
import greekfantasy.entity.Elpis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ElpisRenderer<T extends Elpis> extends MobRenderer<T, ElpisModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/elpis.png");

    public ElpisRenderer(EntityRendererProvider.Context context) {
        super(context, new ElpisModel<>(context.bakeLayer(ElpisModel.ELPIS_MODEL_RESOURCE)), 0.15F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public void render(final T entity, final float renderOffsetX, final float partialTick, final PoseStack poseStack,
                       final MultiBufferSource multiBufferSource, final int packedLight) {
        this.model.setAlpha(entity.getAlpha(partialTick));
        super.render(entity, renderOffsetX, partialTick, poseStack, multiBufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }


}

