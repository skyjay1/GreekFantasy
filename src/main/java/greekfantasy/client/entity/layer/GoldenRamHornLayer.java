package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.GoldenRamModel;
import greekfantasy.entity.GoldenRam;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GoldenRamHornLayer<T extends GoldenRam> extends RenderLayer<T, GoldenRamModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/golden_ram/golden_ram_horn.png");

    public GoldenRamHornLayer(RenderLayerParent<T, GoldenRamModel<T>> context) {
        super(context);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getParentModel().renderHorns(poseStack, multiBufferSource.getBuffer(getParentModel().renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
    }
}
