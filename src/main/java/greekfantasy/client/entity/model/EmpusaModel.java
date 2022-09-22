package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Empusa;
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

public class EmpusaModel<T extends Empusa> extends HumanoidModel<T> {

    public static final ModelLayerLocation EMPUSA_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "empusa"), "empusa");

    private final ModelPart leftWingArm;
    private final ModelPart leftWingInner;
    private final ModelPart leftWingOuter;
    private final ModelPart rightWingArm;
    private final ModelPart rightWingInner;
    private final ModelPart rightWingOuter;

    public EmpusaModel(final ModelPart root) {
        super(root);
        this.leftWingArm = root.getChild("left_wing_arm");
        this.leftWingInner = leftWingArm.getChild("left_inner_wing");
        this.leftWingOuter = leftWingInner.getChild("left_outer_wing");
        this.rightWingArm = root.getChild("right_wing_arm");
        this.rightWingInner = rightWingArm.getChild("right_inner_wing");
        this.rightWingOuter = rightWingInner.getChild("right_outer_wing");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 32).addBox(-3.99F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.2182F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 12.0F, 0.0F));

        PartDefinition left_wing_arm = partdefinition.addOrReplaceChild("left_wing_arm", CubeListBuilder.create().texOffs(47, 33).addBox(0.0F, -2.0F, -0.1F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 4.0F, 2.0F, 0.0873F, 0.3927F, 0.0F));
        PartDefinition left_wing_inner = left_wing_arm.addOrReplaceChild("left_inner_wing", CubeListBuilder.create().texOffs(31, 33).addBox(-4.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 0.0F, 4.0F));
        left_wing_inner.addOrReplaceChild("left_outer_wing", CubeListBuilder.create().texOffs(46, 43).addBox(0.0F, -8.0F, 0.0F, 8.0F, 20.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, -2.0F, 0.0F, 0.0F, 0.3491F, 0.0F));

        PartDefinition right_wing_arm = partdefinition.addOrReplaceChild("right_wing_arm", CubeListBuilder.create().texOffs(47, 33).addBox(-2.0F, -2.0F, -0.1F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 4.0F, 2.0F, 0.0873F, -0.3927F, 0.0F));
        PartDefinition right_wing_inner = right_wing_arm.addOrReplaceChild("right_inner_wing", CubeListBuilder.create().texOffs(31, 33).mirror().addBox(-3.0F, -7.0F, 0.0F, 6.0F, 14.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-2.0F, 0.0F, 4.0F));
        right_wing_inner.addOrReplaceChild("right_outer_wing", CubeListBuilder.create().texOffs(46, 43).mirror().addBox(-8.0F, -8.0F, 0.0F, 8.0F, 20.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-3.0F, -2.0F, 0.0F, 0.0F, -0.3491F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.leftWingArm, this.rightWingArm, this.leftLeg, this.rightLeg, this.hat);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
        if (entity.isDraining()) {
            // extend arms
            this.leftArm.xRot = -0.436F;
            this.leftArm.zRot = -0.698F;
            this.rightArm.xRot = -0.436F;
            this.rightArm.zRot = 0.698F;
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        float ticks = entity.getId() * 2 + entity.tickCount + partialTick;

        final float cosTicks = Mth.cos(ticks * 0.1F);
        this.leftWingInner.yRot = cosTicks * 0.035F;
        this.leftWingOuter.yRot = 0.3491F + cosTicks * 0.05F;

        this.rightWingInner.yRot = -cosTicks * 0.035F;
        this.rightWingOuter.yRot = -0.3491F - cosTicks * 0.05F;
    }

    public ModelPart getHeadPart() {
        return this.head;
    }
}