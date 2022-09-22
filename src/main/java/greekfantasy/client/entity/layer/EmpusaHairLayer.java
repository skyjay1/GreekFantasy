package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import greekfantasy.client.entity.model.EmpusaModel;
import greekfantasy.entity.monster.Empusa;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;

public class EmpusaHairLayer<T extends Empusa> extends RenderLayer<T, EmpusaModel<T>> {

    public EmpusaHairLayer(RenderLayerParent<T, EmpusaModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            // prepare to render fire on top of head
            final float scale = 0.5F;
            poseStack.pushPose();
            this.getParentModel().getHeadPart().translateAndRotate(poseStack);
            poseStack.translate(0.5D * scale, -0.5, -0.5D * scale);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.scale(scale, -scale, -scale);
            // render fire here
            // note: packed light flag 15728640 uses world light, 15728880 uses constant/full light
            // see LightTexture
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.FIRE.defaultBlockState(),
                    poseStack, multiBufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            // finish rendering
            poseStack.popPose();
        }
    }
}