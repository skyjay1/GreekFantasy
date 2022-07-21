package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.HasHorseVariant;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class CyprianModel<T extends Mob & HasHorseVariant> extends CentaurModel<T> {

    public static final ModelLayerLocation CYPRIAN_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "cyprian"), "cyprian");

    public CyprianModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = CentaurModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = addOrReplaceBullHead(partdefinition, "head");

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static PartDefinition addOrReplaceBullHead(final PartDefinition root, final String name) {
        PartDefinition head = root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 2.0F));

        PartDefinition lower_right_horn = head.addOrReplaceChild("lower_right_horn", CubeListBuilder.create().texOffs(58, 48).addBox(-1.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -7.0F, -2.0F, 1.3963F, 1.0472F, 0.0F));
        PartDefinition middle_right_horn = lower_right_horn.addOrReplaceChild("middle_right_horn", CubeListBuilder.create().texOffs(58, 54).addBox(-0.51F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -4.0F, -2.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition top_right_horn = middle_right_horn.addOrReplaceChild("top_right_horn", CubeListBuilder.create().texOffs(58, 59).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition lower_left_horn = head.addOrReplaceChild("lower_left_horn", CubeListBuilder.create().texOffs(51, 48).addBox(0.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -7.0F, -2.0F, 1.3963F, -1.0472F, 0.0F));
        PartDefinition middle_left_horn = lower_left_horn.addOrReplaceChild("middle_left_horn", CubeListBuilder.create().texOffs(51, 54).addBox(7.49F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-7.0F, -4.0F, -2.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition top_left_horn = middle_left_horn.addOrReplaceChild("top_left_horn", CubeListBuilder.create().texOffs(51, 59).addBox(7.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        return head;
    }
}
