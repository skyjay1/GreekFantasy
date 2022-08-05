package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;

public class PlayerSoulFireLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {

    public PlayerSoulFireLayer(RenderLayerParent<T, PlayerModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        final ItemStack item = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!entity.isInvisible() && !item.isEmpty() && item.hasCustomHoverName() && "skyjay1".equals(item.getHoverName().getString())) {
            // prepare to render fire on top of head
            final float scale = 0.5F;
            poseStack.pushPose();
            this.getParentModel().getHead().translateAndRotate(poseStack);
            poseStack.translate(0.5D * scale, -0.5, -0.5D * scale);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.scale(scale, -scale, -scale);
            // render fire here
            // note: packed light flag 15728640 uses world light, 15728880 uses constant/full light
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.SOUL_FIRE.defaultBlockState(),
                    poseStack, multiBufferSource, 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            // finish rendering
            poseStack.popPose();
        }
    }
}