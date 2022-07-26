package greekfantasy.client.entity;

import greekfantasy.client.entity.layer.MadCowEyesLayer;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

public class MadCowRenderer<T extends Cow> extends MobRenderer<T, CowModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/cow/cow.png");

    public MadCowRenderer(EntityRendererProvider.Context context) {
        super(context, new CowModel<>(context.bakeLayer(ModelLayers.COW)), 0.5F);
        this.addLayer(new MadCowEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}