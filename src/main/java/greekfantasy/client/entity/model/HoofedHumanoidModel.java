package greekfantasy.client.entity.model;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class HoofedHumanoidModel<T extends LivingEntity> extends HumanoidModel<T> {

	private final ModelPart leftLegUpper;
	private final ModelPart leftLegLower;
	private final ModelPart leftHoof;
	private final ModelPart rightLegUpper;
	private final ModelPart rightLegLower;
	private final ModelPart rightHoof;
	private final ModelPart tailUpper;
	private final ModelPart tailLower;

	public HoofedHumanoidModel(ModelPart root, final boolean showTail, final boolean showHat) {
		super(root);
		this.tailLower = body.getChild("tail_lower");
		this.tailUpper = tailLower.getChild("tail_upper");
		this.leftLegUpper = leftLeg.getChild("left_leg_upper");
		this.leftLegLower = leftLegUpper.getChild("left_leg_lower");
		this.leftHoof = leftLegLower.getChild("left_hoof");
		this.rightLegUpper = rightLeg.getChild("right_leg_upper");
		this.rightLegLower = rightLegUpper.getChild("right_leg_lower");
		this.rightHoof = rightLegLower.getChild("right_hoof");
		this.tailUpper.visible = this.tailLower.visible = showTail;
		this.hat.visible = showHat;
	}

	public static MeshDefinition createMesh(CubeDeformation cubeDeformation) {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition tail_lower = body.addOrReplaceChild("tail_lower", CubeListBuilder.create().texOffs(0, 51).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 10.0F, 2.0F, 0.5236F, 0.0F, 0.0F));
		PartDefinition tail_upper = tail_lower.addOrReplaceChild("tail_upper", CubeListBuilder.create()
				.texOffs(4, 51).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, cubeDeformation)
				.texOffs(0, 58).addBox(-1.0F, 2.5F, -0.5F, 2.0F, 4.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 5.0F, -1.0F, 0.3491F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(5.0F, 2.5F, 0.0F));
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(-5.0F, 2.5F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 2.0F));
		PartDefinition left_leg_upper = left_leg.addOrReplaceChild("left_leg_upper", CubeListBuilder.create().texOffs(16, 36).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));
		PartDefinition left_leg_lower = left_leg_upper.addOrReplaceChild("left_leg_lower", CubeListBuilder.create().texOffs(16, 46).addBox(-1.91F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 6.0F, -2.0F, 0.7854F, 0.0F, 0.0F));
		PartDefinition left_hoof = left_leg_lower.addOrReplaceChild("left_hoof", CubeListBuilder.create().texOffs(16, 56).addBox(-1.9F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 6.0F, 4.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 2.0F));
		PartDefinition right_leg_upper = right_leg.addOrReplaceChild("right_leg_upper", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));
		PartDefinition right_leg_lower = right_leg_upper.addOrReplaceChild("right_leg_lower", CubeListBuilder.create().texOffs(0, 26).addBox(-2.09F, 0.0F, 0.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 6.0F, -2.0F, 0.7854F, 0.0F, 0.0F));
		PartDefinition right_hoof = right_leg_lower.addOrReplaceChild("right_hoof", CubeListBuilder.create().texOffs(0, 36).addBox(-2.1F, 0.0F, -4.0F, 4.0F, 4.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 6.0F, 4.0F, -0.5236F, 0.0F, 0.0F));

		return meshdefinition;
	}

	@Override
	public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
		final float ticks = entityIn.tickCount + partialTick;
		float limbSwingSin = Mth.cos(limbSwing + (float) Math.PI) * limbSwingAmount;
		float limbSwingCos = Mth.cos(limbSwing) * limbSwingAmount;
		float rightLegSwing = 0.38F * limbSwingSin;
		float leftLegSwing = 0.38F * limbSwingCos;
		// legs
		rightLegLower.xRot = 0.7854F + rightLegSwing;
		rightHoof.xRot = -0.5236F - rightLegSwing;
		leftLegLower.xRot = 0.7854F + leftLegSwing;
		leftHoof.xRot = -0.5236F - leftLegSwing;

		// tail
		if (tailUpper.visible) {
			float idleSwing = 0.1F * Mth.cos(ticks * 0.08F);
			float tailSwing = 0.42F * limbSwingCos;
			tailUpper.xRot = 0.6854F + tailSwing;
			tailLower.xRot = 0.3491F + tailSwing * 0.6F;
			tailUpper.zRot = idleSwing;
			tailLower.zRot = idleSwing * 0.85F;
		}
	}
}