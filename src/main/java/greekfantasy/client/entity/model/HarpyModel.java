package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Harpy;
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

public class HarpyModel<T extends Harpy> extends HumanoidModel<T> {

    public static final ModelLayerLocation HARPY_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "harpy"), "harpy");
    ;

    private final ModelPart leftWing1;
    private final ModelPart leftWing2;
    private final ModelPart leftWing3;
    private final ModelPart rightWing1;
    private final ModelPart rightWing2;
    private final ModelPart rightWing3;

    public HarpyModel(final ModelPart root) {
        super(root);
        leftWing1 = root.getChild("left_inner_wing");
        leftWing2 = leftWing1.getChild("left_middle_wing");
        leftWing3 = leftWing2.getChild("left_outer_wing");
        rightWing1 = root.getChild("right_inner_wing");
        rightWing2 = rightWing1.getChild("right_middle_wing");
        rightWing3 = rightWing2.getChild("right_outer_wing");
        // hide biped arms
        this.rightArm.visible = false;
        this.leftArm.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(40, 16).addBox(-4.0F, 0.0F, 3.0F, 8.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.ZERO);

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.ZERO);
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(16, 33).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.3491F, 0.0F, 0.0F));
        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(46, 56).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.0F, 2.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-2.1F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition right_foot = right_leg.addOrReplaceChild("right_foot", CubeListBuilder.create(), PartPose.offset(-0.1F, 0.0F, 0.0F));
        right_foot.addOrReplaceChild("right_front_outer_toe", CubeListBuilder.create().texOffs(16, 42).addBox(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 10.0F, -2.0F, 0.3491F, 0.3491F, 0.0F));
        right_foot.addOrReplaceChild("right_front_inner_toe", CubeListBuilder.create().texOffs(16, 42).addBox(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 10.0F, -2.0F, 0.3491F, -0.3491F, 0.0F));
        right_foot.addOrReplaceChild("right_back_toe", CubeListBuilder.create().texOffs(27, 43).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 10.0F, 1.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 33).addBox(-0.9F, 0.0F, -2.0F, 3.0F, 11.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition left_foot = left_leg.addOrReplaceChild("left_foot", CubeListBuilder.create(), PartPose.offset(0.1F, 0.0F, 0.0F));
        left_foot.addOrReplaceChild("left_front_inner_toe", CubeListBuilder.create().texOffs(16, 42).addBox(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 10.0F, -2.0F, 0.3491F, 0.3491F, 0.0F));
        left_foot.addOrReplaceChild("left_front_outer_toe", CubeListBuilder.create().texOffs(16, 42).addBox(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 10.0F, -2.0F, 0.3491F, -0.3491F, 0.0F));
        left_foot.addOrReplaceChild("left_back_toe", CubeListBuilder.create().texOffs(27, 43).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 10.0F, 1.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition left_inner_wing = partdefinition.addOrReplaceChild("left_inner_wing", CubeListBuilder.create().texOffs(0, 48).mirror().addBox(0.0F, 0.0F, -1.0F, 6.0F, 10.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 1.0472F));
        PartDefinition left_middle_wing = left_inner_wing.addOrReplaceChild("left_middle_wing", CubeListBuilder.create().texOffs(14, 48).mirror().addBox(0.0F, -1.0F, -1.0F, 8.0F, 10.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(6.0F, 1.0F, 0.0F, 0.0F, 0.5236F, 0.0F));
        PartDefinition left_outer_wing = left_middle_wing.addOrReplaceChild("left_outer_wing", CubeListBuilder.create().texOffs(32, 48).mirror().addBox(0.0F, -1.0F, -1.0F, 6.0F, 10.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

        PartDefinition right_inner_wing = partdefinition.addOrReplaceChild("right_inner_wing", CubeListBuilder.create().texOffs(0, 48).addBox(-6.0F, 0.0F, -1.0F, 6.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
        PartDefinition right_middle_wing = right_inner_wing.addOrReplaceChild("right_middle_wing", CubeListBuilder.create().texOffs(14, 48).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-6.0F, 1.0F, 0.0F));
        PartDefinition right_outer_wing = right_middle_wing.addOrReplaceChild("right_outer_wing", CubeListBuilder.create().texOffs(32, 48).addBox(-6.0F, -1.0F, -1.0F, 6.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-8.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightLeg, this.leftLeg, this.rightWing1, this.leftWing1, this.hat);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        final float flyingTime = entity.getFlyingTime(ageInTicks - entity.tickCount);
        final float flyingTimeLeft = 1.0F - flyingTime;
        // animate legs (only while flying)
        final float flyingTimeLeftAdjusted = Mth.clamp(flyingTimeLeft + 0.15F, 0.0F, 1.0F);
        this.leftLeg.xRot *= (flyingTimeLeftAdjusted * 0.6F);
        this.leftLeg.xRot += (-0.35F * flyingTime);
        this.rightLeg.xRot *= (flyingTimeLeftAdjusted * 0.6F);
        this.rightLeg.xRot += (-0.35F * flyingTime);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        float ticks = entity.getId() * 2 + entity.tickCount + partialTick;
        final float flyingTime = entity.getFlyingTime(partialTick);
        final float flyingTimeLeft = 1.0F - flyingTime;
        final float downSwing = 0.5F;
        final float wingAngle = 0.5F;
        final float wingSpeed = 0.7F;
        final float cosTicks = flyingTime > 0.0F ? Mth.cos(ticks * wingSpeed) : 0.0F;
        final float sinTicks = flyingTime > 0.0F ? Mth.cos(ticks * wingSpeed + (float) Math.PI) : 0.0F;
        final float idleSwing = 0.035F * Mth.cos(ticks * 0.08F);

        // animate wings (combines flying and landing animations)
        this.leftWing1.xRot = 1.0472F - 0.7854F * flyingTime;
        this.leftWing1.yRot = 0.0F + ((cosTicks + downSwing) * wingAngle * 0.75F) * flyingTime;
        this.leftWing1.zRot = 0.9908F - 0.8908F * flyingTime + idleSwing;

        this.leftWing2.yRot = 0.5236F * flyingTimeLeft + ((cosTicks + downSwing) * wingAngle) * flyingTime;
        this.leftWing3.yRot = 0.1745F * flyingTimeLeft + ((cosTicks + downSwing) * wingAngle) * flyingTime;

        this.rightWing1.xRot = this.leftWing1.xRot;
        this.rightWing1.yRot = 0.0F + ((sinTicks - downSwing) * 0.32F) * flyingTime;
        this.rightWing1.zRot = -0.9908F + 0.8908F * flyingTime - idleSwing;

        this.rightWing2.yRot = -0.5236F * flyingTimeLeft + ((sinTicks - downSwing) * wingAngle) * flyingTime;
        this.rightWing3.yRot = -0.1745F * flyingTimeLeft + ((sinTicks - downSwing) * wingAngle) * flyingTime;
    }
}