package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Unicorn;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class UnicornModel<T extends Unicorn> extends HorseModel<T> {
    public static final ModelLayerLocation UNICORN_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "unicorn"), "unicorn");

    private final ModelPart horn;

    public UnicornModel(final ModelPart root) {
        super(root);
        this.horn = root.getChild("horn");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HorseModel.createBodyMesh(CubeDeformation.NONE);
        PartDefinition partdefinition = meshdefinition.getRoot();
        // create horn model as child of root in order to render separately
        PartDefinition horn = partdefinition.addOrReplaceChild("horn", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.5F, -21.0F, 0.5F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 6).addBox(-1.0F, -16.0F, 0.0F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, -4.0F, -11.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    public void renderHorn(T entity, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn,
                           int packedOverlayIn, float limbSwing, float limbSwingAmount) {
        this.horn.copyFrom(this.headParts);
        this.horn.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
    }
}
