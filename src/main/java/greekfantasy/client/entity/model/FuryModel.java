package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Empusa;
import greekfantasy.entity.monster.Fury;
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

public class FuryModel<T extends Fury> extends HumanoidModel<T> {

    public static final ModelLayerLocation FURY_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "fury"), "fury");

    private final ModelPart leftWingArm;
    private final ModelPart leftWingInner;
    private final ModelPart leftWingOuter;
    private final ModelPart rightWingArm;
    private final ModelPart rightWingInner;
    private final ModelPart rightWingOuter;

    public FuryModel(final ModelPart root) {
        super(root);
        this.leftWingArm = root.getChild("left_wing_arm");
        this.leftWingInner = leftWingArm.getChild("left_inner_wing");
        this.leftWingOuter = leftWingInner.getChild("left_outer_wing");
        this.rightWingArm = root.getChild("right_wing_arm");
        this.rightWingInner = rightWingArm.getChild("right_inner_wing");
        this.rightWingOuter = rightWingInner.getChild("right_outer_wing");
        
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 4.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 32).addBox(-3.99F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 5.0F, -2.0F, -0.2182F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 5.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 5.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 16.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 16.0F, 0.0F));

        PartDefinition left_wing_arm = partdefinition.addOrReplaceChild("left_wing_arm", CubeListBuilder.create().texOffs(47, 33).addBox(0.0F, -2.0F, -0.1F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 8.0F, 2.0F, 0.0873F, 0.3927F, 0.0F));
        PartDefinition left_inner_wing = left_wing_arm.addOrReplaceChild("left_inner_wing", CubeListBuilder.create().texOffs(31, 33).addBox(-1.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 4.0F));
        PartDefinition left_outer_wing = left_inner_wing.addOrReplaceChild("left_outer_wing", CubeListBuilder.create().texOffs(46, 43).addBox(0.0F, -3.0F, 0.0F, 8.0F, 20.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -7.0F, 0.0F, 0.0F, 0.3491F, 0.0F));

        PartDefinition right_wing_arm = partdefinition.addOrReplaceChild("right_wing_arm", CubeListBuilder.create().texOffs(47, 33).addBox(-2.0F, -2.0F, -0.1F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 8.0F, 2.0F, 0.0873F, -0.3927F, 0.0F));
        PartDefinition right_inner_wing = right_wing_arm.addOrReplaceChild("right_inner_wing", CubeListBuilder.create().texOffs(31, 33).mirror().addBox(-5.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(0.0F, 0.0F, 4.0F));
        PartDefinition right_outer_wing = right_inner_wing.addOrReplaceChild("right_outer_wing", CubeListBuilder.create().texOffs(46, 43).mirror().addBox(-8.0F, -2.0F, 0.0F, 8.0F, 20.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-5.0F, -8.0F, 0.0F, 0.0F, -0.3491F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.leftWingArm, this.rightWingArm, this.leftLeg, this.rightLeg, this.hat);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch);
        // reset rotation points
        head.setPos(0.0F, 4.0F, 0.0F);
        hat.copyFrom(head);
        rightArm.setPos(-5.0F, 5.0F, 0.0F);
        leftArm.setPos(5.0F, 5.0F, 0.0F);
        rightLeg.setPos(-1.9F, 16.0F, 0.0F);
        leftLeg.setPos(1.9F, 16.0F, 0.0F);
        // animate based on flying time
        final float flyingTime = entity.getFlyingTime(ageInTicks - entity.tickCount);
        final boolean flying = flyingTime > 0.08F;
        float wingSpeed = 0.35F;
        float wingSpan = 0.11F;
        if(flying) {
            wingSpeed += 0.225F;
            wingSpan += 0.06F;
        }
        final float wingAngle = wingSpan - Mth.cos((ageInTicks + entity.getId()) * wingSpeed) * wingSpan;
        // left wing
        this.leftWingInner.yRot = -wingSpan + wingAngle;
        this.leftWingOuter.yRot = 0.1491F + wingAngle;
        // right wing
        this.rightWingInner.yRot = -this.leftWingInner.yRot;
        this.rightWingOuter.yRot = -this.leftWingOuter.yRot;
        // adjust angles when flying
        if(flying) {
            // reduce leg motion
            this.leftLeg.xRot *= 0.25F;
            this.rightLeg.xRot *= 0.25F;
        }
        // always reduce arm motion
        this.leftArm.xRot *= 0.65F;
        this.rightArm.xRot *= 0.65F;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
}