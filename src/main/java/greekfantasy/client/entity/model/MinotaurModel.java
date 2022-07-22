package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Minotaur;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MinotaurModel<T extends Minotaur> extends HoofedHumanoidModel<T> {
    public static final ModelLayerLocation MINOTAUR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "minotaur"), "minotaur");


    public MinotaurModel(ModelPart root) {
        super(root, true, false);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HoofedHumanoidModel.createMesh(CubeDeformation.NONE);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = GFModelUtil.addOrReplaceBullHead(partdefinition, "head", 58, 48);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isCharging()) {
            final float stompSpeed = 0.58F;
            final float limbSwingSin = Mth.cos(ageInTicks * stompSpeed + (float) Math.PI);
            final float limbSwingCos = Mth.cos(ageInTicks * stompSpeed) * 0.75F;
            float rightLegSwing = 0.38F * limbSwingSin;
            float leftLegSwing = 0.38F * limbSwingCos;
            // legs
            rightLegUpper.xRot = -0.2618F + limbSwingSin * 0.42F;
            leftLegUpper.xRot = -0.2618F + limbSwingCos * 0.42F;
            rightLegLower.xRot = 0.7854F + rightLegSwing;
            rightHoof.xRot = -0.5236F - rightLegSwing;
            leftLegLower.xRot = 0.7854F + leftLegSwing;
            leftHoof.xRot = -0.5236F - leftLegSwing;
            // head
            this.head.xRot = 0.558F;
        }
    }
}