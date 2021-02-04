package greekfantasy.entity.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;

public abstract class SwirlGoal<T extends WaterMobEntity> extends Goal {

  protected final T entity;
  protected final int duration;
  protected final int cooldown;
  protected final double range;
  protected final boolean breakBoats;
  
  protected static final Predicate<Entity> target = (EntityPredicates.CAN_AI_TARGET.or(e -> e instanceof BoatEntity || e instanceof ItemEntity))
      .and(e -> e.getType() != GFRegistry.WHIRL_ENTITY && e.isNonBoss() && e.isInWaterOrBubbleColumn());
  
  protected List<Entity> trackedEntities = new ArrayList<>();
  protected int progressTime;
  protected int cooldownTime;
  
  /**
   * @param lEntity the water mob entity
   * @param lDuration the maximum amount of time this goal will run
   * @param lCooldown the minimum amount of time before this goal runs again
   * @param lRange the distance at which entities should be swirled
   * @param lBreakBoats true if this goal should break boats
   **/
  public SwirlGoal(final T lEntity, final int lDuration, final int lCooldown, final double lRange, 
      final boolean lBreakBoats) {
    entity = lEntity;
    duration = lDuration;
    cooldown = lCooldown;
    range = lRange;
    cooldownTime = 60;
    breakBoats = lBreakBoats;
  }
  
  @Override
  public boolean shouldExecute() {
    if(cooldownTime > 0) {
      cooldownTime--;
    } else if(entity.ticksExisted % 10 == 0) {
      trackedEntities = getEntitiesInRange(entity, range, e -> canSwirl(e));
      return trackedEntities.size() > 0;
    }
    return false;
  }
  
  @Override
  public void startExecuting() {
    progressTime = 1;
  }
  
  @Override
  public boolean shouldContinueExecuting() {
    if(entity.ticksExisted % 11 == 0) {
      trackedEntities = getEntitiesInRange(entity, range, e -> canSwirl(e));
    }
    return progressTime > 0 && trackedEntities.size() > 0;
  }
  
  @Override
  public void tick() {
    // goal timer
    if(progressTime++ >= duration) {
      resetTask();
      return;
    }
    // move tracked entities
    for(final Entity e : trackedEntities) {
      // try to break boats
      if(e instanceof BoatEntity) {
        if(breakBoats && entity.getRNG().nextInt(8) == 0) {
          e.attackEntityFrom(DamageSource.causeMobDamage(entity), 3.0F);
        }
        continue;
      }
      // distance math
      double dx = entity.getPosX() - e.getPositionVec().x;
      //double dy = entity.getPosY() - e.getPositionVec().y;
      double dz = entity.getPosZ() - e.getPositionVec().z;
      final double horizDisSq = dx * dx + dz * dz;
      if(entity.getBoundingBox().grow(1.0D).intersects(e.getBoundingBox())) {
        // collide with the entity
        onCollideWith(e);
      } else {
        // move the target toward this entity
        swirlEntity(entity, range, e, horizDisSq);
      }
    }
  }
  
  @Override
  public void resetTask() {
    progressTime = 0;
    cooldownTime = cooldown;
    trackedEntities.clear();
  }
  
  protected abstract void onCollideWith(final Entity e);
  
  protected abstract boolean canSwirl(final Entity e);
    
  protected static List<Entity> getEntitiesInRange(final WaterMobEntity entity, final double range, final Predicate<Entity> predicate) {
    return entity.getEntityWorld().getEntitiesInAABBexcluding(entity, entity.getBoundingBox().grow(range, range / 2, range), predicate);
  }
  
  protected static void swirlEntity(final Entity entity, final double range, final Entity target, final double disSq) {
    // calculate the amount of motion to apply based on distance
    final double motion = 0.062D + 0.11D * (1.0D - (disSq / (range * range)));
    final Vector3d normalVec = entity.getPositionVec().mul(1.0D, 0, 1.0D).subtract(target.getPositionVec().mul(1.0D, 0, 1.0D)).normalize();
    final Vector3d rotatedVec = normalVec.rotateYaw(1.5707963267F).scale(motion);
    final Vector3d motionVec = target.getMotion().add(normalVec.scale(0.028D)).add(rotatedVec).mul(0.65D, 1.0D, 0.65D);
    target.setMotion(motionVec);
    target.addVelocity(0, 0.0068D, 0);
    target.velocityChanged = true;
  }
}
