package greekfantasy.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class WaterAnimalMoveControl extends MoveControl {
    protected final PathfinderMob waterAnimal;

    public WaterAnimalMoveControl(PathfinderMob entity) {
        super(entity);
        this.waterAnimal = entity;
    }

    public void tick() {
        LivingEntity livingentity = this.waterAnimal.getTarget();
        if (this.waterAnimal.isInWater()) {
            if (livingentity != null && livingentity.getY() > this.waterAnimal.getY()) {
                this.waterAnimal.setDeltaMovement(this.waterAnimal.getDeltaMovement().add(0.0D, 0.02D, 0.0D));
            }

            if (this.operation != Operation.MOVE_TO || this.waterAnimal.getNavigation().isDone()) {
                this.waterAnimal.setSpeed(0.0F);
                this.waterAnimal.setDeltaMovement(this.waterAnimal.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
                return;
            }

            double d0 = this.wantedX - this.waterAnimal.getX();
            double d1 = this.wantedY - this.waterAnimal.getY();
            double d2 = this.wantedZ - this.waterAnimal.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 /= d3;
            float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
            this.waterAnimal.setYRot(this.rotlerp(this.waterAnimal.getYRot(), f, 90.0F));
            this.waterAnimal.yBodyRot = this.waterAnimal.getYRot();
            float f1 = (float) (this.speedModifier * this.waterAnimal.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float f2 = Mth.lerp(0.125F, this.waterAnimal.getSpeed(), f1);
            this.waterAnimal.setSpeed(f2);
            this.waterAnimal.setDeltaMovement(this.waterAnimal.getDeltaMovement().add((double) f2 * d0 * 0.03D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.03D));
        } else {
            if (!this.waterAnimal.isOnGround()) {
                this.waterAnimal.setDeltaMovement(this.waterAnimal.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
            }

            super.tick();
        }
    }
}
