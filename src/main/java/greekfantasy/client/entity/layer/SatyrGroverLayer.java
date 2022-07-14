package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.SatyrModel;
import greekfantasy.entity.Satyr;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SatyrGroverLayer<T extends Satyr, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation GROVER_OVERLAY_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/satyr/grover_overlay.png");

    private final SatyrModel<T> layerModel;

    public SatyrGroverLayer(RenderLayerParent<T, M> parent, EntityModelSet entityModelSet) {
        super(parent);
        this.layerModel = new SatyrModel<>(entityModelSet.bakeLayer(SatyrModel.SATYR_INNER_ARMOR_MODEL_RESOURCE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible() && entity.hasCustomName() && "Grover".equals(entity.getCustomName().getString())) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, GROVER_OVERLAY_TEXTURE, poseStack, multiBufferSource, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }
}
