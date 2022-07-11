package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.BabySpider;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class BabySpiderModel<T extends BabySpider> extends SpiderModel<T> {
	public static final ModelLayerLocation BABY_SPIDER_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "baby_spider"), "baby_spider");

	public BabySpiderModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 21.0F, -3.0F));
		PartDefinition body0 = partdefinition.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, 2.0F, -3.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 15.0F, 0.0F));
		PartDefinition body1 = partdefinition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, CubeDeformation.NONE), PartPose.ZERO);
		PartDefinition right_hind_leg = partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 21.0F, 2.0F, 0.0F, 0.7854F, -0.7854F));
		PartDefinition left_hind_leg = partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 21.0F, 2.0F, 0.0F, -0.7854F, 0.7854F));
		PartDefinition right_middle_hind_leg = partdefinition.addOrReplaceChild("right_middle_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 21.0F, 1.0F, 0.0F, 0.2618F, -0.6109F));
		PartDefinition left_middle_hind_leg = partdefinition.addOrReplaceChild("left_middle_hind_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 21.0F, 1.0F, 0.0F, -0.2618F, 0.6109F));
		PartDefinition right_middle_front_leg = partdefinition.addOrReplaceChild("right_middle_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 21.0F, 0.0F, 0.0F, -0.2618F, -0.6109F));
		PartDefinition left_middle_front_leg = partdefinition.addOrReplaceChild("left_middle_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 21.0F, 0.0F, 0.0F, 0.2618F, 0.6109F));
		PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-5.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 21.0F, -1.0F, 0.0F, -0.7854F, -0.7854F));
		PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, 0.0F, -1.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 21.0F, -1.0F, 0.0F, 0.7854F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
}