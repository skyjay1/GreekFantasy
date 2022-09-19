package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.entity.boss.Scylla;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

public final class GFModelUtil {

    /** Used in snake layer helper methods **/
    public static final String HOLDER = "holder_";

    /**
     * Adds or replaces an orthus head part with the given name
     * @param root the root part
     * @param name the head part name
     * @param offsetX the x offset
     * @return the orthus head part definition
     */
    public static PartDefinition addOrReplaceOrthusHead(final PartDefinition root, final String name, final float offsetX) {
        return root.addOrReplaceChild(name, CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 14).addBox(-3.0F, -5.0F, -3.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 14).addBox(1.0F, -5.0F, -3.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 10).addBox(-1.5F, -0.0156F, -7.0F, 3.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(offsetX, 12.5F, -5.0F));
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

    /**
     * Creates a list of all snake layer "lower_snake" parts
     * @param root the root layer (must contain "holder_x" parts which in turn contain one "lower_snake" part)
     * @param count the number of snake layers
     * @return the list of snake model parts
     */
    public static List<ModelPart> getSnakeModelParts(final ModelPart root, final int count) {
        final List<ModelPart> list = new ArrayList<>();
        for(int i = 1; i <= count; i++) {
            list.add(root.getChild(HOLDER + i).getChild("lower_snake"));
        }
        return list;
    }

    /**
     * Iterates over each snake model and adjusts rotation angles
     * @param list a list of snake models
     * @param ticks the entity age in ticks
     * @param baseAngleX the baseline angle to apply to each model in the list
     */
    public static void setupSnakeAnim(final List<ModelPart> list, final float ticks, final float baseAngleX) {
        int i = 0;
        for (final ModelPart m : list) {
            // update rotation angles
            m.xRot = baseAngleX + (float) Math.cos(ticks * 0.15 + i * 2.89F) * 0.08F;
            i++;
        }
    }

    /**
     * Adds or replaces a bull head at the given part definition
     * @param root the root part
     * @param name the name of the head part
     * @param hornTextureX the x offset of the horn textures
     * @param hornTextureY the y offset of the horn textures
     * @return the bull head part definition
     */
    public static PartDefinition addOrReplaceBullHead(final PartDefinition root, final String name, final int hornTextureX, final int hornTextureY) {
        PartDefinition head = root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 2.0F));

