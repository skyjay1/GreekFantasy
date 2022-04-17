package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.client.render.model.MakhaiModel;
import greekfantasy.entity.MakhaiEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class MakhaiHeldItemLayer<T extends MakhaiEntity> extends LayerRenderer<T, MakhaiModel<T>> {

    public MakhaiHeldItemLayer(IEntityRenderer<T, MakhaiModel<T>> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            ItemStack left = entity.getItemBySlot(EquipmentSlotType.OFFHAND);
            ItemStack right = entity.getItemBySlot(EquipmentSlotType.MAINHAND);
            // left hand items
            if (!left.isEmpty()) {
//        renderItem(matrixStackIn, bufferIn, packedLightIn, entity, getEntityModel().bipedLeftArm, left, 0);
                renderItem(matrixStackIn, bufferIn, packedLightIn, entity, getParentModel().bipedRightArm2, left, 0, 0);
                renderItem(matrixStackIn, bufferIn, packedLightIn, entity, getParentModel().bipedLeftArm2, left, -0.125F, 0);
            }
            // right hand items
            if (!right.isEmpty()) {
//        renderItem(matrixStackIn, bufferIn, packedLightIn, entity, getEntityModel().bipedRightArm, right, 0);
                renderItem(matrixStackIn, bufferIn, packedLightIn, entity, getParentModel().bipedLeftArm3, right, 0, 0);
                renderItem(matrixStackIn, bufferIn, packedLightIn, entity, getParentModel().bipedRightArm3, right, -0.125F, -90F);
            }
        }
    }

    private void renderItem(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
                            ModelRenderer arm, ItemStack item, float offsetX, float rotation) {
        matrixStackIn.pushPose();
        // transforms
        arm.translateAndRotate(matrixStackIn);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrixStackIn.translate(1.0F / 16.0F, 0.125D, -0.625D);
        // other rotation
        matrixStackIn.translate(offsetX, -0.3D, 0.125D);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rotation));
        // render the item stack
        Minecraft.getInstance().getItemRenderer().renderStatic(entity, item, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                false, matrixStackIn, bufferIn, entity.getCommandSenderWorld(), packedLightIn, OverlayTexture.NO_OVERLAY);
        // finish rendering
        matrixStackIn.popPose();
    }
}