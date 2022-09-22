package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.FuryEyesLayer;
import greekfantasy.client.entity.layer.FuryHairLayer;
import greekfantasy.client.entity.model.FuryModel;
import greekfantasy.entity.monster.Fury;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FuryRenderer<T extends Fury> extends HumanoidMobRenderer<T, FuryModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/fury/fury.png");

    public FuryRenderer(EntityRendererProvider.Context context) {
        super(context, new FuryModel<>(context.bakeLayer(FuryModel.FURY_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new FuryEyesLayer<>(this));
        this.addLayer(new FuryHairLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}