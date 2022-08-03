package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class GiganteModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation GIGANTE_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "gigante"), "gigante");

    public GiganteModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = GiganteModel.createMesh(CubeDeformation.NONE);
        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static MeshDefinition createMesh(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubeDeformation.extend(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20).addBox(-6.0F, -32.0F, -3.0F, 12.0F, 16.0F, 6.0F, cubeDeformation), PartPose.offset(0.0F, 24.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(64, 20).addBox(0.0F, -2.0F, -3.0F, 6.0F, 16.0F, 6.0F, cubeDeformation), PartPose.offset(6.0F, -6.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 20).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 16.0F, 6.0F, cubeDeformation), PartPose.offset(-6.0F, -6.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(64, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, cubeDeformation), PartPose.offset(3.0F, 8.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(40, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, cubeDeformation), PartPose.offset(-3.0F, 8.0F, 0.0F));

        return meshdefinition;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
        // reset rotation points
        head.setPos(0.0F, -16.0F, 0.0F);
        hat.setPos(0.0F, -16.0F, 0.0F);
        body.setPos(0.0F, 24.0F, 0.0F);
        leftArm.setPos(6.0F, -6.0F, 0.0F);
        rightArm.setPos(-6.0F, -6.0F, 0.0F);
        leftLeg.setPos(3.0F, 8.0F, 0.0F);
        rightLeg.setPos(-3.0F, 8.0F, 0.0F);
        // decrease head rotation
        head.xRot = Mth.clamp(head.xRot, -0.261799F, 0.261799F);
        head.yRot = Mth.clamp(head.xRot, -0.785398F, 0.785398F);
        hat.copyFrom(head);
    }

    @Override
    public void translateToHand(final HumanoidArm sideIn, final PoseStack poseStack) {
        float dX = sideIn == HumanoidArm.RIGHT ? -2F : 2F;
        float dZ = -3.5F;
        float dY = 3.0F;
        ModelPart armModel = this.getArm(sideIn);
        armModel.x += dX;
        armModel.y += dY;
        armModel.z += dZ;
        armModel.translateAndRotate(poseStack);
        armModel.x -= dX;
        armModel.y -= dY;
        armModel.z -= dZ;
    }
}