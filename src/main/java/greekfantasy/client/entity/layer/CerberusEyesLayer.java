package greekfantasy.client.entity.layer;

import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.CerberusModel;
import greekfantasy.entity.boss.Cerberus;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CerberusEyesLayer<T extends Cerberus> extends EyesLayer<T, CerberusModel<T>> {

    private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerberus/cerberus_eyes.png"));

    public CerberusEyesLayer(RenderLayerParent<T, CerberusModel<T>> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public RenderType renderType() {
        return RENDER_TYPE;
    }

}
