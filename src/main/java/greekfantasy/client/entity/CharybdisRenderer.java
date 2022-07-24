package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.CharybdisModel;
import greekfantasy.entity.boss.Charybdis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CharybdisRenderer extends MobRenderer<Charybdis, CharybdisModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/charybdis.png");
    private static final float SCALE = 4.0F;

    public CharybdisRenderer(EntityRendererProvider.Context context) {
        super(context, new CharybdisModel(context.bakeLayer(CharybdisModel.CHARYBDIS_MODEL_RESOURCE)), 1.0F);
    }

    @Override
    protected void scale(final Charybdis entity, PoseStack poseStack, float partialTick) {
        final float s = SCALE * entity.getSpawnPercent(partialTick);
        poseStack.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(final Charybdis entity) {
        return TEXTURE;
    }
}
