package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Cerastes;
import net.minecraft.client.model.AgeableListModel;
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

public class CerastesModel<T extends Cerastes> extends AgeableListModel<T> {
    public static final ModelLayerLocation CERASTES_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "cerastes"), "cerastes");

    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart tongue;

    private final ModelPart body1;
    private final ModelPart body2;
    private final ModelPart body3;
    private final ModelPart body4;
    private final ModelPart body5;
    private final ModelPart body6;
    private final ModelPart body7;

    public CerastesModel(final ModelPart root) {
        super();
        this.head = root.getChild("head");
        this.mouth = head.getChild("mouth");
        this.tongue = mouth.getChild("tongue");
        this.body1 = root.getChild("body1");
        this.body2 = body1.getChild("body2");
        this.body3 = body2.getChild("body3");
        this.body4 = body3.getChild("body4");
        this.body5 = body4.getChild("body5");
        this.body6 = body5.getChild("body6");
        this.body7 = body6.getChild("body7");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(25, 0).addBox(-2.0F, -1.0F, -10.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(43, 6).addBox(-1.5F, 0.75F, -9.5F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 10.0F, -4.0F));

        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(25, 7).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -6.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition tongue = mouth.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(42, 0).addBox(-0.5F, -0.9F, -4.0F, 1.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("left_nub", CubeListBuilder.create().texOffs(59, 13).addBox(0.0F, -4.0F, -1.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -4.0F, -4.0F, 0.48F, 0.0F, 0.1745F));
        head.addOrReplaceChild("right_nub", CubeListBuilder.create().texOffs(59, 13).addBox(-1.0F, -4.0F, -1.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -4.0F, -4.0F, 0.48F, 0.0F, -0.1745F));

        PartDefinition left_horn = head.addOrReplaceChild("left_horn", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -2.0F, -4.0F, -0.2618F, 0.0F, 0.0F));
        PartDefinition lower_left_horn = left_horn.addOrReplaceChild("lower_left_horn", CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition lower_middle_left_horn = lower_left_horn.addOrReplaceChild("lower_middle_left_horn", CubeListBuilder.create().texOffs(58, 6).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.7854F, 0.1745F, 0.0F));
        PartDefinition middle_left_horn = lower_middle_left_horn.addOrReplaceChild("middle_left_horn", CubeListBuilder.create().texOffs(58, 13).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, 0.1745F, 0.0F));
        PartDefinition upper_middle_left_horn = middle_left_horn.addOrReplaceChild("upper_middle_left_horn", CubeListBuilder.create().texOffs(58, 18).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, 0.1745F, 0.0F));
        PartDefinition upper_left_horn = upper_middle_left_horn.addOrReplaceChild("upper_left_horn", CubeListBuilder.create().texOffs(58, 22).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -1.0472F, 0.1745F, 0.0F));

        PartDefinition right_horn = head.addOrReplaceChild("right_horn", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -2.0F, -4.0F, -0.2618F, 0.0F, 0.0F));
        PartDefinition lower_right_horn = right_horn.addOrReplaceChild("lower_right_horn", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition lower_middle_right_horn = lower_right_horn.addOrReplaceChild("lower_middle_right_horn", CubeListBuilder.create().texOffs(58, 6).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.7854F, -0.1745F, 0.0F));
        PartDefinition middle_right_horn = lower_middle_right_horn.addOrReplaceChild("middle_right_horn", CubeListBuilder.create().texOffs(58, 13).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, -0.1745F, 0.0F));
        PartDefinition upper_middle_right_horn = middle_right_horn.addOrReplaceChild("upper_middle_right_horn", CubeListBuilder.create().texOffs(58, 18).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, -0.1745F, 0.0F));
        PartDefinition upper_right_horn = upper_middle_right_horn.addOrReplaceChild("upper_right_horn", CubeListBuilder.create().texOffs(58, 22).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -1.0472F, -0.1745F, 0.0F));

        PartDefinition body1 = partdefinition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 8.0F, -4.5F, -0.7854F, 0.0F, 0.0F));
        PartDefinition body2 = body1.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 13).addBox(-1.99F, 0.0F, 0.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition body3 = body2.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.0F, 6.0F, 0.3491F, 0.0F, 0.0F));
        PartDefinition body4 = body3.addOrReplaceChild("body4", CubeListBuilder.create().texOffs(0, 13).addBox(-1.99F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.9599F, 0.0F, 0.0F));
        PartDefinition body5 = body4.addOrReplaceChild("body5", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 5.5F));
        PartDefinition body6 = body5.addOrReplaceChild("body6", CubeListBuilder.create().texOffs(21, 14).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 5.5F));
        PartDefinition body7 = body6.addOrReplaceChild("body7", CubeListBuilder.create().texOffs(22, 14).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 5.5F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }


    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
        final float partialTick = ageInTicks - entity.tickCount;
        final float standingTime = entity.getStandingPercent(partialTick);
        final float standingTimeLeft = (1.0F - standingTime);
        final float hidingTime = entity.getHidingPercent(partialTick);
        final float limbSwingCos = entity.isHiding() ? 1.0F : Mth.cos(limbSwing);
        final float idleSwingCos = Mth.cos((entity.tickCount + partialTick) * 0.22F);
        // rotation points
        head.y = 21.5F - 10.25F * standingTime + 6.5F * hidingTime;
        head.z = -6.0F + 6.0F * hidingTime;
        body1.y = head.y - 2.0F;
        body1.z = head.z - 0.5F;
        // head rotation
        head.yRot = rotationYaw * 0.017453292F;
        head.xRot = rotationPitch * 0.017453292F;
        // tongue
        tongue.z = -4.0F * (1.0F - 2.0F * Math.abs(entity.getTonguePercent(partialTick) - 0.5F));
        // rotation angles
        // standing
        this.body1.xRot = -0.7854F * standingTime - 1.4707F * hidingTime;
        this.body2.xRot = -0.5236F * standingTime;
        this.body3.xRot = 0.3491F * standingTime;
        this.body4.xRot = 0.9599F * standingTime;
        this.mouth.xRot = (0.5236F + 0.06F * idleSwingCos) * standingTime;
        if(entity.isInSittingPose()) {
            // sitting
            body5.yRot = 0.761799F;
            body6.yRot = 0.761799F;
            body7.yRot = 0.761799F;
        } else {
            // slithering
            body1.yRot = limbSwingCos * -0.4F * standingTimeLeft;
            body2.yRot = limbSwingCos * 0.4F * standingTimeLeft;
            body3.yRot = limbSwingCos * -0.65F * standingTimeLeft;
            body4.yRot = limbSwingCos * 0.75F * standingTimeLeft;
            body5.yRot = limbSwingCos * -0.65F;
            body6.yRot = limbSwingCos * 0.65F;
            body7.yRot = limbSwingCos * -0.25F;
        }
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body1);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(head);
    }
}
