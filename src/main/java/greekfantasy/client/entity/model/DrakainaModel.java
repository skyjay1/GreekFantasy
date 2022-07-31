package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Drakaina;
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
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

public class DrakainaModel<T extends Mob> extends HumanoidModel<T> {

    public static final ModelLayerLocation DRAKAINA_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "drakaina"), "drakaina");

    protected ModelPart upperTail;
    protected ModelPart midTail;
    protected ModelPart lowerTail;
    protected ModelPart lowerTail1;
    protected ModelPart lowerTail2;
    protected ModelPart lowerTail3;

    public DrakainaModel(ModelPart root) {
        super(root);
        this.upperTail = root.getChild("upper_tail");
        this.midTail = upperTail.getChild("mid_tail");
        this.lowerTail = midTail.getChild("lower_tail");
        this.lowerTail1 = lowerTail.getChild("lower_tail1");
        this.lowerTail2 = lowerTail1.getChild("lower_tail2");
        this.lowerTail3 = lowerTail2.getChild("lower_tail3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = DrakainaModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static MeshDefinition createMesh(final CubeDeformation cubeDeformation, final float offset) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(cubeDeformation, offset);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F + offset, -2.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation.extend(0.25F)), PartPose.offset(0.0F, 0.0F + offset, -2.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 0.0F + offset, 0.0F));

        PartDefinition upperTail = partdefinition.addOrReplaceChild("upper_tail", CubeListBuilder.create().texOffs(0, 32).addBox(-3.999F, 0.0F, -4.0F, 8.0F, 10.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F + offset, 12.0F, 0.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition midTail = upperTail.addOrReplaceChild("mid_tail", CubeListBuilder.create().texOffs(0, 46).addBox(-4.001F, 0.0F, 0.0F, 8.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 10.0F + offset, -4.0F, 1.0472F, 0.0F, 0.0F));
        PartDefinition lowerTail = midTail.addOrReplaceChild("lower_tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 1.0472F, 0.0F, 0.0F));
        PartDefinition lowerTail1 = lowerTail.addOrReplaceChild("lower_tail1", CubeListBuilder.create().texOffs(25, 32).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 8.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 2.0F + offset, 0.0F, 0.0F, 0.0F, -0.2618F));
        PartDefinition lowerTail2 = lowerTail1.addOrReplaceChild("lower_tail2", CubeListBuilder.create().texOffs(46, 32).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 8.0F, 3.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 6.0F + offset, 0.0F, 0.0F, 0.0F, 0.5236F));
        PartDefinition lowerTail3 = lowerTail2.addOrReplaceChild("lower_tail3", CubeListBuilder.create().texOffs(46, 43).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 6.0F, 2.0F, cubeDeformation), PartPose.offsetAndRotation(0.0F, 7.0F + offset, 0.0F, 0.0F, 0.0F, -0.5236F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(4.0F, 2.0F + offset, -2.0F, 0.4363F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offsetAndRotation(-4.0F, 2.0F + offset, -2.0F, -0.4363F, 0.0F, 0.0F));

        return meshdefinition;
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leftArm, this.rightArm, this.upperTail, this.hat);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // set arm poses
        final ItemStack item = entity.getMainHandItem();
        if (entity.isAggressive() && item.getItem() instanceof BowItem) {
            if (entity.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
        } else {
            this.rightArmPose = this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        }
        // super method
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        leftArm.x = 4.0F;
        leftArm.z = -2.0F;
        rightArm.x = -4.0F;
        rightArm.z = -2.0F;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        // animate snake body
        final float limbSwingCos = (float) Math.cos(limbSwing);
        upperTail.yRot = limbSwingCos * 0.1F;
        lowerTail1.zRot = limbSwingCos * 0.67F;
        lowerTail2.zRot = limbSwingCos * -0.75F;
        lowerTail3.zRot = limbSwingCos * 0.4F;
    }
}