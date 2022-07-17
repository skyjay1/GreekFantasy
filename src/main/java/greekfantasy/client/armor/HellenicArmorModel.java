package greekfantasy.client.armor;

import greekfantasy.GreekFantasy;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class HellenicArmorModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation HELLENIC_ARMOR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "hellenic_armor"), "hellenic_armor");

    public HellenicArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, LayerDefinitions.OUTER_ARMOR_DEFORMATION), PartPose.ZERO);

        head.addOrReplaceChild("crest", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-12.85F, -0.5F, -1.15F, 10.0F, 1.0F, 9.0F, new CubeDeformation(0.85F, 0.0F, 0.85F))
                .texOffs(0, 18).addBox(-13.85F, -0.5F, -2.15F, 12.0F, 1.0F, 11.0F, new CubeDeformation(1.0F, 0.0F, 1.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
