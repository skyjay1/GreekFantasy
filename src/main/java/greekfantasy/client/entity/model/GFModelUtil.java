package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public static List<Triple<ModelPart, ModelPart, ModelPart>> getCharybdisArmsList(final ModelPart root) {
        return new ImmutableList.Builder<Triple<ModelPart, ModelPart, ModelPart>>()
                .addAll(getCharybdisArms(root.getChild("east_arms")))
                .addAll(getCharybdisArms(root.getChild("south_arms")))
                .addAll(getCharybdisArms(root.getChild("west_arms")))
                .addAll(getCharybdisArms(root.getChild("north_arms")))
                .build();
    }

    public static List<Triple<ModelPart, ModelPart, ModelPart>> getCharybdisArms(final ModelPart root) {
        return new ImmutableList.Builder<Triple<ModelPart, ModelPart, ModelPart>>()
                .add(getCharybdisArmParts(root.getChild("left_arm")))
                .add(getCharybdisArmParts(root.getChild("middle_arm")))
                .add(getCharybdisArmParts(root.getChild("right_arm")))
                .build();
    }

    public static Triple<ModelPart, ModelPart, ModelPart> getCharybdisArmParts(final ModelPart root) {
        ModelPart lowerArm = root.getChild("lower_arm");
        ModelPart middleArm = lowerArm.getChild("middle_arm");
        ModelPart upperArm = middleArm.getChild("upper_arm");
        return ImmutableTriple.of(lowerArm, middleArm, upperArm);
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
     * @param offsetX the x offset
     * @param offsetY the y offset
     * @param offsetZ the z offset
     * @param xRot the x rotation
     * @return the cerberus head part definition
     */
    public static PartDefinition addOrReplaceCerberusHead(final PartDefinition root, final String name,
                                                          final float offsetX, final float offsetY, final float offsetZ,
                                                          final float xRot, final float yRot, final float zRot) {
        PartDefinition head = root.addOrReplaceChild(name, CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F + xRot, -2.0F, -5.0F, 5.0F, 6.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(21, 0).addBox(-1.5F + xRot, 1.0F, -9.0F, 3.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 0).addBox(-2.5F + xRot, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 0).mirror().addBox(0.5F + xRot, -4.0F, -2.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE).mirror(false),
                PartPose.offset(offsetX, offsetY, offsetZ));
        PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(21, 6).addBox(-1.5F + xRot, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 3.0F, -5.0F, 0.0436F, 0.0F, 0.0F));
        return head;
    }

}
