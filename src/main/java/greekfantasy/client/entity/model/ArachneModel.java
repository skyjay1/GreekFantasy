package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Arachne;
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

public class ArachneModel<T extends Arachne> extends HumanoidModel<T> {
    public static final ModelLayerLocation ARACHNE_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "arachne"), "arachne");

    private final ModelPart torso;
    private final ModelPart body0;
    private final ModelPart body1;
    private final ModelPart spiderLeg0;
    private final ModelPart spiderFoot0;
    private final ModelPart spiderLeg1;
    private final ModelPart spiderFoot1;
    private final ModelPart spiderLeg2;
    private final ModelPart spiderFoot2;
    private final ModelPart spiderLeg3;
    private final ModelPart spiderFoot3;
    private final ModelPart spiderLeg4;
    private final ModelPart spiderFoot4;
    private final ModelPart spiderLeg5;
    private final ModelPart spiderFoot5;
    private final ModelPart spiderLeg6;
    private final ModelPart spiderFoot6;
    private final ModelPart spiderLeg7;
    private final ModelPart spiderFoot7;

    public ArachneModel(final ModelPart root) {
        super(root);
        this.torso = root.getChild("torso");
        this.body0 = root.getChild("body0");
        this.body1 = root.getChild("body1");
        this.spiderLeg0 = root.getChild("leg0");
        this.spiderFoot0 = spiderLeg0.getChild("foot0");
        this.spiderLeg1 = root.getChild("leg1");
        this.spiderFoot1 = spiderLeg1.getChild("foot1");
        this.spiderLeg2 = root.getChild("leg2");
        this.spiderFoot2 = spiderLeg2.getChild("foot2");
        this.spiderLeg3 = root.getChild("leg3");
        this.spiderFoot3 = spiderLeg3.getChild("foot3");
        this.spiderLeg4 = root.getChild("leg4");
        this.spiderFoot4 = spiderLeg4.getChild("foot4");
        this.spiderLeg5 = root.getChild("leg5");
        this.spiderFoot5 = spiderLeg5.getChild("foot5");
        this.spiderLeg6 = root.getChild("leg6");
        this.spiderFoot6 = spiderLeg6.getChild("foot6");
        this.spiderLeg7 = root.getChild("leg7");
        this.spiderFoot7 = spiderLeg7.getChild("foot7");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -3.0F));

        head.addOrReplaceChild("mouth", CubeListBuilder.create()
                .texOffs(25, 0).addBox(1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(25, 0).addBox(-2.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.0F, -4.0F, -0.7854F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, -3.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(42, 16).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-4.0F, 1.0F, -3.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(42, 16).mirror().addBox(0.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(4.0F, 1.0F, -3.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 16).addBox(-4.0F, -24.0F, -5.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -23.0F, -5.0F, -0.1745F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(33, 50).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.0F, -5.0F, 0.7854F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 11.0F, 0.0F));
        partdefinition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 44).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 12.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 11.0F, 3.0F));

        PartDefinition leg0 = partdefinition.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 11.0F, 2.0F, 0.0F, 0.7854F, -0.7854F));
        leg0.addOrReplaceChild("foot0", CubeListBuilder.create().texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-13.0F, -1.0F, 0.0F, 0.0F, -0.5236F, -1.0472F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 11.0F, 2.0F, 0.0F, -0.7854F, 0.7854F));
        leg1.addOrReplaceChild("foot1", CubeListBuilder.create().texOffs(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(13.0F, -1.0F, 0.0F, 0.0F, 0.5236F, 1.0472F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 11.0F, 1.0F, 0.0F, 0.2618F, -0.6109F));
        leg2.addOrReplaceChild("foot2", CubeListBuilder.create().texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-13.0F, -1.0F, 0.0F, 0.0F, 0.0F, -1.0472F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 11.0F, 1.0F, 0.0F, -0.2618F, 0.6109F));
        leg3.addOrReplaceChild("foot3", CubeListBuilder.create().texOffs(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(13.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0472F));

        PartDefinition leg4 = partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 11.0F, 0.0F, 0.0F, -0.2618F, -0.6109F));
        leg4.addOrReplaceChild("foot4", CubeListBuilder.create().texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-13.0F, -1.0F, 0.0F, 0.0F, 0.0F, -1.0472F));

        PartDefinition leg5 = partdefinition.addOrReplaceChild("leg5", CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 11.0F, 0.0F, 0.0F, 0.2618F, 0.6109F));
        leg5.addOrReplaceChild("foot5", CubeListBuilder.create().texOffs(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(13.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0472F));

        PartDefinition leg6 = partdefinition.addOrReplaceChild("leg6", CubeListBuilder.create().texOffs(32, 32).addBox(-13.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 11.0F, -1.0F, 0.0F, -0.7854F, -0.7854F));
        leg6.addOrReplaceChild("foot6", CubeListBuilder.create().texOffs(32, 37).addBox(-7.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-13.0F, -1.0F, 0.0F, 0.0F, 0.5236F, -1.0472F));

        PartDefinition leg7 = partdefinition.addOrReplaceChild("leg7", CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 14.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 11.0F, -1.0F, 0.0F, 0.7854F, 0.7854F));
        leg7.addOrReplaceChild("foot7", CubeListBuilder.create().texOffs(32, 37).addBox(0.0F, 0.0F, -1.0F, 7.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(13.0F, -1.0F, 0.0F, 0.0F, -0.5236F, 1.0472F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body, hat, leftArm, rightArm, torso, body0, body1, spiderLeg0, spiderLeg1, spiderLeg2, spiderLeg3, spiderLeg4, spiderLeg5, spiderLeg6, spiderLeg7);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // reset biped rotation points
        head.setPos(0.0F, 0.0F, -3.0F);
        hat.setPos(0.0F, 0.0F, -3.0F);
        body.setPos(0.0F, 24.0F, 0.0F);
        torso.setPos(0.0F, 12.0F, -5.0F);
        leftArm.setPos(4.0F, 1.0F, -3.0F);
        rightArm.setPos(-4.0F, 1.0F, -3.0F);
        // animate spider legs
        final float legFrontBackZ = 0.7853982F;
        final float legMiddleZ = 0.58119464F;
        this.spiderLeg0.zRot = -legFrontBackZ;
        this.spiderLeg1.zRot = legFrontBackZ;
        this.spiderLeg2.zRot = -legMiddleZ;
        this.spiderLeg3.zRot = legMiddleZ;
        this.spiderLeg4.zRot = -legMiddleZ;
        this.spiderLeg5.zRot = legMiddleZ;
        this.spiderLeg6.zRot = -legFrontBackZ;
        this.spiderLeg7.zRot = legFrontBackZ;

        final float legFrontBackY = 0.7853982F;
        final float legMiddleY = 0.3926991F;
        this.spiderLeg0.yRot = legFrontBackY;
        this.spiderLeg1.yRot = -legFrontBackY;
        this.spiderLeg2.yRot = legMiddleY;
        this.spiderLeg3.yRot = -legMiddleY;
        this.spiderLeg4.yRot = -legMiddleY;
        this.spiderLeg5.yRot = legMiddleY;
        this.spiderLeg6.yRot = -legFrontBackY;
        this.spiderLeg7.yRot = legFrontBackY;

        float leg12Y = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
        float leg34Y = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * limbSwingAmount;
        float leg56Y = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * limbSwingAmount;
        float leg78Y = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 4.712389F) * 0.4F) * limbSwingAmount;

        float leg12Z = Math.abs(Mth.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
        float leg34Z = Math.abs(Mth.sin(limbSwing * 0.6662F + 3.1415927F) * 0.4F) * limbSwingAmount;
        float leg56Z = Math.abs(Mth.sin(limbSwing * 0.6662F + 1.5707964F) * 0.4F) * limbSwingAmount;
        float leg78Z = Math.abs(Mth.sin(limbSwing * 0.6662F + 4.712389F) * 0.4F) * limbSwingAmount;

        this.spiderLeg0.yRot += leg12Y;
        this.spiderLeg1.yRot += -leg12Y;
        this.spiderLeg2.yRot += leg34Y;
        this.spiderLeg3.yRot += -leg34Y;
        this.spiderLeg4.yRot += leg56Y;
        this.spiderLeg5.yRot += -leg56Y;
        this.spiderLeg6.yRot += leg78Y;
        this.spiderLeg7.yRot += -leg78Y;

        this.spiderLeg0.zRot += leg12Z;
        this.spiderLeg1.zRot += -leg12Z;
        this.spiderLeg2.zRot += leg34Z;
        this.spiderLeg3.zRot += -leg34Z;
        this.spiderLeg4.zRot += leg56Z;
        this.spiderLeg5.zRot += -leg56Z;
        this.spiderLeg6.zRot += leg78Z;
        this.spiderLeg7.zRot += -leg78Z;

        // feet
        final float footFrontBackZ = 1.0472F;
        final float footMiddleZ = 1.0472F;
        this.spiderFoot0.zRot = -footFrontBackZ;
        this.spiderFoot1.zRot = footFrontBackZ;
        this.spiderFoot2.zRot = -footMiddleZ;
        this.spiderFoot3.zRot = footMiddleZ;
        this.spiderFoot4.zRot = -footMiddleZ;
        this.spiderFoot5.zRot = footMiddleZ;
        this.spiderFoot6.zRot = -footFrontBackZ;
        this.spiderFoot7.zRot = footFrontBackZ;

        final float footFrontBackY = 0.523599F;
        final float footMiddleY = 0.0F;
        this.spiderFoot0.yRot = -footFrontBackY;
        this.spiderFoot1.yRot = footFrontBackY;
        this.spiderFoot2.yRot = -footMiddleY;
        this.spiderFoot3.yRot = footMiddleY;
        this.spiderFoot4.yRot = -footMiddleY;
        this.spiderFoot5.yRot = footMiddleY;
        this.spiderFoot6.yRot = footFrontBackY;
        this.spiderFoot7.yRot = -footFrontBackY;
    }

}
