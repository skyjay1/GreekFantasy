package greekfantasy.client.entity.model;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class MakhaiModel<T extends LivingEntity> extends HumanoidModel<T> {
	public static final ModelLayerLocation MAKHAI_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "makhai"), "makhai");

	protected final ModelPart leftHead;
	public final ModelPart frontRightArm;
	public final ModelPart backRightArm;
	public final ModelPart frontLeftArm;
	public final ModelPart backLeftArm;

	public MakhaiModel(ModelPart root) {
		super(root);
		this.leftHead = root.getChild("left_head");
		this.frontRightArm = root.getChild("front_right_arm");
		this.backRightArm = root.getChild("back_right_arm");
		this.frontLeftArm = root.getChild("front_left_arm");
		this.backLeftArm = root.getChild("back_left_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0436F));
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(-4.0F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		partdefinition.addOrReplaceChild("front_right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, -1.1345F, 0.5236F, -0.1745F));
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, 0.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.0F, 0.0F, 1.1345F, -1.4835F, -0.48F));
		partdefinition.addOrReplaceChild("back_right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, 1.0908F, 0.5672F, 0.9163F));

		partdefinition.addOrReplaceChild("front_left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.0F, 3.0F, 0.0F, -0.6109F, -0.3491F, -0.1745F));
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 2.0F, 0.0F, 1.0908F, 1.3963F, 0.2182F));
		partdefinition.addOrReplaceChild("back_left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(0.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 2.0F, 0.0F, 0.7854F, 0.0873F, -0.0873F));

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return Iterables.concat(super.headParts(), ImmutableList.of(this.leftHead));
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.backLeftArm, this.frontLeftArm, this.backRightArm, this.frontRightArm));
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		// update head angles
		head.setPos(-4.0F, 0.0F, 0.0F);
		leftHead.xRot = head.xRot;
		leftHead.yRot = head.yRot * -1.0F + (float)Math.PI;
		leftHead.zRot = head.zRot + 0.1309F;
		// update arm angles
		// right arms
		float x = rightArm.xRot * 0.85F;
		float y = rightArm.yRot * 0.85F;
		float z = rightArm.zRot * 0.85F;
		// back
		backRightArm.xRot = -x + 1.1345F;
		backRightArm.yRot = -y + -1.5272F;
		backRightArm.zRot = -z + -0.48F;
		// middle
		rightArm.xRot += -1.1345F;
		rightArm.yRot += 0.5236F;
		rightArm.zRot += -0.1745F;
		// front
		frontRightArm.xRot = x + 1.0908F;
		frontRightArm.yRot = y + 0.5672F;
		frontRightArm.zRot = z + 0.9163F;
		// left arms
		x = leftArm.xRot * 0.85F;
		y = leftArm.yRot * 0.85F;
		z = leftArm.zRot * 0.85F;
		// back
		frontLeftArm.xRot = x + 1.0908F;
		frontLeftArm.yRot = y + 1.3963F;
		frontLeftArm.zRot = z + 0.2182F;
		// middle
		leftArm.setPos(5.0F, 3.0F, 0.0F);
		leftArm.xRot += -0.6109F;
		leftArm.yRot += -0.3491F;
		leftArm.zRot += -0.1745F;
		// front
		backLeftArm.xRot = -x + 0.7854F;
		backLeftArm.yRot = -y + 0.0873F;
		backLeftArm.zRot = -z + -0.0873F;
	}

	@Override
	public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
	}
}