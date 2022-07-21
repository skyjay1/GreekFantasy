package greekfantasy.client.entity.layer;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Arachne;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class ArachneEyesLayer<T extends Arachne, M extends EntityModel<T>> extends EyesLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/arachne/arachne_eyes.png");
    private static final RenderType EYES = RenderType.eyes(TEXTURE);

    public ArachneEyesLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}