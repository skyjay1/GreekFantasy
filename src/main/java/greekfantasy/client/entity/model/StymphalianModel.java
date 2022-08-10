package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Stymphalian;
import net.minecraft.client.model.HierarchicalModel;
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

public class StymphalianModel<T extends Stymphalian> extends HierarchicalModel<T> {
    public static final ModelLayerLocation STYMPHALIAN_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "stymphalian"), "stymphalian");

    protected final ModelPart root;
    protected final ModelPart body;
    protected final ModelPart neck;
    protected final ModelPart head;
    protected final ModelPart mouth;
    protected final ModelPart leftLeg;
    protected final ModelPart rightLeg;
    protected final ModelPart leftWing;
    protected final ModelPart leftUpperWing;
    protected final ModelPart leftLowerWing;
    protected final ModelPart rightWing;
    protected final ModelPart rightUpperWing;
    protected final ModelPart rightLowerWing;


    public StymphalianModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.mouth = head.getChild("mouth");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftWing = body.getChild("left_wing");
        this.leftUpperWing = leftWing.getChild("left_upper_wing");
        this.leftLowerWing = leftUpperWing.getChild("left_lower_wing");
        this.rightWing = body.getChild("right_wing");
        this.rightUpperWing = rightWing.getChild("right_upper_wing");
        this.rightLowerWing = rightUpperWing.getChild("right_lower_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 12).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 8.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(21, 10).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(-3.0F, 0.0F, 1.0F, 6.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(15, 25).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 19.0F, 0.0F, -2.4435F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.0F, 0.5F, -0.3491F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 3.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(30, 25).addBox(0.0F, 2.5F, -2.5F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(37, 6).addBox(-1.0F, 0.0F, 2.5F, 2.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, -0.3491F, 0.0F, 0.0F));

        head.addOrReplaceChild("beak_tip", CubeListBuilder.create().texOffs(37, 11).addBox(-0.99F, -0.75F, -0.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F, -0.25F, -0.25F)), PartPose.offsetAndRotation(0.0F, 1.0F, 5.5F, 1.0472F, 0.0F, 0.0F));
        head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(37, 0).addBox(-1.0F, -0.73F, -1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 0.0F, 2.5F));

        PartDefinition leftWing = body.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition upperLeftWing = leftWing.addOrReplaceChild("left_upper_wing", CubeListBuilder.create().texOffs(14, 0).addBox(0.0F, -8.0F, 0.0F, 5.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 0.0F, -3.0F, 0.0F, -1.2217F, 0.0F));
        upperLeftWing.addOrReplaceChild("left_lower_wing", CubeListBuilder.create().texOffs(25, 0).addBox(0.0F, -8.0F, 0.0F, 5.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        PartDefinition rightWing = body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition upperRightWing = rightWing.addOrReplaceChild("right_upper_wing", CubeListBuilder.create().texOffs(14, 0).mirror().addBox(-5.0F, -8.0F, 0.0F, 5.0F, 8.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-3.0F, 0.0F, -3.0F));
        upperRightWing.addOrReplaceChild("right_lower_wing", CubeListBuilder.create().texOffs(25, 0).mirror().addBox(-5.0F, -8.0F, 0.0F, 5.0F, 8.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-5.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(21, 19).addBox(0.0F, -0.5F, 0.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(1.0F, 20.5F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(21, 19).addBox(0.0F, -0.5F, 0.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 20.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // animate head
        this.neck.xRot = headPitch * ((float)Math.PI / 180F) - 0.3491F;
        this.head.yRot = -netHeadYaw * ((float)Math.PI / 180F);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        // prepare to animate wings
        float ticks = entity.getId() * 2 + entity.tickCount + partialTick;
        final float flyingTime = entity.getFlyingTime(partialTick);
        final float flyingTimeLeft = 1.0F - flyingTime;
        final float downSwing = -0.5F;
        final float wingAngle = 0.64F;
        final float wingSpeed = 0.82F;
        final float cosTicks = flyingTime * Mth.cos(ticks * wingSpeed);
        final float cosIdle = Mth.cos(ticks * 0.11F);
        // animate wings (combines flying and landing animations)
        this.leftUpperWing.yRot = 0.0F + ((cosTicks + downSwing) * wingAngle) * flyingTime - 1.2217F * flyingTimeLeft + 0.015F * cosIdle;
        this.leftLowerWing.yRot = leftUpperWing.yRot * 0.8F;
        this.rightUpperWing.yRot = -leftUpperWing.yRot;
        this.rightLowerWing.yRot = -leftLowerWing.yRot;
        // animate legs
        this.rightLeg.xRot = Mth.cos(ticks * 0.6662F) * 0.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(ticks * 0.6662F + (float)Math.PI) * 0.4F * limbSwingAmount;
        // animate bobbing
        float y = cosTicks * 0.2F;
        this.body.y = 19.0F + y;
        this.leftLeg.y = 20.5F + y;
        this.rightLeg.y = this.leftLeg.y;
        // open mouth
        if(entity.isAggressive()) {
            this.mouth.xRot = -0.361799F;
        } else {
            this.mouth.xRot = 0.0F;
        }
    }

    @Override
    public ModelPart root() {
        return root;
    }
}