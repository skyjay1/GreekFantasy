package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import greekfantasy.GFRegistry;
import greekfantasy.client.entity.model.SatyrModel;
import greekfantasy.entity.Satyr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class SatyrPanfluteLayer<T extends Satyr> extends RenderLayer<T, SatyrModel<T>> {

    private static ItemStack panflute;

    public SatyrPanfluteLayer(RenderLayerParent<T, SatyrModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (null == panflute) {
            panflute = new ItemStack(GFRegistry.ItemReg.PANFLUTE.get());
        }
        if (!entity.isInvisible() && entity.holdingPanfluteTime > 0) {
            // prepare to render panflute item
            poseStack.pushPose();
            // transforms
            this.getParentModel().translateToHand(HumanoidArm.RIGHT, poseStack);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.translate(1.0F / 16.0F, 2.0F / 16.0F, -10.0F / 16.0F);
            // render the item stack
            Minecraft.getInstance().getItemRenderer().renderStatic(entity, panflute, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    false, poseStack, multiBufferSource, entity.level, packedLight, OverlayTexture.NO_OVERLAY, 0);
            // finish rendering
            poseStack.popPose();
        }
    }
}