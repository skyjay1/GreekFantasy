package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Cerberus;
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

public class CerberusModel<T extends Cerberus> extends AgeableListModel<T> {
    public static final ModelLayerLocation CERBERUS_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "cerberus"), "cerberus");

    private final ModelPart body;
    private final ModelPart middleNeck;
    private final ModelPart middleHead;
    private final ModelPart middleMouth;
    private final ModelPart rightNeck;
    private final ModelPart rightHead;
    private final ModelPart rightMouth;
    private final ModelPart leftNeck;
    private final ModelPart leftHead;
    private final ModelPart leftMouth;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart upperTail;
    private final ModelPart lowerTail;

    float color = 1.0F;

    public CerberusModel(final ModelPart root) {
        this.body = root.getChild("body");
        this.middleNeck = root.getChild("middle_neck");
        this.middleHead = middleNeck.getChild("middle_head");
        this.middleMouth = middleHead.getChild("mouth");
        this.leftNeck = root.getChild("left_neck");
        this.leftHead = leftNeck.getChild("left_head");
        this.leftMouth = leftHead.getChild("mouth");
        this.rightNeck = root.getChild("right_neck");
        this.rightHead = rightNeck.getChild("right_head");
        this.rightMouth = rightHead.getChild("mouth");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.upperTail = root.getChild("upper_tail");
        this.lowerTail = upperTail.getChild("lower_tail");
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.middleNeck, this.rightNeck, this.leftNeck);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.upperTail);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 29).addBox(-5.0F, -18.0F, -6.0F, 10.0F, 8.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(0, 12).addBox(-4.0F, -17.0F, 0.0F, 8.0F, 7.0F, 9.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition middleNeck = partdefinition.addOrReplaceChild("middle_neck", CubeListBuilder.create().texOffs(50, 18).addBox(-1.5F, -4.0F, -3.0F, 3.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.0F, -6.0F, 0.0F, 0.0F, 0.0F));
        GFModelUtil.addOrReplaceCerberusHead(middleNeck, "middle_head", 0.0F, -4.0F, -3.0F, 0.0F);
        
        PartDefinition leftNeck = partdefinition.addOrReplaceChild("left_neck", CubeListBuilder.create().texOffs(50, 17).addBox(-2.0F, -4.0F, -3.0F, 3.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 14.0F, -5.0F, 0.0F, -0.5236F, 0.0F));
        GFModelUtil.addOrReplaceCerberusHead(leftNeck, "left_head", 0.0F, -4.0F, -3.0F, -0.5F);

        PartDefinition rightNeck = partdefinition.addOrReplaceChild("right_neck", CubeListBuilder.create().texOffs(50, 17).addBox(-1.0F, -4.0F, -3.0F, 3.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 14.0F, -5.0F, 0.0F, 0.5236F, 0.0F));
        GFModelUtil.addOrReplaceCerberusHead(rightNeck, "right_head", 0.0F, -4.0F, -3.0F, 0.5F);

        partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
                .texOffs(37, 0).addBox(-1.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(33, 10).addBox(-1.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-3.0F, 14.0F, -2.0F));
        partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create()
                .texOffs(37, 0).addBox(-1.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(33, 10).addBox(-1.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-3.0F, 14.0F, 7.0F));
        partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
                .texOffs(37, 0).addBox(-2.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(33, 10).addBox(-2.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 14.0F, -2.0F));
        partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create()
                .texOffs(37, 0).addBox(-2.0F, 4.0F, -2.0F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(33, 10).addBox(-2.5F, -2.0F, -2.5F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 14.0F, 7.0F));

        PartDefinition upperTail = partdefinition.addOrReplaceChild("upper_tail", CubeListBuilder.create().texOffs(50, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.0F, 9.0F, 0.7854F, 0.0F, 0.0F));
        upperTail.addOrReplaceChild("lower_tail", CubeListBuilder.create().texOffs(50, 8).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.0F, -2.0F, 0.2618F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        // limb swing and ticks
        final float limbSwingCos = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        final float limbSwingSin = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
        final float ticks = entity.tickCount + partialTick;

        // animate tail
        float idleSwing = 0.12F * Mth.cos(ticks * 0.11F);
        float tailSwing = 0.42F * limbSwingCos;
        upperTail.xRot = 0.5854F + tailSwing;
        lowerTail.xRot = 0.2618F + tailSwing * 0.6F;
        upperTail.zRot = idleSwing;
        lowerTail.zRot = idleSwing * 0.85F;

        // animate legs
        final float legAngle = 1.2F;
        this.rightHindLeg.xRot = limbSwingCos * legAngle;
        this.leftHindLeg.xRot = limbSwingSin * legAngle;
        this.rightFrontLeg.xRot = limbSwingSin * legAngle;
        this.leftFrontLeg.xRot = limbSwingCos * legAngle;

        // animate mouths
        final float howlingTimeLeft = 1.0F - entity.getSummonPercent(partialTick);
        final float mouthAngle = 0.26F;
        this.middleMouth.xRot = (mouthAngle - Mth.cos((ticks + 0.0F) * 0.28F) * mouthAngle * 0.3F) * howlingTimeLeft;
        this.rightMouth.xRot = (mouthAngle - Mth.cos((ticks + 0.9F) * 0.19F) * mouthAngle * 0.3F) * howlingTimeLeft;
        this.leftMouth.xRot = (mouthAngle - Mth.cos((ticks + 0.4F) * 0.24F) * mouthAngle * 0.3F) * howlingTimeLeft;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw,
                          float rotationPitch) {
        final float partialTick = ageInTicks - entity.tickCount;
        // set color if the entity is spawning
        color = entity.isSpawning() ? entity.getSpawnPercent(partialTick) : 1.0F;
        // prepare animations
        final float howlingTime = entity.getSummonPercent(partialTick);
        final float howlingTimeLeft = 1.0F - howlingTime;
        // update neck angles
        final float howlingAngle = howlingTime > 0 ? -4.0F * (float) Math.pow(2.0F * howlingTime - 1.0F, 2) + 5.0F : 1.0F;
        final float neckAngleX = 0.10F;
        final float neck1X = -0.2618F * howlingAngle + Mth.cos((ageInTicks + 0.1F) * 0.049F) * neckAngleX * howlingTimeLeft;
        final float neck2X = -0.1745F * howlingAngle + Mth.cos((ageInTicks + 0.9F) * 0.059F) * neckAngleX * howlingTimeLeft;
        final float neck3X = -0.1745F * howlingAngle + Mth.cos((ageInTicks + 1.5F) * 0.055F) * neckAngleX * howlingTimeLeft;
        this.middleNeck.xRot = neck1X;
        this.rightNeck.xRot = neck2X;
        this.leftNeck.xRot = neck3X;

        // update head angles
        final float offsetX = 0.2309F;
        final float pitch = rotationPitch * 0.017453292F;
        final float yaw = rotationYaw * 0.017453292F * howlingTimeLeft;
        this.middleHead.xRot = (pitch - middleNeck.xRot) * howlingTimeLeft;
        this.middleHead.yRot = yaw;
        this.rightHead.xRot = (pitch - rightNeck.xRot) * howlingTimeLeft;
        this.rightHead.yRot = (yaw * 0.8F - offsetX);
        this.leftHead.xRot = (pitch - leftNeck.xRot) * howlingTimeLeft;
        this.leftHead.yRot = (yaw * 0.8F + offsetX);
    }

    @Override
    public void renderToBuffer(final PoseStack matrixStackIn, final VertexConsumer vertexBuilder, final int packedLightIn, final int packedOverlayIn,
                               final float redIn, final float greenIn, final float blueIn, final float alphaIn) {
        super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, 1.0F, color, color, alphaIn);
    }
}
