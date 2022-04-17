package greekfantasy.client.render;

import greekfantasy.client.render.layer.CyprianHeadLayer;
import greekfantasy.client.render.model.CyprianModel;
import greekfantasy.entity.CyprianEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class CyprianRenderer<T extends CyprianEntity> extends CentaurRenderer<T> {

    public CyprianRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new CyprianModel<T>(0.0F));
        this.addLayer(new CyprianHeadLayer<>(this));
    }
}
