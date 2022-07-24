package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.SirenModel;
import greekfantasy.entity.monster.Siren;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class SirenRenderer<T extends Siren> extends MobRenderer<T, SirenModel<T>> {

    private static final ResourceLocation TEXTURE_IDLE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/siren/siren.png");
    private static final ResourceLocation TEXTURE_ATTACKING = new ResourceLocation(GreekFantasy.MODID, "textures/entity/siren/siren_attacking.png");

    public SirenRenderer(EntityRendererProvider.Context context) {
        super(context, new SirenModel<>(context.bakeLayer(SirenModel.SIREN_MODEL_RESOURCE)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return !entity.isCharming() ? TEXTURE_IDLE : TEXTURE_ATTACKING;
    }
}
