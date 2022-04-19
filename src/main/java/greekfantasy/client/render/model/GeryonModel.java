package greekfantasy.client.render.model;

import com.google.common.collect.ImmutableList;
import greekfantasy.entity.GeryonEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public class GeryonModel<T extends GeryonEntity> extends GiganteModel<T> {

    protected ModelRenderer bipedLeftHead;
    protected ModelRenderer bipedRightHead;

    public GeryonModel(float modelSize) {
        super(modelSize);

        bipedLeftHead = new ModelRenderer(this);
        bipedLeftHead.texOffs(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize - 1.0F);

        bipedRightHead = new ModelRenderer(this);
        bipedRightHead.texOffs(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F, modelSize - 1.0F);
    }

    @Override
    protected Iterable<ModelRenderer> headParts() {
        return ImmutableList.of(this.head, this.bipedLeftHead, this.bipedRightHead);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float rotationYaw, float rotationPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, rotationYaw, rotationPitch);
        // head rotations
        bipedLeftHead.copyFrom(head);
        bipedLeftHead.setPos(-8.0F, -11F, 3.0F);
        bipedRightHead.copyFrom(head);
        bipedRightHead.setPos(8.0F, -11F, 3.0F);
        // smash animation
        final float smashTime = entity.getSmashPercent(partialTick);
        final float summonTime = entity.getSummonPercent(partialTick);
        if (summonTime > 0) {
            final ModelRenderer arm = this.getArm(entity.getMainArm().getOpposite());
            arm.xRot = -1.5708F * summonTime;
            arm.yRot = 0.680678F * summonTime * (entity.getMainArm() == HandSide.RIGHT ? -1 : 1);
        } else if (smashTime > 0) {
            // when smashTime is >= downTrigger, arms will move downwards
            final float downTrigger = 0.9F;
            final float downMult = 12.5F;
            // maximum x and y angles
            final float smashAngleX = 2.02F;
            final float smashAngleY = 0.52F;
            rightArm.xRot = -downMult * Math.min((1.0F - downTrigger) * smashTime, -0.5F * (smashTime - 0.95F)) * smashAngleX;
            rightArm.yRot = -(Math.min(smashTime * 1.35F, 1.0F)) * smashAngleY;
            leftArm.xRot = rightArm.xRot;
            leftArm.yRot = -rightArm.yRot;
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
}
