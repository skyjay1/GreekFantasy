package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class SpearModel extends Model {
    private final ModelPart root;

    public SpearModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
    }

    @Override
    public void renderToBuffer(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int packedLight, final int packedOverlay,
                               final float colorRed, final float colorGreen, final float colorBlue, final float colorAlpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, colorRed, colorGreen, colorBlue, colorAlpha);
    }
}