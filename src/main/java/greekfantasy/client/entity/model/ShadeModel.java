package greekfantasy.client.entity.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Shade;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ShadeModel extends HumanoidModel<Shade> {

    public static final ModelLayerLocation SHADE_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "shade"), "shade");;

    public ShadeModel(final ModelPart root) {
        super(root, RenderType::entityTranslucent);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(leftArm, rightArm, body, hat);
    }
}
