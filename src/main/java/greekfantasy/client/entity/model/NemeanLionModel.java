package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.NemeanLion;
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

public class NemeanLionModel<T extends NemeanLion> extends BigCatModel<T> {
    public static final ModelLayerLocation NEMEAN_LION_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "lion"), "lion");

    protected final ModelPart mouth;

    public NemeanLionModel(ModelPart root) {
        super(root);
        mouth = head.getChild("mouth");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = BigCatModel.createBodyMesh(CubeDeformation.NONE);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(29, 0).addBox(-4.0F, -17.0F, -3.0F, 8.0F, 11.0F, 7.0F, CubeDeformation.NONE)
                .texOffs(35, 19).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 8.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(0, 34).addBox(-5.0F, -19.0F, -4.0F, 10.0F, 4.0F, 10.0F, CubeDeformation.NONE)
                .texOffs(20, 36).addBox(5.0F, -18.0F, -3.0F, 1.0F, 2.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(2, 36).addBox(-6.0F, -18.0F, -3.0F, 1.0F, 2.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(9, 43).addBox(-4.0F, -18.0F, 6.0F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 43).addBox(-4.0F, -18.0F, -5.0F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(17, 43).addBox(-3.0F, -15.0F, 4.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.0F, 8.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(2, 2).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 13).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(15, 17).addBox(-2.0F, 2.5F, -4.5F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(21, 1).addBox(-3.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(21, 1).mirror().addBox(1.5F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 10.5F, -10.0F));
        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(15, 14).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 3.0F, -3.0F));


        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        final float idleSwingCos = Mth.cos((entity.tickCount + partialTick) * 0.22F);
        mouth.xRot = (0.5236F + 0.05F * idleSwingCos);
    }

    @Override
    protected float getAttackingPercent(T entity, float partialTick) {
        return entity.getAttackPercent(partialTick);
    }

    @Override
    protected float getSittingPercent(T entity, float partialTick) {
        return entity.getSittingPercent(partialTick);
    }
}
