package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.client.render.model.PalladiumModel;
import greekfantasy.entity.misc.PalladiumEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolenFlowerLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;

public class PalladiumTorchLayer<T extends PalladiumEntity> extends LayerRenderer<T, PalladiumModel<T>> {

    public PalladiumTorchLayer(IEntityRenderer<T, PalladiumModel<T>> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {

            matrixStackIn.pushPose();
            // transforms
            this.getParentModel().translateToHand(HandSide.RIGHT, matrixStackIn);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrixStackIn.translate(1.0F / 16.0F, 0.125D, -0.625D);
            // render the item stack
            Minecraft.getInstance().getItemRenderer().renderStatic(entity, new ItemStack(Items.SOUL_TORCH), ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    false, matrixStackIn, bufferIn, entity.getCommandSenderWorld(), packedLightIn, OverlayTexture.NO_OVERLAY);
            // finish rendering
            matrixStackIn.popPose();
            /*
            matrixStackIn.pushPose();
            // transforms
            this.getParentModel().translateToHand(HandSide.LEFT, matrixStackIn);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrixStackIn.translate(-1.0F / 16.0F, 0.125D, -0.625D);
            // render the item stack
            Minecraft.getInstance().getItemRenderer().renderStatic(entity, new ItemStack(Items.SOUL_TORCH), ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                    true, matrixStackIn, bufferIn, entity.getCommandSenderWorld(), packedLightIn, OverlayTexture.NO_OVERLAY);
            // finish rendering
            matrixStackIn.popPose();
            */
        }
    }
}