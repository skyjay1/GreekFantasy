package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.BronzeBull;
import net.minecraft.client.model.QuadrupedModel;
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

public class BronzeBullModel<T extends BronzeBull> extends QuadrupedModel<T> {
    public static final ModelLayerLocation BULL_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "bull"), "bull");

    protected final ModelPart mouth;
    protected final ModelPart upperTail;
    protected final ModelPart lowerTail;

    public BronzeBullModel(ModelPart root) {
        super(root, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
        this.mouth = head.getChild("mouth");
        this.upperTail = root.getChild("upper_tail");
        this.lowerTail = upperTail.getChild("lower_tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -8.0F, 10.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(29, 0).addBox(-3.0F, 2.0F, -10.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -13.0F));
        head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(47, 28).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 21).addBox(-7.0F, -15.0F, -4.0F, 14.0F, 16.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(65, 26).addBox(-6.0F, 1.0F, -4.0F, 12.0F, 13.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition leftLowerHorn = head.addOrReplaceChild("left_lower_horn", CubeListBuilder.create().texOffs(47, 17).addBox(-1.0F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -3.0F, -5.0F, 1.3963F, -1.0472F, 0.0F));
        PartDefinition leftMiddleHorn = leftLowerHorn.addOrReplaceChild("left_middle_horn", CubeListBuilder.create().texOffs(48, 8).addBox(-2.01F, -5.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.0F, -2.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition leftUpperHorn = leftMiddleHorn.addOrReplaceChild("left_upper_horn", CubeListBuilder.create().texOffs(49, 0).addBox(-1.5F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition rightLowerHorn = head.addOrReplaceChild("right_lower_horn", CubeListBuilder.create().texOffs(47, 17).addBox(-2.0F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -3.0F, -5.0F, 1.3963F, 1.0472F, 0.0F));
        PartDefinition rightMiddleHorn = rightLowerHorn.addOrReplaceChild("right_middle_horn", CubeListBuilder.create().texOffs(48, 8).addBox(-2.99F, -5.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.0F, -2.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition rightUpperHorn = rightMiddleHorn.addOrReplaceChild("right_upper_horn", CubeListBuilder.create().texOffs(49, 0).addBox(-1.5F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -5.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(91, 0).mirror().addBox(-2.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 6.0F, 12.0F));
        partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(91, 0).addBox(-4.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 6.0F, 12.0F));
        partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(62, 0).mirror().addBox(-2.0F, 0.0F, -3.0F, 7.0F, 18.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-7.0F, 6.0F, -8.0F));
        partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(62, 0).addBox(-5.0F, 0.0F, -3.0F, 7.0F, 18.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 6.0F, -8.0F));

        PartDefinition upperTail = partdefinition.addOrReplaceChild("upper_tail", CubeListBuilder.create().texOffs(116, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 16.0F, 0.5236F, 0.0F, 0.0F));
        upperTail.addOrReplaceChild("lower_tail", CubeListBuilder.create()
                .texOffs(116, 11).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(116, 20).addBox(-1.5F, 3.0F, -0.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -2.0F, 0.2618F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(upperTail));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float goringPercent = entity.getGoringPercent(ageInTicks - entity.tickCount);
        body.xRot = 1.5708F + 0.25F * Math.max(goringPercent, 0.4F);
        if (goringPercent > 0) {
            // head animation
            head.xRot += 0.5F;
            head.yRot += Mth.cos(goringPercent * (float) Math.PI * 10.0F) * 0.4F;
            body.setPos(0.0F, 7.0F, 2.0F);
            head.setPos(0.0F, 0.0F, -16.0F);
            upperTail.setPos(0.0F, -8.0F, 13.0F);
        } else {
            body.setPos(0.0F, 5.0F, 2.0F);
            head.setPos(0.0F, -3.0F, -13.0F);
            upperTail.setPos(0.0F, -6.0F, 16.0F);
            head.zRot = 0.0F;
            head.yRot = 0.0F;
            body.xRot = 1.5708F;
        }
        // mouth animation
        float firingPercent = entity.getFiringPercent(1.0F);
        if (firingPercent > 0) {
            mouth.xRot = 0.56F;
        } else {
            mouth.xRot = 0;
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        // goring animation
        float goringPercent = entity.getGoringPercent(partialTick);
        head.zRot = Mth.cos(goringPercent * (float) Math.PI * 16.0F) * 0.44F;
        // tail animation
        float limbSwingCos = Mth.cos(limbSwing) * limbSwingAmount;
        float idleSwing = 0.1F * Mth.cos((entity.tickCount + partialTick) * 0.08F);
        float tailSwing = 0.42F * limbSwingCos;
        upperTail.xRot = 0.5236F + tailSwing;
        lowerTail.xRot = 0.2618F + tailSwing * 0.6F;
        upperTail.zRot = idleSwing;
        lowerTail.zRot = idleSwing * 0.85F;
        body.zRot = limbSwingCos * 0.08F;
    }


}
