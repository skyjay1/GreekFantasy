package greekfantasy.client.entity.model;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Siren;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public class SirenModel<T extends Siren> extends TritonModel<T> {

    public static final ModelLayerLocation SIREN_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(GreekFantasy.MODID, "siren"), "siren");

    public SirenModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return TritonModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // singing animation
        if (entity.isCharming()) {
            head.xRot = -0.24F;
        }
    }
}