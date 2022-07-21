package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.HasHorseVariant;
import net.minecraft.client.model.EntityModel;
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

public class HalfHorseModel<T extends LivingEntity & HasHorseVariant> extends EntityModel<T> {

    public static final ModelLayerLocation HALF_HORSE_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "half_horse"), "half_horse");

    // horse parts
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart tail;

    public HalfHorseModel(final ModelPart root) {
        super();
        this.root = root;
        this.body = root.getChild("body");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HalfHorseModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    public static MeshDefinition createMesh(final CubeDeformation cubeDeformation, final float offset) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, cubeDeformation), PartPose.offset(0.0F, 11.0F + offset, 6.0F));

        partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, cubeDeformation), PartPose.offset(-3.0F, 13.0F + offset, -9.0F));
        partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, cubeDeformation), PartPose.offset(3.0F, 13.0F + offset, -9.0F));
        partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, cubeDeformation), PartPose.offset(-3.0F, 13.0F + offset, 9.0F));
        partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, cubeDeformation), PartPose.offset(3.0F, 13.0F + offset, 9.0F));

        partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 4.0F + offset, 11.0F, 0.5236F, 0.0F, 0.0F));

        return meshdefinition;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
        // horse rotation angles
        //this.body.z = 6.0F;
        this.body.y = 11.0F;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        float ticks = entity.tickCount + partialTick;
        float swimmingOffset = entity.isInWater() ? 0.2F : 1.0F;
        float limbSwingCos = Mth.cos(swimmingOffset * limbSwing * 0.6662F + 3.1415927F);
        float limbSwingAmountCos = limbSwingCos * 0.8F * limbSwingAmount;
        // body rotations
        float rearingTime = entity.getRearingAmount(partialTick);
        float rearingTimeLeft = 1.0F - rearingTime;
        this.body.xRot = 0;
        this.body.xRot = rearingTime * -0.7853982F + rearingTimeLeft * this.body.xRot;
        // front leg rotations
        float rearingAmount = 0.2617994F * rearingTime;
        float rearingCos = Mth.cos(ticks * 0.6F + 3.1415927F);
        this.leftFrontLeg.y = 2.0F * rearingTime + 13.0F * rearingTimeLeft;
        this.leftFrontLeg.z = -6.0F * rearingTime - 9.0F * rearingTimeLeft;
        this.rightFrontLeg.y = this.leftFrontLeg.y;
        this.rightFrontLeg.z = this.leftFrontLeg.z;
        // back leg rotations
        float frontAngleCos = (-1.0471976F + rearingCos) * rearingTime + limbSwingAmountCos * rearingTimeLeft;
        float frontAngleSin = (-1.0471976F - rearingCos) * rearingTime - limbSwingAmountCos * rearingTimeLeft;
        this.leftHindLeg.xRot = rearingAmount - limbSwingCos * 0.5F * limbSwingAmount * rearingTimeLeft;
        this.rightHindLeg.xRot = rearingAmount + limbSwingCos * 0.5F * limbSwingAmount * rearingTimeLeft;
        this.leftFrontLeg.xRot = frontAngleCos;
        this.rightFrontLeg.xRot = frontAngleSin;
        // tail rotations
        boolean tailSwinging = (entity.getTailCounter() != 0);
        this.tail.xRot = 0.5235988F + limbSwingAmount * 0.75F;
        this.tail.y = 10.0F * rearingTime + 4.0F * rearingTimeLeft;
        this.tail.z = 13.5F * rearingTime + 9.5F * rearingTimeLeft;
        this.tail.yRot = tailSwinging ? Mth.cos(ticks * 0.7F) : 0.0F;
        // child rotation points
        boolean child = entity.isBaby();
        this.body.y = child ? 10.8F : 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}
