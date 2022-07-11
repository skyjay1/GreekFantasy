package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.BabySpiderModel;
import greekfantasy.entity.monster.BabySpider;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BabySpiderRenderer<T extends BabySpider> extends MobRenderer<T, BabySpiderModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/baby_spider.png");

    public BabySpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new BabySpiderModel<>(context.bakeLayer(BabySpiderModel.BABY_SPIDER_MODEL_RESOURCE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }
}

