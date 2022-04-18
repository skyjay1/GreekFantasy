package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import greekfantasy.entity.misc.PalladiumEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PalladiumModel<T extends PalladiumEntity> extends BipedModel<T> {

    protected ModelRenderer bodyChest;

    public PalladiumModel(final float modelSizeIn) {
        super(modelSizeIn, 0.0F, 64, 64);
        // arms
        this.leftArm = new ModelRenderer(this, 32, 48);
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSizeIn);
        this.leftArm.setPos(5.0F, 2.5F, 0.0F);
        this.leftArm.mirror = true;
        this.rightArm = new ModelRenderer(this, 40, 16);
        this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSizeIn);
        this.rightArm.setPos(-5.0F, 2.5F, 0.0F);
        // body chest
        this.bodyChest = new ModelRenderer(this);
        this.bodyChest.setPos(0.0F, 1.0F, -2.0F);
        this.bodyChest.xRot = -0.2182F;
        this.bodyChest.texOffs(19, 20).addBox(-4.01F, 0.0F, 0.0F, 8.0F, 4.0F, 1.0F, modelSizeIn);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.bodyChest));
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.young = false;
        this.head.xRot = -0.174533F;
        // arm rotations
        this.rightArm.xRot = -1.675516F;
        this.rightArm.zRot = 0.05236F;
        this.leftArm.xRot = 0.0F;
        this.leftArm.zRot = -this.rightArm.yRot;
        // leg rotations
        this.rightLeg.xRot = -0.174533F;
        this.leftLeg.xRot = -this.rightLeg.xRot;
        // body chest
        this.bodyChest.xRot = -0.2182F;
        this.bodyChest.yRot = 0.0F;
        this.bodyChest.zRot = 0.0F;
    }
}