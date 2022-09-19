package greekfantasy.client.entity.layer;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.ScyllaModel;
import greekfantasy.entity.boss.Scylla;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class ScyllaEyesLayer extends EyesLayer<Scylla, ScyllaModel> {

    private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(GreekFantasy.MODID, "textures/entity/scylla/scylla_eyes.png"));

    public ScyllaEyesLayer(RenderLayerParent<Scylla, ScyllaModel> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public RenderType renderType() {
        return RENDER_TYPE;
    }

}
