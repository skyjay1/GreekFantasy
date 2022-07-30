package greekfantasy.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.client.entity.model.GoldenRamModel;
import greekfantasy.entity.GoldenRam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class GoldenRamFurLayer<T extends GoldenRam> extends RenderLayer<T, GoldenRamModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GreekFantasy.MODID, "textures/entity/golden_ram/golden_ram_wool.png");

    private final SheepFurModel<T> model;

    public GoldenRamFurLayer(RenderLayerParent<T, GoldenRamModel<T>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new SheepFurModel<>(modelSet.bakeLayer(ModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isSheared()) {
            return;
        }

        if (entity.isInvisible()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.shouldEntityAppearGlowing(entity)) {
                this.getParentModel().copyPropertiesTo(this.model);
                this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
                this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.outline(TEXTURE));
                this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0.0F, 0.0F, 0.0F, 1.0F);
            }

        } else {
            coloredCutoutModelCopyLayerRender(getParentModel(), this.model, TEXTURE, poseStack, multiBufferSource, packedLight, entity, limbSwing,
                    limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
        }
    }
}
