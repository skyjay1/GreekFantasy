package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.ScyllaEyesLayer;
import greekfantasy.client.entity.model.ScyllaModel;
import greekfantasy.entity.boss.Scylla;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ScyllaRenderer extends MobRenderer<Scylla, ScyllaModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/scylla/scylla.png");
    private static final float SCALE = 2.0F;

    public ScyllaRenderer(EntityRendererProvider.Context context) {
        super(context, new ScyllaModel(context.bakeLayer(ScyllaModel.SCYLLA_MODEL_RESOURCE)), 1.0F);
        this.addLayer(new ScyllaEyesLayer(this));
    }

    @Override
    protected void scale(final Scylla entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(final Scylla entity) {
        return TEXTURE;
    }
}
