package greekfantasy.client.entity.model;

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
import net.minecraft.world.entity.LivingEntity;

public class PalladiumModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation PALLADIUM_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "palladium"), "palladium");

    public PalladiumModel(final ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        float offsetY = 0.0F;
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, offsetY);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F + offsetY, 0.0F));
        //body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(20, 20).addBox(-3.99F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.2182F, 0.0F, 0.0F));
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(20, 20).addBox(-3.99F, 0.0F, 0.0F, 8.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.3491F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F + offsetY, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F + offsetY, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }


    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch);
        this.head.xRot = -0.174533F;
        // arm rotations
        this.rightArm.xRot = -1.675516F;
        this.rightArm.zRot = 0.05236F;
        this.leftArm.xRot = 0.0F;
        this.leftArm.zRot = -this.rightArm.yRot;
        // leg rotations
        this.rightLeg.xRot = -0.174533F;
        this.leftLeg.xRot = -this.rightLeg.xRot;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
}