package greekfantasy.client.render;

import greekfantasy.client.render.layer.MadCowEyesLayer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.CowEntity;

public class MadCowRenderer<T extends CowEntity> extends CowRenderer {

    public MadCowRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.addLayer(new MadCowEyesLayer<>(this));
    }
}
