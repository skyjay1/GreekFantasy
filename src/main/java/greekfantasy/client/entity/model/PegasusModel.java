package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Pegasus;
import net.minecraft.client.model.HorseModel;
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

public class PegasusModel<T extends Pegasus> extends HorseModel<T> {
    public static final ModelLayerLocation PEGASUS_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "pegasus"), "pegasus");

    protected final ModelPart wings;
    protected final ModelPart leftWing;
    protected final ModelPart leftOuterWing;
    protected final ModelPart rightWing;
    protected final ModelPart rightOuterWing;

    public PegasusModel(final ModelPart root) {
        super(root);
        this.wings = root.getChild("wings");
        this.leftWing = wings.getChild("left_wing");
        this.leftOuterWing = leftWing.getChild("left_outer_wing");
        this.rightWing = wings.getChild("right_wing");
        this.rightOuterWing = rightWing.getChild("right_outer_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HorseModel.createBodyMesh(CubeDeformation.NONE);
        PartDefinition partdefinition = meshdefinition.getRoot();
        // create wing models in order to render separately
        PartDefinition wings = partdefinition.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 5.0F));

        PartDefinition leftWing = wings.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 0).mirror(true)
                .addBox(-6.0F, 0.0F, 0.0F, 11.0F, 10.0F, 1.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(5.0F, 3.0F, -1.0F, 0.0F, -1.5708F, 0.0F));
        leftWing.addOrReplaceChild("left_outer_wing", CubeListBuilder.create().texOffs(0, 21).mirror(true)
                .addBox(-6.0F, 0.0F, 0.0F, 11.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition rightWing = wings.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 0).mirror(false)
                        .addBox(-6.0F, 0.0F, 0.0F, 11.0F, 10.0F, 1.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(-5.0F, 3.0F, -1.0F, 0.0F, 1.5708F, 0.0F));
        rightWing.addOrReplaceChild("right_outer_wing", CubeListBuilder.create().texOffs(0, 21).mirror(false)
                .addBox(-6.0F, 0.0F, 0.0F, 11.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 10.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.wings.copyFrom(this.body);
        // calculate wing rotations
        float wingSpan = 0.4F;
        float wingSpeed = 0.08F;
        if(entity.isFlying()) {
            wingSpan += 0.2F;
        }
        if(entity.isVehicle()) {
            wingSpeed += 0.32F;
        }
        final float wingAngle = Mth.cos((entity.tickCount + entity.getId() * 3 + partialTick) * wingSpeed) * wingSpan;
        // update rotations
        leftWing.zRot = -(1.5708F + wingAngle * 0.75F);
        leftOuterWing.xRot = -(wingAngle * 0.5F);

        rightWing.zRot = (1.5708F + wingAngle * 0.75F);
        rightOuterWing.xRot = leftOuterWing.xRot;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                          float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void renderWings(T entity, PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn,
                            int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTick) {
        // actually render the wings
        this.leftWing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        this.rightWing.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    public ModelPart getWings() {
        return this.wings;
    }
}
