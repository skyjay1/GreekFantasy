package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.TalosCrackinessLayer;
import greekfantasy.client.entity.model.AutomatonModel;
import greekfantasy.entity.boss.Talos;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TalosRenderer<T extends Talos> extends MobRenderer<T, AutomatonModel<T>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/talos/talos.png");
    public static final float SCALE = 2.0F;

    public TalosRenderer(EntityRendererProvider.Context context) {
        super(context, new AutomatonModel<>(context.bakeLayer(AutomatonModel.AUTOMATON_MODEL_RESOURCE)), 0.75F);
        this.addLayer(new TalosCrackinessLayer<>(this));
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