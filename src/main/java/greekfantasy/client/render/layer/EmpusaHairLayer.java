package greekfantasy.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.client.render.model.EmpusaModel;
import greekfantasy.entity.EmpusaEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class EmpusaHairLayer<T extends EmpusaEntity> extends LayerRenderer<T, EmpusaModel<T>> {

    public EmpusaHairLayer(IEntityRenderer<T, EmpusaModel<T>> ientityrenderer) {
        super(ientityrenderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            // prepare to render fire on top of head
            final float scale = 0.5F;
            matrixStackIn.pushPose();
            this.getParentModel().getHeadModel().translateAndRotate(matrixStackIn);
            matrixStackIn.translate(0.5D * scale, -0.5, -0.5D * scale);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrixStackIn.scale(scale, -scale, -scale);
            // render fire here
            // note: packed light flag 15728640 uses world light, 15728880 uses constant/full light
            Minecraft.getInstance().getBlockRenderer().renderBlock(Blocks.FIRE.defaultBlockState(),
                    matrixStackIn, bufferIn, 15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
            // finish rendering
            matrixStackIn.popPose();
        }
    }
}