package greekfantasy.client.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.EmpusaHairLayer;
import greekfantasy.client.entity.model.EmpusaModel;
import greekfantasy.entity.monster.Empusa;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EmpusaRenderer<T extends Empusa> extends HumanoidMobRenderer<T, EmpusaModel<T>> {
    private static final ResourceLocation TEXTURE_IDLE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/empusa/empusa.png");
    private static final ResourceLocation TEXTURE_ATTACKING = new ResourceLocation(GreekFantasy.MODID, "textures/entity/empusa/empusa_attacking.png");

    public EmpusaRenderer(final EntityRendererProvider.Context context) {
        super(context, new EmpusaModel<>(context.bakeLayer(EmpusaModel.EMPUSA_MODEL_RESOURCE)), 0.5F);
        this.addLayer(new EmpusaHairLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.isDraining() ? TEXTURE_ATTACKING : TEXTURE_IDLE;
    }
}