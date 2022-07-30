package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Hydra;
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

public class HydraBodyModel<T extends Hydra> extends EntityModel<T> {
    public static final ModelLayerLocation HYDRA_BODY_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "hydra_body"), "hydra_body");

    private final ModelPart body;
    private final ModelPart upperBody;
    private final ModelPart middleBody;
    private final ModelPart bodyLower1;
    private final ModelPart bodyLower2;
    private final ModelPart bodyLower3;
    private final ModelPart bodyLower4;
    private final ModelPart bodyLower5;

    public HydraBodyModel(ModelPart root) {
        super();
        this.body = root.getChild("body");
        this.upperBody = body.getChild("upper_body");
        this.middleBody = upperBody.getChild("middle_body");
        this.bodyLower1 = middleBody.getChild("lower_body1");
        this.bodyLower2 = bodyLower1.getChild("lower_body2");
        this.bodyLower3 = bodyLower2.getChild("lower_body3");
        this.bodyLower4 = bodyLower3.getChild("lower_body4");
        this.bodyLower5 = bodyLower4.getChild("lower_body5");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, -9.0F));

        PartDefinition upperBody = body.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
        upperBody.addOrReplaceChild("front_right_body", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-14.0F, -7.0F, 0.0F, 14.0F, 12.0F, 6.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 2.0F, -6.0F, 0.0F, 0.2618F, 0.0F));
        upperBody.addOrReplaceChild("front_left_body", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -7.0F, 0.0F, 14.0F, 12.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, -6.0F, 0.0F, -0.2618F, 0.0F));
        upperBody.addOrReplaceChild("back_left_body", CubeListBuilder.create()
                .texOffs(42, 0).addBox(0.0F, -7.01F, -6.0F, 12.0F, 12.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(2.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(6.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(10.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 6.5F, 0.0F, 0.2618F, 0.0F));
        upperBody.addOrReplaceChild("back_right_body", CubeListBuilder.create()
                .texOffs(42, 0).mirror().addBox(-12.0F, -7.01F, -6.0F, 12.0F, 12.0F, 6.0F, CubeDeformation.NONE).mirror(false)
                .texOffs(120, 0).mirror().addBox(-3.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, CubeDeformation.NONE).mirror(false)
                .texOffs(120, 0).mirror().addBox(-7.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, CubeDeformation.NONE).mirror(false)
                .texOffs(120, 0).mirror().addBox(-11.0F, -7.0F, 0.0F, 1.0F, 12.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 2.0F, 6.5F, 0.0F, -0.2618F, 0.0F));

        PartDefinition middleBody = upperBody.addOrReplaceChild("middle_body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.0F, -4.0F, 0.2618F, 0.0F, 0.0F));
        middleBody.addOrReplaceChild("front_right_middle_body", CubeListBuilder.create().texOffs(0, 20).mirror().addBox(-10.0F, -8.0F, 0.0F, 10.0F, 12.0F, 6.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 7.0F, -1.0F, 0.0F, 0.2618F, 0.0F));
        middleBody.addOrReplaceChild("front_left_middle_body", CubeListBuilder.create().texOffs(0, 20).addBox(0.0F, -8.0F, 0.0F, 10.0F, 12.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.0F, -1.0F, 0.0F, -0.2618F, 0.0F));
        middleBody.addOrReplaceChild("back_left_middle_body", CubeListBuilder.create()
                .texOffs(34, 20).addBox(0.0F, -8.01F, -4.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(1.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(5.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.0F, 9.0F, 0.0F, 0.2182F, 0.0F));
        middleBody.addOrReplaceChild("back_right_middle_body", CubeListBuilder.create()
                .texOffs(34, 20).mirror().addBox(-8.0F, -8.01F, -4.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE).mirror(false)
                .texOffs(120, 0).mirror().addBox(-2.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE).mirror(false)
                .texOffs(120, 0).mirror().addBox(-6.0F, -4.0F, 0.0F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, 7.0F, 9.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition lower_body1 = middleBody.addOrReplaceChild("lower_body1", CubeListBuilder.create().texOffs(0, 40).addBox(-6.0F, 0.0F, 1.0F, 12.0F, 10.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(0.0F, 3.0F, 7.0F, 1.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, 0.4363F, 0.0F, 0.0F));
        PartDefinition lower_body2 = lower_body1.addOrReplaceChild("lower_body2", CubeListBuilder.create().texOffs(84, 0).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 10.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(0.0F, 3.0F, 5.0F, 1.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 10.0F, 1.0F, 0.6109F, 0.0F, 0.0F));
        PartDefinition lower_body3 = lower_body2.addOrReplaceChild("lower_body3", CubeListBuilder.create().texOffs(84, 16).addBox(-4.0F, 0.0F, 0.01F, 8.0F, 10.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(0.0F, 0.0F, 5.0F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 9.0F, 0.0F));
        PartDefinition lower_body4 = lower_body3.addOrReplaceChild("lower_body4", CubeListBuilder.create().texOffs(84, 32).addBox(-3.0F, 0.0F, 0.01F, 6.0F, 10.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(120, 0).addBox(0.0F, 0.0F, 5.0F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 9.01F, 0.0F));
        PartDefinition lower_body5 = lower_body4.addOrReplaceChild("lower_body5", CubeListBuilder.create().texOffs(84, 48).addBox(-2.0F, 0.0F, 0.01F, 4.0F, 10.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(120, 16).addBox(0.0F, 0.0F, 4.0F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 9.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        // animate snake body
        final float idleSwingCos = Mth.cos((entityIn.tickCount + partialTick) * 0.12F);
        final float limbSwingCos = Mth.cos(limbSwing);
        // limb swing rotations
        upperBody.yRot = limbSwingCos * 0.04F + idleSwingCos * 0.011F;
        middleBody.yRot = limbSwingCos * -0.12F + idleSwingCos * 0.011F;
        bodyLower1.yRot = idleSwingCos * -0.022F;
        bodyLower1.zRot = limbSwingCos * 0.1F;
        bodyLower2.zRot = limbSwingCos * -0.37F;
        bodyLower3.zRot = limbSwingCos * 0.77F;
        bodyLower4.zRot = limbSwingCos * -0.95F;
        bodyLower5.zRot = limbSwingCos * 0.72F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, float red,
                               float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLightIn, packedOverlayIn);
    }
}
