package greekfantasy.client.armor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import greekfantasy.GreekFantasy;
import net.minecraft.client.Minecraft;
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

public class WingedSandalsModel extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation WINGED_SANDALS_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "winged_sandals"), "winged_sandals");;

    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public WingedSandalsModel(final ModelPart root) {
        super(root);
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftWing = leftLeg.getChild("left_wing");
        this.rightWing = rightLeg.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 12.0F, 2.0F));
        left_leg.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 18).mirror().addBox(0.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.5F, 11.0F, 2.0F, 0.0F, -0.7854F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 12.0F, 2.0F, -0.5236F, 0.0F, 0.0F));
        right_leg.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 18).addBox(-5.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 11.0F, 2.0F, 0.0F, 0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(leftLeg, rightLeg);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupWingsAnim(entity);
    }

    public void setupWingsAnim(LivingEntity entity) {
        float ageInTicks = entity.tickCount + Minecraft.getInstance().getFrameTime();
        // animate wings
        final float wingSpeed = entity.isOnGround() ? 0.64F : 1.18F;
        final float wingSpan = 0.76854F;
        final float wingAngle = wingSpan - Mth.cos((ageInTicks + entity.getId()) * wingSpeed) * wingSpan * 0.65F;
        this.rightWing.yRot = wingAngle;
        this.leftWing.yRot = -wingAngle;
    }
}
