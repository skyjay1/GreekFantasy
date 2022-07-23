package greekfantasy.client.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;

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
}
