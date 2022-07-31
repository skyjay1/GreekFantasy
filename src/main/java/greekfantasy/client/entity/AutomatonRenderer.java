package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.AutomatonCrackinessLayer;
import greekfantasy.client.entity.model.AutomatonModel;
import greekfantasy.entity.Automaton;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AutomatonRenderer<T extends Automaton> extends MobRenderer<T, AutomatonModel<T>> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/automaton/automaton.png");

    public AutomatonRenderer(EntityRendererProvider.Context context) {
        super(context, new AutomatonModel<>(context.bakeLayer(AutomatonModel.AUTOMATON_MODEL_RESOURCE)), 0.45F);
        this.addLayer(new AutomatonCrackinessLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}