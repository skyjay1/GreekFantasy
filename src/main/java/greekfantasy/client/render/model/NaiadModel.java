package greekfantasy.client.render.model;

import greekfantasy.entity.NaiadEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class NaiadModel<T extends NaiadEntity> extends NymphModel<T> {

    public NaiadModel(final float modelSize) {
        super(modelSize);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                          float headPitch) {
        // set arm poses
        final ItemStack item = entityIn.getItemInHand(Hand.MAIN_HAND);
        if (item.getItem() instanceof net.minecraft.item.TridentItem && entityIn.isAggressive()) {
            if (entityIn.getMainArm() == HandSide.RIGHT) {
                this.rightArmPose = BipedModel.ArmPose.THROW_SPEAR;
            } else {
                this.leftArmPose = BipedModel.ArmPose.THROW_SPEAR;
            }
        } else {
            this.rightArmPose = this.leftArmPose = BipedModel.ArmPose.EMPTY;
        }
        // super method
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // correct rotation points
        head.setPos(0.0F, 0.0F, 1.5F);
        leftArm.setPos(3.0F, 2.0F, 1.5F);
        rightArm.setPos(-3.0F, 2.0F, 1.5F);
        leftLeg.setPos(1.5F, 12.0F, 1.5F);
        rightLeg.setPos(-1.5F, 12.0F, 1.5F);
        // swimming animation
        final float swimming = entityIn.animateSwimmingPercent();
        if (swimming > 0.0F) {
            // animate legs
            final float ticks = ageInTicks + entityIn.getId();
            final float legAngle = 0.28F;
            final float legSpeed = 0.18F;
            final float cosTicks = MathHelper.cos(ticks * legSpeed) * legAngle;
            final float sinTicks = MathHelper.cos(ticks * legSpeed + (float) Math.PI) * legAngle;
            rightLeg.xRot = cosTicks * swimming;
            leftLeg.xRot = sinTicks * swimming;
            // animate arms
            if (!entityIn.swinging && this.rightArmPose != ArmPose.THROW_SPEAR && this.leftArmPose != ArmPose.THROW_SPEAR) {
                final float cosArms = MathHelper.cos(ticks * legSpeed * 0.5F) * 0.5F + 0.5F;
                final float minX = -0.09F;
                final float maxX = 0.18F;
                final float minZ = 0.52F;
                final float maxZ = 1.08F;
                rightArm.xRot = (-minX - cosArms * (maxX - minX)) * swimming;
                rightArm.zRot = (minZ + cosArms * (maxZ - minZ)) * swimming;
                leftArm.xRot = (-minX - cosArms * (maxX - minX)) * swimming;
                leftArm.zRot = (-minZ - cosArms * (maxZ - minZ)) * swimming;
            }
        }
    }
}