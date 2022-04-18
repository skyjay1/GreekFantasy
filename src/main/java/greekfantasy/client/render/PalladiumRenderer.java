package greekfantasy.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.layer.PalladiumTorchLayer;
import greekfantasy.client.render.model.PalladiumModel;
import greekfantasy.entity.misc.PalladiumEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PalladiumRenderer<T extends PalladiumEntity> extends LivingRenderer<T, PalladiumModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/wood.png");

    public PalladiumRenderer(final EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PalladiumModel<T>(0.0F), 0.5F);
        this.addLayer(new PalladiumTorchLayer<>(this));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
                       IRenderTypeBuffer bufferIn, int packedLightIn) {
        // render block
        matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
        Minecraft.getInstance().getBlockRenderer().renderBlock(Blocks.OAK_SLAB.defaultBlockState(),
                matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        // translate model
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        // render model
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(final T entity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return entity.shouldShowName() && entity.hasCustomName() && super.shouldShowName(entity);
    }
}
