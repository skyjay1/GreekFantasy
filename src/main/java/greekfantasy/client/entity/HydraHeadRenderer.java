package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.HydraHeadModel;
import greekfantasy.entity.boss.HydraHead;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HydraHeadRenderer<T extends HydraHead> extends MobRenderer<T, HydraHeadModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra_head.png");
    private static final ResourceLocation TEXTURE_CHARRED = new ResourceLocation(GreekFantasy.MODID, "textures/entity/hydra/hydra_head_charred.png");

    public HydraHeadRenderer(final EntityRendererProvider.Context context) {
        super(context, new HydraHeadModel<T>(context.bakeLayer(HydraHeadModel.HYDRA_HEAD_MODEL_RESOURCE)), 0.0F);
    }

    @Override
    protected void scale(final T entity, PoseStack matrix, float partialTick) {
        getModel().setSpawnPercent(entity.getSpawnPercent(partialTick));
        getModel().setCharred(entity.isCharred());
        getModel().setSevered(entity.isSevered());
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return !entity.isCharred() ? TEXTURE : TEXTURE_CHARRED;
    }
}