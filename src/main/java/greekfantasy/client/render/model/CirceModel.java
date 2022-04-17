package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import greekfantasy.entity.CirceEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CirceModel<T extends CirceEntity> extends BipedModel<T> {

    private final ModelRenderer chest;

    public CirceModel(float modelSize) {
        super(modelSize, 0.0F, 64, 64);
        // left arm
        leftArm = new ModelRenderer(this, 32, 48);
        leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
        leftArm.setPos(5.0F, 2.5F, 0.0F);
        leftArm.mirror = true;
        // right arm
        rightArm = new ModelRenderer(this, 40, 16);
        rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
        rightArm.setPos(-5.0F, 2.5F, 0.0F);
        // chest
        chest = new ModelRenderer(this);
        chest.setPos(0.0F, 1.0F, -2.0F);
        chest.xRot = -0.2182F;
        chest.texOffs(0, 32).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSize);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.chest));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

}
