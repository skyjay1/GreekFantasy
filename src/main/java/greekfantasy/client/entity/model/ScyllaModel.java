package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Charybdis;
import greekfantasy.entity.boss.Scylla;
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

import java.util.List;

public class ScyllaModel extends HumanoidModel<Scylla> {

    public static final ModelLayerLocation SCYLLA_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "scylla"), "scylla");
    public static final int LEG_COUNT = 8;


    protected final ModelPart monsterBody;
    protected final ModelPart torso;
    protected final ModelPart legs;
    protected final List<GFModelUtil.Tuple4<ModelPart>> legsList;

    public ScyllaModel(ModelPart root) {
        super(root);
        this.torso = body.getChild("torso");
        this.monsterBody = torso.getChild("monster_body");
        this.legs = body.getChild("legs");
        this.legsList = GFModelUtil.getScyllaLegs(this.legs, LEG_COUNT);
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, -3.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -3.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(48, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 54).mirror().addBox(-1.0F, -0.5F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(5.0F, -2.5F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 54).addBox(-3.0F, -0.5F, -2.0F, 3.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-4.0F, -2.5F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 9.0F, 0.0F));
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(50, 54).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -11.0F, -2.0F, -0.2182F, 0.0F, 0.0F));
        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(48, 38).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        torso.addOrReplaceChild("monster_body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 10.0F, 12.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 5.0F, 0.0F));
        
        PartDefinition legs = body.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));
        GFModelUtil.addOrReplaceScyllaLegs(legs, LEG_COUNT);
        
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Scylla entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // reset rotation points
        this.head.setPos(0.0F, -3.0F, 0.0F);
        this.hat.setPos(0.0F, -3.0F, 0.0F);
        this.body.setPos(0.0F, 9.0F, 0.0F);
        this.leftArm.setPos(5.0F, -2.5F, 0.0F);
        this.rightArm.setPos(-4.0F, -2.5F, 0.0F);
        // animate legs
        setupArmAnim(entity, ageInTicks);
    }

    public void setupArmAnim(Scylla entity, float ageInTicks) {
        GFModelUtil.Tuple4<ModelPart> leg;
        for (int i = 0, n = legsList.size(); i < n; i++) {
            leg = legsList.get(i);
            float idleSwing = 0.5F + 0.5F * Mth.cos(ageInTicks * 0.11F + (i) * 1.62F);
            GFModelUtil.setupScyllaLegAnim(leg, idleSwing);
        }
    }
}
