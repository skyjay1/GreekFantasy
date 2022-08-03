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

public class NemeanArmorModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation NEMEAN_ARMOR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "nemean_armor"), "nemean_armor");

    public NemeanArmorModel(ModelPart root) {
        super(root);
        leftLeg.visible = false;
        rightLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        CubeDeformation cubeDeformation = LayerDefinitions.INNER_ARMOR_DEFORMATION;
        CubeDeformation noDeformation = cubeDeformation.extend(-0.5F);
        MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition lionHead = hat.addOrReplaceChild("lion_head", CubeListBuilder.create()
                .texOffs(36, 0).addBox(-5.0F, -8.9F, -7.0F, 10.0F, 10.0F, 4.0F, cubeDeformation)
                .texOffs(36, 14).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 9.0F, 1.0F, cubeDeformation)
                .texOffs(0, 0).addBox(-4.0F, -9.0F, -10.0F, 8.0F, 1.0F, 2.0F, cubeDeformation)
                .texOffs(0, 4).addBox(-4.0F, -7.0F, -12.0F, 8.0F, 3.0F, 4.0F, cubeDeformation)
                .texOffs(21, 0).addBox(-2.0F, -6.01F, -13.0F, 4.0F, 2.0F, 1.0F, cubeDeformation)
                .texOffs(0, 11).addBox(-4.0F, -4.01F, -12.0F, 8.0F, 2.0F, 3.0F, noDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, -0.2618F, 0.0F, 0.0F));
        lionHead.addOrReplaceChild("ears", CubeListBuilder.create().texOffs(21, 4).mirror().addBox(2.0F, -2.75F, 1.0F, 2.0F, 2.0F, 1.0F, cubeDeformation).mirror(false)
                .texOffs(21, 4).addBox(-4.0F, -2.75F, 1.0F, 2.0F, 2.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -9.5F, -8.25F, -0.9599F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 18.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("upper_tail", CubeListBuilder.create()
                .texOffs(56, 35).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 5.0F, 1.0F, noDeformation)
                .texOffs(56, 42).addBox(-1.0F, -5.5F, -1.0F, 2.0F, 2.0F, 1.0F, noDeformation), PartPose.offsetAndRotation(0.0F, 13.0F, 2.0F, -2.7925F, 0.0F, 0.0F));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(-5.0F, 2.0F, 0.0F));
        rightArm.addOrReplaceChild("right_claws", CubeListBuilder.create().texOffs(56, 32).addBox(-1.0F, 0.5F, 0.5F, 3.0F, 1.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(-1.0F, -1.5F, -2.5F, -0.9599F, 0.0F, 0.0F));

        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(5.0F, 2.0F, 0.0F));
        leftArm.addOrReplaceChild("left_claws", CubeListBuilder.create().texOffs(56, 32).addBox(-1.0F, 0.5F, 0.5F, 3.0F, 1.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -1.5F, -2.5F, -0.9599F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void copyRotations(HumanoidModel<?> model) {
        this.head.copyFrom(model.head);
        this.hat.copyFrom(model.hat);
        this.body.copyFrom(model.body);
        this.leftArm.copyFrom(model.leftArm);
        this.rightArm.copyFrom(model.rightArm);
        this.leftLeg.copyFrom(model.leftLeg);
        this.rightLeg.copyFrom(model.rightLeg);
    }
}
