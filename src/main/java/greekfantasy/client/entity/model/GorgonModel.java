package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Gorgon;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GorgonModel<T extends Gorgon> extends DrakainaModel<T> {

    public static final ModelLayerLocation GORGON_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "gorgon"), "gorgon");

    protected static final int LOWER_HAIR_COUNT = 12;
    protected static final int MIDDLE_HAIR_COUNT = 8;
    protected static final int UPPER_HAIR_COUNT = 6;

    protected final ModelPart hair;
    protected final ModelPart lowerHair;
    protected final ModelPart middleHair;
    protected final ModelPart upperHair;
    protected final List<ModelPart> lowerSnakes;
    protected final List<ModelPart> middleSnakes;
    protected final List<ModelPart> upperSnakes;

    public GorgonModel(ModelPart root) {
        super(root);
        this.hair = this.head.getChild("hair");
        this.lowerHair = hair.getChild("lower_hair");
        this.middleHair = hair.getChild("middle_hair");
        this.upperHair = hair.getChild("upper_hair");
        // locate snake model parts
        this.lowerSnakes = GFModelUtil.getSnakeModelParts(lowerHair, LOWER_HAIR_COUNT);
        this.middleSnakes = GFModelUtil.getSnakeModelParts(middleHair, MIDDLE_HAIR_COUNT);
        this.upperSnakes = GFModelUtil.getSnakeModelParts(upperHair, UPPER_HAIR_COUNT);
        // hide hat
        this.hat.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = DrakainaModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition chest = partdefinition.getChild("body").addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -4.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -2.0F));

        PartDefinition hair = head.addOrReplaceChild("hair", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition lowerHair = hair.addOrReplaceChild("lower_hair", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition middleHair = hair.addOrReplaceChild("middle_hair", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition upperHair = hair.addOrReplaceChild("upper_hair", CubeListBuilder.create(), PartPose.ZERO);

        GFModelUtil.createSnakeLayers(lowerHair, 3.8F, (float) Math.PI / (LOWER_HAIR_COUNT * 0.5F), CubeDeformation.NONE, 46, 52);
        GFModelUtil.createSnakeLayers(middleHair, 2.25F, (float) Math.PI / (MIDDLE_HAIR_COUNT * 0.5F), CubeDeformation.NONE, 46, 52);
        GFModelUtil.createSnakeLayers(upperHair, 1.25F, (float) Math.PI / (UPPER_HAIR_COUNT * 0.5F), CubeDeformation.NONE, 46, 52);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupSnakeAnim(ageInTicks);
    }

    public void setupSnakeAnim(final float ageInTicks) {
        // set up animations for each hair part
        GFModelUtil.setupSnakeAnim(lowerSnakes, ageInTicks, 1.7F);
        GFModelUtil.setupSnakeAnim(middleSnakes, ageInTicks, 1.03F);
        GFModelUtil.setupSnakeAnim(upperSnakes, ageInTicks, 0.82F);
    }
}