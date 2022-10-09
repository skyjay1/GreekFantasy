package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public abstract class BigCatModel<T extends LivingEntity> extends QuadrupedModel<T> {

    protected final ModelPart upperTail;
    protected final ModelPart lowerTail;

    protected final Vector3f headPoints;

    public BigCatModel(ModelPart root) {
        super(root, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
        upperTail = root.getChild("upper_tail");
        lowerTail = upperTail.getChild("lower_tail");
        // save head position to use later
        headPoints = new Vector3f(head.x, head.y, head.z);
    }

    public static MeshDefinition createBodyMesh(final CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = QuadrupedModel.createBodyMesh(0, cubeDeformation);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(29, 0).addBox(-4.0F, -17.0F, -3.0F, 8.0F, 11.0F, 7.0F, cubeDeformation)
                .texOffs(35, 19).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 8.0F, 6.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 12.0F, 8.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 6.0F, cubeDeformation)
                .texOffs(0, 13).addBox(-2.5F, 1.0F, -5.0F, 5.0F, 4.0F, 2.0F, cubeDeformation)
                .texOffs(21, 1).addBox(-3.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, cubeDeformation)
                .texOffs(21, 1).mirror().addBox(1.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, cubeDeformation), PartPose.offset(0.0F, 10.5F, -10.0F));

        PartDefinition upperTail = partdefinition.addOrReplaceChild("upper_tail", CubeListBuilder.create().texOffs(42, 35).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 10.0F, 10.0F, 0.5236F, 0.0F, 0.0F));
        upperTail.addOrReplaceChild("lower_tail", CubeListBuilder.create().texOffs(47, 35).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, cubeDeformation)
                .texOffs(42, 42).addBox(-1.0F, 3.0F, -0.5F, 2.0F, 4.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 5.0F, -1.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition rightFrontLeg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-1.5F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, cubeDeformation)
                .texOffs(18, 19).addBox(-1.0F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation), PartPose.offset(-3.0F, 14.0F, -5.0F));
        rightFrontLeg.addOrReplaceChild("right_front_claws", CubeListBuilder.create().texOffs(0, 29).addBox(-1.75F, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(1.0F, 9.0F, -1.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition rightHindLeg = partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-0.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, cubeDeformation)
                .texOffs(18, 19).addBox(-0.5F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation), PartPose.offset(-3.0F, 14.0F, 7.0F));
        rightHindLeg.addOrReplaceChild("right_hind_claws", CubeListBuilder.create().texOffs(0, 29).addBox(-1.25F, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(1.0F, 9.0F, -1.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition leftFrontLeg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-2.5F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, cubeDeformation)
                .texOffs(18, 19).addBox(-2.0F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation), PartPose.offset(3.0F, 14.0F, -5.0F));
        leftFrontLeg.addOrReplaceChild("left_front_claws", CubeListBuilder.create().texOffs(0, 29).addBox(-1.75F, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 9.0F, -1.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition leftHindLeg = partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-2.5F, 0.0F, -2.0F, 3.0F, 5.0F, 4.0F, cubeDeformation)
                .texOffs(18, 19).addBox(-2.5F, 5.0F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation), PartPose.offset(3.0F, 14.0F, 7.0F));
        leftHindLeg.addOrReplaceChild("left_hind_claws", CubeListBuilder.create().texOffs(0, 29).addBox(-1.25F, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(-1.0F, 9.0F, -1.5F, -0.7854F, 0.0F, 0.0F));

        return meshdefinition;
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(upperTail));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        final float partialTick = ageInTicks - entity.tickCount;
        float sittingPercent = getSittingPercent(entity, partialTick);
        float sittingPercentLeft = 1.0F - sittingPercent;
        float attackingPercent = getAttackingPercent(entity, partialTick);
        float attackingPercentLeft = 1.0F - attackingPercent;
        // set rotation points
        body.setPos(0.0F, 12.0F + 10.0F * sittingPercent, 8.0F - 1.0F * sittingPercent);
        final float headX = headPoints.x();
        final float headY = headPoints.y() + 1.0F * sittingPercent - 10.0F * attackingPercent + 2.0F * attackingPercent * sittingPercent;
        final float headZ = headPoints.z() + 2.0F * sittingPercent + 3.0F * attackingPercent + 4.0F * attackingPercent * sittingPercent;
        headParts().forEach(m -> m.setPos(headX, headY, headZ));
        leftFrontLeg.setPos(3.0F, 14.0F - 7.0F * attackingPercent + 4.0F * attackingPercent * sittingPercent, -5.0F + 1.0F * attackingPercent + 3.0F * attackingPercent * sittingPercent);
        rightFrontLeg.setPos(-3.0F, leftFrontLeg.y, leftFrontLeg.z);
        leftHindLeg.setPos(3.0F, 14.0F + 8.0F * sittingPercent, 7.0F + 1.0F * sittingPercent);
        rightHindLeg.setPos(-3.0F, leftHindLeg.y, leftHindLeg.z);
        upperTail.setPos(0.0F, 10.0F + 12.0F * sittingPercent + 1.0F * attackingPercent, 10.0F - 2.0F * sittingPercent + 1.0F * attackingPercent);
        // set rotations
        head.xRot = attackingPercentLeft * head.xRot - 0.34F * attackingPercent;
        body.xRot = 1.5708F - 0.5236F * sittingPercent - 0.56F * attackingPercent;
        leftFrontLeg.xRot = attackingPercentLeft * leftFrontLeg.xRot - 1.52F * attackingPercent;
        rightFrontLeg.xRot = attackingPercentLeft * rightFrontLeg.xRot - 1.52F * attackingPercent;
        leftHindLeg.xRot = sittingPercentLeft * leftHindLeg.xRot - 1.4708F * sittingPercent;
        rightHindLeg.xRot = sittingPercentLeft * rightHindLeg.xRot - 1.4708F * sittingPercent;
        leftHindLeg.yRot = 0.0F - 0.2F * sittingPercent;
        rightHindLeg.yRot = -leftHindLeg.yRot;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        float limbSwingCos = Mth.cos(limbSwing) * limbSwingAmount;
        float idleSwing = 0.1F * Mth.cos((entity.tickCount + partialTick) * 0.08F);
        float tailSwing = 0.42F * limbSwingCos;
        upperTail.xRot = 0.6854F + tailSwing + 0.7F * getSittingPercent(entity, partialTick);
        lowerTail.xRot = 0.3491F + tailSwing * 0.6F;
        upperTail.zRot = idleSwing;
        lowerTail.zRot = idleSwing * 0.85F;
        body.zRot = limbSwingCos * 0.12F;
    }

    protected abstract float getAttackingPercent(T entity, float partialTick);

    protected abstract float getSittingPercent(T entity, float partialTick);
}
