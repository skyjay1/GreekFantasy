package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
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
import net.minecraft.world.entity.LivingEntity;

public class MerfolkModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation MERFOLK_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "merfolk"), "merfolk");

    protected final ModelPart chest;
    protected final ModelPart upperTail;
    protected final ModelPart middleTail;
    protected final ModelPart lowerTail;

    public MerfolkModel(ModelPart root) {
        super(root);
        this.chest = body.getChild("chest");
        this.upperTail = root.getChild("upper_tail");
        this.middleTail = upperTail.getChild("middle_tail");
        this.lowerTail = middleTail.getChild("lower_tail");

        // hide legs
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 17).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition upper_tail = partdefinition.addOrReplaceChild("upper_tail", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 10.0F, 2.0F, -0.2618F, 0.0F, 0.0F));
        PartDefinition middle_tail = upper_tail.addOrReplaceChild("middle_tail", CubeListBuilder.create().texOffs(0, 46).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.0F, -4.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition lower_tail = middle_tail.addOrReplaceChild("lower_tail", CubeListBuilder.create().texOffs(0, 23).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
        PartDefinition fin = lower_tail.addOrReplaceChild("fin", CubeListBuilder.create().texOffs(0, 56).addBox(-5.0F, -1.0F, 2.0F, 10.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 5.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(4.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-4.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.upperTail, this.hat);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // reset head position
        this.head.y += -2.0F;
        this.hat.copyFrom(this.head);
        // reset arm positions
        this.leftArm.x += -1.0F;
        this.leftArm.y += -2.0F;
        this.rightArm.x += 1.0F;
        this.rightArm.y += -2.0F;
        // swimming animation
        if (entity.isSwimming() || entity.isInWater()) {
            final float tailAngle = 0.21F;
            final float tailSpeed = 0.14F;
            // animate tail
            final float cosTail = Mth.cos((ageInTicks + entity.getId() * 3) * tailSpeed) * tailAngle;
            upperTail.xRot = -0.2618F + cosTail * 0.4F;
            middleTail.xRot = 0.5236F + cosTail * 0.6F;
            lowerTail.xRot = 0.2618F + cosTail * 0.8F;
            //lowerTail.xRot = cosTail * 0.8F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        // translate and rotate based on swim amount
        if (swimAmount > 0.0F) {
            poseStack.translate(0.0D, swimAmount * 1.175D, swimAmount * -0.4D);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(swimAmount * 90.0F));
        }
        // render model
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}