        PartDefinition lower_right_horn = head.addOrReplaceChild("lower_right_horn", CubeListBuilder.create().texOffs(hornTextureX, hornTextureY).addBox(-1.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -7.0F, -2.0F, 1.3963F, 1.0472F, 0.0F));
        PartDefinition middle_right_horn = lower_right_horn.addOrReplaceChild("middle_right_horn", CubeListBuilder.create().texOffs(hornTextureX, hornTextureY + 6).addBox(-0.51F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -4.0F, -2.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition top_right_horn = middle_right_horn.addOrReplaceChild("top_right_horn", CubeListBuilder.create().texOffs(hornTextureX, hornTextureY + 11).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition lower_left_horn = head.addOrReplaceChild("lower_left_horn", CubeListBuilder.create().texOffs(hornTextureX, hornTextureY).addBox(0.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -7.0F, -2.0F, 1.3963F, -1.0472F, 0.0F));
        PartDefinition middle_left_horn = lower_left_horn.addOrReplaceChild("middle_left_horn", CubeListBuilder.create().texOffs(hornTextureX, hornTextureY + 6).addBox(7.49F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-7.0F, -4.0F, -2.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition top_left_horn = middle_left_horn.addOrReplaceChild("top_left_horn", CubeListBuilder.create().texOffs(hornTextureX, hornTextureY + 11).addBox(7.5F, -3.0F, 0.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        return head;
    }

    /**
     * Creates a set of 8 teeth, rotated as a group around the center of the model
     * @param root the root part
     * @param name the name of the group
     * @param yRot the y rotation of the group
     * @return the group model part
     */
    public static PartDefinition createCharybdisTeeth(final PartDefinition root, final String name, final float yRot) {
        PartDefinition teeth = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -30.0F, 0.0F, 0.0F, yRot, 0.0F));
        teeth.addOrReplaceChild("tooth1", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.0F, 0.0F, 8.5F, -0.5672F, -0.1309F, -2.0944F));
        teeth.addOrReplaceChild("tooth2", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.0F, 0.0F, 6.0F, -0.4363F, -0.0873F, -2.0944F));
        teeth.addOrReplaceChild("tooth3", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.0F, 0.0F, 3.5F, -0.2618F, 0.2182F, -2.0944F));
        teeth.addOrReplaceChild("tooth4", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.0F, 0.0F, 0.5F, -0.0436F, -0.1309F, -2.0944F));
        teeth.addOrReplaceChild("tooth5", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.0F, 0.0F, -2.5F, 0.2618F, 0.1745F, -2.0944F));
        teeth.addOrReplaceChild("tooth6", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.0F, 0.0F, -5.0F, 0.4363F, 0.1309F, -2.0944F));
        teeth.addOrReplaceChild("tooth7", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.0F, 0.0F, -7.5F, 0.5672F, -0.0873F, -2.0944F));
        teeth.addOrReplaceChild("tooth8", CubeListBuilder.create().texOffs(61, 31).addBox(0.0F, -0.5F, 0.0F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-9.0F, -0.5F, -10.0F, 0.7854F, 0.0873F, -2.0944F));
        return teeth;
    }

    /**
     * Creates a set of 3 arms, rotated as a group around the center of the model
     * @param root the root part
     * @param name the name of the group
     * @param yRot the y rotation of the group
     * @return the group model part
     */
    public static PartDefinition createCharybdisArms(final PartDefinition root, final String name, final float yRot) {
        PartDefinition arms = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, yRot, 0.0F));

        PartDefinition holder_0 = arms.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-12.0F, -1.0F, -8.0F, 0.3491F, 0.0F, -1.2217F));
        PartDefinition lower_arm = holder_0.addOrReplaceChild("lower_arm", CubeListBuilder.create().texOffs(73, 0).addBox(-1.0F, -5.0F, -1.5F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition middle_arm = lower_arm.addOrReplaceChild("middle_arm", CubeListBuilder.create().texOffs(86, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.5F, -5.0F, 0.0F));
        PartDefinition upper_arm = middle_arm.addOrReplaceChild("upper_arm", CubeListBuilder.create().texOffs(95, 0).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(73, 10).addBox(-1.0F, -8.0F, -2.5F, 1.0F, 8.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition holder_1 = arms.addOrReplaceChild("middle_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-12.0F, -1.0F, 0.0F, 0.0F, 0.0F, -1.2217F));
        PartDefinition lower_arm2 = holder_1.addOrReplaceChild("lower_arm", CubeListBuilder.create().texOffs(73, 0).addBox(-1.0F, -5.0F, -1.5F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition middle_arm2 = lower_arm2.addOrReplaceChild("middle_arm", CubeListBuilder.create().texOffs(86, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.5F, -5.0F, 0.0F));
        PartDefinition upper_arm2 = middle_arm2.addOrReplaceChild("upper_arm", CubeListBuilder.create().texOffs(95, 0).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(73, 10).addBox(-1.0F, -8.0F, -2.5F, 1.0F, 8.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition holder_2 = arms.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-12.0F, -1.0F, 8.0F, -0.3491F, 0.0F, -1.2217F));
        PartDefinition lower_arm3 = holder_2.addOrReplaceChild("lower_arm", CubeListBuilder.create().texOffs(73, 0).addBox(-1.0F, -5.0F, -1.5F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition middle_arm3 = lower_arm3.addOrReplaceChild("middle_arm", CubeListBuilder.create().texOffs(86, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.5F, -5.0F, 0.0F));
        PartDefinition upper_arm3 = middle_arm3.addOrReplaceChild("upper_arm", CubeListBuilder.create().texOffs(95, 0).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(73, 10).addBox(-1.0F, -8.0F, -2.5F, 1.0F, 8.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 0.0F));

        return arms;
    }

    public static List<Tuple3<ModelPart>> getCharybdisArmsList(final ModelPart root) {
        return new ImmutableList.Builder<Tuple3<ModelPart>>()
                .addAll(getCharybdisArms(root.getChild("east_arms")))
                .addAll(getCharybdisArms(root.getChild("south_arms")))
                .addAll(getCharybdisArms(root.getChild("west_arms")))
                .addAll(getCharybdisArms(root.getChild("north_arms")))
                .build();
    }

    public static List<Tuple3<ModelPart>> getCharybdisArms(final ModelPart root) {
        return new ImmutableList.Builder<Tuple3<ModelPart>>()
                .add(getCharybdisArmParts(root.getChild("left_arm")))
                .add(getCharybdisArmParts(root.getChild("middle_arm")))
                .add(getCharybdisArmParts(root.getChild("right_arm")))
                .build();
    }

    public static Tuple3<ModelPart> getCharybdisArmParts(final ModelPart root) {
        ModelPart lowerArm = root.getChild("lower_arm");
        ModelPart middleArm = lowerArm.getChild("middle_arm");
        ModelPart upperArm = middleArm.getChild("upper_arm");
        return new Tuple3<>(lowerArm, middleArm, upperArm);
    }


    /**
     * Creates a group with an upper fringe, rotated around the center of the model
     * @param root the root part
     * @param name the name of the group
     * @param yRot the y rotation of the group
     * @return the group model part
     */
    public static PartDefinition createCharybdisUpperFringe(final PartDefinition root, final String name, final float yRot) {
        PartDefinition holder = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, yRot, 0.0F));
        holder.addOrReplaceChild("fringe", CubeListBuilder.create().texOffs(0, 84).addBox(-12.0F, 0.0F, -4.0F, 24.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -12.0F, -0.0436F, 0.0F, 0.0F));
        return holder;
    }

    /**
     * @param root the root part
     * @return the east, south, west, and north fringes
     */
    public static List<ModelPart> getSingleCharybdisFringeList(final ModelPart root) {
        return new ImmutableList.Builder<ModelPart>()
                .add(getSingleCharybdisFringe(root.getChild("east_fringe")))
                .add(getSingleCharybdisFringe(root.getChild("south_fringe")))
                .add(getSingleCharybdisFringe(root.getChild("west_fringe")))
                .add(getSingleCharybdisFringe(root.getChild("north_fringe")))
                .build();
    }

    /**
     * @param root the root part
     * @return a single fringe model
     */
    public static ModelPart getSingleCharybdisFringe(final ModelPart root) {
        return root.getChild("fringe");
    }

    /**
     * @param root the root part
     * @return the east, south, west, and north fringes
     */
    public static List<ModelPart> getTripleCharybdisFringeList(final ModelPart root) {
        return new ImmutableList.Builder<ModelPart>()
                .addAll(getTripleCharybdisFringe(root.getChild("east_fringe")))
                .addAll(getTripleCharybdisFringe(root.getChild("south_fringe")))
                .addAll(getTripleCharybdisFringe(root.getChild("west_fringe")))
                .addAll(getTripleCharybdisFringe(root.getChild("north_fringe")))
                .build();
    }

    /**
     * @param root the root part
     * @return a set of left, middle, and right fringe models
     */
    public static List<ModelPart> getTripleCharybdisFringe(final ModelPart root) {
        return ImmutableList.of(
            root.getChild("left_fringe"),
            root.getChild("middle_fringe"),
            root.getChild("right_fringe")
        );
    }

    /**
     * Creates a group with upper middle fringes, rotated around the center of the model
     * @param root the root part
     * @param name the name of the group
     * @param yRot the y rotation of the group
     * @return the group model part
     */
    public static PartDefinition createCharybdisUpperMiddleFringe(final PartDefinition root, final String name, final float yRot) {
        PartDefinition holder = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, yRot, 0.0F));
        holder.addOrReplaceChild("left_fringe", CubeListBuilder.create().texOffs(0, 89).addBox(-1.0F, -6.0F, -3.0F, 1.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.0F, -8.0F));
        holder.addOrReplaceChild("middle_fringe", CubeListBuilder.create().texOffs(0, 89).addBox(-1.0F, -6.0F, -4.0F, 1.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -8.0F));
        holder.addOrReplaceChild("right_fringe", CubeListBuilder.create().texOffs(0, 89).addBox(-1.0F, -6.0F, -3.0F, 1.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.0F, -8.0F));
        return holder;
    }

    /**
     * Creates a group with lower middle fringes, rotated around the center of the model
     * @param root the root part
     * @param name the name of the group
     * @param yRot the y rotation of the group
     * @return the group model part
     */
    public static PartDefinition createCharybdisLowerMiddleFringe(final PartDefinition root, final String name, final float yRot) {
        PartDefinition holder = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, yRot, 0.0F));
        holder.addOrReplaceChild("left_fringe", CubeListBuilder.create().texOffs(9, 89).addBox(-1.0F, -4.0F, -3.0F, 1.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, 0.0F, -5.0F, 0.0F, -0.0436F, 0.0F));
        holder.addOrReplaceChild("middle_fringe", CubeListBuilder.create().texOffs(9, 89).addBox(-1.0F, -4.0F, -4.0F, 1.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.0F, -0.0436F, 0.0F));
        holder.addOrReplaceChild("right_fringe", CubeListBuilder.create().texOffs(9, 89).addBox(-1.0F, -4.0F, -3.0F, 1.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 0.0F, -5.0F, 0.0F, -0.0436F, 0.0F));


        return holder;
    }

    /**
     * Creates a group with a lower fringe, rotated around the center of the model
     * @param root the root part
     * @param name the name of the group
     * @param yRot the y rotation of the group
     * @return the group model part
     */
    public static PartDefinition createCharybdisLowerFringe(final PartDefinition root, final String name, final float yRot) {
        PartDefinition holder = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, yRot, 0.0F));
        holder.addOrReplaceChild("fringe", CubeListBuilder.create().texOffs(9, 89).addBox(-1.0F, -4.0F, -3.0F, 1.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -4.0F, -3.0F));
        return holder;
    }

    /**
     * Animates a single charybdis arm
     * @param lowerArm the lower arm part
     * @param middleArm the middle arm part
     * @param upperArm the upper arm part
     * @param idleSwingCos the idle swing rotation
     * @param throwingTimeLeft the amount of throwing time remaining
     * @param throwingZ the amount of z rotation to apply
     */
    public static void setupCharybdisArmAnim(ModelPart lowerArm, ModelPart middleArm, ModelPart upperArm,
                                              float idleSwingCos, float throwingTimeLeft, float throwingZ) {
        lowerArm.xRot = (idleSwingCos * 0.14F) * throwingTimeLeft;
        lowerArm.zRot = idleSwingCos * 0.09F * throwingTimeLeft + 1.15F * throwingZ;
        middleArm.zRot = idleSwingCos * 0.14F * throwingTimeLeft + 0.26F * throwingZ;
        upperArm.zRot = idleSwingCos * 0.14F * throwingTimeLeft + 0.36F * throwingZ;
    }

    /**
     * Creates a part definition for a cerberus head with the given offsets
     * @param root the root part
     * @param name the part name
     * @param x the x offset
     * @param y the y offset
     * @param z the z offset
     * @param xRot the x rotation
     * @return the cerberus head part definition
     */
    public static PartDefinition addOrReplaceCerberusHead(final PartDefinition root, final String name,
                                                          final float x, final float y, final float z,
                                                          final float xRot) {
        PartDefinition head = root.addOrReplaceChild(name, CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F + xRot, -2.0F, -5.0F, 5.0F, 6.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(21, 0).addBox(-1.5F + xRot, 1.0F, -9.0F, 3.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 0).addBox(-2.5F + xRot, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 0).mirror().addBox(0.5F + xRot, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE).mirror(false),
                PartPose.offset(x, y, z));
        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(21, 6).addBox(-1.5F + xRot, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 3.0F, -5.0F, 0.0436F, 0.0F, 0.0F));
        return head;
    }

    /**
     * Creates a part definition for ram horns, as used by cerastes and golden ram
     * @param root the root part
     * @param name the part name
     * @param x the x offset
     * @param y the y offset
     * @param z the z offset
     * @param isLeft true if the horn is angled left
     * @return the horn part definition
     */
    public static PartDefinition addOrReplaceRamHorn(final PartDefinition root, final String name, final float x, final float y, final float z, final boolean isLeft) {
        PartDefinition horn;
        if(isLeft) {
            horn = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(x, y, z, -0.2618F, 0.0F, 0.0F));
            PartDefinition lowerLeftHorn = horn.addOrReplaceChild("lower_left_horn", CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition lowerMiddleLeftHorn = lowerLeftHorn.addOrReplaceChild("lower_middle_left_horn", CubeListBuilder.create().texOffs(58, 6).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.7854F, 0.1745F, 0.0F));
            PartDefinition middleLeftHorn = lowerMiddleLeftHorn.addOrReplaceChild("middle_left_horn", CubeListBuilder.create().texOffs(58, 13).addBox(0.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, 0.1745F, 0.0F));
            PartDefinition upperMiddleLeftHorn = middleLeftHorn.addOrReplaceChild("upper_middle_left_horn", CubeListBuilder.create().texOffs(58, 18).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, 0.1745F, 0.0F));
            PartDefinition upperLeftHorn = upperMiddleLeftHorn.addOrReplaceChild("upper_left_horn", CubeListBuilder.create().texOffs(58, 22).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -1.0472F, 0.1745F, 0.0F));
        } else {
            horn = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(x, y, z, -0.2618F, 0.0F, 0.0F));
            PartDefinition lowerRightHorn = horn.addOrReplaceChild("lower_right_horn", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition lowerMiddleRightHorn = lowerRightHorn.addOrReplaceChild("lower_middle_right_horn", CubeListBuilder.create().texOffs(58, 6).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.7854F, -0.1745F, 0.0F));
            PartDefinition middleRightHorn = lowerMiddleRightHorn.addOrReplaceChild("middle_right_horn", CubeListBuilder.create().texOffs(58, 13).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, -0.1745F, 0.0F));
            PartDefinition upperMiddleRightHorn = middleRightHorn.addOrReplaceChild("upper_middle_right_horn", CubeListBuilder.create().texOffs(58, 18).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -1.2217F, -0.1745F, 0.0F));
            PartDefinition upperRightHorn = upperMiddleRightHorn.addOrReplaceChild("upper_right_horn", CubeListBuilder.create().texOffs(58, 22).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -1.0472F, -0.1745F, 0.0F));
        }
        return horn;
    }

    /**
     * Constructs the given number of scylla legs
     * @param root the root part
     * @param count the number of legs
     * @return the root part
     */
    public static PartDefinition addOrReplaceScyllaLegs(final PartDefinition root, final int count) {
        final float arc = ((float)Math.PI * 2.0F) / Math.max(1.0F, count);
        for(int index = 0; index < count; index++) {
            final float angY = (arc * index);
            final PartDefinition holder = root.addOrReplaceChild(HOLDER + String.valueOf((int)index), CubeListBuilder.create(),
                    PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, angY, 0.0F));
            final PartDefinition leg = addOrReplaceScyllaLeg(holder);
        }
        return root;
    }

    /**
     * Creates a single scylla leg with all of its parts
     * @param root the root part
     * @return the leg part definition
     */
    public static PartDefinition addOrReplaceScyllaLeg(final PartDefinition root) {
        // leg parts
        PartDefinition lowerLeg = root.addOrReplaceChild("lower_leg", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition lowerMiddleLeg = lowerLeg.addOrReplaceChild("lower_middle_leg", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, -0.7854F, 0.0F, 0.0F));
        PartDefinition upperMiddleLeg = lowerMiddleLeg.addOrReplaceChild("upper_middle_leg", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, -0.5236F, 0.0F, 0.0F));
        PartDefinition upperLeg = upperMiddleLeg.addOrReplaceChild("upper_leg", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, 0.0F, -6.0F, 4.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.0F, -6.0F, 0.3491F, 0.0F, 0.0F));
        // head
        PartDefinition head = upperLeg.addOrReplaceChild("leg_head", CubeListBuilder.create().texOffs(0, 38).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 5.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(0, 51).addBox(-2.5F, -1.0F, -6.0F, 5.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, -6.0F, 0.9599F, 0.0F, 0.0F));
        // horns
        head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(0, 58).addBox(-1.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -2.0F, 0.0F, 0.5236F, 0.0F, 0.5236F));
        head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(0, 58).addBox(0.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -2.0F, 0.0F, 0.5236F, 0.0F, -0.5236F));
        return root;
    }

    /**
     * Locates the given number of scylla leg holders and constructs tuples for them.
     * @param root the root part
     * @param count the number of legs
     * @return a list of tuples containing legs
     * @see #getScyllaLeg(ModelPart)
     */
    public static List<Tuple5<ModelPart>> getScyllaLegs(final ModelPart root, final int count) {
        final List<Tuple5<ModelPart>> list = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            list.add(getScyllaLeg(root.getChild(HOLDER + i)));
        }
        return list;
    }

    /**
     * @param holder the model part that contains the leg parts
     * @return The model parts that make up the scylla leg
     */
    public static Tuple5<ModelPart> getScyllaLeg(final ModelPart holder) {
        ModelPart lowerLeg = holder.getChild("lower_leg");
        ModelPart lowerMiddleLeg = lowerLeg.getChild("lower_middle_leg");
        ModelPart upperMiddleLeg = lowerMiddleLeg.getChild("upper_middle_leg");
        ModelPart upperLeg = upperMiddleLeg.getChild("upper_leg");
        ModelPart head = upperLeg.getChild("leg_head");
        return new Tuple5<>(lowerLeg, lowerMiddleLeg, upperMiddleLeg, upperLeg, head);
    }

    /**
     * Animates the given scylla leg
     * @param leg the leg tuple
     * @param idleSwingCos the idle angle multiplier, from 0 to 1
     */
    public static void setupScyllaLegAnim(final Tuple5<ModelPart> leg, final float idleSwingCos) {
        leg.left.xRot = -idleSwingCos * 0.18F;
        leg.leftMiddle.xRot = -0.785398F + idleSwingCos * 0.18F;
        leg.middle.xRot = -0.523599F + idleSwingCos * 0.20F;
        leg.rightMiddle.xRot = 0.349066F + idleSwingCos * 0.22F;
        leg.right.xRot = 0.959931F - idleSwingCos * 0.4F;
    }

    @Immutable
    public static class Tuple3<T> {
        public final T left;
        public final T middle;
        public final T right;

        public Tuple3(T left, T middle, T right) {
            this.left = left;
            this.middle = middle;
            this.right = right;
        }
    }

    @Immutable
    public static class Tuple4<T> {
        public final T left;
        public final T leftMiddle;
        public final T rightMiddle;
        public final T right;

        public Tuple4(T left, T leftMiddle, T rightMiddle, T right) {
            this.left = left;
            this.leftMiddle = leftMiddle;
            this.rightMiddle = rightMiddle;
            this.right = right;
        }
    }

    @Immutable
    public static class Tuple5<T> {
        public final T left;
        public final T leftMiddle;
        public final T middle;
        public final T rightMiddle;
        public final T right;

        public Tuple5(T left, T leftMiddle, T middle, T rightMiddle, T right) {
            this.left = left;
            this.leftMiddle = leftMiddle;
            this.middle = middle;
            this.rightMiddle = rightMiddle;
            this.right = right;
        }
    }
}
