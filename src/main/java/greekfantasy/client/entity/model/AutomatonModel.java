package greekfantasy.client.entity.model;


import com.mojang.blaze3d.vertex.PoseStack;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Automaton;
import greekfantasy.entity.boss.Talos;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
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
import net.minecraft.world.entity.HumanoidArm;

public class AutomatonModel<T extends Automaton> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {
    public static final ModelLayerLocation AUTOMATON_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "automaton"), "automaton");

    // root
    protected final ModelPart root;
    // head
    protected final ModelPart head;
    // body
    protected final ModelPart body;
    protected final ModelPart upperBody;
    protected final ModelPart upperMiddleBody;
    protected final ModelPart lowerMiddleBody;
    protected final ModelPart lowerBody;
    // arms
    protected final ModelPart rightArm;
    protected final ModelPart rightUpperArm;
    protected final ModelPart rightMiddleArm;
    protected final ModelPart rightLowerArm;
    protected final ModelPart leftArm;
    protected final ModelPart leftUpperArm;
    protected final ModelPart leftMiddleArm;
    protected final ModelPart leftLowerArm;
    // legs
    protected final ModelPart rightLeg;
    protected final ModelPart rightUpperLeg;
    protected final ModelPart rightMiddleLeg;
    protected final ModelPart rightLowerLeg;
    protected final ModelPart leftLeg;
    protected final ModelPart leftUpperLeg;
    protected final ModelPart leftMiddleLeg;
    protected final ModelPart leftLowerLeg;

    public AutomatonModel(final ModelPart root) {
        super();
        this.root = root;
        // head
        this.head = root.getChild("head");
        // body
        this.body = root.getChild("body");
        this.upperBody = body.getChild("upper_body");
        this.upperMiddleBody = upperBody.getChild("upper_middle_body");
        this.lowerMiddleBody = upperMiddleBody.getChild("lower_middle_body");
        this.lowerBody = lowerMiddleBody.getChild("lower_body");
        // arms
        this.rightArm = root.getChild("right_arm");
        this.rightUpperArm = rightArm.getChild("right_upper_arm");
        this.rightMiddleArm = rightUpperArm.getChild("right_middle_arm");
        this.rightLowerArm = rightMiddleArm.getChild("right_lower_arm");
        this.leftArm = root.getChild("left_arm");
        this.leftUpperArm = leftArm.getChild("left_upper_arm");
        this.leftMiddleArm = leftUpperArm.getChild("left_middle_arm");
        this.leftLowerArm = leftMiddleArm.getChild("left_lower_arm");
        // legs
        this.rightLeg = root.getChild("right_leg");
        this.rightUpperLeg = rightLeg.getChild("right_upper_leg");
        this.rightMiddleLeg = rightUpperLeg.getChild("right_middle_leg");
        this.rightLowerLeg = rightMiddleLeg.getChild("right_lower_leg");
        this.leftLeg = root.getChild("left_leg");
        this.leftUpperLeg = leftLeg.getChild("left_upper_leg");
        this.leftMiddleLeg = leftUpperLeg.getChild("left_middle_leg");
        this.leftLowerLeg = leftMiddleLeg.getChild("left_lower_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(66, 0).addBox(-5.5F, -5.0F, -3.0F, 8.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-9.0F, -10.0F, 0.0F, -1.0472F, 0.0F, 0.0F));
        PartDefinition rightUpperArm = rightArm.addOrReplaceChild("right_upper_arm", CubeListBuilder.create().texOffs(66, 11).addBox(-4.0F, -1.0F, -2.5F, 4.0F, 6.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightMiddleArm = rightUpperArm.addOrReplaceChild("right_middle_arm", CubeListBuilder.create().texOffs(66, 23).addBox(-5.0F, 0.0F, -3.5F, 5.0F, 6.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition rightLowerArm = rightMiddleArm.addOrReplaceChild("right_lower_arm", CubeListBuilder.create().texOffs(64, 37).addBox(-6.0F, 0.0F, -4.5F, 7.0F, 8.0F, 9.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(66, 0).addBox(-2.5F, -5.0F, -3.0F, 8.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(9.0F, -10.0F, 0.0F));
        PartDefinition leftUpperArm = leftArm.addOrReplaceChild("left_upper_arm", CubeListBuilder.create().texOffs(66, 11).addBox(0.0F, -1.0F, -2.5F, 4.0F, 6.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftMiddleArm = leftUpperArm.addOrReplaceChild("left_middle_arm", CubeListBuilder.create().texOffs(66, 23).addBox(0.0F, 0.0F, -3.5F, 5.0F, 6.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition leftLowerArm = leftMiddleArm.addOrReplaceChild("left_lower_arm", CubeListBuilder.create().texOffs(96, 37).addBox(-1.0F, 0.0F, -4.5F, 7.0F, 8.0F, 9.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition upperBody = body.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(0, 15).addBox(-9.0F, 0.0F, -5.0F, 18.0F, 8.0F, 11.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -16.0F, 0.0F));
        PartDefinition upperMiddleBody = upperBody.addOrReplaceChild("upper_middle_body", CubeListBuilder.create().texOffs(0, 35).addBox(-7.0F, 0.0F, -4.5F, 14.0F, 5.0F, 10.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition lowerMiddleBody = upperMiddleBody.addOrReplaceChild("lower_middle_body", CubeListBuilder.create().texOffs(0, 51).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 4.0F, 9.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition lowerBody = lowerMiddleBody.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(39, 53).addBox(-3.0F, 0.0F, -2.5F, 6.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-3.0F, 9.0F, 0.5F));
        PartDefinition rightUpperLeg = rightLeg.addOrReplaceChild("right_upper_leg", CubeListBuilder.create().texOffs(103, 0).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightMiddleLeg = rightUpperLeg.addOrReplaceChild("right_middle_leg", CubeListBuilder.create().texOffs(103, 11).addBox(-4.5F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition rightLowerLeg = rightMiddleLeg.addOrReplaceChild("right_lower_leg", CubeListBuilder.create().texOffs(103, 23).addBox(-5.0F, 0.0F, -3.0F, 6.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(3.0F, 9.0F, 0.5F));
        PartDefinition leftUpperLeg = leftLeg.addOrReplaceChild("left_upper_leg", CubeListBuilder.create().texOffs(103, 0).addBox(0.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftMiddleLeg = leftUpperLeg.addOrReplaceChild("left_middle_leg", CubeListBuilder.create().texOffs(103, 11).addBox(-0.5F, 0.0F, -2.5F, 5.0F, 6.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition leftLowerLeg = leftMiddleLeg.addOrReplaceChild("left_lower_leg", CubeListBuilder.create().texOffs(103, 23).addBox(-1.0F, 0.0F, -3.0F, 6.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        if (entity.getAttackTimer() > 0) {
            float attackingPercent = entity.getAttackPercent(partialTick);
            this.leftArm.xRot = -1.5F * (Mth.sin(attackingPercent * (float)Math.PI) * 0.5F + 0.5F);
        } else {
            this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(limbSwing, 10.0F)) * limbSwingAmount;
        }
        this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(limbSwing, 10.0F)) * limbSwingAmount;
        // shooting animation
        rightArm.xRot += entity.getShootAnglePercent(partialTick) * entity.getMaxShootingAngle();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // these represent the percent of spawn time spent on just that body part
        final float armPercentAmount = 0.32F;
        final float bodyPercentAmount = 0.32F;
        final float legPercentAmount = 0.32F;
        // these convert from the [0.0, 1.0] spawn percent to a comparable portion for each body part
        final float spawnPercentRaw = entity.getSpawnPercent(ageInTicks - entity.tickCount);
        final float spawnPercent = 1.04F - spawnPercentRaw;
        final float armPercent = Mth.clamp(spawnPercent, 0.0F, armPercentAmount) * (1.0F / armPercentAmount);
        final float bodyPercent = Mth.clamp(spawnPercent - armPercentAmount, 0.0F, bodyPercentAmount) * (1.0F / bodyPercentAmount);
        final float legPercent = Mth.clamp(spawnPercent - armPercentAmount - bodyPercentAmount, 0.0F, legPercentAmount) * (1.0F / legPercentAmount);
        // rotation points and spawn animation
        // head
        head.setPos(0, -11.0F + 16.01F * bodyPercent + 12F * legPercent, 0);
        head.xRot = 0;
        head.yRot = netHeadYaw * ((float)Math.PI / 180F) * spawnPercentRaw;
        head.zRot *= spawnPercentRaw;
        // body
        body.setPos(0, 5 + 10 * bodyPercent + 12 * legPercent, 0);
        lowerMiddleBody.setPos(0, 5 - 5 * bodyPercent, 0);
        lowerBody.setPos(0, 4 - 4 * bodyPercent, 0);
        // arms (some are offset by 0.01 to avoid z-fighting)
        rightArm.setPos(-9.0F, -10 + 14.01F * bodyPercent + 12 * legPercent, 0.0F);
        rightUpperArm.setPos(0, 0 - 3.99F * armPercent, 0);
        rightMiddleArm.setPos(0, 5 - 5.99F * armPercent, 0);
        rightLowerArm.setPos(0, 6 - 5.99F * armPercent, 0);
        leftArm.setPos(9.0F, -10 + 14.01F * bodyPercent + 12 * legPercent, 0.0F);
        leftUpperArm.setPos(0, 0 - 3.99F * armPercent, 0);
        leftMiddleArm.setPos(0, 5 - 5.99F * armPercent, 0);
        leftLowerArm.setPos(0, 6 - 5.99F * armPercent, 0);
        // legs
        leftLeg.setPos(3.0F, 9 + 12 * legPercent, 0.5F);
        leftMiddleLeg.setPos(0, 4 - 6 * legPercent, 0);
        leftLowerLeg.setPos(0, 6 - 6 * legPercent, 0);
        rightLeg.setPos(-3.0F, 9 + 12 * legPercent, 0.5F);
        rightMiddleLeg.setPos(0, 4 - 6 * legPercent, 0);
        rightLowerLeg.setPos(0, 6 - 6 * legPercent, 0);
        // walking
        this.rightLeg.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        float dX = humanoidArm == HumanoidArm.RIGHT ? -2F : 2F;
        float dZ = -3.5F;
        float dY = 3.0F;
        ModelPart armModel = this.getArm(humanoidArm);
        armModel.x += dX;
        armModel.y += dY;
        armModel.z += dZ;
        armModel.translateAndRotate(poseStack);
        armModel.x -= dX;
        armModel.y -= dY;
        armModel.z -= dZ;
    }

    protected ModelPart getArm(HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }
}
