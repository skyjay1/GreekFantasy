package greekfantasy.client.entity.layer;

import greekfantasy.GreekFantasy;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

public class MadCowEyesLayer<T extends Cow, M extends CowModel<T>> extends EyesLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/mad_cow_eyes.png");
    private static final RenderType EYES = RenderType.entityCutoutNoCull(TEXTURE);

    public MadCowEyesLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}