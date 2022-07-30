package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.HydraHead;
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

public class HydraHeadModel<T extends HydraHead> extends EntityModel<T> {
    public static final ModelLayerLocation HYDRA_HEAD_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "hydra_head"), "hydra_head");

    protected final ModelPart severedNeck;
    protected final ModelPart neck;
    protected final ModelPart neck1;
    protected final ModelPart neck2;
    protected final ModelPart neck3;
    protected final ModelPart head;
    protected final ModelPart mouth;

    protected float spawnPercent;
    protected boolean severed;
    protected boolean charred;

    public HydraHeadModel(final ModelPart root) {
        super();
        this.severedNeck = root.getChild("severed_neck");
        this.neck = root.getChild("neck");
        this.neck1 = neck.getChild("neck1");
        this.neck2 = neck1.getChild("neck2");
        this.neck3 = neck2.getChild("neck3");
        this.head = neck3.getChild("head");
        this.mouth = head.getChild("mouth");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("severed_neck", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -9.0F, 0.0F, 4.0F, 10.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(25, 22).addBox(-1.0F, -8.0F, 6.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 24.0F, 1.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition neck = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, -3.0F));
        PartDefinition neck1 = neck.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -9.0F, 0.0F, 4.0F, 10.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(25, 22).addBox(-1.0F, -8.0F, 6.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, -3.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.01F, -10.0F, 0.0F, 4.0F, 10.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(25, 22).addBox(-1.0F, -10.0F, 6.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, -0.3491F, 0.0F, 0.0F));
        PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -10.0F, 0.0F, 4.0F, 10.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(25, 22).addBox(-1.0F, -9.0F, 6.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.1745F, 0.0F, 0.0F));
        PartDefinition head = neck3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -8.0F, -4.0F, 6.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(33, 0).addBox(-3.0F, -4.0F, -10.0F, 6.0F, 3.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(38, 25).addBox(-2.5F, -3.25F, -9.5F, 5.0F, 3.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(25, 22).addBox(-1.0F, -8.0F, 4.0F, 2.0F, 8.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -9.0F, 3.0F));
        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(33, 9).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition leftLowerHorn = head.addOrReplaceChild("left_lower_horn", CubeListBuilder.create().texOffs(56, 27).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -6.0F, 1.0F, 0.0F, 0.0F, 0.5236F));
        PartDefinition leftMiddleHorn = leftLowerHorn.addOrReplaceChild("left_middle_horn", CubeListBuilder.create().texOffs(56, 22).addBox(-2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.5236F, 0.0873F, 0.0F));
        PartDefinition leftUpperHorn = leftMiddleHorn.addOrReplaceChild("left_upper_horn", CubeListBuilder.create().texOffs(56, 17).addBox(-2.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 1.0472F, 0.0873F, 0.0F));

        PartDefinition rightLowerHorn = head.addOrReplaceChild("right_lower_horn", CubeListBuilder.create().texOffs(56, 27).addBox(0.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -6.0F, 1.0F, 0.0F, 0.0F, -0.5236F));
        PartDefinition rightMiddleHorn = rightLowerHorn.addOrReplaceChild("right_middle_horn", CubeListBuilder.create().texOffs(56, 22).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.5236F, -0.0873F, 0.0F));
        PartDefinition rightUpperHorn = rightMiddleHorn.addOrReplaceChild("right_upper_horn", CubeListBuilder.create().texOffs(56, 17).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 1.0472F, -0.0873F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // rotate head angles
        float yaw = netHeadYaw * ((float) Math.PI / 180F);
        head.xRot = headPitch * ((float) Math.PI / 180F);
        head.yRot = yaw * 0.4F;
        neck1.yRot = yaw * 0.1F;
        neck2.yRot = yaw * 0.2F;
        neck3.yRot = yaw * 0.3F;
        // copy head angles to severed portion
        severedNeck.xRot = neck1.xRot + neck.xRot;
        severedNeck.yRot = neck1.yRot + neck.yRot;
        severedNeck.zRot = neck1.zRot + neck.zRot;
        severedNeck.setPos(neck.x, neck.y + 2, neck.z - 3);
    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        final float idleSwingCos = Mth.cos((31 * entityIn.getId() + entityIn.tickCount + partialTick) * 0.19F);
        // used to move the entire neck in a swinging motion (bite attack)
        final float swingPercent = entityIn.getAttackAnim(partialTick);
        // alternative that does not use cos: swing = 2.0D * Math.min(swingPercent - 0.5D, -(swingPercent - 0.5D)) + 1.0D;
        final float swing = Mth.cos((swingPercent - 0.5F) * (float) Math.PI);
        // animate mouth and neck parts
        mouth.xRot = (0.2618F + 0.45F * swing + 0.08F * idleSwingCos);
        neck1.xRot = 0.5236F + swing * 0.78F;
        neck2.xRot = -0.3491F + idleSwingCos * 0.04F;
        neck3.xRot = -0.1745F + idleSwingCos * 0.02F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn,
                               float red, float green, float blue, float alpha) {
        poseStack.translate(0, 0.0D, 0.25D);
        // render the severed neck portion
        severedNeck.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        // render the rest of the head if growing
        if (!severed && !charred && spawnPercent > 0.0F) {
            // scale according to spawn percent and render normally
            float scale = spawnPercent;
            poseStack.translate(0, (1.0F - scale), 0);
            poseStack.scale(scale, scale, scale);
            // render neck (and all children)
            neck.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
        }
    }

    public void setSpawnPercent(final float spawnPercentIn) {
        spawnPercent = spawnPercentIn;
    }

    public void setSevered(final boolean severedIn) {
        severed = severedIn;
    }

    public void setCharred(final boolean charredIn) {
        charred = charredIn;
    }

}
