package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import greekfantasy.client.entity.model.AraModel;
import greekfantasy.entity.monster.Ara;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AraFlintLayer<T extends Ara> extends RenderLayer<T, AraModel<T>> {

    protected ItemStack itemStack;

    public AraFlintLayer(RenderLayerParent<T, AraModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // initialize item stack
        if (null == itemStack) {
            itemStack = new ItemStack(Items.FLINT);
        }
        if (!entity.isInvisible()) {
            // prepare to render flint inside body opening
            final float scale = 0.25F;
            final float tx = (4.0F / 16.0F) * scale;
            final float ty = (12.0F / 16.0F) * scale;
            final float tz = (-2.0F / 16.0F) * scale;
            final float spin = 180.0F + (180.0F) * Mth.cos((entity.tickCount + entity.getId() * 3 + partialTick) * 0.08F);
            poseStack.pushPose();
            // transforms
            this.getParentModel().body.translateAndRotate(poseStack);
            poseStack.translate(tx, ty, tz);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.scale(scale, -scale, -scale);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(spin));
            // render the item stack
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED,
                    packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, 0);
            // finish rendering
            poseStack.popPose();
        }
    }
}