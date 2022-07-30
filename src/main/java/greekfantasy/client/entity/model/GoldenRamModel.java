package greekfantasy.client.entity.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.GoldenRam;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class GoldenRamModel<T extends GoldenRam> extends SheepModel<T> {
    public static final ModelLayerLocation RAM_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "ram"), "ram");

    protected final ModelPart horns;

    public GoldenRamModel(ModelPart root) {
        super(root);
        this.horns = root.getChild("horns");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = QuadrupedModel.createBodyMesh(12, CubeDeformation.NONE);
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 6.0F, -8.0F));
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        PartDefinition horns = partdefinition.addOrReplaceChild("horns", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, -8.0F));
        GFModelUtil.addOrReplaceRamHorn(horns, "left_horn", 3.0F, -2.0F, -4.0F, true);
        GFModelUtil.addOrReplaceRamHorn(horns, "right_horn", -3.0F, -2.0F, -4.0F, false);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void renderHorns(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int packedLight, final int packedOverlay) {
        horns.copyFrom(head);
        horns.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

}
