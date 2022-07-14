package greekfantasy.client.entity.model;


import greekfantasy.GreekFantasy;
import greekfantasy.entity.Satyr;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class SatyrModel<T extends Satyr> extends HoofedHumanoidModel<T> {
    public static final ModelLayerLocation SATYR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "satyr"), "satyr");
	public static final ModelLayerLocation SATYR_INNER_ARMOR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "satyr_inner_armor"), "satyr_inner_armor");

    public SatyrModel(ModelPart root) {
        super(root, true, true);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = HoofedHumanoidModel.createMesh(cubeDeformation);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation)
                .texOffs(24, 0).addBox(-1.0F, -3.0F, -5.0F, 2.0F, 1.0F, 1.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(56, 16).addBox(-1.5F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, cubeDeformation), PartPose.offsetAndRotation(-3.0F, -4.0F, -1.0F, -0.2618F, -0.3491F, 0.0F));
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(56, 22).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, cubeDeformation), PartPose.offsetAndRotation(4.0F, -4.0F, -1.0F, -0.2618F, 0.3491F, 0.0F));

        PartDefinition right_horn_lower = head.addOrReplaceChild("right_horn_lower", CubeListBuilder.create().texOffs(47, 48).addBox(-5.0F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -6.0F, -1.0F, 0.8727F, 0.0F, 0.0F));
        PartDefinition right_horn_middle = right_horn_lower.addOrReplaceChild("right_horn_middle", CubeListBuilder.create().texOffs(47, 54).addBox(-1.25F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(-4.0F, -4.0F, -1.0F, -0.7854F, 0.0F, 0.0F));
        PartDefinition right_horn_upper = right_horn_middle.addOrReplaceChild("right_horn_upper", CubeListBuilder.create().texOffs(47, 59).addBox(-1.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition left_horn_lower = head.addOrReplaceChild("left_horn_lower", CubeListBuilder.create().texOffs(54, 48).addBox(4.0F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -6.0F, -1.0F, 0.8727F, 0.0F, 0.0F));
        PartDefinition left_horn_middle = left_horn_lower.addOrReplaceChild("left_horn_middle", CubeListBuilder.create().texOffs(54, 54).addBox(8.25F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(-4.0F, -4.0F, -1.0F, -0.7854F, 0.0F, 0.0F));
        PartDefinition left_horn_upper = left_horn_middle.addOrReplaceChild("left_horn_upper", CubeListBuilder.create().texOffs(54, 59).addBox(8.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
        if (entity.holdingPanfluteTime > 0) {
            // set arm rotations when holding panflute
            final float armPercent = entity.getArmMovementPercent(partialTick);
            this.rightArm.xRot = -1.31F * armPercent;
            this.rightArm.yRot = -0.68F * armPercent;
            this.leftArm.xRot = -1.22F * armPercent;
            this.leftArm.yRot = -0.43F * armPercent;
            this.leftArm.zRot = 1.17F * armPercent;
        }
    }
}