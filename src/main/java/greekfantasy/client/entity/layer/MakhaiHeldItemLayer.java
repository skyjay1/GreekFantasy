package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import greekfantasy.client.entity.model.MakhaiModel;
import greekfantasy.entity.Makhai;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class MakhaiHeldItemLayer<T extends Makhai> extends RenderLayer<T, MakhaiModel<T>> {

    public MakhaiHeldItemLayer(RenderLayerParent<T, MakhaiModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            ItemStack left = entity.getItemBySlot(EquipmentSlot.OFFHAND);
            ItemStack right = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            // left hand items
            if (!left.isEmpty()) {
                renderItem(poseStack, multiBufferSource, packedLight, entity, getParentModel().backRightArm, left, 0, 0, 0, 0);
                renderItem(poseStack, multiBufferSource, packedLight, entity, getParentModel().backLeftArm, left, -0.175F, 0.025F, 0, 0);
            }
            // right hand items
            if (!right.isEmpty()) {
                renderItem(poseStack, multiBufferSource, packedLight, entity, getParentModel().frontLeftArm, right, -0.125F, 0, 0, 0);
                renderItem(poseStack, multiBufferSource, packedLight, entity, getParentModel().frontRightArm, right, 0.125F, 0.175F, 0, -90F);
            }
        }
    }

    private void renderItem(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                            ModelPart arm, ItemStack item, float offsetX, float offsetY, float offsetZ, float rotation) {
        poseStack.pushPose();
        // transforms
        arm.translateAndRotate(poseStack);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        poseStack.translate(1.0F / 16.0F, 0.125D, -0.625D);
        // other rotation
        poseStack.translate(offsetX, offsetY - 0.3D, offsetZ + 0.125D);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation));
        // render the item stack
        Minecraft.getInstance().getItemRenderer().renderStatic(entity, item, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                false, poseStack, multiBufferSource, entity.getCommandSenderWorld(), packedLight, OverlayTexture.NO_OVERLAY, 0);
        // finish rendering
        poseStack.popPose();
    }
}