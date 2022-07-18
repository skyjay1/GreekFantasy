package greekfantasy.client.entity.model;


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Elpis;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class ElpisModel<T extends Elpis> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
	public static final ModelLayerLocation ELPIS_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "elpis"), "elpis");

	protected final ModelPart head;
	protected final ModelPart body;
	protected final ModelPart leftArm;
	protected final ModelPart rightArm;
	protected final ModelPart leftWing;
	protected final ModelPart rightWing;
	protected final ModelPart item;

	protected float alpha;

	public ElpisModel(ModelPart root) {
		super(RenderType::entityTranslucent, false, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.leftArm = body.getChild("left_arm");
		this.rightArm = body.getChild("right_arm");
		this.leftWing = body.getChild("left_wing");
		this.rightWing = body.getChild("right_wing");
		this.item = body.getChild("item");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.01F, -2.5F, 5.0F, 5.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 16).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 20.0F, 0.0F)); // offset was 0, -4, 0

		body.addOrReplaceChild("item", CubeListBuilder.create(), PartPose.offset(0.5F, 4.0F, 3.5F));

		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-1.75F, 0.5F, 0.0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(1.75F, 0.5F, 0.0F));

		body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(14, 14).mirror().addBox(-1.0F, -1.0F, 0.0F, 1.0F, 6.0F, 8.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.5F, 1.0F, 1.0F, 0.43633232F, 0.0F, 0.0F));
		body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(14, 14).addBox(0.0F, -1.0F, 0.0F, 1.0F, 6.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 1.0F, 1.0F, 0.43633232F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// set head rotation
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		// determine wing rotation
		float baseWingY = 0.61086524F;
		float wingY = Mth.cos(ageInTicks * 20.0F * ((float)Math.PI / 180F) + limbSwingAmount) * (float)Math.PI * 0.15F;
		this.leftWing.yRot = baseWingY - wingY;
		this.rightWing.yRot = -baseWingY + wingY;
		// determine arm rotation
		if(!entity.getMainHandItem().isEmpty() || !entity.getOffhandItem().isEmpty()) {
			this.rightArm.xRot = -0.7854F;
			this.leftArm.xRot = -0.7854F;
			this.item.xRot = -0.7854F;
		} else {
			this.rightArm.xRot = 0.0F;
			this.leftArm.xRot = 0.0F;
			this.item.xRot = 0.0F;
		}
		this.rightArm.zRot = 0.0F;
		this.leftArm.zRot = 0.0F;
		this.item.zRot = 0.0F;
		AnimationUtils.bobModelPart(this.rightArm, ageInTicks, 1.0F);
		AnimationUtils.bobModelPart(this.leftArm, ageInTicks, -1.0F);
		AnimationUtils.bobModelPart(this.item, ageInTicks, 0.5F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
							   float red, float green, float blue, float alpha) {
		super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, this.alpha);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(getHead());
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(body);
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
		this.body.translateAndRotate(poseStack);
		this.item.translateAndRotate(poseStack);
		poseStack.mulPose(Vector3f.XP.rotation(-0.53633232F));
		poseStack.scale(0.7F, 0.7F, 0.7F);
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}