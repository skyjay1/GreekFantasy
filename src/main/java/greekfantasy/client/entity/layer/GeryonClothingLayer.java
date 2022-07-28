package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.GeryonModel;
import greekfantasy.entity.boss.Geryon;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class GeryonClothingLayer<T extends Geryon, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation GERYON_CLOTHES_TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/geryon/geryon_overlay.png");

    private final GeryonModel<T> layerModel;

    public GeryonClothingLayer(RenderLayerParent<T, M> parent, final EntityModelSet entityModelSet) {
        super(parent);
        layerModel = new GeryonModel<>(entityModelSet.bakeLayer(GeryonModel.GERYON_ARMOR_MODEL_RESOURCE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(getParentModel(), this.layerModel, GERYON_CLOTHES_TEXTURE, poseStack, multiBufferSource, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
    }
}
