package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import greekfantasy.client.entity.model.PalladiumModel;
import greekfantasy.entity.Palladium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PalladiumTorchLayer<T extends Palladium> extends RenderLayer<T, PalladiumModel<T>> {

    private static final ItemStack itemStack = new ItemStack(Items.SOUL_TORCH);

    public PalladiumTorchLayer(RenderLayerParent<T, PalladiumModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {

            poseStack.pushPose();
            // transforms
            this.getParentModel().translateToHand(HumanoidArm.RIGHT, poseStack);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.translate(1.0F / 16.0F, 0.125D, -0.625D);
            // render the item stack
            Minecraft.getInstance().getItemRenderer().renderStatic(entity, itemStack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    false, poseStack, multiBufferSource, entity.level, packedLightIn, OverlayTexture.NO_OVERLAY, 0);
            // finish rendering
            poseStack.popPose();
        }
    }
}