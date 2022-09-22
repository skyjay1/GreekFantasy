package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.HasHorseVariant;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

public class CentaurModel<T extends Mob & HasHorseVariant> extends HumanoidModel<T> {

    public static final ModelLayerLocation CENTAUR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "centaur"), "centaur");

    protected final ModelPart quiver;

    public CentaurModel(final ModelPart root) {
        super(root);
        this.quiver = root.getChild("quiver");
        // hide legs
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;
        // hide quiver
        this.quiver.visible = false;
    }

    public static MeshDefinition createMesh(CubeDeformation cubeDeformation, float offset) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, offset);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -9.0F, -10.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F + offset, 0.0F));
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, -9.0F + offset, -8.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation.extend(0.5F)), PartPose.offset(0.0F, 0.0F + offset, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(0.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(4.0F, -8.0F + offset, -8.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-4.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(-4.0F, -8.0F + offset, -8.0F));

        PartDefinition quiver = partdefinition.addOrReplaceChild("quiver", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 10.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -4.0F + offset, -4.0F, 0.0F, 0.0F, 0.6109F));
        PartDefinition arrows = quiver.addOrReplaceChild("arrows", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));
        PartDefinition arrow1 = arrows.addOrReplaceChild("arrow1", CubeListBuilder.create().texOffs(27, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 1.0F, cubeDeformation), PartPose.offset(0.0F, -2.0F + offset, 0.0F));
        PartDefinition arrow2 = arrows.addOrReplaceChild("arrow2", CubeListBuilder.create().texOffs(27, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 1.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, -2.0F + offset, 0.0F, 0.0F, -1.5708F, 0.0F));

        return meshdefinition;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = CentaurModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.hat);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
        // set arm poses
        final ItemStack item = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (item.getItem() instanceof BowItem && entity.isAggressive()) {
            if (entity.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
        } else {
            this.rightArmPose = this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        }
        // super method
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch);
        // horse-based rotation angles
        float rearingTime = entity.getRearingAmount(ageInTicks - (float) Math.floor(ageInTicks));
        float rearingTimeLeft = 1.0F - rearingTime;
        this.body.setPos(0.0F, -6.0F * rearingTime, 10.0F * rearingTime - 1.0F * rearingTimeLeft);
        this.head.setPos(0.0F, -15.0F * rearingTime - 9.0F * rearingTimeLeft, 2.0F * rearingTime - 9.0F * rearingTimeLeft);
        this.hat.setPos(head.x, head.y, head.z);
        this.leftArm.setPos(4.0F, -14.0F * rearingTime - 8.0F * rearingTimeLeft, 2.0F * rearingTime - 9.0F * rearingTimeLeft);
        this.rightArm.setPos(-4.0F, leftArm.y, leftArm.z);
        this.quiver.setPos(0.0F, -9.0F * rearingTime - 3.0F * rearingTimeLeft, 6.5F * rearingTime - 5.0F * rearingTimeLeft);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    public void renderQuiver(T entity, PoseStack matrixStackIn, VertexConsumer vertexConsumer, int packedLight,
                             int packedOverlay, float limbSwing, float limbSwingAmount) {
        this.quiver.visible = true;
        this.body.render(matrixStackIn, vertexConsumer, packedLight, packedOverlay);
        this.quiver.render(matrixStackIn, vertexConsumer, packedLight, packedOverlay);
    }
}
