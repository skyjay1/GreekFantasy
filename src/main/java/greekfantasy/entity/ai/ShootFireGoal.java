package greekfantasy.entity.ai;

import java.util.EnumSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class ShootFireGoal extends Goal {
  private final MobEntity entity;
  private final int maxCooldown;
  private final int maxShootFireTime;
  private final double fireRange;

  private int fireBreathingTime;
  private int cooldown;
  
  protected ShootFireGoal(final MobEntity entityIn, final int fireTimeIn, final int maxCooldownIn, final double fireRangeIn) {
    this.setFlags(EnumSet.allOf(Goal.Flag.class));
    entity = entityIn;
    maxShootFireTime = fireTimeIn;
    fireRange = fireRangeIn;
    maxCooldown = maxCooldownIn;
    cooldown = 30;
  }

  @Override
  public boolean canUse() {  
    if(this.cooldown > 0) {
      cooldown--;
    } else if (entity.getTarget() != null
        && entity.distanceToSqr(entity.getTarget()) < (fireRange * fireRange)
        && entity.canSee(entity.getTarget())) {
      return true;
    }
    return false;
  }
  
  @Override
  public boolean canContinueToUse() {
    return entity.getTarget() != null
        && entity.canSee(entity.getTarget())
        && entity.distanceToSqr(entity.getTarget()) < (fireRange * fireRange);
  }
 
  @Override
  public void start() {
    this.fireBreathingTime = 1;
    entity.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.2F);
  }
  
  @Override
  public void tick() {
    if(fireBreathingTime > 0 && fireBreathingTime < maxShootFireTime) {
      fireBreathingTime++;
      // stop the entity from moving, and adjust look vecs
      entity.getNavigation().stop();
      entity.lookAt(entity.getTarget(), 100.0F, 100.0F);
      entity.getLookControl().setLookAt(entity.getTarget(), 100.0F, 100.0F);
      // set fire to targetPos
      if(fireBreathingTime > 18 && fireBreathingTime % 7 == 0) {
        final Vector3d entityPos = new Vector3d(entity.getX(), entity.getEyeY(), entity.getZ());
        igniteInRange(entityPos, entity.getTarget().position(), 0.65D, 5);
        entity.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
      }
    } else {
      stop();
    } 
  }
  
  @Override
  public void stop() {
    this.fireBreathingTime = 0;
    this.cooldown = maxCooldown;
  }
  
  /**
   * Ignites all entities along a raytrace given the start and end positions
   * @param startPos the starting position
   * @param endPos the ending position
   * @param radius the radius around each point in the ray to check for entities
   * @param fireTime the amount of time to set fire to the entity
   **/
  private void igniteInRange(final Vector3d startPos, final Vector3d endPos, final double radius, final int fireTime) {    
    Vector3d vecDifference = endPos.subtract(startPos);
    // step along the vector created by adding the start position and the difference vector
    for(double i = 0.1, l = vecDifference.length(), stepSize = radius * 0.75D; i < l; i += stepSize) {
      Vector3d scaled = startPos.add(vecDifference.scale(i));
      // make a box at this position along the vector
      final AxisAlignedBB aabb = new AxisAlignedBB(scaled.x - radius, scaled.y - radius, scaled.z - radius, scaled.x + radius, scaled.y + radius, scaled.z + radius);
      for(final Entity e : entity.getCommandSenderWorld().getEntities(entity, aabb)) {
        // set fire to any entities inside the box
        e.setSecondsOnFire(fireTime + entity.getRandom().nextInt(5) - 2);
      }
    }
  }
}
