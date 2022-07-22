package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import greekfantasy.GreekFantasy;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class NymphModel<T extends LivingEntity> extends HumanoidModel<T> {
	public static final ModelLayerLocation NYMPH_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "nymph"), "nymph");

	public NymphModel(ModelPart root, boolean hatVisible) {
		super(root);
		this.hat.visible = hatVisible;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(21, 0).addBox(-3.5F, 0.0F, -1.5F, 7.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, 0.0F, -1.5F, 6.0F, 12.0F, 3.0F, CubeDeformation.NONE), PartPose.ZERO);
		body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(30, 7).addBox(-2.99F, 0.3473F, -2.5F, 6.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, -0.1745F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(19, 16).addBox(0.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 2.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(29, 16).addBox(-2.0F, -2.0F, -1.5F, 2.0F, 12.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-3.0F, 2.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(40, 16).addBox(-1.49F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(1.5F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(52, 16).addBox(-1.51F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-1.5F, 12.0F, 0.0F));
		
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
						  float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		// fix rotation points
		leftArm.setPos(3.0F, 2.0F, 0.0F);
		rightArm.setPos(-3.0F, 2.0F, 0.0F);
		leftLeg.setPos(1.5F, 12.0F, 0.0F);
		rightLeg.setPos(-1.5F, 12.0F, 0.0F);
		// held item
		if (!entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
			this.getArm(entity.getMainArm()).xRot += -0.42F;
		}
	}
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
							   float red, float green, float blue, float alpha) {
		// translate and rotate based on swim amount
		if(swimAmount > 0.0F) {
			poseStack.translate(0.0D, swimAmount * 1.375D, swimAmount * -0.4D);
			poseStack.mulPose(Vector3f.XP.rotationDegrees(swimAmount * 90.0F));
		}
		// render model
		super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}