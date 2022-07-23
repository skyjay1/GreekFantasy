package greekfantasy.client.entity.model;
import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import net.minecraft.client.model.AgeableListModel;
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
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;

public class OrthusModel<T extends TamableAnimal & NeutralMob> extends AgeableListModel<T> {
	public static final ModelLayerLocation ORTHUS_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "orthus"), "orthus");

	protected final ModelPart rightHead;
	protected final ModelPart leftHead;
	protected final ModelPart body;
	protected final ModelPart mane;
	protected final ModelPart rightHindLeg;
	protected final ModelPart leftHindLeg;
	protected final ModelPart rightFrontLeg;
	protected final ModelPart leftFrontLeg;
	protected final ModelPart tail;
	protected final ModelPart realTail;

	public OrthusModel(ModelPart root) {
		//super(false, 5.0F, 2.0F, 2.0F, 2.0F, 13.5F);
		this.rightHead = root.getChild("right_head");
		this.leftHead = root.getChild("left_head");
		this.body = root.getChild("body");
		this.mane = root.getChild("mane");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.tail = root.getChild("tail");
		this.realTail = this.tail.getChild("real_tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		GFModelUtil.addOrReplaceOrthusHead(partdefinition, "right_head", -4.0F);
		GFModelUtil.addOrReplaceOrthusHead(partdefinition, "left_head", 2.0F);

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 14).addBox(-4.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(21, 0).addBox(-4.0F, 2.0F, -4.0F, 8.0F, 6.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 14.0F, 2.0F, -1.5708F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 16.0F, 7.0F));
		partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.5F, 16.0F, 7.0F));
		partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 16.0F, -4.0F));
		partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.5F, 16.0F, -4.0F));

		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, ((float)Math.PI / 5F), 0.0F, 0.0F));
		tail.addOrReplaceChild("real_tail", CubeListBuilder.create().texOffs(9, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.rightHead, this.leftHead);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.mane, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float bobAmount, float netHeadYaw, float headPitch) {
		// head rotations
		this.rightHead.y = 12.5F;
		float headXRot = headPitch * ((float)Math.PI / 180F);
		float headYRot = netHeadYaw * ((float)Math.PI / 180F);
		this.rightHead.xRot = headXRot;
		this.rightHead.yRot = headYRot * 0.8F + 0.3491F;
		this.leftHead.xRot = headXRot;
		this.leftHead.yRot = headYRot * 0.8F - 0.3491F;
		// tail rotations
		// bobAmount is usually ageInTicks but the renderer overrides it with a tail angle calculation
		this.tail.xRot = bobAmount;
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		// tail
		if (entity.isAngry()) {
			this.tail.yRot = 0.0F;
		} else {
			this.tail.yRot = -Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}
		// sitting / walking
		if (entity.isInSittingPose() || entity.isOrderedToSit()) {
			this.mane.setPos(-1.0F, 14.0F, -7.5F);
			this.mane.xRot = 1.2566371F;
			this.mane.yRot = 0.0F;

			this.body.setPos(0.0F, 18.0F, 0.0F);
			this.body.xRot = 0.7853982F;

			this.tail.setPos(-1.0F, 21.0F, 6.0F);

			this.rightHindLeg.setPos(-2.5F, 22.7F, 2.0F);
			this.rightHindLeg.xRot = 4.712389F;
			this.leftHindLeg.setPos(0.5F, 22.7F, 2.0F);
			this.leftHindLeg.xRot = 4.712389F;

			this.rightFrontLeg.xRot = 5.811947F;
			this.rightFrontLeg.setPos(-2.49F, 17.0F, -4.0F);
			this.leftFrontLeg.xRot = 5.811947F;
			this.leftFrontLeg.setPos(0.51F, 17.0F, -4.0F);
		} else {
			this.body.setPos(0.0F, 13.0F, 2.0F);
			this.body.xRot = 1.5707964F;
			this.mane.setPos(-1.0F, 12.0F, -8.0F);
			this.mane.xRot = this.body.xRot;

			this.tail.setPos(-1.0F, 12.0F, 8.0F);

			this.rightHindLeg.setPos(-2.5F, 16.0F, 7.0F);
			this.leftHindLeg.setPos(0.5F, 16.0F, 7.0F);
			this.rightFrontLeg.setPos(-2.5F, 16.0F, -4.0F);
			this.leftFrontLeg.setPos(0.5F, 16.0F, -4.0F);

			this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
			this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
			this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
			this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}
	}
}