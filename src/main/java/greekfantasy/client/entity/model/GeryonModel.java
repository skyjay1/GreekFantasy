package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Geryon;
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
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class GeryonModel<T extends Geryon> extends GiganteModel<T> {
    public static final ModelLayerLocation GERYON_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "geryon"), "geryon");
    public static final ModelLayerLocation GERYON_ARMOR_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "geryon_armor"), "geryon_armor");

    protected final ModelPart leftHead;
    protected final ModelPart rightHead;

    public GeryonModel(ModelPart root) {
        super(root);
        this.leftHead = root.getChild("left_head");
        this.rightHead = root.getChild("right_head");
    }

    public static LayerDefinition createBodyLayer(final CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = GiganteModel.createMesh(cubeDeformation);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("left_head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubeDeformation.extend(-1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubeDeformation), PartPose.offset(0.0F, 0.25F, 0.0F));
        partdefinition.addOrReplaceChild("right_head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubeDeformation.extend(-1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return Iterables.concat(super.headParts(), ImmutableList.of(this.leftHead, this.rightHead));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
        // update heads
        this.leftHead.copyFrom(this.head);
        this.leftHead.x += -8.0F;
        this.leftHead.y += 1.0F;
        this.rightHead.copyFrom(this.head);
        this.rightHead.x += 8.0F;
        this.rightHead.y += 1.0F;
        //this.head.y -= 0.25F;
        // smash animation
        final float smashTime = entity.getSmashPercent(partialTick);
        final float summonTime = entity.getSummonPercent(partialTick);
        if (summonTime > 0) {
            final ModelPart arm = this.getArm(entity.getMainArm().getOpposite());
            arm.xRot = -1.5708F * summonTime;
            arm.yRot = 0.680678F * summonTime * (entity.getMainArm() == HumanoidArm.RIGHT ? -1 : 1);
        }
        if (smashTime > 0) {
            // when smashTime is >= downTrigger, arms will move downwards
            final float downTrigger = 0.9F;
            final float downMult = 12.5F;
            // maximum x and y angles
            final float smashAngleX = 2.02F;
            final float smashAngleY = 0.52F;
            rightArm.xRot = -downMult * Math.min((1.0F - downTrigger) * smashTime, -0.5F * (smashTime - 0.95F)) * smashAngleX;
            rightArm.yRot = -(Math.min(smashTime * 1.35F, 1.0F)) * smashAngleY;
            leftArm.xRot = rightArm.xRot;
            leftArm.yRot = -rightArm.yRot;
        }
    }
}