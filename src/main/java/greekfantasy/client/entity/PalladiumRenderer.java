package greekfantasy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.layer.PalladiumTorchLayer;
import greekfantasy.client.entity.model.PalladiumModel;
import greekfantasy.entity.Palladium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PalladiumRenderer<T extends Palladium> extends LivingEntityRenderer<T, PalladiumModel<T>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/palladium.png");

    public PalladiumRenderer(final EntityRendererProvider.Context context) {
        super(context, new PalladiumModel<T>(context.bakeLayer(PalladiumModel.PALLADIUM_MODEL_RESOURCE)), 0.0F);
        this.addLayer(new PalladiumTorchLayer<>(this));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int packedLightIn) {
        // render block
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.OAK_SLAB.defaultBlockState(),
                poseStack, multiBufferSource, packedLightIn, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        // translate model
        poseStack.translate(0.5D, 0.5D, 0.5D);
        // render model
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
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
