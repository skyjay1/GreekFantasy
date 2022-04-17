package greekfantasy.client.render.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;

public class NemeanLionHideModel extends BipedModel<LivingEntity> {

    public NemeanLionHideModel(final float modelSize) {
        super(modelSize);
        // hide unused parts
        this.hat.visible = false;
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.body.visible = true;
    }
}
