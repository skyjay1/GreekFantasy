package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.entity.boss.GiantBoar;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelPart;

public class GiantBoarModel<T extends GiantBoar> extends HoglinModel<T> {

    private float colorAlpha = 1.0F;


    public GiantBoarModel(ModelPart root) {
        super(root);
    }

    public float getColorAlpha() {
        return colorAlpha;
    }

    public void setColorAlpha(float alpha) {
        this.colorAlpha = alpha;
    }

    @Override
    public void renderToBuffer(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int packedLightIn, final int packedOverlayIn, final float redIn,
                               final float greenIn, final float blueIn, final float alphaIn) {
        // render with custom alpha value
        super.renderToBuffer(poseStack, vertexConsumer, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, colorAlpha);
    }
}
