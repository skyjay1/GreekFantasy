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

import java.util.ArrayList;
import java.util.List;

public class GorgonModel<T extends Gorgon> extends DrakainaModel<T> {

    public static final ModelLayerLocation GORGON_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "gorgon"), "gorgon");

    protected static final String HOLDER = "holder_";

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
        // locate children for lower hair
        this.lowerSnakes = new ArrayList<>();
        for(int i = 1; i <= LOWER_HAIR_COUNT; i++) {
            this.lowerSnakes.add(lowerHair.getChild(HOLDER + i).getChild("lower_snake"));
        }
        // locate children for middle hair
        this.middleSnakes = new ArrayList<>();
        for(int i = 1; i <= MIDDLE_HAIR_COUNT; i++) {
            this.middleSnakes.add(middleHair.getChild(HOLDER + i).getChild("lower_snake"));
        }
        // locate children for upper hair
        this.upperSnakes = new ArrayList<>();
        for(int i = 1; i <= UPPER_HAIR_COUNT; i++) {
            upperSnakes.add(upperHair.getChild(HOLDER + i).getChild("lower_snake"));
        }
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

        createSnakeLayers(lowerHair, 3.8F, (float) Math.PI / (LOWER_HAIR_COUNT * 0.5F), CubeDeformation.NONE, 46, 52);
        createSnakeLayers(middleHair, 2.25F, (float) Math.PI / (MIDDLE_HAIR_COUNT * 0.5F), CubeDeformation.NONE, 46, 52);
        createSnakeLayers(upperHair, 1.25F, (float) Math.PI / (UPPER_HAIR_COUNT * 0.5F), CubeDeformation.NONE, 46, 52);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    /**
     * Creates snake model definitions in a circle with the given size
     *
     * @param root            the root part
     * @param radius          the radius of the circle
     * @param deltaAngle      the angle separation between child models
     * @param cubeDeformation the cube deformation
     * @param textureX the texture x offset
     * @param textureY the texture y offset
     * @return the root part with its new children
     */
    public static PartDefinition createSnakeLayers(final PartDefinition root, final float radius,
                                                   final float deltaAngle, final CubeDeformation cubeDeformation,
                                                   final int textureX, final int textureY) {
        for (double angle = 0.0D, count = 1.0D, tau = Math.PI * 2.0D; angle < tau; angle += deltaAngle) {
            final float ptX = (float) (Math.cos(angle) * radius);
            final float ptZ = (float) (Math.sin(angle) * radius);
            final float angY = (float) (angle - (deltaAngle * 2 * count));
            final PartDefinition holder = root.addOrReplaceChild(HOLDER + String.valueOf((int)count), CubeListBuilder.create(),
                    PartPose.offset(ptX, -0.5F, ptZ));
            final PartDefinition snake = createSnakeLayer(holder, cubeDeformation, 0.0F, 0.0F, 0.0F, 0.0F, angY, 0.0F, textureX, textureY);
            count++;
        }
        return root;
    }

    /**
     *
     * @param root the root part
     * @param cubeDeformation the cube deformation
     * @param offsetX the part pose x offset
     * @param offsetY the part pose y offset
     * @param offsetZ the part pose z offset
     * @param angleX the initial x rotation
     * @param angleY the initial y rotation
     * @param angleZ the initial z rotation
     * @param textureX the texture x offset
     * @param textureY the texture y offset
     * @return
     */
    public static PartDefinition createSnakeLayer(final PartDefinition root, final CubeDeformation cubeDeformation,
                                                   final float offsetX, final float offsetY, final float offsetZ,
                                                   final float angleX, final float angleY, final float angleZ,
                                                   final int textureX, final int textureY) {
        // create part definitions for the snake and add it to the given root
        PartDefinition lowerSnake = root.addOrReplaceChild("lower_snake", CubeListBuilder.create()
                        .texOffs(textureX, textureY)
                        .addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, cubeDeformation),
                PartPose.offsetAndRotation(offsetX, offsetY, offsetZ, 0.5236F + angleX, angleY, angleZ));
        PartDefinition middleSnake = lowerSnake.addOrReplaceChild("middle_snake", CubeListBuilder.create()
                        .texOffs(textureX, textureY + 4)
                        .addBox(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.5236F, 0.0F, 0.0F));
        PartDefinition upperSnake = middleSnake.addOrReplaceChild("upper_snake", CubeListBuilder.create()
                        .texOffs(textureX, textureY + 8)
                        .addBox(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -3.0F, -0.5F, 0.5236F, 0.0F, 0.0F));

        return lowerSnake;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupSnakeAnim(ageInTicks);
    }

    public void setupSnakeAnim(final float ageInTicks) {
        // set up animations for each hair part
        setupSnakeAnim(lowerSnakes, ageInTicks, 1.7F);
        setupSnakeAnim(middleSnakes, ageInTicks, 1.03F);
        setupSnakeAnim(upperSnakes, ageInTicks, 0.82F);
    }

    public static void setupSnakeAnim(final List<ModelPart> list, final float ticks, final float baseAngleX) {
        int i = 0;
        for (final ModelPart m : list) {
            // update rotation angles
            m.xRot = baseAngleX + (float) Math.cos(ticks * 0.15 + i * 2.89F) * 0.08F;
            i++;
        }
    }

}