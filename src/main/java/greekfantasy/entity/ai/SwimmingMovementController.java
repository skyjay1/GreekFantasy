package greekfantasy.entity.ai;

import greekfantasy.entity.misc.ISwimmingMob;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.MathHelper;

public class SwimmingMovementController<T extends CreatureEntity & ISwimmingMob> extends MovementController {
  private final T entity;

  public SwimmingMovementController(T entityIn) {
    super(entityIn);
    this.entity = entityIn;
  }

  @Override
  public void tick() {
    // All of this is copied from DrownedEntity#MoveHelperController
    LivingEntity target = this.entity.getTarget();
    if (this.entity.isSwimmingUpCalculated() && this.entity.isInWater()) {
      if ((target != null && target.getY() > this.entity.getY()) || this.entity.isSwimmingUp()) {
        this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
      }

      if (this.operation != MovementController.Action.MOVE_TO || this.entity.getNavigation().isDone()) {
        this.entity.setSpeed(0.0F);
        return;
      }
      double dX = this.wantedX - this.entity.getX();
      double dY = this.wantedY - this.entity.getY();
      double dZ = this.wantedZ - this.entity.getZ();
      double dTotal = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
      dY /= dTotal;

      float rot = (float) (MathHelper.atan2(dZ, dX) * 57.2957763671875D) - 90.0F;
      this.entity.yRot = rotlerp(this.entity.yRot, rot, 90.0F);
      this.entity.yBodyRot = this.entity.yRot;

      float moveSpeed = (float) (this.speedModifier * this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED));
      float moveSpeedAdjusted = MathHelper.lerp(0.125F, this.entity.getSpeed(), moveSpeed);
      this.entity.setSpeed(moveSpeedAdjusted);
      this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(moveSpeedAdjusted * dX * 0.005D, moveSpeedAdjusted * dY * 0.1D,
          moveSpeedAdjusted * dZ * 0.005D));
    } else {
      if (!this.entity.isOnGround()) {
        this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
      }
      super.tick();
    }
  }
}