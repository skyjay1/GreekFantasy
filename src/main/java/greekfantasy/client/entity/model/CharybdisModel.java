package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Charybdis;
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
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class CharybdisModel extends AgeableListModel<Charybdis> {

    public static final ModelLayerLocation CHARYBDIS_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "charybdis"), "charybdis");

    protected final ModelPart body;
    protected final ModelPart upperBody;
    protected final ModelPart upperMiddleBody;
    protected final ModelPart lowerMiddleBody;
    protected final ModelPart lowerBody;
    protected final ModelPart head;
    protected final ModelPart arms;
    protected final List<Triple<ModelPart, ModelPart, ModelPart>> armList;
    protected final List<ModelPart> upperFringes;
    protected final List<ModelPart> upperMiddleFringes;
    protected final List<ModelPart> lowerMiddleFringes;
    protected final List<ModelPart> lowerFringes;

    public CharybdisModel(ModelPart root) {
        super();
        this.head = root.getChild("head");
        // locate arms
        this.arms = root.getChild("arms");
        this.armList = GFModelUtil.getCharybdisArmsList(this.arms);
        // locate body parts
        this.body = root.getChild("body");
        this.upperBody = this.body.getChild("upper_body");
        this.upperMiddleBody = this.body.getChild("upper_middle_body");
        this.lowerMiddleBody = this.body.getChild("lower_middle_body");
        this.lowerBody = this.body.getChild("lower_body");
        // locate fringes
        upperFringes = GFModelUtil.getSingleCharybdisFringeList(this.upperBody);
        upperMiddleFringes = GFModelUtil.getTripleCharybdisFringeList(this.upperMiddleBody);
        lowerMiddleFringes = GFModelUtil.getTripleCharybdisFringeList(this.lowerMiddleBody);
        lowerFringes = GFModelUtil.getSingleCharybdisFringeList(this.lowerBody);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        final float r0 = 0.0F;
        final float r90 = (float) Math.toRadians(90);
        final float r180 = (float) Math.toRadians(180);
        final float r270 = (float) Math.toRadians(270);

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition upperBody = body.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -2.0F, -12.0F, 24.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -30.0F, 0.0F));
        GFModelUtil.createCharybdisUpperFringe(upperBody, "east_fringe", r0);
        GFModelUtil.createCharybdisUpperFringe(upperBody, "south_fringe", r90);
        GFModelUtil.createCharybdisUpperFringe(upperBody, "west_fringe", r180);
        GFModelUtil.createCharybdisUpperFringe(upperBody, "north_fringe", r270);

        PartDefinition upperMiddleBody = body.addOrReplaceChild("upper_middle_body", CubeListBuilder.create().texOffs(0, 57).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -26.0F, 0.0F));
        GFModelUtil.createCharybdisUpperMiddleFringe(upperMiddleBody, "east_fringe", r0);
        GFModelUtil.createCharybdisUpperMiddleFringe(upperMiddleBody, "south_fringe", r90);
        GFModelUtil.createCharybdisUpperMiddleFringe(upperMiddleBody, "west_fringe", r180);
        GFModelUtil.createCharybdisUpperMiddleFringe(upperMiddleBody, "north_fringe", r270);

        PartDefinition lowerMiddleBody = body.addOrReplaceChild("lower_middle_body", CubeListBuilder.create().texOffs(64, 57).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        GFModelUtil.createCharybdisLowerMiddleFringe(lowerMiddleBody, "east_fringe", r0);
        GFModelUtil.createCharybdisLowerMiddleFringe(lowerMiddleBody, "south_fringe", r90);
        GFModelUtil.createCharybdisLowerMiddleFringe(lowerMiddleBody, "west_fringe", r180);
        GFModelUtil.createCharybdisLowerMiddleFringe(lowerMiddleBody, "north_fringe", r270);

        PartDefinition lowerBody = body.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(104, 57).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        GFModelUtil.createCharybdisLowerFringe(lowerBody, "east_fringe", r0);
        GFModelUtil.createCharybdisLowerFringe(lowerBody, "south_fringe", r90);
        GFModelUtil.createCharybdisLowerFringe(lowerBody, "west_fringe", r180);
        GFModelUtil.createCharybdisLowerFringe(lowerBody, "north_fringe", r270);

        // head
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 30).addBox(-10.0F, -32.01F, -10.0F, 20.0F, 6.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(80, 40).addBox(-6.0F, -26.0F, -6.0F, 12.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        // teeth
        GFModelUtil.createCharybdisTeeth(head, "east_teeth", r0);
        GFModelUtil.createCharybdisTeeth(head, "south_teeth", r90);
        GFModelUtil.createCharybdisTeeth(head, "west_teeth", r180);
        GFModelUtil.createCharybdisTeeth(head, "north_teeth", r270);

        // arms
        PartDefinition arms = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        GFModelUtil.createCharybdisArms(arms, "east_arms", r0);
        GFModelUtil.createCharybdisArms(arms, "south_arms", r90);
        GFModelUtil.createCharybdisArms(arms, "west_arms", r180);
        GFModelUtil.createCharybdisArms(arms, "north_arms", r270);

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.arms);
    }

    @Override
    public void setupAnim(Charybdis entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch) {
        // animate arms
        setupArmAnim(entity, ageInTicks);
        // animate fringes, using slightly different rotations for each body part
        final float idleCos = Mth.cos(ageInTicks * 0.064F);
        float cosRot = idleCos * 0.44F;
        for (ModelPart part : upperFringes) {
            part.xRot = cosRot;
        }
        cosRot = idleCos * 0.62F;
        for (ModelPart part : upperMiddleFringes) {
            part.yRot = cosRot;
        }
        cosRot = idleCos * 0.58F;
        for (ModelPart part : lowerMiddleFringes) {
            part.yRot = cosRot;
        }
        cosRot = idleCos * 0.54F;
        for (ModelPart part : lowerFringes) {
            part.yRot = cosRot;
        }
    }

    public void setupArmAnim(Charybdis entity, float ageInTicks) {
        final float partialTick = ageInTicks - entity.tickCount;
        final float swirlingTime = entity.isSwirling() ? entity.getSwirlPercent(partialTick) : 0;
        final float throwingTime = entity.isThrowing() ? entity.getThrowPercent(partialTick) : 0;
        final float throwingTimeLeft = 1.0F - throwingTime;
        final float throwingZ = 0.9F - 2F * Math.abs(throwingTime - 0.5F);
        final float armAmplitude = 0.25F + swirlingTime * 0.6F;
        Triple<ModelPart, ModelPart, ModelPart> arm;
        for (int i = 0, n = armList.size(); i < n; i++) {
            arm = armList.get(i);
            float idleSwing = Mth.cos(ageInTicks * 0.08F + i * 1.62F) * armAmplitude;
            GFModelUtil.setupCharybdisArmAnim(arm.getLeft(), arm.getMiddle(), arm.getRight(), idleSwing, throwingTimeLeft, throwingZ);
        }
    }


}
