package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Python;
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

public class PythonModel<T extends Python> extends AgeableListModel<T> {

    public static final ModelLayerLocation PYTHON_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "python"), "python");

    private final ModelPart head;
    private final ModelPart mouth;

    private final ModelPart body1;
    private final ModelPart body2;
    private final ModelPart body3;
    private final ModelPart body4;
    private final ModelPart body5;
    private final ModelPart body6;
    private final ModelPart body7;
    private final ModelPart body8;

    public PythonModel(final ModelPart root) {
        super(true, 0.0F, 0.0F);
        head = root.getChild("head");
        mouth = head.getChild("mouth");
        body1 = root.getChild("body1");
        body2 = body1.getChild("body2");
        body3 = body2.getChild("body3");
        body4 = body3.getChild("body4");
        body5 = body4.getChild("body5");
        body6 = body5.getChild("body6");
        body7 = body6.getChild("body7");
        body8 = body7.getChild("body8");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -10.0F, 8.0F, 8.0F, 10.0F, CubeDeformation.NONE)
                .texOffs(27, 0).addBox(-3.0F, -6.0F, -15.0F, 6.0F, 3.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(51, 0).addBox(1.0F, -3.5F, -14.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(51, 0).addBox(-2.0F, -3.5F, -14.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 10.0F, -4.0F));
        head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(37, 12).addBox(-3.0F, 0.0F, -5.0F, 6.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, -10.0F, 0.5236F, 0.0F, 0.0F));
        head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(56, 0).addBox(0.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -10.0F, -5.0F, 0.48F, 0.0F, 0.1745F));
        head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(56, 0).addBox(-2.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -10.0F, -5.0F, 0.48F, 0.0F, -0.1745F));

        PartDefinition body1 = partdefinition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, -4.5F, -4.0F, 6.0F, 6.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 9.0F, -4.5F, -0.7854F, 0.0F, 0.0F));
        PartDefinition body2 = body1.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 20).addBox(-2.99F, -2.0F, -3.5F, 6.0F, 6.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, 6.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition body3 = body2.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.0F, 4.0F, 0.3491F, 0.0F, 0.0F));
        PartDefinition body4 = body3.addOrReplaceChild("body4", CubeListBuilder.create().texOffs(0, 35).addBox(-2.99F, -6.0F, 0.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.9599F, 0.0F, 0.0F));
        PartDefinition body5 = body4.addOrReplaceChild("body5", CubeListBuilder.create().texOffs(0, 35).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 5.5F));
        PartDefinition body6 = body5.addOrReplaceChild("body6", CubeListBuilder.create().texOffs(0, 48).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 5.5F));
        PartDefinition body7 = body6.addOrReplaceChild("body7", CubeListBuilder.create().texOffs(30, 24).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 5.5F));
        PartDefinition body8 = body7.addOrReplaceChild("body8", CubeListBuilder.create().texOffs(30, 35).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 5.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
        // head rotation
        head.yRot = rotationYaw * 0.017453292F;
        head.xRot = rotationPitch * 0.017453292F;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);

        // animate snake body
        final float limbSwingCos = Mth.cos(limbSwing);
        final float idleSwingCos = Mth.cos((entity.tickCount + partialTick) * 0.22F);
        this.mouth.xRot = (0.5236F + 0.06F * idleSwingCos);
        body4.yRot = limbSwingCos * 0.15F;
        body5.yRot = limbSwingCos * -0.60F;
        body6.yRot = limbSwingCos * 0.85F;
        body7.yRot = limbSwingCos * -0.65F;
        body8.yRot = limbSwingCos * 0.35F;
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
