package greekfantasy.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.projectile.Projectile;

public class SpellModel<T extends Projectile> extends EntityModel<T> {

    private final boolean usePackedLight;
    private final ModelPart root;
    private final ModelPart outerCube;
    private final ModelPart innerCube;

    public SpellModel(ModelPart root) {
        this(root, true);
    }

    public SpellModel(ModelPart root, boolean usePackedLight) {
        this.root = root;
        this.usePackedLight = usePackedLight;
        this.outerCube = root.getChild("outer_cube");
        this.innerCube = root.getChild("inner_cube");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("outer_cube", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("inner_cube", CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 2.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        outerCube.xRot = (ageInTicks + 5) * 0.122F;
        innerCube.xRot = -outerCube.xRot;
        outerCube.zRot = ageInTicks * 0.14F;
        innerCube.zRot = -outerCube.zRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red,
                               float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, usePackedLight ? packedLight : 15728880, packedOverlay);
    }

}
