package greekfantasy.entity.ai;

import greekfantasy.entity.ISwimmingMob;
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
    LivingEntity target = this.entity.getAttackTarget();
    if (this.entity.isSwimmingUpCalculated() && this.entity.isInWater()) {
      if ((target != null && target.getPosY() > this.entity.getPosY()) || this.entity.isSwimmingUp()) {
        this.entity.setMotion(this.entity.getMotion().add(0.0D, 0.002D, 0.0D));
      }

      if (this.action != MovementController.Action.MOVE_TO || this.entity.getNavigator().noPath()) {
        this.entity.setAIMoveSpeed(0.0F);
        return;
      }
      double dX = this.posX - this.entity.getPosX();
      double dY = this.posY - this.entity.getPosY();
      double dZ = this.posZ - this.entity.getPosZ();
      double dTotal = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
      dY /= dTotal;

      float rot = (float) (MathHelper.atan2(dZ, dX) * 57.2957763671875D) - 90.0F;
      this.entity.rotationYaw = limitAngle(this.entity.rotationYaw, rot, 90.0F);
      this.entity.renderYawOffset = this.entity.rotationYaw;

      float moveSpeed = (float) (this.speed * this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED));
      float moveSpeedAdjusted = MathHelper.lerp(0.125F, this.entity.getAIMoveSpeed(), moveSpeed);
      this.entity.setAIMoveSpeed(moveSpeedAdjusted);
      this.entity.setMotion(this.entity.getMotion().add(moveSpeedAdjusted * dX * 0.005D, moveSpeedAdjusted * dY * 0.1D,
          moveSpeedAdjusted * dZ * 0.005D));
    } else {
      if (!this.entity.isOnGround()) {
        this.entity.setMotion(this.entity.getMotion().add(0.0D, -0.008D, 0.0D));
      }
      super.tick();
    }
  }
}